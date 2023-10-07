package com.yali.finspin.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.yali.finspin.domain.Organisation;
import com.yali.finspin.repository.OrganisationRepository;
import com.yali.finspin.repository.search.OrganisationSearchRepository;
import com.yali.finspin.service.OrganisationService;
import com.yali.finspin.service.dto.OrganisationDTO;
import com.yali.finspin.service.mapper.OrganisationMapper;
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
 * Service Implementation for managing {@link Organisation}.
 */
@Service
public class OrganisationServiceImpl implements OrganisationService {

    private final Logger log = LoggerFactory.getLogger(OrganisationServiceImpl.class);

    private final OrganisationRepository organisationRepository;

    private final OrganisationMapper organisationMapper;

    private final OrganisationSearchRepository organisationSearchRepository;

    public OrganisationServiceImpl(
        OrganisationRepository organisationRepository,
        OrganisationMapper organisationMapper,
        OrganisationSearchRepository organisationSearchRepository
    ) {
        this.organisationRepository = organisationRepository;
        this.organisationMapper = organisationMapper;
        this.organisationSearchRepository = organisationSearchRepository;
    }

    @Override
    public Mono<OrganisationDTO> save(OrganisationDTO organisationDTO) {
        log.debug("Request to save Organisation : {}", organisationDTO);
        return organisationRepository
            .save(organisationMapper.toEntity(organisationDTO))
            .flatMap(organisationSearchRepository::save)
            .map(organisationMapper::toDto);
    }

    @Override
    public Mono<OrganisationDTO> update(OrganisationDTO organisationDTO) {
        log.debug("Request to save Organisation : {}", organisationDTO);
        return organisationRepository
            .save(organisationMapper.toEntity(organisationDTO))
            .flatMap(organisationSearchRepository::save)
            .map(organisationMapper::toDto);
    }

    @Override
    public Mono<OrganisationDTO> partialUpdate(OrganisationDTO organisationDTO) {
        log.debug("Request to partially update Organisation : {}", organisationDTO);

        return organisationRepository
            .findById(organisationDTO.getId())
            .map(existingOrganisation -> {
                organisationMapper.partialUpdate(existingOrganisation, organisationDTO);

                return existingOrganisation;
            })
            .flatMap(organisationRepository::save)
            .flatMap(savedOrganisation -> {
                organisationSearchRepository.save(savedOrganisation);

                return Mono.just(savedOrganisation);
            })
            .map(organisationMapper::toDto);
    }

    @Override
    public Flux<OrganisationDTO> findAll() {
        log.debug("Request to get all Organisations");
        return organisationRepository.findAll().map(organisationMapper::toDto);
    }

    public Mono<Long> countAll() {
        return organisationRepository.count();
    }

    public Mono<Long> searchCount() {
        return organisationSearchRepository.count();
    }

    @Override
    public Mono<OrganisationDTO> findOne(String id) {
        log.debug("Request to get Organisation : {}", id);
        return organisationRepository.findById(id).map(organisationMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Organisation : {}", id);
        return organisationRepository.deleteById(id).then(organisationSearchRepository.deleteById(id));
    }

    @Override
    public Flux<OrganisationDTO> search(String query) {
        log.debug("Request to search Organisations for query {}", query);
        return organisationSearchRepository.search(query).map(organisationMapper::toDto);
    }
}
