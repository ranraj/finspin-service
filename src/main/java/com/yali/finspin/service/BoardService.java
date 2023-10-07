package com.yali.finspin.service;

import com.yali.finspin.service.dto.BoardDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.yali.finspin.domain.Board}.
 */
public interface BoardService {
    /**
     * Save a board.
     *
     * @param boardDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<BoardDTO> save(BoardDTO boardDTO);

    /**
     * Updates a board.
     *
     * @param boardDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<BoardDTO> update(BoardDTO boardDTO);

    /**
     * Partially updates a board.
     *
     * @param boardDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<BoardDTO> partialUpdate(BoardDTO boardDTO);

    /**
     * Get all the boards.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<BoardDTO> findAll(Pageable pageable);

    /**
     * Returns the number of boards available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of boards available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" board.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<BoardDTO> findOne(String id);

    /**
     * Delete the "id" board.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(String id);

    /**
     * Search for the board corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<BoardDTO> search(String query, Pageable pageable);
}
