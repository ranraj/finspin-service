package com.yali.finspin.service;

import com.yali.finspin.service.dto.OrgAccountDTO;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.yali.finspin.domain.OrgAccount}.
 */
public interface OrgAccountService {
    /**
     * Save a orgAccount.
     *
     * @param orgAccountDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<OrgAccountDTO> save(OrgAccountDTO orgAccountDTO);

    /**
     * Updates a orgAccount.
     *
     * @param orgAccountDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<OrgAccountDTO> update(OrgAccountDTO orgAccountDTO);

    /**
     * Partially updates a orgAccount.
     *
     * @param orgAccountDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<OrgAccountDTO> partialUpdate(OrgAccountDTO orgAccountDTO);

    /**
     * Get all the orgAccounts.
     *
     * @return the list of entities.
     */
    Flux<OrgAccountDTO> findAll();

    /**
     * Returns the number of orgAccounts available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of orgAccounts available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" orgAccount.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<OrgAccountDTO> findOne(String id);

    /**
     * Delete the "id" orgAccount.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(String id);

    /**
     * Search for the orgAccount corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    Flux<OrgAccountDTO> search(String query);
}
