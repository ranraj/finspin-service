package com.yali.finspin.service;

import com.yali.finspin.service.dto.OrgGroupDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.yali.finspin.domain.OrgGroup}.
 */
public interface OrgGroupService {
    /**
     * Save a orgGroup.
     *
     * @param orgGroupDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<OrgGroupDTO> save(OrgGroupDTO orgGroupDTO);

    /**
     * Updates a orgGroup.
     *
     * @param orgGroupDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<OrgGroupDTO> update(OrgGroupDTO orgGroupDTO);

    /**
     * Partially updates a orgGroup.
     *
     * @param orgGroupDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<OrgGroupDTO> partialUpdate(OrgGroupDTO orgGroupDTO);

    /**
     * Get all the orgGroups.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<OrgGroupDTO> findAll(Pageable pageable);

    /**
     * Get all the orgGroups with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<OrgGroupDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Returns the number of orgGroups available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of orgGroups available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" orgGroup.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<OrgGroupDTO> findOne(String id);

    /**
     * Delete the "id" orgGroup.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(String id);

    /**
     * Search for the orgGroup corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<OrgGroupDTO> search(String query, Pageable pageable);
}
