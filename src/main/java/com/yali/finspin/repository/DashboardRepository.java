package com.yali.finspin.repository;

import com.yali.finspin.domain.Dashboard;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * Spring Data MongoDB reactive repository for the Dashboard entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DashboardRepository extends ReactiveMongoRepository<Dashboard, String> {
    Flux<Dashboard> findAllBy(Pageable pageable);
}
