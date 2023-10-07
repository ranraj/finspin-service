package com.yali.finspin.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.yali.finspin.IntegrationTest;
import com.yali.finspin.domain.Task;
import com.yali.finspin.domain.enumeration.TaskStatus;
import com.yali.finspin.domain.enumeration.TaskType;
import com.yali.finspin.repository.TaskRepository;
import com.yali.finspin.repository.search.TaskSearchRepository;
import com.yali.finspin.service.TaskService;
import com.yali.finspin.service.dto.TaskDTO;
import com.yali.finspin.service.mapper.TaskMapper;
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
 * Integration tests for the {@link TaskResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TaskResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATE_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATE_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final TaskStatus DEFAULT_STATUS = TaskStatus.Open;
    private static final TaskStatus UPDATED_STATUS = TaskStatus.Inprogress;

    private static final TaskType DEFAULT_TYPE = TaskType.Epic;
    private static final TaskType UPDATED_TYPE = TaskType.Spike;

    private static final Long DEFAULT_EFFORT_HRS = 1L;
    private static final Long UPDATED_EFFORT_HRS = 2L;

    private static final Instant DEFAULT_START_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/tasks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/tasks";

    @Autowired
    private TaskRepository taskRepository;

    @Mock
    private TaskRepository taskRepositoryMock;

    @Autowired
    private TaskMapper taskMapper;

    @Mock
    private TaskService taskServiceMock;

    /**
     * This repository is mocked in the com.yali.finspin.repository.search test package.
     *
     * @see com.yali.finspin.repository.search.TaskSearchRepositoryMockConfiguration
     */
    @Autowired
    private TaskSearchRepository mockTaskSearchRepository;

    @Autowired
    private WebTestClient webTestClient;

    private Task task;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Task createEntity() {
        Task task = new Task()
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION)
            .createDate(DEFAULT_CREATE_DATE)
            .updatedDate(DEFAULT_UPDATED_DATE)
            .status(DEFAULT_STATUS)
            .type(DEFAULT_TYPE)
            .effortHrs(DEFAULT_EFFORT_HRS)
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE);
        return task;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Task createUpdatedEntity() {
        Task task = new Task()
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .createDate(UPDATED_CREATE_DATE)
            .updatedDate(UPDATED_UPDATED_DATE)
            .status(UPDATED_STATUS)
            .type(UPDATED_TYPE)
            .effortHrs(UPDATED_EFFORT_HRS)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE);
        return task;
    }

    @BeforeEach
    public void initTest() {
        taskRepository.deleteAll().block();
        task = createEntity();
    }

    @Test
    void createTask() throws Exception {
        int databaseSizeBeforeCreate = taskRepository.findAll().collectList().block().size();
        // Configure the mock search repository
        when(mockTaskSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Create the Task
        TaskDTO taskDTO = taskMapper.toDto(task);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(taskDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll().collectList().block();
        assertThat(taskList).hasSize(databaseSizeBeforeCreate + 1);
        Task testTask = taskList.get(taskList.size() - 1);
        assertThat(testTask.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testTask.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testTask.getCreateDate()).isEqualTo(DEFAULT_CREATE_DATE);
        assertThat(testTask.getUpdatedDate()).isEqualTo(DEFAULT_UPDATED_DATE);
        assertThat(testTask.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testTask.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testTask.getEffortHrs()).isEqualTo(DEFAULT_EFFORT_HRS);
        assertThat(testTask.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testTask.getEndDate()).isEqualTo(DEFAULT_END_DATE);

        // Validate the Task in Elasticsearch
        verify(mockTaskSearchRepository, times(1)).save(testTask);
    }

    @Test
    void createTaskWithExistingId() throws Exception {
        // Create the Task with an existing ID
        task.setId("existing_id");
        TaskDTO taskDTO = taskMapper.toDto(task);

        int databaseSizeBeforeCreate = taskRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(taskDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll().collectList().block();
        assertThat(taskList).hasSize(databaseSizeBeforeCreate);

        // Validate the Task in Elasticsearch
        verify(mockTaskSearchRepository, times(0)).save(task);
    }

    @Test
    void getAllTasks() {
        // Initialize the database
        taskRepository.save(task).block();

        // Get all the taskList
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
            .value(hasItem(task.getId()))
            .jsonPath("$.[*].title")
            .value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].createDate")
            .value(hasItem(DEFAULT_CREATE_DATE.toString()))
            .jsonPath("$.[*].updatedDate")
            .value(hasItem(DEFAULT_UPDATED_DATE.toString()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].type")
            .value(hasItem(DEFAULT_TYPE.toString()))
            .jsonPath("$.[*].effortHrs")
            .value(hasItem(DEFAULT_EFFORT_HRS.intValue()))
            .jsonPath("$.[*].startDate")
            .value(hasItem(DEFAULT_START_DATE.toString()))
            .jsonPath("$.[*].endDate")
            .value(hasItem(DEFAULT_END_DATE.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTasksWithEagerRelationshipsIsEnabled() {
        when(taskServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(taskServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTasksWithEagerRelationshipsIsNotEnabled() {
        when(taskServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(taskServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getTask() {
        // Initialize the database
        taskRepository.save(task).block();

        // Get the task
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, task.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(task.getId()))
            .jsonPath("$.title")
            .value(is(DEFAULT_TITLE))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.createDate")
            .value(is(DEFAULT_CREATE_DATE.toString()))
            .jsonPath("$.updatedDate")
            .value(is(DEFAULT_UPDATED_DATE.toString()))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()))
            .jsonPath("$.type")
            .value(is(DEFAULT_TYPE.toString()))
            .jsonPath("$.effortHrs")
            .value(is(DEFAULT_EFFORT_HRS.intValue()))
            .jsonPath("$.startDate")
            .value(is(DEFAULT_START_DATE.toString()))
            .jsonPath("$.endDate")
            .value(is(DEFAULT_END_DATE.toString()));
    }

    @Test
    void getNonExistingTask() {
        // Get the task
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewTask() throws Exception {
        // Configure the mock search repository
        when(mockTaskSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        taskRepository.save(task).block();

        int databaseSizeBeforeUpdate = taskRepository.findAll().collectList().block().size();

        // Update the task
        Task updatedTask = taskRepository.findById(task.getId()).block();
        updatedTask
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .createDate(UPDATED_CREATE_DATE)
            .updatedDate(UPDATED_UPDATED_DATE)
            .status(UPDATED_STATUS)
            .type(UPDATED_TYPE)
            .effortHrs(UPDATED_EFFORT_HRS)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE);
        TaskDTO taskDTO = taskMapper.toDto(updatedTask);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, taskDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(taskDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll().collectList().block();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
        Task testTask = taskList.get(taskList.size() - 1);
        assertThat(testTask.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testTask.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTask.getCreateDate()).isEqualTo(UPDATED_CREATE_DATE);
        assertThat(testTask.getUpdatedDate()).isEqualTo(UPDATED_UPDATED_DATE);
        assertThat(testTask.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testTask.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testTask.getEffortHrs()).isEqualTo(UPDATED_EFFORT_HRS);
        assertThat(testTask.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testTask.getEndDate()).isEqualTo(UPDATED_END_DATE);

        // Validate the Task in Elasticsearch
        verify(mockTaskSearchRepository).save(testTask);
    }

    @Test
    void putNonExistingTask() throws Exception {
        int databaseSizeBeforeUpdate = taskRepository.findAll().collectList().block().size();
        task.setId(UUID.randomUUID().toString());

        // Create the Task
        TaskDTO taskDTO = taskMapper.toDto(task);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, taskDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(taskDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll().collectList().block();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Task in Elasticsearch
        verify(mockTaskSearchRepository, times(0)).save(task);
    }

    @Test
    void putWithIdMismatchTask() throws Exception {
        int databaseSizeBeforeUpdate = taskRepository.findAll().collectList().block().size();
        task.setId(UUID.randomUUID().toString());

        // Create the Task
        TaskDTO taskDTO = taskMapper.toDto(task);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(taskDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll().collectList().block();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Task in Elasticsearch
        verify(mockTaskSearchRepository, times(0)).save(task);
    }

    @Test
    void putWithMissingIdPathParamTask() throws Exception {
        int databaseSizeBeforeUpdate = taskRepository.findAll().collectList().block().size();
        task.setId(UUID.randomUUID().toString());

        // Create the Task
        TaskDTO taskDTO = taskMapper.toDto(task);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(taskDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll().collectList().block();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Task in Elasticsearch
        verify(mockTaskSearchRepository, times(0)).save(task);
    }

    @Test
    void partialUpdateTaskWithPatch() throws Exception {
        // Initialize the database
        taskRepository.save(task).block();

        int databaseSizeBeforeUpdate = taskRepository.findAll().collectList().block().size();

        // Update the task using partial update
        Task partialUpdatedTask = new Task();
        partialUpdatedTask.setId(task.getId());

        partialUpdatedTask
            .description(UPDATED_DESCRIPTION)
            .updatedDate(UPDATED_UPDATED_DATE)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTask.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTask))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll().collectList().block();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
        Task testTask = taskList.get(taskList.size() - 1);
        assertThat(testTask.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testTask.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTask.getCreateDate()).isEqualTo(DEFAULT_CREATE_DATE);
        assertThat(testTask.getUpdatedDate()).isEqualTo(UPDATED_UPDATED_DATE);
        assertThat(testTask.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testTask.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testTask.getEffortHrs()).isEqualTo(DEFAULT_EFFORT_HRS);
        assertThat(testTask.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testTask.getEndDate()).isEqualTo(UPDATED_END_DATE);
    }

    @Test
    void fullUpdateTaskWithPatch() throws Exception {
        // Initialize the database
        taskRepository.save(task).block();

        int databaseSizeBeforeUpdate = taskRepository.findAll().collectList().block().size();

        // Update the task using partial update
        Task partialUpdatedTask = new Task();
        partialUpdatedTask.setId(task.getId());

        partialUpdatedTask
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .createDate(UPDATED_CREATE_DATE)
            .updatedDate(UPDATED_UPDATED_DATE)
            .status(UPDATED_STATUS)
            .type(UPDATED_TYPE)
            .effortHrs(UPDATED_EFFORT_HRS)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTask.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTask))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll().collectList().block();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
        Task testTask = taskList.get(taskList.size() - 1);
        assertThat(testTask.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testTask.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTask.getCreateDate()).isEqualTo(UPDATED_CREATE_DATE);
        assertThat(testTask.getUpdatedDate()).isEqualTo(UPDATED_UPDATED_DATE);
        assertThat(testTask.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testTask.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testTask.getEffortHrs()).isEqualTo(UPDATED_EFFORT_HRS);
        assertThat(testTask.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testTask.getEndDate()).isEqualTo(UPDATED_END_DATE);
    }

    @Test
    void patchNonExistingTask() throws Exception {
        int databaseSizeBeforeUpdate = taskRepository.findAll().collectList().block().size();
        task.setId(UUID.randomUUID().toString());

        // Create the Task
        TaskDTO taskDTO = taskMapper.toDto(task);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, taskDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(taskDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll().collectList().block();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Task in Elasticsearch
        verify(mockTaskSearchRepository, times(0)).save(task);
    }

    @Test
    void patchWithIdMismatchTask() throws Exception {
        int databaseSizeBeforeUpdate = taskRepository.findAll().collectList().block().size();
        task.setId(UUID.randomUUID().toString());

        // Create the Task
        TaskDTO taskDTO = taskMapper.toDto(task);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(taskDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll().collectList().block();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Task in Elasticsearch
        verify(mockTaskSearchRepository, times(0)).save(task);
    }

    @Test
    void patchWithMissingIdPathParamTask() throws Exception {
        int databaseSizeBeforeUpdate = taskRepository.findAll().collectList().block().size();
        task.setId(UUID.randomUUID().toString());

        // Create the Task
        TaskDTO taskDTO = taskMapper.toDto(task);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(taskDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll().collectList().block();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Task in Elasticsearch
        verify(mockTaskSearchRepository, times(0)).save(task);
    }

    @Test
    void deleteTask() {
        // Configure the mock search repository
        when(mockTaskSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockTaskSearchRepository.deleteById(anyString())).thenReturn(Mono.empty());
        // Initialize the database
        taskRepository.save(task).block();

        int databaseSizeBeforeDelete = taskRepository.findAll().collectList().block().size();

        // Delete the task
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, task.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Task> taskList = taskRepository.findAll().collectList().block();
        assertThat(taskList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Task in Elasticsearch
        verify(mockTaskSearchRepository, times(1)).deleteById(task.getId());
    }

    @Test
    void searchTask() {
        // Configure the mock search repository
        when(mockTaskSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockTaskSearchRepository.count()).thenReturn(Mono.just(1L));
        // Initialize the database
        taskRepository.save(task).block();
        when(mockTaskSearchRepository.search("id:" + task.getId(), PageRequest.of(0, 20))).thenReturn(Flux.just(task));

        // Search the task
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + task.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(task.getId()))
            .jsonPath("$.[*].title")
            .value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].createDate")
            .value(hasItem(DEFAULT_CREATE_DATE.toString()))
            .jsonPath("$.[*].updatedDate")
            .value(hasItem(DEFAULT_UPDATED_DATE.toString()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].type")
            .value(hasItem(DEFAULT_TYPE.toString()))
            .jsonPath("$.[*].effortHrs")
            .value(hasItem(DEFAULT_EFFORT_HRS.intValue()))
            .jsonPath("$.[*].startDate")
            .value(hasItem(DEFAULT_START_DATE.toString()))
            .jsonPath("$.[*].endDate")
            .value(hasItem(DEFAULT_END_DATE.toString()));
    }
}
