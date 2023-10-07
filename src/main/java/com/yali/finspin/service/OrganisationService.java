package com.yali.finspin.service;

import com.yali.finspin.service.dto.OrganisationDTO;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.yali.finspin.domain.Organisation}.
 */
public interface OrganisationService {
    /**
     * Save a organisation.
     *
     * @param organisationDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<OrganisationDTO> save(OrganisationDTO organisationDTO);

    /**
     * Updates a organisation.
     *
     * @param organisationDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<OrganisationDTO> update(OrganisationDTO organisationDTO);

    /**
     * Partially updates a organisation.
     *
     * @param organisationDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<OrganisationDTO> partialUpdate(OrganisationDTO organisationDTO);

    /**
     * Get all the organisations.
     *
     * @return the list of entities.
     */
    Flux<OrganisationDTO> findAll();

    /**
     * Returns the number of organisations available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of organisations available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" organisation.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<OrganisationDTO> findOne(String id);

    /**
     * Delete the "id" organisation.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(String id);

    /**
     * Search for the organisation corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    Flux<OrganisationDTO> search(String query);
}
