package com.yali.finspin.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.yali.finspin.IntegrationTest;
import com.yali.finspin.domain.OrgGroup;
import com.yali.finspin.repository.OrgGroupRepository;
import com.yali.finspin.repository.search.OrgGroupSearchRepository;
import com.yali.finspin.service.OrgGroupService;
import com.yali.finspin.service.dto.OrgGroupDTO;
import com.yali.finspin.service.mapper.OrgGroupMapper;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Integration tests for the {@link OrgGroupResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class OrgGroupResourceIT {

    private static final String DEFAULT_COUNTRY_NAME = "AAAAAAAAAA";
    private static final String UPDATED_COUNTRY_NAME = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATE_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATE_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/org-groups";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/org-groups";

    @Autowired
    private OrgGroupRepository orgGroupRepository;

    @Mock
    private OrgGroupRepository orgGroupRepositoryMock;

    @Autowired
    private OrgGroupMapper orgGroupMapper;

    @Mock
    private OrgGroupService orgGroupServiceMock;

    /**
     * This repository is mocked in the com.yali.finspin.repository.search test package.
     *
     * @see com.yali.finspin.repository.search.OrgGroupSearchRepositoryMockConfiguration
     */
    @Autowired
    private OrgGroupSearchRepository mockOrgGroupSearchRepository;

    @Autowired
    private WebTestClient webTestClient;

    private OrgGroup orgGroup;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrgGroup createEntity() {
        OrgGroup orgGroup = new OrgGroup()
            .countryName(DEFAULT_COUNTRY_NAME)
            .createdDate(DEFAULT_CREATED_DATE)
            .updateDate(DEFAULT_UPDATE_DATE);
        return orgGroup;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrgGroup createUpdatedEntity() {
        OrgGroup orgGroup = new OrgGroup()
            .countryName(UPDATED_COUNTRY_NAME)
            .createdDate(UPDATED_CREATED_DATE)
            .updateDate(UPDATED_UPDATE_DATE);
        return orgGroup;
    }

    @BeforeEach
    public void initTest() {
        orgGroupRepository.deleteAll().block();
        orgGroup = createEntity();
    }

    @Test
    void createOrgGroup() throws Exception {
        int databaseSizeBeforeCreate = orgGroupRepository.findAll().collectList().block().size();
        // Configure the mock search repository
        when(mockOrgGroupSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Create the OrgGroup
        OrgGroupDTO orgGroupDTO = orgGroupMapper.toDto(orgGroup);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(orgGroupDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the OrgGroup in the database
        List<OrgGroup> orgGroupList = orgGroupRepository.findAll().collectList().block();
        assertThat(orgGroupList).hasSize(databaseSizeBeforeCreate + 1);
        OrgGroup testOrgGroup = orgGroupList.get(orgGroupList.size() - 1);
        assertThat(testOrgGroup.getCountryName()).isEqualTo(DEFAULT_COUNTRY_NAME);
        assertThat(testOrgGroup.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testOrgGroup.getUpdateDate()).isEqualTo(DEFAULT_UPDATE_DATE);

        // Validate the OrgGroup in Elasticsearch
        verify(mockOrgGroupSearchRepository, times(1)).save(testOrgGroup);
    }

    @Test
    void createOrgGroupWithExistingId() throws Exception {
        // Create the OrgGroup with an existing ID
        orgGroup.setId("existing_id");
        OrgGroupDTO orgGroupDTO = orgGroupMapper.toDto(orgGroup);

        int databaseSizeBeforeCreate = orgGroupRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(orgGroupDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OrgGroup in the database
        List<OrgGroup> orgGroupList = orgGroupRepository.findAll().collectList().block();
        assertThat(orgGroupList).hasSize(databaseSizeBeforeCreate);

        // Validate the OrgGroup in Elasticsearch
        verify(mockOrgGroupSearchRepository, times(0)).save(orgGroup);
    }

    @Test
    void getAllOrgGroups() {
        // Initialize the database
        orgGroupRepository.save(orgGroup).block();

        // Get all the orgGroupList
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
            .value(hasItem(orgGroup.getId()))
            .jsonPath("$.[*].countryName")
            .value(hasItem(DEFAULT_COUNTRY_NAME))
            .jsonPath("$.[*].createdDate")
            .value(hasItem(DEFAULT_CREATED_DATE.toString()))
            .jsonPath("$.[*].updateDate")
            .value(hasItem(DEFAULT_UPDATE_DATE.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllOrgGroupsWithEagerRelationshipsIsEnabled() {
        when(orgGroupServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(orgGroupServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllOrgGroupsWithEagerRelationshipsIsNotEnabled() {
        when(orgGroupServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(orgGroupServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getOrgGroup() {
        // Initialize the database
        orgGroupRepository.save(orgGroup).block();

        // Get the orgGroup
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, orgGroup.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(orgGroup.getId()))
            .jsonPath("$.countryName")
            .value(is(DEFAULT_COUNTRY_NAME))
            .jsonPath("$.createdDate")
            .value(is(DEFAULT_CREATED_DATE.toString()))
            .jsonPath("$.updateDate")
            .value(is(DEFAULT_UPDATE_DATE.toString()));
    }

    @Test
    void getNonExistingOrgGroup() {
        // Get the orgGroup
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewOrgGroup() throws Exception {
        // Configure the mock search repository
        when(mockOrgGroupSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        orgGroupRepository.save(orgGroup).block();

        int databaseSizeBeforeUpdate = orgGroupRepository.findAll().collectList().block().size();

        // Update the orgGroup
        OrgGroup updatedOrgGroup = orgGroupRepository.findById(orgGroup.getId()).block();
        updatedOrgGroup.countryName(UPDATED_COUNTRY_NAME).createdDate(UPDATED_CREATED_DATE).updateDate(UPDATED_UPDATE_DATE);
        OrgGroupDTO orgGroupDTO = orgGroupMapper.toDto(updatedOrgGroup);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, orgGroupDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(orgGroupDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the OrgGroup in the database
        List<OrgGroup> orgGroupList = orgGroupRepository.findAll().collectList().block();
        assertThat(orgGroupList).hasSize(databaseSizeBeforeUpdate);
        OrgGroup testOrgGroup = orgGroupList.get(orgGroupList.size() - 1);
        assertThat(testOrgGroup.getCountryName()).isEqualTo(UPDATED_COUNTRY_NAME);
        assertThat(testOrgGroup.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testOrgGroup.getUpdateDate()).isEqualTo(UPDATED_UPDATE_DATE);

        // Validate the OrgGroup in Elasticsearch
        verify(mockOrgGroupSearchRepository).save(testOrgGroup);
    }

    @Test
    void putNonExistingOrgGroup() throws Exception {
        int databaseSizeBeforeUpdate = orgGroupRepository.findAll().collectList().block().size();
        orgGroup.setId(UUID.randomUUID().toString());

        // Create the OrgGroup
        OrgGroupDTO orgGroupDTO = orgGroupMapper.toDto(orgGroup);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, orgGroupDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(orgGroupDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OrgGroup in the database
        List<OrgGroup> orgGroupList = orgGroupRepository.findAll().collectList().block();
        assertThat(orgGroupList).hasSize(databaseSizeBeforeUpdate);

        // Validate the OrgGroup in Elasticsearch
        verify(mockOrgGroupSearchRepository, times(0)).save(orgGroup);
    }

    @Test
    void putWithIdMismatchOrgGroup() throws Exception {
        int databaseSizeBeforeUpdate = orgGroupRepository.findAll().collectList().block().size();
        orgGroup.setId(UUID.randomUUID().toString());

        // Create the OrgGroup
        OrgGroupDTO orgGroupDTO = orgGroupMapper.toDto(orgGroup);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(orgGroupDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OrgGroup in the database
        List<OrgGroup> orgGroupList = orgGroupRepository.findAll().collectList().block();
        assertThat(orgGroupList).hasSize(databaseSizeBeforeUpdate);

        // Validate the OrgGroup in Elasticsearch
        verify(mockOrgGroupSearchRepository, times(0)).save(orgGroup);
    }

    @Test
    void putWithMissingIdPathParamOrgGroup() throws Exception {
        int databaseSizeBeforeUpdate = orgGroupRepository.findAll().collectList().block().size();
        orgGroup.setId(UUID.randomUUID().toString());

        // Create the OrgGroup
        OrgGroupDTO orgGroupDTO = orgGroupMapper.toDto(orgGroup);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(orgGroupDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the OrgGroup in the database
        List<OrgGroup> orgGroupList = orgGroupRepository.findAll().collectList().block();
        assertThat(orgGroupList).hasSize(databaseSizeBeforeUpdate);

        // Validate the OrgGroup in Elasticsearch
        verify(mockOrgGroupSearchRepository, times(0)).save(orgGroup);
    }

    @Test
    void partialUpdateOrgGroupWithPatch() throws Exception {
        // Initialize the database
        orgGroupRepository.save(orgGroup).block();

        int databaseSizeBeforeUpdate = orgGroupRepository.findAll().collectList().block().size();

        // Update the orgGroup using partial update
        OrgGroup partialUpdatedOrgGroup = new OrgGroup();
        partialUpdatedOrgGroup.setId(orgGroup.getId());

        partialUpdatedOrgGroup.updateDate(UPDATED_UPDATE_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOrgGroup.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedOrgGroup))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the OrgGroup in the database
        List<OrgGroup> orgGroupList = orgGroupRepository.findAll().collectList().block();
        assertThat(orgGroupList).hasSize(databaseSizeBeforeUpdate);
        OrgGroup testOrgGroup = orgGroupList.get(orgGroupList.size() - 1);
        assertThat(testOrgGroup.getCountryName()).isEqualTo(DEFAULT_COUNTRY_NAME);
        assertThat(testOrgGroup.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testOrgGroup.getUpdateDate()).isEqualTo(UPDATED_UPDATE_DATE);
    }

    @Test
    void fullUpdateOrgGroupWithPatch() throws Exception {
        // Initialize the database
        orgGroupRepository.save(orgGroup).block();

        int databaseSizeBeforeUpdate = orgGroupRepository.findAll().collectList().block().size();

        // Update the orgGroup using partial update
        OrgGroup partialUpdatedOrgGroup = new OrgGroup();
        partialUpdatedOrgGroup.setId(orgGroup.getId());

        partialUpdatedOrgGroup.countryName(UPDATED_COUNTRY_NAME).createdDate(UPDATED_CREATED_DATE).updateDate(UPDATED_UPDATE_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOrgGroup.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedOrgGroup))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the OrgGroup in the database
        List<OrgGroup> orgGroupList = orgGroupRepository.findAll().collectList().block();
        assertThat(orgGroupList).hasSize(databaseSizeBeforeUpdate);
        OrgGroup testOrgGroup = orgGroupList.get(orgGroupList.size() - 1);
        assertThat(testOrgGroup.getCountryName()).isEqualTo(UPDATED_COUNTRY_NAME);
        assertThat(testOrgGroup.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testOrgGroup.getUpdateDate()).isEqualTo(UPDATED_UPDATE_DATE);
    }

    @Test
    void patchNonExistingOrgGroup() throws Exception {
        int databaseSizeBeforeUpdate = orgGroupRepository.findAll().collectList().block().size();
        orgGroup.setId(UUID.randomUUID().toString());

        // Create the OrgGroup
        OrgGroupDTO orgGroupDTO = orgGroupMapper.toDto(orgGroup);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, orgGroupDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(orgGroupDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OrgGroup in the database
        List<OrgGroup> orgGroupList = orgGroupRepository.findAll().collectList().block();
        assertThat(orgGroupList).hasSize(databaseSizeBeforeUpdate);

        // Validate the OrgGroup in Elasticsearch
        verify(mockOrgGroupSearchRepository, times(0)).save(orgGroup);
    }

    @Test
    void patchWithIdMismatchOrgGroup() throws Exception {
        int databaseSizeBeforeUpdate = orgGroupRepository.findAll().collectList().block().size();
        orgGroup.setId(UUID.randomUUID().toString());

        // Create the OrgGroup
        OrgGroupDTO orgGroupDTO = orgGroupMapper.toDto(orgGroup);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(orgGroupDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OrgGroup in the database
        List<OrgGroup> orgGroupList = orgGroupRepository.findAll().collectList().block();
        assertThat(orgGroupList).hasSize(databaseSizeBeforeUpdate);

        // Validate the OrgGroup in Elasticsearch
        verify(mockOrgGroupSearchRepository, times(0)).save(orgGroup);
    }

    @Test
    void patchWithMissingIdPathParamOrgGroup() throws Exception {
        int databaseSizeBeforeUpdate = orgGroupRepository.findAll().collectList().block().size();
        orgGroup.setId(UUID.randomUUID().toString());

        // Create the OrgGroup
        OrgGroupDTO orgGroupDTO = orgGroupMapper.toDto(orgGroup);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(orgGroupDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the OrgGroup in the database
        List<OrgGroup> orgGroupList = orgGroupRepository.findAll().collectList().block();
        assertThat(orgGroupList).hasSize(databaseSizeBeforeUpdate);

        // Validate the OrgGroup in Elasticsearch
        verify(mockOrgGroupSearchRepository, times(0)).save(orgGroup);
    }

    @Test
    void deleteOrgGroup() {
        // Configure the mock search repository
        when(mockOrgGroupSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockOrgGroupSearchRepository.deleteById(anyString())).thenReturn(Mono.empty());
        // Initialize the database
        orgGroupRepository.save(orgGroup).block();

        int databaseSizeBeforeDelete = orgGroupRepository.findAll().collectList().block().size();

        // Delete the orgGroup
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, orgGroup.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<OrgGroup> orgGroupList = orgGroupRepository.findAll().collectList().block();
        assertThat(orgGroupList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the OrgGroup in Elasticsearch
        verify(mockOrgGroupSearchRepository, times(1)).deleteById(orgGroup.getId());
    }

    @Test
    void searchOrgGroup() {
        // Configure the mock search repository
        when(mockOrgGroupSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockOrgGroupSearchRepository.count()).thenReturn(Mono.just(1L));
        // Initialize the database
        orgGroupRepository.save(orgGroup).block();
        when(mockOrgGroupSearchRepository.search("id:" + orgGroup.getId(), PageRequest.of(0, 20))).thenReturn(Flux.just(orgGroup));

        // Search the orgGroup
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + orgGroup.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(orgGroup.getId()))
            .jsonPath("$.[*].countryName")
            .value(hasItem(DEFAULT_COUNTRY_NAME))
            .jsonPath("$.[*].createdDate")
            .value(hasItem(DEFAULT_CREATED_DATE.toString()))
            .jsonPath("$.[*].updateDate")
            .value(hasItem(DEFAULT_UPDATE_DATE.toString()));
    }
}
