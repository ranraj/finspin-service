package com.yali.finspin.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.yali.finspin.IntegrationTest;
import com.yali.finspin.domain.Project;
import com.yali.finspin.repository.ProjectRepository;
import com.yali.finspin.repository.search.ProjectSearchRepository;
import com.yali.finspin.service.dto.ProjectDTO;
import com.yali.finspin.service.mapper.ProjectMapper;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Integration tests for the {@link ProjectResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ProjectResourceIT {

    private static final String DEFAULT_STRING = "AAAAAAAAAA";
    private static final String UPDATED_STRING = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATE_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATE_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/projects";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/projects";

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectMapper projectMapper;

    /**
     * This repository is mocked in the com.yali.finspin.repository.search test package.
     *
     * @see com.yali.finspin.repository.search.ProjectSearchRepositoryMockConfiguration
     */
    @Autowired
    private ProjectSearchRepository mockProjectSearchRepository;

    @Autowired
    private WebTestClient webTestClient;

    private Project project;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Project createEntity() {
        Project project = new Project().string(DEFAULT_STRING).createdDate(DEFAULT_CREATED_DATE).updateDate(DEFAULT_UPDATE_DATE);
        return project;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Project createUpdatedEntity() {
        Project project = new Project().string(UPDATED_STRING).createdDate(UPDATED_CREATED_DATE).updateDate(UPDATED_UPDATE_DATE);
        return project;
    }

    @BeforeEach
    public void initTest() {
        projectRepository.deleteAll().block();
        project = createEntity();
    }

    @Test
    void createProject() throws Exception {
        int databaseSizeBeforeCreate = projectRepository.findAll().collectList().block().size();
        // Configure the mock search repository
        when(mockProjectSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Create the Project
        ProjectDTO projectDTO = projectMapper.toDto(project);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Project in the database
        List<Project> projectList = projectRepository.findAll().collectList().block();
        assertThat(projectList).hasSize(databaseSizeBeforeCreate + 1);
        Project testProject = projectList.get(projectList.size() - 1);
        assertThat(testProject.getString()).isEqualTo(DEFAULT_STRING);
        assertThat(testProject.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testProject.getUpdateDate()).isEqualTo(DEFAULT_UPDATE_DATE);

        // Validate the Project in Elasticsearch
        verify(mockProjectSearchRepository, times(1)).save(testProject);
    }

    @Test
    void createProjectWithExistingId() throws Exception {
        // Create the Project with an existing ID
        project.setId("existing_id");
        ProjectDTO projectDTO = projectMapper.toDto(project);

        int databaseSizeBeforeCreate = projectRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Project in the database
        List<Project> projectList = projectRepository.findAll().collectList().block();
        assertThat(projectList).hasSize(databaseSizeBeforeCreate);

        // Validate the Project in Elasticsearch
        verify(mockProjectSearchRepository, times(0)).save(project);
    }

    @Test
    void checkStringIsRequired() throws Exception {
        int databaseSizeBeforeTest = projectRepository.findAll().collectList().block().size();
        // set the field null
        project.setString(null);

        // Create the Project, which fails.
        ProjectDTO projectDTO = projectMapper.toDto(project);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Project> projectList = projectRepository.findAll().collectList().block();
        assertThat(projectList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllProjectsAsStream() {
        // Initialize the database
        projectRepository.save(project).block();

        List<Project> projectList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(ProjectDTO.class)
            .getResponseBody()
            .map(projectMapper::toEntity)
            .filter(project::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(projectList).isNotNull();
        assertThat(projectList).hasSize(1);
        Project testProject = projectList.get(0);
        assertThat(testProject.getString()).isEqualTo(DEFAULT_STRING);
        assertThat(testProject.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testProject.getUpdateDate()).isEqualTo(DEFAULT_UPDATE_DATE);
    }

    @Test
    void getAllProjects() {
        // Initialize the database
        projectRepository.save(project).block();

        // Get all the projectList
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
            .value(hasItem(project.getId()))
            .jsonPath("$.[*].string")
            .value(hasItem(DEFAULT_STRING))
            .jsonPath("$.[*].createdDate")
            .value(hasItem(DEFAULT_CREATED_DATE.toString()))
            .jsonPath("$.[*].updateDate")
            .value(hasItem(DEFAULT_UPDATE_DATE.toString()));
    }

    @Test
    void getProject() {
        // Initialize the database
        projectRepository.save(project).block();

        // Get the project
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, project.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(project.getId()))
            .jsonPath("$.string")
            .value(is(DEFAULT_STRING))
            .jsonPath("$.createdDate")
            .value(is(DEFAULT_CREATED_DATE.toString()))
            .jsonPath("$.updateDate")
            .value(is(DEFAULT_UPDATE_DATE.toString()));
    }

    @Test
    void getNonExistingProject() {
        // Get the project
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewProject() throws Exception {
        // Configure the mock search repository
        when(mockProjectSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        projectRepository.save(project).block();

        int databaseSizeBeforeUpdate = projectRepository.findAll().collectList().block().size();

        // Update the project
        Project updatedProject = projectRepository.findById(project.getId()).block();
        updatedProject.string(UPDATED_STRING).createdDate(UPDATED_CREATED_DATE).updateDate(UPDATED_UPDATE_DATE);
        ProjectDTO projectDTO = projectMapper.toDto(updatedProject);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, projectDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Project in the database
        List<Project> projectList = projectRepository.findAll().collectList().block();
        assertThat(projectList).hasSize(databaseSizeBeforeUpdate);
        Project testProject = projectList.get(projectList.size() - 1);
        assertThat(testProject.getString()).isEqualTo(UPDATED_STRING);
        assertThat(testProject.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testProject.getUpdateDate()).isEqualTo(UPDATED_UPDATE_DATE);

        // Validate the Project in Elasticsearch
        verify(mockProjectSearchRepository).save(testProject);
    }

    @Test
    void putNonExistingProject() throws Exception {
        int databaseSizeBeforeUpdate = projectRepository.findAll().collectList().block().size();
        project.setId(UUID.randomUUID().toString());

        // Create the Project
        ProjectDTO projectDTO = projectMapper.toDto(project);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, projectDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Project in the database
        List<Project> projectList = projectRepository.findAll().collectList().block();
        assertThat(projectList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Project in Elasticsearch
        verify(mockProjectSearchRepository, times(0)).save(project);
    }

    @Test
    void putWithIdMismatchProject() throws Exception {
        int databaseSizeBeforeUpdate = projectRepository.findAll().collectList().block().size();
        project.setId(UUID.randomUUID().toString());

        // Create the Project
        ProjectDTO projectDTO = projectMapper.toDto(project);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Project in the database
        List<Project> projectList = projectRepository.findAll().collectList().block();
        assertThat(projectList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Project in Elasticsearch
        verify(mockProjectSearchRepository, times(0)).save(project);
    }

    @Test
    void putWithMissingIdPathParamProject() throws Exception {
        int databaseSizeBeforeUpdate = projectRepository.findAll().collectList().block().size();
        project.setId(UUID.randomUUID().toString());

        // Create the Project
        ProjectDTO projectDTO = projectMapper.toDto(project);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Project in the database
        List<Project> projectList = projectRepository.findAll().collectList().block();
        assertThat(projectList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Project in Elasticsearch
        verify(mockProjectSearchRepository, times(0)).save(project);
    }

    @Test
    void partialUpdateProjectWithPatch() throws Exception {
        // Initialize the database
        projectRepository.save(project).block();

        int databaseSizeBeforeUpdate = projectRepository.findAll().collectList().block().size();

        // Update the project using partial update
        Project partialUpdatedProject = new Project();
        partialUpdatedProject.setId(project.getId());

        partialUpdatedProject.string(UPDATED_STRING);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProject.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProject))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Project in the database
        List<Project> projectList = projectRepository.findAll().collectList().block();
        assertThat(projectList).hasSize(databaseSizeBeforeUpdate);
        Project testProject = projectList.get(projectList.size() - 1);
        assertThat(testProject.getString()).isEqualTo(UPDATED_STRING);
        assertThat(testProject.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testProject.getUpdateDate()).isEqualTo(DEFAULT_UPDATE_DATE);
    }

    @Test
    void fullUpdateProjectWithPatch() throws Exception {
        // Initialize the database
        projectRepository.save(project).block();

        int databaseSizeBeforeUpdate = projectRepository.findAll().collectList().block().size();

        // Update the project using partial update
        Project partialUpdatedProject = new Project();
        partialUpdatedProject.setId(project.getId());

        partialUpdatedProject.string(UPDATED_STRING).createdDate(UPDATED_CREATED_DATE).updateDate(UPDATED_UPDATE_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProject.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProject))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Project in the database
        List<Project> projectList = projectRepository.findAll().collectList().block();
        assertThat(projectList).hasSize(databaseSizeBeforeUpdate);
        Project testProject = projectList.get(projectList.size() - 1);
        assertThat(testProject.getString()).isEqualTo(UPDATED_STRING);
        assertThat(testProject.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testProject.getUpdateDate()).isEqualTo(UPDATED_UPDATE_DATE);
    }

    @Test
    void patchNonExistingProject() throws Exception {
        int databaseSizeBeforeUpdate = projectRepository.findAll().collectList().block().size();
        project.setId(UUID.randomUUID().toString());

        // Create the Project
        ProjectDTO projectDTO = projectMapper.toDto(project);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, projectDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Project in the database
        List<Project> projectList = projectRepository.findAll().collectList().block();
        assertThat(projectList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Project in Elasticsearch
        verify(mockProjectSearchRepository, times(0)).save(project);
    }

    @Test
    void patchWithIdMismatchProject() throws Exception {
        int databaseSizeBeforeUpdate = projectRepository.findAll().collectList().block().size();
        project.setId(UUID.randomUUID().toString());

        // Create the Project
        ProjectDTO projectDTO = projectMapper.toDto(project);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Project in the database
        List<Project> projectList = projectRepository.findAll().collectList().block();
        assertThat(projectList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Project in Elasticsearch
        verify(mockProjectSearchRepository, times(0)).save(project);
    }

    @Test
    void patchWithMissingIdPathParamProject() throws Exception {
        int databaseSizeBeforeUpdate = projectRepository.findAll().collectList().block().size();
        project.setId(UUID.randomUUID().toString());

        // Create the Project
        ProjectDTO projectDTO = projectMapper.toDto(project);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Project in the database
        List<Project> projectList = projectRepository.findAll().collectList().block();
        assertThat(projectList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Project in Elasticsearch
        verify(mockProjectSearchRepository, times(0)).save(project);
    }

    @Test
    void deleteProject() {
        // Configure the mock search repository
        when(mockProjectSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockProjectSearchRepository.deleteById(anyString())).thenReturn(Mono.empty());
        // Initialize the database
        projectRepository.save(project).block();

        int databaseSizeBeforeDelete = projectRepository.findAll().collectList().block().size();

        // Delete the project
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, project.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Project> projectList = projectRepository.findAll().collectList().block();
        assertThat(projectList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Project in Elasticsearch
        verify(mockProjectSearchRepository, times(1)).deleteById(project.getId());
    }

    @Test
    void searchProject() {
        // Configure the mock search repository
        when(mockProjectSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        projectRepository.save(project).block();
        when(mockProjectSearchRepository.search("id:" + project.getId())).thenReturn(Flux.just(project));

        // Search the project
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + project.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(project.getId()))
            .jsonPath("$.[*].string")
            .value(hasItem(DEFAULT_STRING))
            .jsonPath("$.[*].createdDate")
            .value(hasItem(DEFAULT_CREATED_DATE.toString()))
            .jsonPath("$.[*].updateDate")
            .value(hasItem(DEFAULT_UPDATE_DATE.toString()));
    }
}
