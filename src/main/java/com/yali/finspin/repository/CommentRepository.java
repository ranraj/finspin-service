package com.yali.finspin.repository;

import com.yali.finspin.domain.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * Spring Data MongoDB reactive repository for the Comment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CommentRepository extends ReactiveMongoRepository<Comment, String> {
    Flux<Comment> findAllBy(Pageable pageable);
}
