package com.yali.finspin.repository;

import com.yali.finspin.domain.Task;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data MongoDB reactive repository for the Task entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TaskRepository extends ReactiveMongoRepository<Task, String> {
    Flux<Task> findAllBy(Pageable pageable);

    @Query("{}")
    Flux<Task> findAllWithEagerRelationships(Pageable pageable);

    @Query("{}")
    Flux<Task> findAllWithEagerRelationships();

    @Query("{'id': ?0}")
    Mono<Task> findOneWithEagerRelationships(String id);
}
