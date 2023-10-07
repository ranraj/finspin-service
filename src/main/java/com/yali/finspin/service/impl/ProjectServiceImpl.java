package com.yali.finspin.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.yali.finspin.domain.Project;
import com.yali.finspin.repository.ProjectRepository;
import com.yali.finspin.repository.search.ProjectSearchRepository;
import com.yali.finspin.service.ProjectService;
import com.yali.finspin.service.dto.ProjectDTO;
import com.yali.finspin.service.mapper.ProjectMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Project}.
 */
@Service
public class ProjectServiceImpl implements ProjectService {

    private final Logger log = LoggerFactory.getLogger(ProjectServiceImpl.class);

    private final ProjectRepository projectRepository;

    private final ProjectMapper projectMapper;

    private final ProjectSearchRepository projectSearchRepository;

    public ProjectServiceImpl(
        ProjectRepository projectRepository,
        ProjectMapper projectMapper,
        ProjectSearchRepository projectSearchRepository
    ) {
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
        this.projectSearchRepository = projectSearchRepository;
    }

    @Override
    public Mono<ProjectDTO> save(ProjectDTO projectDTO) {
        log.debug("Request to save Project : {}", projectDTO);
        return projectRepository.save(projectMapper.toEntity(projectDTO)).flatMap(projectSearchRepository::save).map(projectMapper::toDto);
    }

    @Override
    public Mono<ProjectDTO> update(ProjectDTO projectDTO) {
        log.debug("Request to save Project : {}", projectDTO);
        return projectRepository.save(projectMapper.toEntity(projectDTO)).flatMap(projectSearchRepository::save).map(projectMapper::toDto);
    }

    @Override
    public Mono<ProjectDTO> partialUpdate(ProjectDTO projectDTO) {
        log.debug("Request to partially update Project : {}", projectDTO);

        return projectRepository
            .findById(projectDTO.getId())
            .map(existingProject -> {
                projectMapper.partialUpdate(existingProject, projectDTO);

                return existingProject;
            })
            .flatMap(projectRepository::save)
            .flatMap(savedProject -> {
                projectSearchRepository.save(savedProject);

                return Mono.just(savedProject);
            })
            .map(projectMapper::toDto);
    }

    @Override
    public Flux<ProjectDTO> findAll() {
        log.debug("Request to get all Projects");
        return projectRepository.findAll().map(projectMapper::toDto);
    }

    public Mono<Long> countAll() {
        return projectRepository.count();
    }

    public Mono<Long> searchCount() {
        return projectSearchRepository.count();
    }

    @Override
    public Mono<ProjectDTO> findOne(String id) {
        log.debug("Request to get Project : {}", id);
        return projectRepository.findById(id).map(projectMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Project : {}", id);
        return projectRepository.deleteById(id).then(projectSearchRepository.deleteById(id));
    }

    @Override
    public Flux<ProjectDTO> search(String query) {
        log.debug("Request to search Projects for query {}", query);
        return projectSearchRepository.search(query).map(projectMapper::toDto);
    }
}
