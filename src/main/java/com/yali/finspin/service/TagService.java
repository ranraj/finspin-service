package com.yali.finspin.service;

import com.yali.finspin.service.dto.TagDTO;
import java.util.List;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.yali.finspin.domain.Tag}.
 */
public interface TagService {
    /**
     * Save a tag.
     *
     * @param tagDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<TagDTO> save(TagDTO tagDTO);

    /**
     * Updates a tag.
     *
     * @param tagDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<TagDTO> update(TagDTO tagDTO);

    /**
     * Partially updates a tag.
     *
     * @param tagDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<TagDTO> partialUpdate(TagDTO tagDTO);

    /**
     * Get all the tags.
     *
     * @return the list of entities.
     */
    Flux<TagDTO> findAll();

    /**
     * Get all the tags with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<TagDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Returns the number of tags available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of tags available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" tag.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<TagDTO> findOne(String id);

    /**
     * Delete the "id" tag.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(String id);

    /**
     * Search for the tag corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    Flux<TagDTO> search(String query);
}
