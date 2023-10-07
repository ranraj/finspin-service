package com.yali.finspin.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.yali.finspin.domain.OrgGroup;
import com.yali.finspin.repository.OrgGroupRepository;
import com.yali.finspin.repository.search.OrgGroupSearchRepository;
import com.yali.finspin.service.OrgGroupService;
import com.yali.finspin.service.dto.OrgGroupDTO;
import com.yali.finspin.service.mapper.OrgGroupMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link OrgGroup}.
 */
@Service
public class OrgGroupServiceImpl implements OrgGroupService {

    private final Logger log = LoggerFactory.getLogger(OrgGroupServiceImpl.class);

    private final OrgGroupRepository orgGroupRepository;

    private final OrgGroupMapper orgGroupMapper;

    private final OrgGroupSearchRepository orgGroupSearchRepository;

    public OrgGroupServiceImpl(
        OrgGroupRepository orgGroupRepository,
        OrgGroupMapper orgGroupMapper,
        OrgGroupSearchRepository orgGroupSearchRepository
    ) {
        this.orgGroupRepository = orgGroupRepository;
        this.orgGroupMapper = orgGroupMapper;
        this.orgGroupSearchRepository = orgGroupSearchRepository;
    }

    @Override
    public Mono<OrgGroupDTO> save(OrgGroupDTO orgGroupDTO) {
        log.debug("Request to save OrgGroup : {}", orgGroupDTO);
        return orgGroupRepository
            .save(orgGroupMapper.toEntity(orgGroupDTO))
            .flatMap(orgGroupSearchRepository::save)
            .map(orgGroupMapper::toDto);
    }

    @Override
    public Mono<OrgGroupDTO> update(OrgGroupDTO orgGroupDTO) {
        log.debug("Request to save OrgGroup : {}", orgGroupDTO);
        return orgGroupRepository
            .save(orgGroupMapper.toEntity(orgGroupDTO))
            .flatMap(orgGroupSearchRepository::save)
            .map(orgGroupMapper::toDto);
    }

    @Override
    public Mono<OrgGroupDTO> partialUpdate(OrgGroupDTO orgGroupDTO) {
        log.debug("Request to partially update OrgGroup : {}", orgGroupDTO);

        return orgGroupRepository
            .findById(orgGroupDTO.getId())
            .map(existingOrgGroup -> {
                orgGroupMapper.partialUpdate(existingOrgGroup, orgGroupDTO);

                return existingOrgGroup;
            })
            .flatMap(orgGroupRepository::save)
            .flatMap(savedOrgGroup -> {
                orgGroupSearchRepository.save(savedOrgGroup);

                return Mono.just(savedOrgGroup);
            })
            .map(orgGroupMapper::toDto);
    }

    @Override
    public Flux<OrgGroupDTO> findAll(Pageable pageable) {
        log.debug("Request to get all OrgGroups");
        return orgGroupRepository.findAllBy(pageable).map(orgGroupMapper::toDto);
    }

    public Flux<OrgGroupDTO> findAllWithEagerRelationships(Pageable pageable) {
        return orgGroupRepository.findAllWithEagerRelationships(pageable).map(orgGroupMapper::toDto);
    }

    public Mono<Long> countAll() {
        return orgGroupRepository.count();
    }

    public Mono<Long> searchCount() {
        return orgGroupSearchRepository.count();
    }

    @Override
    public Mono<OrgGroupDTO> findOne(String id) {
        log.debug("Request to get OrgGroup : {}", id);
        return orgGroupRepository.findOneWithEagerRelationships(id).map(orgGroupMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete OrgGroup : {}", id);
        return orgGroupRepository.deleteById(id).then(orgGroupSearchRepository.deleteById(id));
    }

    @Override
    public Flux<OrgGroupDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of OrgGroups for query {}", query);
        return orgGroupSearchRepository.search(query, pageable).map(orgGroupMapper::toDto);
    }
}
