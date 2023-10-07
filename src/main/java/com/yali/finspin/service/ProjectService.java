package com.yali.finspin.service;

import com.yali.finspin.service.dto.ProjectDTO;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.yali.finspin.domain.Project}.
 */
public interface ProjectService {
    /**
     * Save a project.
     *
     * @param projectDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<ProjectDTO> save(ProjectDTO projectDTO);

    /**
     * Updates a project.
     *
     * @param projectDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<ProjectDTO> update(ProjectDTO projectDTO);

    /**
     * Partially updates a project.
     *
     * @param projectDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<ProjectDTO> partialUpdate(ProjectDTO projectDTO);

    /**
     * Get all the projects.
     *
     * @return the list of entities.
     */
    Flux<ProjectDTO> findAll();

    /**
     * Returns the number of projects available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of projects available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" project.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<ProjectDTO> findOne(String id);

    /**
     * Delete the "id" project.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(String id);

    /**
     * Search for the project corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    Flux<ProjectDTO> search(String query);
}
