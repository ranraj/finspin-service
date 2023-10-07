package com.yali.finspin.repository;

import com.yali.finspin.domain.Sprint;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * Spring Data MongoDB reactive repository for the Sprint entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SprintRepository extends ReactiveMongoRepository<Sprint, String> {
    Flux<Sprint> findAllBy(Pageable pageable);
}
