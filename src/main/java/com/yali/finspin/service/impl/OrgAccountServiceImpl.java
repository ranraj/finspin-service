package com.yali.finspin.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.yali.finspin.domain.OrgAccount;
import com.yali.finspin.repository.OrgAccountRepository;
import com.yali.finspin.repository.search.OrgAccountSearchRepository;
import com.yali.finspin.service.OrgAccountService;
import com.yali.finspin.service.dto.OrgAccountDTO;
import com.yali.finspin.service.mapper.OrgAccountMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link OrgAccount}.
 */
@Service
public class OrgAccountServiceImpl implements OrgAccountService {

    private final Logger log = LoggerFactory.getLogger(OrgAccountServiceImpl.class);

    private final OrgAccountRepository orgAccountRepository;

    private final OrgAccountMapper orgAccountMapper;

    private final OrgAccountSearchRepository orgAccountSearchRepository;

    public OrgAccountServiceImpl(
        OrgAccountRepository orgAccountRepository,
        OrgAccountMapper orgAccountMapper,
        OrgAccountSearchRepository orgAccountSearchRepository
    ) {
        this.orgAccountRepository = orgAccountRepository;
        this.orgAccountMapper = orgAccountMapper;
        this.orgAccountSearchRepository = orgAccountSearchRepository;
    }

    @Override
    public Mono<OrgAccountDTO> save(OrgAccountDTO orgAccountDTO) {
        log.debug("Request to save OrgAccount : {}", orgAccountDTO);
        return orgAccountRepository
            .save(orgAccountMapper.toEntity(orgAccountDTO))
            .flatMap(orgAccountSearchRepository::save)
            .map(orgAccountMapper::toDto);
    }

    @Override
    public Mono<OrgAccountDTO> update(OrgAccountDTO orgAccountDTO) {
        log.debug("Request to save OrgAccount : {}", orgAccountDTO);
        return orgAccountRepository
            .save(orgAccountMapper.toEntity(orgAccountDTO))
            .flatMap(orgAccountSearchRepository::save)
            .map(orgAccountMapper::toDto);
    }

    @Override
    public Mono<OrgAccountDTO> partialUpdate(OrgAccountDTO orgAccountDTO) {
        log.debug("Request to partially update OrgAccount : {}", orgAccountDTO);

        return orgAccountRepository
            .findById(orgAccountDTO.getId())
            .map(existingOrgAccount -> {
                orgAccountMapper.partialUpdate(existingOrgAccount, orgAccountDTO);

                return existingOrgAccount;
            })
            .flatMap(orgAccountRepository::save)
            .flatMap(savedOrgAccount -> {
                orgAccountSearchRepository.save(savedOrgAccount);

                return Mono.just(savedOrgAccount);
            })
            .map(orgAccountMapper::toDto);
    }

    @Override
    public Flux<OrgAccountDTO> findAll() {
        log.debug("Request to get all OrgAccounts");
        return orgAccountRepository.findAll().map(orgAccountMapper::toDto);
    }

    public Mono<Long> countAll() {
        return orgAccountRepository.count();
    }

    public Mono<Long> searchCount() {
        return orgAccountSearchRepository.count();
    }

    @Override
    public Mono<OrgAccountDTO> findOne(String id) {
        log.debug("Request to get OrgAccount : {}", id);
        return orgAccountRepository.findById(id).map(orgAccountMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete OrgAccount : {}", id);
        return orgAccountRepository.deleteById(id).then(orgAccountSearchRepository.deleteById(id));
    }

    @Override
    public Flux<OrgAccountDTO> search(String query) {
        log.debug("Request to search OrgAccounts for query {}", query);
        return orgAccountSearchRepository.search(query).map(orgAccountMapper::toDto);
    }
}
