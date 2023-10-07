package com.yali.finspin.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.yali.finspin.domain.Tile;
import com.yali.finspin.repository.TileRepository;
import com.yali.finspin.repository.search.TileSearchRepository;
import com.yali.finspin.service.TileService;
import com.yali.finspin.service.dto.TileDTO;
import com.yali.finspin.service.mapper.TileMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Tile}.
 */
@Service
public class TileServiceImpl implements TileService {

    private final Logger log = LoggerFactory.getLogger(TileServiceImpl.class);

    private final TileRepository tileRepository;

    private final TileMapper tileMapper;

    private final TileSearchRepository tileSearchRepository;

    public TileServiceImpl(TileRepository tileRepository, TileMapper tileMapper, TileSearchRepository tileSearchRepository) {
        this.tileRepository = tileRepository;
        this.tileMapper = tileMapper;
        this.tileSearchRepository = tileSearchRepository;
    }

    @Override
    public Mono<TileDTO> save(TileDTO tileDTO) {
        log.debug("Request to save Tile : {}", tileDTO);
        return tileRepository.save(tileMapper.toEntity(tileDTO)).flatMap(tileSearchRepository::save).map(tileMapper::toDto);
    }

    @Override
    public Mono<TileDTO> update(TileDTO tileDTO) {
        log.debug("Request to save Tile : {}", tileDTO);
        return tileRepository.save(tileMapper.toEntity(tileDTO)).flatMap(tileSearchRepository::save).map(tileMapper::toDto);
    }

    @Override
    public Mono<TileDTO> partialUpdate(TileDTO tileDTO) {
        log.debug("Request to partially update Tile : {}", tileDTO);

        return tileRepository
            .findById(tileDTO.getId())
            .map(existingTile -> {
                tileMapper.partialUpdate(existingTile, tileDTO);

                return existingTile;
            })
            .flatMap(tileRepository::save)
            .flatMap(savedTile -> {
                tileSearchRepository.save(savedTile);

                return Mono.just(savedTile);
            })
            .map(tileMapper::toDto);
    }

    @Override
    public Flux<TileDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Tiles");
        return tileRepository.findAllBy(pageable).map(tileMapper::toDto);
    }

    public Mono<Long> countAll() {
        return tileRepository.count();
    }

    public Mono<Long> searchCount() {
        return tileSearchRepository.count();
    }

    @Override
    public Mono<TileDTO> findOne(String id) {
        log.debug("Request to get Tile : {}", id);
        return tileRepository.findById(id).map(tileMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Tile : {}", id);
        return tileRepository.deleteById(id).then(tileSearchRepository.deleteById(id));
    }

    @Override
    public Flux<TileDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Tiles for query {}", query);
        return tileSearchRepository.search(query, pageable).map(tileMapper::toDto);
    }
}
