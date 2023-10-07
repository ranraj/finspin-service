package com.yali.finspin.service;

import com.yali.finspin.service.dto.DashboardDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.yali.finspin.domain.Dashboard}.
 */
public interface DashboardService {
    /**
     * Save a dashboard.
     *
     * @param dashboardDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<DashboardDTO> save(DashboardDTO dashboardDTO);

    /**
     * Updates a dashboard.
     *
     * @param dashboardDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<DashboardDTO> update(DashboardDTO dashboardDTO);

    /**
     * Partially updates a dashboard.
     *
     * @param dashboardDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<DashboardDTO> partialUpdate(DashboardDTO dashboardDTO);

    /**
     * Get all the dashboards.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<DashboardDTO> findAll(Pageable pageable);

    /**
     * Returns the number of dashboards available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of dashboards available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" dashboard.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<DashboardDTO> findOne(String id);

    /**
     * Delete the "id" dashboard.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(String id);

    /**
     * Search for the dashboard corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<DashboardDTO> search(String query, Pageable pageable);
}
