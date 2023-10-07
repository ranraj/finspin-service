package com.yali.finspin.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.yali.finspin.IntegrationTest;
import com.yali.finspin.domain.OrgAccount;
import com.yali.finspin.repository.OrgAccountRepository;
import com.yali.finspin.repository.search.OrgAccountSearchRepository;
import com.yali.finspin.service.dto.OrgAccountDTO;
import com.yali.finspin.service.mapper.OrgAccountMapper;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Integration tests for the {@link OrgAccountResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class OrgAccountResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Boolean DEFAULT_BLOCKED = false;
    private static final Boolean UPDATED_BLOCKED = true;

    private static final String ENTITY_API_URL = "/api/org-accounts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/org-accounts";

    @Autowired
    private OrgAccountRepository orgAccountRepository;

    @Autowired
    private OrgAccountMapper orgAccountMapper;

    /**
     * This repository is mocked in the com.yali.finspin.repository.search test package.
     *
     * @see com.yali.finspin.repository.search.OrgAccountSearchRepositoryMockConfiguration
     */
    @Autowired
    private OrgAccountSearchRepository mockOrgAccountSearchRepository;

    @Autowired
    private WebTestClient webTestClient;

    private OrgAccount orgAccount;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrgAccount createEntity() {
        OrgAccount orgAccount = new OrgAccount().name(DEFAULT_NAME).blocked(DEFAULT_BLOCKED);
        return orgAccount;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrgAccount createUpdatedEntity() {
        OrgAccount orgAccount = new OrgAccount().name(UPDATED_NAME).blocked(UPDATED_BLOCKED);
        return orgAccount;
    }

    @BeforeEach
    public void initTest() {
        orgAccountRepository.deleteAll().block();
        orgAccount = createEntity();
    }

    @Test
    void createOrgAccount() throws Exception {
        int databaseSizeBeforeCreate = orgAccountRepository.findAll().collectList().block().size();
        // Configure the mock search repository
        when(mockOrgAccountSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Create the OrgAccount
        OrgAccountDTO orgAccountDTO = orgAccountMapper.toDto(orgAccount);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(orgAccountDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the OrgAccount in the database
        List<OrgAccount> orgAccountList = orgAccountRepository.findAll().collectList().block();
        assertThat(orgAccountList).hasSize(databaseSizeBeforeCreate + 1);
        OrgAccount testOrgAccount = orgAccountList.get(orgAccountList.size() - 1);
        assertThat(testOrgAccount.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testOrgAccount.getBlocked()).isEqualTo(DEFAULT_BLOCKED);

        // Validate the OrgAccount in Elasticsearch
        verify(mockOrgAccountSearchRepository, times(1)).save(testOrgAccount);
    }

    @Test
    void createOrgAccountWithExistingId() throws Exception {
        // Create the OrgAccount with an existing ID
        orgAccount.setId("existing_id");
        OrgAccountDTO orgAccountDTO = orgAccountMapper.toDto(orgAccount);

        int databaseSizeBeforeCreate = orgAccountRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(orgAccountDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OrgAccount in the database
        List<OrgAccount> orgAccountList = orgAccountRepository.findAll().collectList().block();
        assertThat(orgAccountList).hasSize(databaseSizeBeforeCreate);

        // Validate the OrgAccount in Elasticsearch
        verify(mockOrgAccountSearchRepository, times(0)).save(orgAccount);
    }

    @Test
    void getAllOrgAccountsAsStream() {
        // Initialize the database
        orgAccountRepository.save(orgAccount).block();

        List<OrgAccount> orgAccountList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(OrgAccountDTO.class)
            .getResponseBody()
            .map(orgAccountMapper::toEntity)
            .filter(orgAccount::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(orgAccountList).isNotNull();
        assertThat(orgAccountList).hasSize(1);
        OrgAccount testOrgAccount = orgAccountList.get(0);
        assertThat(testOrgAccount.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testOrgAccount.getBlocked()).isEqualTo(DEFAULT_BLOCKED);
    }

    @Test
    void getAllOrgAccounts() {
        // Initialize the database
        orgAccountRepository.save(orgAccount).block();

        // Get all the orgAccountList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(orgAccount.getId()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].blocked")
            .value(hasItem(DEFAULT_BLOCKED.booleanValue()));
    }

    @Test
    void getOrgAccount() {
        // Initialize the database
        orgAccountRepository.save(orgAccount).block();

        // Get the orgAccount
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, orgAccount.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(orgAccount.getId()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.blocked")
            .value(is(DEFAULT_BLOCKED.booleanValue()));
    }

    @Test
    void getNonExistingOrgAccount() {
        // Get the orgAccount
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewOrgAccount() throws Exception {
        // Configure the mock search repository
        when(mockOrgAccountSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        orgAccountRepository.save(orgAccount).block();

        int databaseSizeBeforeUpdate = orgAccountRepository.findAll().collectList().block().size();

        // Update the orgAccount
        OrgAccount updatedOrgAccount = orgAccountRepository.findById(orgAccount.getId()).block();
        updatedOrgAccount.name(UPDATED_NAME).blocked(UPDATED_BLOCKED);
        OrgAccountDTO orgAccountDTO = orgAccountMapper.toDto(updatedOrgAccount);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, orgAccountDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(orgAccountDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the OrgAccount in the database
        List<OrgAccount> orgAccountList = orgAccountRepository.findAll().collectList().block();
        assertThat(orgAccountList).hasSize(databaseSizeBeforeUpdate);
        OrgAccount testOrgAccount = orgAccountList.get(orgAccountList.size() - 1);
        assertThat(testOrgAccount.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testOrgAccount.getBlocked()).isEqualTo(UPDATED_BLOCKED);

        // Validate the OrgAccount in Elasticsearch
        verify(mockOrgAccountSearchRepository).save(testOrgAccount);
    }

    @Test
    void putNonExistingOrgAccount() throws Exception {
        int databaseSizeBeforeUpdate = orgAccountRepository.findAll().collectList().block().size();
        orgAccount.setId(UUID.randomUUID().toString());

        // Create the OrgAccount
        OrgAccountDTO orgAccountDTO = orgAccountMapper.toDto(orgAccount);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, orgAccountDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(orgAccountDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OrgAccount in the database
        List<OrgAccount> orgAccountList = orgAccountRepository.findAll().collectList().block();
        assertThat(orgAccountList).hasSize(databaseSizeBeforeUpdate);

        // Validate the OrgAccount in Elasticsearch
        verify(mockOrgAccountSearchRepository, times(0)).save(orgAccount);
    }

    @Test
    void putWithIdMismatchOrgAccount() throws Exception {
        int databaseSizeBeforeUpdate = orgAccountRepository.findAll().collectList().block().size();
        orgAccount.setId(UUID.randomUUID().toString());

        // Create the OrgAccount
        OrgAccountDTO orgAccountDTO = orgAccountMapper.toDto(orgAccount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(orgAccountDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OrgAccount in the database
        List<OrgAccount> orgAccountList = orgAccountRepository.findAll().collectList().block();
        assertThat(orgAccountList).hasSize(databaseSizeBeforeUpdate);

        // Validate the OrgAccount in Elasticsearch
        verify(mockOrgAccountSearchRepository, times(0)).save(orgAccount);
    }

    @Test
    void putWithMissingIdPathParamOrgAccount() throws Exception {
        int databaseSizeBeforeUpdate = orgAccountRepository.findAll().collectList().block().size();
        orgAccount.setId(UUID.randomUUID().toString());

        // Create the OrgAccount
        OrgAccountDTO orgAccountDTO = orgAccountMapper.toDto(orgAccount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(orgAccountDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the OrgAccount in the database
        List<OrgAccount> orgAccountList = orgAccountRepository.findAll().collectList().block();
        assertThat(orgAccountList).hasSize(databaseSizeBeforeUpdate);

        // Validate the OrgAccount in Elasticsearch
        verify(mockOrgAccountSearchRepository, times(0)).save(orgAccount);
    }

    @Test
    void partialUpdateOrgAccountWithPatch() throws Exception {
        // Initialize the database
        orgAccountRepository.save(orgAccount).block();

        int databaseSizeBeforeUpdate = orgAccountRepository.findAll().collectList().block().size();

        // Update the orgAccount using partial update
        OrgAccount partialUpdatedOrgAccount = new OrgAccount();
        partialUpdatedOrgAccount.setId(orgAccount.getId());

        partialUpdatedOrgAccount.name(UPDATED_NAME).blocked(UPDATED_BLOCKED);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOrgAccount.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedOrgAccount))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the OrgAccount in the database
        List<OrgAccount> orgAccountList = orgAccountRepository.findAll().collectList().block();
        assertThat(orgAccountList).hasSize(databaseSizeBeforeUpdate);
        OrgAccount testOrgAccount = orgAccountList.get(orgAccountList.size() - 1);
        assertThat(testOrgAccount.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testOrgAccount.getBlocked()).isEqualTo(UPDATED_BLOCKED);
    }

    @Test
    void fullUpdateOrgAccountWithPatch() throws Exception {
        // Initialize the database
        orgAccountRepository.save(orgAccount).block();

        int databaseSizeBeforeUpdate = orgAccountRepository.findAll().collectList().block().size();

        // Update the orgAccount using partial update
        OrgAccount partialUpdatedOrgAccount = new OrgAccount();
        partialUpdatedOrgAccount.setId(orgAccount.getId());

        partialUpdatedOrgAccount.name(UPDATED_NAME).blocked(UPDATED_BLOCKED);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOrgAccount.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedOrgAccount))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the OrgAccount in the database
        List<OrgAccount> orgAccountList = orgAccountRepository.findAll().collectList().block();
        assertThat(orgAccountList).hasSize(databaseSizeBeforeUpdate);
        OrgAccount testOrgAccount = orgAccountList.get(orgAccountList.size() - 1);
        assertThat(testOrgAccount.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testOrgAccount.getBlocked()).isEqualTo(UPDATED_BLOCKED);
    }

    @Test
    void patchNonExistingOrgAccount() throws Exception {
        int databaseSizeBeforeUpdate = orgAccountRepository.findAll().collectList().block().size();
        orgAccount.setId(UUID.randomUUID().toString());

        // Create the OrgAccount
        OrgAccountDTO orgAccountDTO = orgAccountMapper.toDto(orgAccount);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, orgAccountDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(orgAccountDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OrgAccount in the database
        List<OrgAccount> orgAccountList = orgAccountRepository.findAll().collectList().block();
        assertThat(orgAccountList).hasSize(databaseSizeBeforeUpdate);

        // Validate the OrgAccount in Elasticsearch
        verify(mockOrgAccountSearchRepository, times(0)).save(orgAccount);
    }

    @Test
    void patchWithIdMismatchOrgAccount() throws Exception {
        int databaseSizeBeforeUpdate = orgAccountRepository.findAll().collectList().block().size();
        orgAccount.setId(UUID.randomUUID().toString());

        // Create the OrgAccount
        OrgAccountDTO orgAccountDTO = orgAccountMapper.toDto(orgAccount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(orgAccountDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OrgAccount in the database
        List<OrgAccount> orgAccountList = orgAccountRepository.findAll().collectList().block();
        assertThat(orgAccountList).hasSize(databaseSizeBeforeUpdate);

        // Validate the OrgAccount in Elasticsearch
        verify(mockOrgAccountSearchRepository, times(0)).save(orgAccount);
    }

    @Test
    void patchWithMissingIdPathParamOrgAccount() throws Exception {
        int databaseSizeBeforeUpdate = orgAccountRepository.findAll().collectList().block().size();
        orgAccount.setId(UUID.randomUUID().toString());

        // Create the OrgAccount
        OrgAccountDTO orgAccountDTO = orgAccountMapper.toDto(orgAccount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(orgAccountDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the OrgAccount in the database
        List<OrgAccount> orgAccountList = orgAccountRepository.findAll().collectList().block();
        assertThat(orgAccountList).hasSize(databaseSizeBeforeUpdate);

        // Validate the OrgAccount in Elasticsearch
        verify(mockOrgAccountSearchRepository, times(0)).save(orgAccount);
    }

    @Test
    void deleteOrgAccount() {
        // Configure the mock search repository
        when(mockOrgAccountSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockOrgAccountSearchRepository.deleteById(anyString())).thenReturn(Mono.empty());
        // Initialize the database
        orgAccountRepository.save(orgAccount).block();

        int databaseSizeBeforeDelete = orgAccountRepository.findAll().collectList().block().size();

        // Delete the orgAccount
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, orgAccount.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<OrgAccount> orgAccountList = orgAccountRepository.findAll().collectList().block();
        assertThat(orgAccountList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the OrgAccount in Elasticsearch
        verify(mockOrgAccountSearchRepository, times(1)).deleteById(orgAccount.getId());
    }

    @Test
    void searchOrgAccount() {
        // Configure the mock search repository
        when(mockOrgAccountSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        orgAccountRepository.save(orgAccount).block();
        when(mockOrgAccountSearchRepository.search("id:" + orgAccount.getId())).thenReturn(Flux.just(orgAccount));

        // Search the orgAccount
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + orgAccount.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(orgAccount.getId()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].blocked")
            .value(hasItem(DEFAULT_BLOCKED.booleanValue()));
    }
}
