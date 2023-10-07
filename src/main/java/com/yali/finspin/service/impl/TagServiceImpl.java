package com.yali.finspin.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.yali.finspin.domain.Tag;
import com.yali.finspin.repository.TagRepository;
import com.yali.finspin.repository.search.TagSearchRepository;
import com.yali.finspin.service.TagService;
import com.yali.finspin.service.dto.TagDTO;
import com.yali.finspin.service.mapper.TagMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Tag}.
 */
@Service
public class TagServiceImpl implements TagService {

    private final Logger log = LoggerFactory.getLogger(TagServiceImpl.class);

    private final TagRepository tagRepository;

    private final TagMapper tagMapper;

    private final TagSearchRepository tagSearchRepository;

    public TagServiceImpl(TagRepository tagRepository, TagMapper tagMapper, TagSearchRepository tagSearchRepository) {
        this.tagRepository = tagRepository;
        this.tagMapper = tagMapper;
        this.tagSearchRepository = tagSearchRepository;
    }

    @Override
    public Mono<TagDTO> save(TagDTO tagDTO) {
        log.debug("Request to save Tag : {}", tagDTO);
        return tagRepository.save(tagMapper.toEntity(tagDTO)).flatMap(tagSearchRepository::save).map(tagMapper::toDto);
    }

    @Override
    public Mono<TagDTO> update(TagDTO tagDTO) {
        log.debug("Request to save Tag : {}", tagDTO);
        return tagRepository.save(tagMapper.toEntity(tagDTO)).flatMap(tagSearchRepository::save).map(tagMapper::toDto);
    }

    @Override
    public Mono<TagDTO> partialUpdate(TagDTO tagDTO) {
        log.debug("Request to partially update Tag : {}", tagDTO);

        return tagRepository
            .findById(tagDTO.getId())
            .map(existingTag -> {
                tagMapper.partialUpdate(existingTag, tagDTO);

                return existingTag;
            })
            .flatMap(tagRepository::save)
            .flatMap(savedTag -> {
                tagSearchRepository.save(savedTag);

                return Mono.just(savedTag);
            })
            .map(tagMapper::toDto);
    }

    @Override
    public Flux<TagDTO> findAll() {
        log.debug("Request to get all Tags");
        return tagRepository.findAllWithEagerRelationships().map(tagMapper::toDto);
    }

    public Flux<TagDTO> findAllWithEagerRelationships(Pageable pageable) {
        return tagRepository.findAllWithEagerRelationships(pageable).map(tagMapper::toDto);
    }

    public Mono<Long> countAll() {
        return tagRepository.count();
    }

    public Mono<Long> searchCount() {
        return tagSearchRepository.count();
    }

    @Override
    public Mono<TagDTO> findOne(String id) {
        log.debug("Request to get Tag : {}", id);
        return tagRepository.findOneWithEagerRelationships(id).map(tagMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Tag : {}", id);
        return tagRepository.deleteById(id).then(tagSearchRepository.deleteById(id));
    }

    @Override
    public Flux<TagDTO> search(String query) {
        log.debug("Request to search Tags for query {}", query);
        return tagSearchRepository.search(query).map(tagMapper::toDto);
    }
}
