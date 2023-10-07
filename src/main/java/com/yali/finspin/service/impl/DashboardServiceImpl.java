package com.yali.finspin.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.yali.finspin.domain.Dashboard;
import com.yali.finspin.repository.DashboardRepository;
import com.yali.finspin.repository.search.DashboardSearchRepository;
import com.yali.finspin.service.DashboardService;
import com.yali.finspin.service.dto.DashboardDTO;
import com.yali.finspin.service.mapper.DashboardMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Dashboard}.
 */
@Service
public class DashboardServiceImpl implements DashboardService {

    private final Logger log = LoggerFactory.getLogger(DashboardServiceImpl.class);

    private final DashboardRepository dashboardRepository;

    private final DashboardMapper dashboardMapper;

    private final DashboardSearchRepository dashboardSearchRepository;

    public DashboardServiceImpl(
        DashboardRepository dashboardRepository,
        DashboardMapper dashboardMapper,
        DashboardSearchRepository dashboardSearchRepository
    ) {
        this.dashboardRepository = dashboardRepository;
        this.dashboardMapper = dashboardMapper;
        this.dashboardSearchRepository = dashboardSearchRepository;
    }

    @Override
    public Mono<DashboardDTO> save(DashboardDTO dashboardDTO) {
        log.debug("Request to save Dashboard : {}", dashboardDTO);
        return dashboardRepository
            .save(dashboardMapper.toEntity(dashboardDTO))
            .flatMap(dashboardSearchRepository::save)
            .map(dashboardMapper::toDto);
    }

    @Override
    public Mono<DashboardDTO> update(DashboardDTO dashboardDTO) {
        log.debug("Request to save Dashboard : {}", dashboardDTO);
        return dashboardRepository
            .save(dashboardMapper.toEntity(dashboardDTO))
            .flatMap(dashboardSearchRepository::save)
            .map(dashboardMapper::toDto);
    }

    @Override
    public Mono<DashboardDTO> partialUpdate(DashboardDTO dashboardDTO) {
        log.debug("Request to partially update Dashboard : {}", dashboardDTO);

        return dashboardRepository
            .findById(dashboardDTO.getId())
            .map(existingDashboard -> {
                dashboardMapper.partialUpdate(existingDashboard, dashboardDTO);

                return existingDashboard;
            })
            .flatMap(dashboardRepository::save)
            .flatMap(savedDashboard -> {
                dashboardSearchRepository.save(savedDashboard);

                return Mono.just(savedDashboard);
            })
            .map(dashboardMapper::toDto);
    }

    @Override
    public Flux<DashboardDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Dashboards");
        return dashboardRepository.findAllBy(pageable).map(dashboardMapper::toDto);
    }

    public Mono<Long> countAll() {
        return dashboardRepository.count();
    }

    public Mono<Long> searchCount() {
        return dashboardSearchRepository.count();
    }

    @Override
    public Mono<DashboardDTO> findOne(String id) {
        log.debug("Request to get Dashboard : {}", id);
        return dashboardRepository.findById(id).map(dashboardMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Dashboard : {}", id);
        return dashboardRepository.deleteById(id).then(dashboardSearchRepository.deleteById(id));
    }

    @Override
    public Flux<DashboardDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Dashboards for query {}", query);
        return dashboardSearchRepository.search(query, pageable).map(dashboardMapper::toDto);
    }
}
