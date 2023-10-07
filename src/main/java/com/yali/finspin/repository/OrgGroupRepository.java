package com.yali.finspin.repository;

import com.yali.finspin.domain.OrgGroup;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data MongoDB reactive repository for the OrgGroup entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrgGroupRepository extends ReactiveMongoRepository<OrgGroup, String> {
    Flux<OrgGroup> findAllBy(Pageable pageable);

    @Query("{}")
    Flux<OrgGroup> findAllWithEagerRelationships(Pageable pageable);

    @Query("{}")
    Flux<OrgGroup> findAllWithEagerRelationships();

    @Query("{'id': ?0}")
    Mono<OrgGroup> findOneWithEagerRelationships(String id);
}
