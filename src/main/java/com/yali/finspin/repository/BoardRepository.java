package com.yali.finspin.repository;

import com.yali.finspin.domain.Board;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * Spring Data MongoDB reactive repository for the Board entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BoardRepository extends ReactiveMongoRepository<Board, String> {
    Flux<Board> findAllBy(Pageable pageable);
}
