package com.yali.finspin.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.yali.finspin.IntegrationTest;
import com.yali.finspin.domain.Dashboard;
import com.yali.finspin.repository.DashboardRepository;
import com.yali.finspin.repository.search.DashboardSearchRepository;
import com.yali.finspin.service.dto.DashboardDTO;
import com.yali.finspin.service.mapper.DashboardMapper;
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
 * Integration tests for the {@link DashboardResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class DashboardResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATE_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATE_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/dashboards";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/dashboards";

    @Autowired
    private DashboardRepository dashboardRepository;

    @Autowired
    private DashboardMapper dashboardMapper;

    /**
     * This repository is mocked in the com.yali.finspin.repository.search test package.
     *
     * @see com.yali.finspin.repository.search.DashboardSearchRepositoryMockConfiguration
     */
    @Autowired
    private DashboardSearchRepository mockDashboardSearchRepository;

    @Autowired
    private WebTestClient webTestClient;

    private Dashboard dashboard;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Dashboard createEntity() {
        Dashboard dashboard = new Dashboard().name(DEFAULT_NAME).createdDate(DEFAULT_CREATED_DATE).updateDate(DEFAULT_UPDATE_DATE);
        return dashboard;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Dashboard createUpdatedEntity() {
        Dashboard dashboard = new Dashboard().name(UPDATED_NAME).createdDate(UPDATED_CREATED_DATE).updateDate(UPDATED_UPDATE_DATE);
        return dashboard;
    }

    @BeforeEach
    public void initTest() {
        dashboardRepository.deleteAll().block();
        dashboard = createEntity();
    }

    @Test
    void createDashboard() throws Exception {
        int databaseSizeBeforeCreate = dashboardRepository.findAll().collectList().block().size();
        // Configure the mock search repository
        when(mockDashboardSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Create the Dashboard
        DashboardDTO dashboardDTO = dashboardMapper.toDto(dashboard);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(dashboardDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Dashboard in the database
        List<Dashboard> dashboardList = dashboardRepository.findAll().collectList().block();
        assertThat(dashboardList).hasSize(databaseSizeBeforeCreate + 1);
        Dashboard testDashboard = dashboardList.get(dashboardList.size() - 1);
        assertThat(testDashboard.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testDashboard.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testDashboard.getUpdateDate()).isEqualTo(DEFAULT_UPDATE_DATE);

        // Validate the Dashboard in Elasticsearch
        verify(mockDashboardSearchRepository, times(1)).save(testDashboard);
    }

    @Test
    void createDashboardWithExistingId() throws Exception {
        // Create the Dashboard with an existing ID
        dashboard.setId("existing_id");
        DashboardDTO dashboardDTO = dashboardMapper.toDto(dashboard);

        int databaseSizeBeforeCreate = dashboardRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(dashboardDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Dashboard in the database
        List<Dashboard> dashboardList = dashboardRepository.findAll().collectList().block();
        assertThat(dashboardList).hasSize(databaseSizeBeforeCreate);

        // Validate the Dashboard in Elasticsearch
        verify(mockDashboardSearchRepository, times(0)).save(dashboard);
    }

    @Test
    void getAllDashboards() {
        // Initialize the database
        dashboardRepository.save(dashboard).block();

        // Get all the dashboardList
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
            .value(hasItem(dashboard.getId()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].createdDate")
            .value(hasItem(DEFAULT_CREATED_DATE.toString()))
            .jsonPath("$.[*].updateDate")
            .value(hasItem(DEFAULT_UPDATE_DATE.toString()));
    }

    @Test
    void getDashboard() {
        // Initialize the database
        dashboardRepository.save(dashboard).block();

        // Get the dashboard
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, dashboard.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(dashboard.getId()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.createdDate")
            .value(is(DEFAULT_CREATED_DATE.toString()))
            .jsonPath("$.updateDate")
            .value(is(DEFAULT_UPDATE_DATE.toString()));
    }

    @Test
    void getNonExistingDashboard() {
        // Get the dashboard
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewDashboard() throws Exception {
        // Configure the mock search repository
        when(mockDashboardSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        dashboardRepository.save(dashboard).block();

        int databaseSizeBeforeUpdate = dashboardRepository.findAll().collectList().block().size();

        // Update the dashboard
        Dashboard updatedDashboard = dashboardRepository.findById(dashboard.getId()).block();
        updatedDashboard.name(UPDATED_NAME).createdDate(UPDATED_CREATED_DATE).updateDate(UPDATED_UPDATE_DATE);
        DashboardDTO dashboardDTO = dashboardMapper.toDto(updatedDashboard);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, dashboardDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(dashboardDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Dashboard in the database
        List<Dashboard> dashboardList = dashboardRepository.findAll().collectList().block();
        assertThat(dashboardList).hasSize(databaseSizeBeforeUpdate);
        Dashboard testDashboard = dashboardList.get(dashboardList.size() - 1);
        assertThat(testDashboard.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testDashboard.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testDashboard.getUpdateDate()).isEqualTo(UPDATED_UPDATE_DATE);

        // Validate the Dashboard in Elasticsearch
        verify(mockDashboardSearchRepository).save(testDashboard);
    }

    @Test
    void putNonExistingDashboard() throws Exception {
        int databaseSizeBeforeUpdate = dashboardRepository.findAll().collectList().block().size();
        dashboard.setId(UUID.randomUUID().toString());

        // Create the Dashboard
        DashboardDTO dashboardDTO = dashboardMapper.toDto(dashboard);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, dashboardDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(dashboardDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Dashboard in the database
        List<Dashboard> dashboardList = dashboardRepository.findAll().collectList().block();
        assertThat(dashboardList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Dashboard in Elasticsearch
        verify(mockDashboardSearchRepository, times(0)).save(dashboard);
    }

    @Test
    void putWithIdMismatchDashboard() throws Exception {
        int databaseSizeBeforeUpdate = dashboardRepository.findAll().collectList().block().size();
        dashboard.setId(UUID.randomUUID().toString());

        // Create the Dashboard
        DashboardDTO dashboardDTO = dashboardMapper.toDto(dashboard);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(dashboardDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Dashboard in the database
        List<Dashboard> dashboardList = dashboardRepository.findAll().collectList().block();
        assertThat(dashboardList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Dashboard in Elasticsearch
        verify(mockDashboardSearchRepository, times(0)).save(dashboard);
    }

    @Test
    void putWithMissingIdPathParamDashboard() throws Exception {
        int databaseSizeBeforeUpdate = dashboardRepository.findAll().collectList().block().size();
        dashboard.setId(UUID.randomUUID().toString());

        // Create the Dashboard
        DashboardDTO dashboardDTO = dashboardMapper.toDto(dashboard);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(dashboardDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Dashboard in the database
        List<Dashboard> dashboardList = dashboardRepository.findAll().collectList().block();
        assertThat(dashboardList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Dashboard in Elasticsearch
        verify(mockDashboardSearchRepository, times(0)).save(dashboard);
    }

    @Test
    void partialUpdateDashboardWithPatch() throws Exception {
        // Initialize the database
        dashboardRepository.save(dashboard).block();

        int databaseSizeBeforeUpdate = dashboardRepository.findAll().collectList().block().size();

        // Update the dashboard using partial update
        Dashboard partialUpdatedDashboard = new Dashboard();
        partialUpdatedDashboard.setId(dashboard.getId());

        partialUpdatedDashboard.name(UPDATED_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDashboard.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedDashboard))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Dashboard in the database
        List<Dashboard> dashboardList = dashboardRepository.findAll().collectList().block();
        assertThat(dashboardList).hasSize(databaseSizeBeforeUpdate);
        Dashboard testDashboard = dashboardList.get(dashboardList.size() - 1);
        assertThat(testDashboard.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testDashboard.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testDashboard.getUpdateDate()).isEqualTo(DEFAULT_UPDATE_DATE);
    }

    @Test
    void fullUpdateDashboardWithPatch() throws Exception {
        // Initialize the database
        dashboardRepository.save(dashboard).block();

        int databaseSizeBeforeUpdate = dashboardRepository.findAll().collectList().block().size();

        // Update the dashboard using partial update
        Dashboard partialUpdatedDashboard = new Dashboard();
        partialUpdatedDashboard.setId(dashboard.getId());

        partialUpdatedDashboard.name(UPDATED_NAME).createdDate(UPDATED_CREATED_DATE).updateDate(UPDATED_UPDATE_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDashboard.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedDashboard))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Dashboard in the database
        List<Dashboard> dashboardList = dashboardRepository.findAll().collectList().block();
        assertThat(dashboardList).hasSize(databaseSizeBeforeUpdate);
        Dashboard testDashboard = dashboardList.get(dashboardList.size() - 1);
        assertThat(testDashboard.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testDashboard.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testDashboard.getUpdateDate()).isEqualTo(UPDATED_UPDATE_DATE);
    }

    @Test
    void patchNonExistingDashboard() throws Exception {
        int databaseSizeBeforeUpdate = dashboardRepository.findAll().collectList().block().size();
        dashboard.setId(UUID.randomUUID().toString());

        // Create the Dashboard
        DashboardDTO dashboardDTO = dashboardMapper.toDto(dashboard);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, dashboardDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(dashboardDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Dashboard in the database
        List<Dashboard> dashboardList = dashboardRepository.findAll().collectList().block();
        assertThat(dashboardList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Dashboard in Elasticsearch
        verify(mockDashboardSearchRepository, times(0)).save(dashboard);
    }

    @Test
    void patchWithIdMismatchDashboard() throws Exception {
        int databaseSizeBeforeUpdate = dashboardRepository.findAll().collectList().block().size();
        dashboard.setId(UUID.randomUUID().toString());

        // Create the Dashboard
        DashboardDTO dashboardDTO = dashboardMapper.toDto(dashboard);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(dashboardDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Dashboard in the database
        List<Dashboard> dashboardList = dashboardRepository.findAll().collectList().block();
        assertThat(dashboardList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Dashboard in Elasticsearch
        verify(mockDashboardSearchRepository, times(0)).save(dashboard);
    }

    @Test
    void patchWithMissingIdPathParamDashboard() throws Exception {
        int databaseSizeBeforeUpdate = dashboardRepository.findAll().collectList().block().size();
        dashboard.setId(UUID.randomUUID().toString());

        // Create the Dashboard
        DashboardDTO dashboardDTO = dashboardMapper.toDto(dashboard);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(dashboardDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Dashboard in the database
        List<Dashboard> dashboardList = dashboardRepository.findAll().collectList().block();
        assertThat(dashboardList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Dashboard in Elasticsearch
        verify(mockDashboardSearchRepository, times(0)).save(dashboard);
    }

    @Test
    void deleteDashboard() {
        // Configure the mock search repository
        when(mockDashboardSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockDashboardSearchRepository.deleteById(anyString())).thenReturn(Mono.empty());
        // Initialize the database
        dashboardRepository.save(dashboard).block();

        int databaseSizeBeforeDelete = dashboardRepository.findAll().collectList().block().size();

        // Delete the dashboard
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, dashboard.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Dashboard> dashboardList = dashboardRepository.findAll().collectList().block();
        assertThat(dashboardList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Dashboard in Elasticsearch
        verify(mockDashboardSearchRepository, times(1)).deleteById(dashboard.getId());
    }

    @Test
    void searchDashboard() {
        // Configure the mock search repository
        when(mockDashboardSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockDashboardSearchRepository.count()).thenReturn(Mono.just(1L));
        // Initialize the database
        dashboardRepository.save(dashboard).block();
        when(mockDashboardSearchRepository.search("id:" + dashboard.getId(), PageRequest.of(0, 20))).thenReturn(Flux.just(dashboard));

        // Search the dashboard
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + dashboard.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(dashboard.getId()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].createdDate")
            .value(hasItem(DEFAULT_CREATED_DATE.toString()))
            .jsonPath("$.[*].updateDate")
            .value(hasItem(DEFAULT_UPDATE_DATE.toString()));
    }
}
