package com.yali.finspin.repository;

import com.yali.finspin.domain.Tile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * Spring Data MongoDB reactive repository for the Tile entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TileRepository extends ReactiveMongoRepository<Tile, String> {
    Flux<Tile> findAllBy(Pageable pageable);
}
