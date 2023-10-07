package com.yali.finspin.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.yali.finspin.domain.Task;
import com.yali.finspin.repository.TaskRepository;
import com.yali.finspin.repository.search.TaskSearchRepository;
import com.yali.finspin.service.TaskService;
import com.yali.finspin.service.dto.TaskDTO;
import com.yali.finspin.service.mapper.TaskMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Task}.
 */
@Service
public class TaskServiceImpl implements TaskService {

    private final Logger log = LoggerFactory.getLogger(TaskServiceImpl.class);

    private final TaskRepository taskRepository;

    private final TaskMapper taskMapper;

    private final TaskSearchRepository taskSearchRepository;

    public TaskServiceImpl(TaskRepository taskRepository, TaskMapper taskMapper, TaskSearchRepository taskSearchRepository) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.taskSearchRepository = taskSearchRepository;
    }

    @Override
    public Mono<TaskDTO> save(TaskDTO taskDTO) {
        log.debug("Request to save Task : {}", taskDTO);
        return taskRepository.save(taskMapper.toEntity(taskDTO)).flatMap(taskSearchRepository::save).map(taskMapper::toDto);
    }

    @Override
    public Mono<TaskDTO> update(TaskDTO taskDTO) {
        log.debug("Request to save Task : {}", taskDTO);
        return taskRepository.save(taskMapper.toEntity(taskDTO)).flatMap(taskSearchRepository::save).map(taskMapper::toDto);
    }

    @Override
    public Mono<TaskDTO> partialUpdate(TaskDTO taskDTO) {
        log.debug("Request to partially update Task : {}", taskDTO);

        return taskRepository
            .findById(taskDTO.getId())
            .map(existingTask -> {
                taskMapper.partialUpdate(existingTask, taskDTO);

                return existingTask;
            })
            .flatMap(taskRepository::save)
            .flatMap(savedTask -> {
                taskSearchRepository.save(savedTask);

                return Mono.just(savedTask);
            })
            .map(taskMapper::toDto);
    }

    @Override
    public Flux<TaskDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Tasks");
        return taskRepository.findAllBy(pageable).map(taskMapper::toDto);
    }

    public Flux<TaskDTO> findAllWithEagerRelationships(Pageable pageable) {
        return taskRepository.findAllWithEagerRelationships(pageable).map(taskMapper::toDto);
    }

    public Mono<Long> countAll() {
        return taskRepository.count();
    }

    public Mono<Long> searchCount() {
        return taskSearchRepository.count();
    }

    @Override
    public Mono<TaskDTO> findOne(String id) {
        log.debug("Request to get Task : {}", id);
        return taskRepository.findOneWithEagerRelationships(id).map(taskMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Task : {}", id);
        return taskRepository.deleteById(id).then(taskSearchRepository.deleteById(id));
    }

    @Override
    public Flux<TaskDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Tasks for query {}", query);
        return taskSearchRepository.search(query, pageable).map(taskMapper::toDto);
    }
}
