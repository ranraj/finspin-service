package com.yali.finspin.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.yali.finspin.domain.Comment;
import com.yali.finspin.repository.CommentRepository;
import com.yali.finspin.repository.search.CommentSearchRepository;
import com.yali.finspin.service.dto.CommentDTO;
import com.yali.finspin.service.mapper.CommentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Comment}.
 */
@Service
public class CommentService {

    private final Logger log = LoggerFactory.getLogger(CommentService.class);

    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    private final CommentSearchRepository commentSearchRepository;

    public CommentService(
        CommentRepository commentRepository,
        CommentMapper commentMapper,
        CommentSearchRepository commentSearchRepository
    ) {
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
        this.commentSearchRepository = commentSearchRepository;
    }

    /**
     * Save a comment.
     *
     * @param commentDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<CommentDTO> save(CommentDTO commentDTO) {
        log.debug("Request to save Comment : {}", commentDTO);
        return commentRepository.save(commentMapper.toEntity(commentDTO)).flatMap(commentSearchRepository::save).map(commentMapper::toDto);
    }

    /**
     * Update a comment.
     *
     * @param commentDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<CommentDTO> update(CommentDTO commentDTO) {
        log.debug("Request to save Comment : {}", commentDTO);
        return commentRepository.save(commentMapper.toEntity(commentDTO)).flatMap(commentSearchRepository::save).map(commentMapper::toDto);
    }

    /**
     * Partially update a comment.
     *
     * @param commentDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<CommentDTO> partialUpdate(CommentDTO commentDTO) {
        log.debug("Request to partially update Comment : {}", commentDTO);

        return commentRepository
            .findById(commentDTO.getId())
            .map(existingComment -> {
                commentMapper.partialUpdate(existingComment, commentDTO);

                return existingComment;
            })
            .flatMap(commentRepository::save)
            .flatMap(savedComment -> {
                commentSearchRepository.save(savedComment);

                return Mono.just(savedComment);
            })
            .map(commentMapper::toDto);
    }

    /**
     * Get all the comments.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Flux<CommentDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Comments");
        return commentRepository.findAllBy(pageable).map(commentMapper::toDto);
    }

    /**
     * Returns the number of comments available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return commentRepository.count();
    }

    /**
     * Returns the number of comments available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return commentSearchRepository.count();
    }

    /**
     * Get one comment by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Mono<CommentDTO> findOne(String id) {
        log.debug("Request to get Comment : {}", id);
        return commentRepository.findById(id).map(commentMapper::toDto);
    }

    /**
     * Delete the comment by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Comment : {}", id);
        return commentRepository.deleteById(id).then(commentSearchRepository.deleteById(id));
    }

    /**
     * Search for the comment corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Flux<CommentDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Comments for query {}", query);
        return commentSearchRepository.search(query, pageable).map(commentMapper::toDto);
    }
}
