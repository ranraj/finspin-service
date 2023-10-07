package com.yali.finspin.repository;

import com.yali.finspin.domain.Organisation;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB reactive repository for the Organisation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrganisationRepository extends ReactiveMongoRepository<Organisation, String> {}
