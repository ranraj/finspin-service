package com.yali.finspin.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.yali.finspin.IntegrationTest;
import com.yali.finspin.domain.Sprint;
import com.yali.finspin.repository.SprintRepository;
import com.yali.finspin.repository.search.SprintSearchRepository;
import com.yali.finspin.service.dto.SprintDTO;
import com.yali.finspin.service.mapper.SprintMapper;
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
 * Integration tests for the {@link SprintResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class SprintResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATE_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATE_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_START_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/sprints";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/sprints";

    @Autowired
    private SprintRepository sprintRepository;

    @Autowired
    private SprintMapper sprintMapper;

    /**
     * This repository is mocked in the com.yali.finspin.repository.search test package.
     *
     * @see com.yali.finspin.repository.search.SprintSearchRepositoryMockConfiguration
     */
    @Autowired
    private SprintSearchRepository mockSprintSearchRepository;

    @Autowired
    private WebTestClient webTestClient;

    private Sprint sprint;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Sprint createEntity() {
        Sprint sprint = new Sprint()
            .name(DEFAULT_NAME)
            .code(DEFAULT_CODE)
            .createdDate(DEFAULT_CREATED_DATE)
            .updateDate(DEFAULT_UPDATE_DATE)
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE);
        return sprint;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Sprint createUpdatedEntity() {
        Sprint sprint = new Sprint()
            .name(UPDATED_NAME)
            .code(UPDATED_CODE)
            .createdDate(UPDATED_CREATED_DATE)
            .updateDate(UPDATED_UPDATE_DATE)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE);
        return sprint;
    }

    @BeforeEach
    public void initTest() {
        sprintRepository.deleteAll().block();
        sprint = createEntity();
    }

    @Test
    void createSprint() throws Exception {
        int databaseSizeBeforeCreate = sprintRepository.findAll().collectList().block().size();
        // Configure the mock search repository
        when(mockSprintSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Create the Sprint
        SprintDTO sprintDTO = sprintMapper.toDto(sprint);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(sprintDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Sprint in the database
        List<Sprint> sprintList = sprintRepository.findAll().collectList().block();
        assertThat(sprintList).hasSize(databaseSizeBeforeCreate + 1);
        Sprint testSprint = sprintList.get(sprintList.size() - 1);
        assertThat(testSprint.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSprint.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testSprint.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testSprint.getUpdateDate()).isEqualTo(DEFAULT_UPDATE_DATE);
        assertThat(testSprint.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testSprint.getEndDate()).isEqualTo(DEFAULT_END_DATE);

        // Validate the Sprint in Elasticsearch
        verify(mockSprintSearchRepository, times(1)).save(testSprint);
    }

    @Test
    void createSprintWithExistingId() throws Exception {
        // Create the Sprint with an existing ID
        sprint.setId("existing_id");
        SprintDTO sprintDTO = sprintMapper.toDto(sprint);

        int databaseSizeBeforeCreate = sprintRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(sprintDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Sprint in the database
        List<Sprint> sprintList = sprintRepository.findAll().collectList().block();
        assertThat(sprintList).hasSize(databaseSizeBeforeCreate);

        // Validate the Sprint in Elasticsearch
        verify(mockSprintSearchRepository, times(0)).save(sprint);
    }

    @Test
    void getAllSprints() {
        // Initialize the database
        sprintRepository.save(sprint).block();

        // Get all the sprintList
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
            .value(hasItem(sprint.getId()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].code")
            .value(hasItem(DEFAULT_CODE))
            .jsonPath("$.[*].createdDate")
            .value(hasItem(DEFAULT_CREATED_DATE.toString()))
            .jsonPath("$.[*].updateDate")
            .value(hasItem(DEFAULT_UPDATE_DATE.toString()))
            .jsonPath("$.[*].startDate")
            .value(hasItem(DEFAULT_START_DATE.toString()))
            .jsonPath("$.[*].endDate")
            .value(hasItem(DEFAULT_END_DATE.toString()));
    }

    @Test
    void getSprint() {
        // Initialize the database
        sprintRepository.save(sprint).block();

        // Get the sprint
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, sprint.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(sprint.getId()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.code")
            .value(is(DEFAULT_CODE))
            .jsonPath("$.createdDate")
            .value(is(DEFAULT_CREATED_DATE.toString()))
            .jsonPath("$.updateDate")
            .value(is(DEFAULT_UPDATE_DATE.toString()))
            .jsonPath("$.startDate")
            .value(is(DEFAULT_START_DATE.toString()))
            .jsonPath("$.endDate")
            .value(is(DEFAULT_END_DATE.toString()));
    }

    @Test
    void getNonExistingSprint() {
        // Get the sprint
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewSprint() throws Exception {
        // Configure the mock search repository
        when(mockSprintSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        sprintRepository.save(sprint).block();

        int databaseSizeBeforeUpdate = sprintRepository.findAll().collectList().block().size();

        // Update the sprint
        Sprint updatedSprint = sprintRepository.findById(sprint.getId()).block();
        updatedSprint
            .name(UPDATED_NAME)
            .code(UPDATED_CODE)
            .createdDate(UPDATED_CREATED_DATE)
            .updateDate(UPDATED_UPDATE_DATE)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE);
        SprintDTO sprintDTO = sprintMapper.toDto(updatedSprint);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, sprintDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(sprintDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Sprint in the database
        List<Sprint> sprintList = sprintRepository.findAll().collectList().block();
        assertThat(sprintList).hasSize(databaseSizeBeforeUpdate);
        Sprint testSprint = sprintList.get(sprintList.size() - 1);
        assertThat(testSprint.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSprint.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testSprint.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testSprint.getUpdateDate()).isEqualTo(UPDATED_UPDATE_DATE);
        assertThat(testSprint.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testSprint.getEndDate()).isEqualTo(UPDATED_END_DATE);

        // Validate the Sprint in Elasticsearch
        verify(mockSprintSearchRepository).save(testSprint);
    }

    @Test
    void putNonExistingSprint() throws Exception {
        int databaseSizeBeforeUpdate = sprintRepository.findAll().collectList().block().size();
        sprint.setId(UUID.randomUUID().toString());

        // Create the Sprint
        SprintDTO sprintDTO = sprintMapper.toDto(sprint);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, sprintDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(sprintDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Sprint in the database
        List<Sprint> sprintList = sprintRepository.findAll().collectList().block();
        assertThat(sprintList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Sprint in Elasticsearch
        verify(mockSprintSearchRepository, times(0)).save(sprint);
    }

    @Test
    void putWithIdMismatchSprint() throws Exception {
        int databaseSizeBeforeUpdate = sprintRepository.findAll().collectList().block().size();
        sprint.setId(UUID.randomUUID().toString());

        // Create the Sprint
        SprintDTO sprintDTO = sprintMapper.toDto(sprint);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(sprintDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Sprint in the database
        List<Sprint> sprintList = sprintRepository.findAll().collectList().block();
        assertThat(sprintList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Sprint in Elasticsearch
        verify(mockSprintSearchRepository, times(0)).save(sprint);
    }

    @Test
    void putWithMissingIdPathParamSprint() throws Exception {
        int databaseSizeBeforeUpdate = sprintRepository.findAll().collectList().block().size();
        sprint.setId(UUID.randomUUID().toString());

        // Create the Sprint
        SprintDTO sprintDTO = sprintMapper.toDto(sprint);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(sprintDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Sprint in the database
        List<Sprint> sprintList = sprintRepository.findAll().collectList().block();
        assertThat(sprintList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Sprint in Elasticsearch
        verify(mockSprintSearchRepository, times(0)).save(sprint);
    }

    @Test
    void partialUpdateSprintWithPatch() throws Exception {
        // Initialize the database
        sprintRepository.save(sprint).block();

        int databaseSizeBeforeUpdate = sprintRepository.findAll().collectList().block().size();

        // Update the sprint using partial update
        Sprint partialUpdatedSprint = new Sprint();
        partialUpdatedSprint.setId(sprint.getId());

        partialUpdatedSprint.code(UPDATED_CODE).createdDate(UPDATED_CREATED_DATE).updateDate(UPDATED_UPDATE_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSprint.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedSprint))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Sprint in the database
        List<Sprint> sprintList = sprintRepository.findAll().collectList().block();
        assertThat(sprintList).hasSize(databaseSizeBeforeUpdate);
        Sprint testSprint = sprintList.get(sprintList.size() - 1);
        assertThat(testSprint.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSprint.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testSprint.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testSprint.getUpdateDate()).isEqualTo(UPDATED_UPDATE_DATE);
        assertThat(testSprint.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testSprint.getEndDate()).isEqualTo(DEFAULT_END_DATE);
    }

    @Test
    void fullUpdateSprintWithPatch() throws Exception {
        // Initialize the database
        sprintRepository.save(sprint).block();

        int databaseSizeBeforeUpdate = sprintRepository.findAll().collectList().block().size();

        // Update the sprint using partial update
        Sprint partialUpdatedSprint = new Sprint();
        partialUpdatedSprint.setId(sprint.getId());

        partialUpdatedSprint
            .name(UPDATED_NAME)
            .code(UPDATED_CODE)
            .createdDate(UPDATED_CREATED_DATE)
            .updateDate(UPDATED_UPDATE_DATE)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSprint.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedSprint))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Sprint in the database
        List<Sprint> sprintList = sprintRepository.findAll().collectList().block();
        assertThat(sprintList).hasSize(databaseSizeBeforeUpdate);
        Sprint testSprint = sprintList.get(sprintList.size() - 1);
        assertThat(testSprint.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSprint.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testSprint.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testSprint.getUpdateDate()).isEqualTo(UPDATED_UPDATE_DATE);
        assertThat(testSprint.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testSprint.getEndDate()).isEqualTo(UPDATED_END_DATE);
    }

    @Test
    void patchNonExistingSprint() throws Exception {
        int databaseSizeBeforeUpdate = sprintRepository.findAll().collectList().block().size();
        sprint.setId(UUID.randomUUID().toString());

        // Create the Sprint
        SprintDTO sprintDTO = sprintMapper.toDto(sprint);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, sprintDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(sprintDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Sprint in the database
        List<Sprint> sprintList = sprintRepository.findAll().collectList().block();
        assertThat(sprintList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Sprint in Elasticsearch
        verify(mockSprintSearchRepository, times(0)).save(sprint);
    }

    @Test
    void patchWithIdMismatchSprint() throws Exception {
        int databaseSizeBeforeUpdate = sprintRepository.findAll().collectList().block().size();
        sprint.setId(UUID.randomUUID().toString());

        // Create the Sprint
        SprintDTO sprintDTO = sprintMapper.toDto(sprint);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(sprintDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Sprint in the database
        List<Sprint> sprintList = sprintRepository.findAll().collectList().block();
        assertThat(sprintList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Sprint in Elasticsearch
        verify(mockSprintSearchRepository, times(0)).save(sprint);
    }

    @Test
    void patchWithMissingIdPathParamSprint() throws Exception {
        int databaseSizeBeforeUpdate = sprintRepository.findAll().collectList().block().size();
        sprint.setId(UUID.randomUUID().toString());

        // Create the Sprint
        SprintDTO sprintDTO = sprintMapper.toDto(sprint);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(sprintDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Sprint in the database
        List<Sprint> sprintList = sprintRepository.findAll().collectList().block();
        assertThat(sprintList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Sprint in Elasticsearch
        verify(mockSprintSearchRepository, times(0)).save(sprint);
    }

    @Test
    void deleteSprint() {
        // Configure the mock search repository
        when(mockSprintSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockSprintSearchRepository.deleteById(anyString())).thenReturn(Mono.empty());
        // Initialize the database
        sprintRepository.save(sprint).block();

        int databaseSizeBeforeDelete = sprintRepository.findAll().collectList().block().size();

        // Delete the sprint
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, sprint.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Sprint> sprintList = sprintRepository.findAll().collectList().block();
        assertThat(sprintList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Sprint in Elasticsearch
        verify(mockSprintSearchRepository, times(1)).deleteById(sprint.getId());
    }

    @Test
    void searchSprint() {
        // Configure the mock search repository
        when(mockSprintSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockSprintSearchRepository.count()).thenReturn(Mono.just(1L));
        // Initialize the database
        sprintRepository.save(sprint).block();
        when(mockSprintSearchRepository.search("id:" + sprint.getId(), PageRequest.of(0, 20))).thenReturn(Flux.just(sprint));

        // Search the sprint
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + sprint.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(sprint.getId()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].code")
            .value(hasItem(DEFAULT_CODE))
            .jsonPath("$.[*].createdDate")
            .value(hasItem(DEFAULT_CREATED_DATE.toString()))
            .jsonPath("$.[*].updateDate")
            .value(hasItem(DEFAULT_UPDATE_DATE.toString()))
            .jsonPath("$.[*].startDate")
            .value(hasItem(DEFAULT_START_DATE.toString()))
            .jsonPath("$.[*].endDate")
            .value(hasItem(DEFAULT_END_DATE.toString()));
    }
}
