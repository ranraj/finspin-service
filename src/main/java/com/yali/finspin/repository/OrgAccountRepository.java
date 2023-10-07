package com.yali.finspin.repository;

import com.yali.finspin.domain.OrgAccount;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB reactive repository for the OrgAccount entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrgAccountRepository extends ReactiveMongoRepository<OrgAccount, String> {}
