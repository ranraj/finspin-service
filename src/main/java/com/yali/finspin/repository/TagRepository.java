package com.yali.finspin.repository;

import com.yali.finspin.domain.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data MongoDB reactive repository for the Tag entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TagRepository extends ReactiveMongoRepository<Tag, String> {
    @Query("{}")
    Flux<Tag> findAllWithEagerRelationships(Pageable pageable);

    @Query("{}")
    Flux<Tag> findAllWithEagerRelationships();

    @Query("{'id': ?0}")
    Mono<Tag> findOneWithEagerRelationships(String id);
}
