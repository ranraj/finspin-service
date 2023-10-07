package com.yali.finspin.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.yali.finspin.domain.Sprint;
import com.yali.finspin.repository.SprintRepository;
import com.yali.finspin.repository.search.SprintSearchRepository;
import com.yali.finspin.service.SprintService;
import com.yali.finspin.service.dto.SprintDTO;
import com.yali.finspin.service.mapper.SprintMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Sprint}.
 */
@Service
public class SprintServiceImpl implements SprintService {

    private final Logger log = LoggerFactory.getLogger(SprintServiceImpl.class);

    private final SprintRepository sprintRepository;

    private final SprintMapper sprintMapper;

    private final SprintSearchRepository sprintSearchRepository;

    public SprintServiceImpl(SprintRepository sprintRepository, SprintMapper sprintMapper, SprintSearchRepository sprintSearchRepository) {
        this.sprintRepository = sprintRepository;
        this.sprintMapper = sprintMapper;
        this.sprintSearchRepository = sprintSearchRepository;
    }

    @Override
    public Mono<SprintDTO> save(SprintDTO sprintDTO) {
        log.debug("Request to save Sprint : {}", sprintDTO);
        return sprintRepository.save(sprintMapper.toEntity(sprintDTO)).flatMap(sprintSearchRepository::save).map(sprintMapper::toDto);
    }

    @Override
    public Mono<SprintDTO> update(SprintDTO sprintDTO) {
        log.debug("Request to save Sprint : {}", sprintDTO);
        return sprintRepository.save(sprintMapper.toEntity(sprintDTO)).flatMap(sprintSearchRepository::save).map(sprintMapper::toDto);
    }

    @Override
    public Mono<SprintDTO> partialUpdate(SprintDTO sprintDTO) {
        log.debug("Request to partially update Sprint : {}", sprintDTO);

        return sprintRepository
            .findById(sprintDTO.getId())
            .map(existingSprint -> {
                sprintMapper.partialUpdate(existingSprint, sprintDTO);

                return existingSprint;
            })
            .flatMap(sprintRepository::save)
            .flatMap(savedSprint -> {
                sprintSearchRepository.save(savedSprint);

                return Mono.just(savedSprint);
            })
            .map(sprintMapper::toDto);
    }

    @Override
    public Flux<SprintDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Sprints");
        return sprintRepository.findAllBy(pageable).map(sprintMapper::toDto);
    }

    public Mono<Long> countAll() {
        return sprintRepository.count();
    }

    public Mono<Long> searchCount() {
        return sprintSearchRepository.count();
    }

    @Override
    public Mono<SprintDTO> findOne(String id) {
        log.debug("Request to get Sprint : {}", id);
        return sprintRepository.findById(id).map(sprintMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Sprint : {}", id);
        return sprintRepository.deleteById(id).then(sprintSearchRepository.deleteById(id));
    }

    @Override
    public Flux<SprintDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Sprints for query {}", query);
        return sprintSearchRepository.search(query, pageable).map(sprintMapper::toDto);
    }
}
