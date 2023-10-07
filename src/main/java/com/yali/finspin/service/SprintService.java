package com.yali.finspin.service;

import com.yali.finspin.service.dto.SprintDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.yali.finspin.domain.Sprint}.
 */
public interface SprintService {
    /**
     * Save a sprint.
     *
     * @param sprintDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<SprintDTO> save(SprintDTO sprintDTO);

    /**
     * Updates a sprint.
     *
     * @param sprintDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<SprintDTO> update(SprintDTO sprintDTO);

    /**
     * Partially updates a sprint.
     *
     * @param sprintDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<SprintDTO> partialUpdate(SprintDTO sprintDTO);

    /**
     * Get all the sprints.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<SprintDTO> findAll(Pageable pageable);

    /**
     * Returns the number of sprints available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of sprints available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" sprint.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<SprintDTO> findOne(String id);

    /**
     * Delete the "id" sprint.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(String id);

    /**
     * Search for the sprint corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<SprintDTO> search(String query, Pageable pageable);
}
