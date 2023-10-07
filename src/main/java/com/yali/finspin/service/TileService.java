package com.yali.finspin.service;

import com.yali.finspin.service.dto.TileDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.yali.finspin.domain.Tile}.
 */
public interface TileService {
    /**
     * Save a tile.
     *
     * @param tileDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<TileDTO> save(TileDTO tileDTO);

    /**
     * Updates a tile.
     *
     * @param tileDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<TileDTO> update(TileDTO tileDTO);

    /**
     * Partially updates a tile.
     *
     * @param tileDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<TileDTO> partialUpdate(TileDTO tileDTO);

    /**
     * Get all the tiles.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<TileDTO> findAll(Pageable pageable);

    /**
     * Returns the number of tiles available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of tiles available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" tile.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<TileDTO> findOne(String id);

    /**
     * Delete the "id" tile.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(String id);

    /**
     * Search for the tile corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<TileDTO> search(String query, Pageable pageable);
}
