package com.yali.finspin.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.yali.finspin.IntegrationTest;
import com.yali.finspin.domain.Organisation;
import com.yali.finspin.repository.OrganisationRepository;
import com.yali.finspin.repository.search.OrganisationSearchRepository;
import com.yali.finspin.service.dto.OrganisationDTO;
import com.yali.finspin.service.mapper.OrganisationMapper;
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
 * Integration tests for the {@link OrganisationResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class OrganisationResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Boolean DEFAULT_BLOCKED = false;
    private static final Boolean UPDATED_BLOCKED = true;

    private static final String ENTITY_API_URL = "/api/organisations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/organisations";

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private OrganisationMapper organisationMapper;

    /**
     * This repository is mocked in the com.yali.finspin.repository.search test package.
     *
     * @see com.yali.finspin.repository.search.OrganisationSearchRepositoryMockConfiguration
     */
    @Autowired
    private OrganisationSearchRepository mockOrganisationSearchRepository;

    @Autowired
    private WebTestClient webTestClient;

    private Organisation organisation;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Organisation createEntity() {
        Organisation organisation = new Organisation().name(DEFAULT_NAME).blocked(DEFAULT_BLOCKED);
        return organisation;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Organisation createUpdatedEntity() {
        Organisation organisation = new Organisation().name(UPDATED_NAME).blocked(UPDATED_BLOCKED);
        return organisation;
    }

    @BeforeEach
    public void initTest() {
        organisationRepository.deleteAll().block();
        organisation = createEntity();
    }

    @Test
    void createOrganisation() throws Exception {
        int databaseSizeBeforeCreate = organisationRepository.findAll().collectList().block().size();
        // Configure the mock search repository
        when(mockOrganisationSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Create the Organisation
        OrganisationDTO organisationDTO = organisationMapper.toDto(organisation);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(organisationDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Organisation in the database
        List<Organisation> organisationList = organisationRepository.findAll().collectList().block();
        assertThat(organisationList).hasSize(databaseSizeBeforeCreate + 1);
        Organisation testOrganisation = organisationList.get(organisationList.size() - 1);
        assertThat(testOrganisation.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testOrganisation.getBlocked()).isEqualTo(DEFAULT_BLOCKED);

        // Validate the Organisation in Elasticsearch
        verify(mockOrganisationSearchRepository, times(1)).save(testOrganisation);
    }

    @Test
    void createOrganisationWithExistingId() throws Exception {
        // Create the Organisation with an existing ID
        organisation.setId("existing_id");
        OrganisationDTO organisationDTO = organisationMapper.toDto(organisation);

        int databaseSizeBeforeCreate = organisationRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(organisationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Organisation in the database
        List<Organisation> organisationList = organisationRepository.findAll().collectList().block();
        assertThat(organisationList).hasSize(databaseSizeBeforeCreate);

        // Validate the Organisation in Elasticsearch
        verify(mockOrganisationSearchRepository, times(0)).save(organisation);
    }

    @Test
    void getAllOrganisationsAsStream() {
        // Initialize the database
        organisationRepository.save(organisation).block();

        List<Organisation> organisationList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(OrganisationDTO.class)
            .getResponseBody()
            .map(organisationMapper::toEntity)
            .filter(organisation::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(organisationList).isNotNull();
        assertThat(organisationList).hasSize(1);
        Organisation testOrganisation = organisationList.get(0);
        assertThat(testOrganisation.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testOrganisation.getBlocked()).isEqualTo(DEFAULT_BLOCKED);
    }

    @Test
    void getAllOrganisations() {
        // Initialize the database
        organisationRepository.save(organisation).block();

        // Get all the organisationList
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
            .value(hasItem(organisation.getId()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].blocked")
            .value(hasItem(DEFAULT_BLOCKED.booleanValue()));
    }

    @Test
    void getOrganisation() {
        // Initialize the database
        organisationRepository.save(organisation).block();

        // Get the organisation
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, organisation.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(organisation.getId()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.blocked")
            .value(is(DEFAULT_BLOCKED.booleanValue()));
    }

    @Test
    void getNonExistingOrganisation() {
        // Get the organisation
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewOrganisation() throws Exception {
        // Configure the mock search repository
        when(mockOrganisationSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        organisationRepository.save(organisation).block();

        int databaseSizeBeforeUpdate = organisationRepository.findAll().collectList().block().size();

        // Update the organisation
        Organisation updatedOrganisation = organisationRepository.findById(organisation.getId()).block();
        updatedOrganisation.name(UPDATED_NAME).blocked(UPDATED_BLOCKED);
        OrganisationDTO organisationDTO = organisationMapper.toDto(updatedOrganisation);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, organisationDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(organisationDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Organisation in the database
        List<Organisation> organisationList = organisationRepository.findAll().collectList().block();
        assertThat(organisationList).hasSize(databaseSizeBeforeUpdate);
        Organisation testOrganisation = organisationList.get(organisationList.size() - 1);
        assertThat(testOrganisation.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testOrganisation.getBlocked()).isEqualTo(UPDATED_BLOCKED);

        // Validate the Organisation in Elasticsearch
        verify(mockOrganisationSearchRepository).save(testOrganisation);
    }

    @Test
    void putNonExistingOrganisation() throws Exception {
        int databaseSizeBeforeUpdate = organisationRepository.findAll().collectList().block().size();
        organisation.setId(UUID.randomUUID().toString());

        // Create the Organisation
        OrganisationDTO organisationDTO = organisationMapper.toDto(organisation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, organisationDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(organisationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Organisation in the database
        List<Organisation> organisationList = organisationRepository.findAll().collectList().block();
        assertThat(organisationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Organisation in Elasticsearch
        verify(mockOrganisationSearchRepository, times(0)).save(organisation);
    }

    @Test
    void putWithIdMismatchOrganisation() throws Exception {
        int databaseSizeBeforeUpdate = organisationRepository.findAll().collectList().block().size();
        organisation.setId(UUID.randomUUID().toString());

        // Create the Organisation
        OrganisationDTO organisationDTO = organisationMapper.toDto(organisation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(organisationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Organisation in the database
        List<Organisation> organisationList = organisationRepository.findAll().collectList().block();
        assertThat(organisationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Organisation in Elasticsearch
        verify(mockOrganisationSearchRepository, times(0)).save(organisation);
    }

    @Test
    void putWithMissingIdPathParamOrganisation() throws Exception {
        int databaseSizeBeforeUpdate = organisationRepository.findAll().collectList().block().size();
        organisation.setId(UUID.randomUUID().toString());

        // Create the Organisation
        OrganisationDTO organisationDTO = organisationMapper.toDto(organisation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(organisationDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Organisation in the database
        List<Organisation> organisationList = organisationRepository.findAll().collectList().block();
        assertThat(organisationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Organisation in Elasticsearch
        verify(mockOrganisationSearchRepository, times(0)).save(organisation);
    }

    @Test
    void partialUpdateOrganisationWithPatch() throws Exception {
        // Initialize the database
        organisationRepository.save(organisation).block();

        int databaseSizeBeforeUpdate = organisationRepository.findAll().collectList().block().size();

        // Update the organisation using partial update
        Organisation partialUpdatedOrganisation = new Organisation();
        partialUpdatedOrganisation.setId(organisation.getId());

        partialUpdatedOrganisation.blocked(UPDATED_BLOCKED);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOrganisation.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedOrganisation))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Organisation in the database
        List<Organisation> organisationList = organisationRepository.findAll().collectList().block();
        assertThat(organisationList).hasSize(databaseSizeBeforeUpdate);
        Organisation testOrganisation = organisationList.get(organisationList.size() - 1);
        assertThat(testOrganisation.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testOrganisation.getBlocked()).isEqualTo(UPDATED_BLOCKED);
    }

    @Test
    void fullUpdateOrganisationWithPatch() throws Exception {
        // Initialize the database
        organisationRepository.save(organisation).block();

        int databaseSizeBeforeUpdate = organisationRepository.findAll().collectList().block().size();

        // Update the organisation using partial update
        Organisation partialUpdatedOrganisation = new Organisation();
        partialUpdatedOrganisation.setId(organisation.getId());

        partialUpdatedOrganisation.name(UPDATED_NAME).blocked(UPDATED_BLOCKED);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOrganisation.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedOrganisation))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Organisation in the database
        List<Organisation> organisationList = organisationRepository.findAll().collectList().block();
        assertThat(organisationList).hasSize(databaseSizeBeforeUpdate);
        Organisation testOrganisation = organisationList.get(organisationList.size() - 1);
        assertThat(testOrganisation.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testOrganisation.getBlocked()).isEqualTo(UPDATED_BLOCKED);
    }

    @Test
    void patchNonExistingOrganisation() throws Exception {
        int databaseSizeBeforeUpdate = organisationRepository.findAll().collectList().block().size();
        organisation.setId(UUID.randomUUID().toString());

        // Create the Organisation
        OrganisationDTO organisationDTO = organisationMapper.toDto(organisation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, organisationDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(organisationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Organisation in the database
        List<Organisation> organisationList = organisationRepository.findAll().collectList().block();
        assertThat(organisationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Organisation in Elasticsearch
        verify(mockOrganisationSearchRepository, times(0)).save(organisation);
    }

    @Test
    void patchWithIdMismatchOrganisation() throws Exception {
        int databaseSizeBeforeUpdate = organisationRepository.findAll().collectList().block().size();
        organisation.setId(UUID.randomUUID().toString());

        // Create the Organisation
        OrganisationDTO organisationDTO = organisationMapper.toDto(organisation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(organisationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Organisation in the database
        List<Organisation> organisationList = organisationRepository.findAll().collectList().block();
        assertThat(organisationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Organisation in Elasticsearch
        verify(mockOrganisationSearchRepository, times(0)).save(organisation);
    }

    @Test
    void patchWithMissingIdPathParamOrganisation() throws Exception {
        int databaseSizeBeforeUpdate = organisationRepository.findAll().collectList().block().size();
        organisation.setId(UUID.randomUUID().toString());

        // Create the Organisation
        OrganisationDTO organisationDTO = organisationMapper.toDto(organisation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(organisationDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Organisation in the database
        List<Organisation> organisationList = organisationRepository.findAll().collectList().block();
        assertThat(organisationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Organisation in Elasticsearch
        verify(mockOrganisationSearchRepository, times(0)).save(organisation);
    }

    @Test
    void deleteOrganisation() {
        // Configure the mock search repository
        when(mockOrganisationSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockOrganisationSearchRepository.deleteById(anyString())).thenReturn(Mono.empty());
        // Initialize the database
        organisationRepository.save(organisation).block();

        int databaseSizeBeforeDelete = organisationRepository.findAll().collectList().block().size();

        // Delete the organisation
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, organisation.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Organisation> organisationList = organisationRepository.findAll().collectList().block();
        assertThat(organisationList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Organisation in Elasticsearch
        verify(mockOrganisationSearchRepository, times(1)).deleteById(organisation.getId());
    }

    @Test
    void searchOrganisation() {
        // Configure the mock search repository
        when(mockOrganisationSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        organisationRepository.save(organisation).block();
        when(mockOrganisationSearchRepository.search("id:" + organisation.getId())).thenReturn(Flux.just(organisation));

        // Search the organisation
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + organisation.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(organisation.getId()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].blocked")
            .value(hasItem(DEFAULT_BLOCKED.booleanValue()));
    }
}
