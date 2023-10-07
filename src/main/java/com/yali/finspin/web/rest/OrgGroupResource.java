package com.yali.finspin.web.rest;

import com.yali.finspin.repository.OrgGroupRepository;
import com.yali.finspin.service.OrgGroupService;
import com.yali.finspin.service.dto.OrgGroupDTO;
import com.yali.finspin.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.yali.finspin.domain.OrgGroup}.
 */
@RestController
@RequestMapping("/api")
public class OrgGroupResource {

    private final Logger log = LoggerFactory.getLogger(OrgGroupResource.class);

    private static final String ENTITY_NAME = "orgGroup";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OrgGroupService orgGroupService;

    private final OrgGroupRepository orgGroupRepository;

    public OrgGroupResource(OrgGroupService orgGroupService, OrgGroupRepository orgGroupRepository) {
        this.orgGroupService = orgGroupService;
        this.orgGroupRepository = orgGroupRepository;
    }

    /**
     * {@code POST  /org-groups} : Create a new orgGroup.
     *
     * @param orgGroupDTO the orgGroupDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new orgGroupDTO, or with status {@code 400 (Bad Request)} if the orgGroup has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/org-groups")
    public Mono<ResponseEntity<OrgGroupDTO>> createOrgGroup(@RequestBody OrgGroupDTO orgGroupDTO) throws URISyntaxException {
        log.debug("REST request to save OrgGroup : {}", orgGroupDTO);
        if (orgGroupDTO.getId() != null) {
            throw new BadRequestAlertException("A new orgGroup cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return orgGroupService
            .save(orgGroupDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/org-groups/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /org-groups/:id} : Updates an existing orgGroup.
     *
     * @param id the id of the orgGroupDTO to save.
     * @param orgGroupDTO the orgGroupDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orgGroupDTO,
     * or with status {@code 400 (Bad Request)} if the orgGroupDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the orgGroupDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/org-groups/{id}")
    public Mono<ResponseEntity<OrgGroupDTO>> updateOrgGroup(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody OrgGroupDTO orgGroupDTO
    ) throws URISyntaxException {
        log.debug("REST request to update OrgGroup : {}, {}", id, orgGroupDTO);
        if (orgGroupDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, orgGroupDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return orgGroupRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return orgGroupService
                    .update(orgGroupDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /org-groups/:id} : Partial updates given fields of an existing orgGroup, field will ignore if it is null
     *
     * @param id the id of the orgGroupDTO to save.
     * @param orgGroupDTO the orgGroupDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orgGroupDTO,
     * or with status {@code 400 (Bad Request)} if the orgGroupDTO is not valid,
     * or with status {@code 404 (Not Found)} if the orgGroupDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the orgGroupDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/org-groups/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<OrgGroupDTO>> partialUpdateOrgGroup(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody OrgGroupDTO orgGroupDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update OrgGroup partially : {}, {}", id, orgGroupDTO);
        if (orgGroupDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, orgGroupDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return orgGroupRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<OrgGroupDTO> result = orgGroupService.partialUpdate(orgGroupDTO);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /org-groups} : get all the orgGroups.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of orgGroups in body.
     */
    @GetMapping("/org-groups")
    public Mono<ResponseEntity<List<OrgGroupDTO>>> getAllOrgGroups(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(required = false, defaultValue = "true") boolean eagerload
    ) {
        log.debug("REST request to get a page of OrgGroups");
        return orgGroupService
            .countAll()
            .zipWith(orgGroupService.findAll(pageable).collectList())
            .map(countWithEntities ->
                ResponseEntity
                    .ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            UriComponentsBuilder.fromHttpRequest(request),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /org-groups/:id} : get the "id" orgGroup.
     *
     * @param id the id of the orgGroupDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the orgGroupDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/org-groups/{id}")
    public Mono<ResponseEntity<OrgGroupDTO>> getOrgGroup(@PathVariable String id) {
        log.debug("REST request to get OrgGroup : {}", id);
        Mono<OrgGroupDTO> orgGroupDTO = orgGroupService.findOne(id);
        return ResponseUtil.wrapOrNotFound(orgGroupDTO);
    }

    /**
     * {@code DELETE  /org-groups/:id} : delete the "id" orgGroup.
     *
     * @param id the id of the orgGroupDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/org-groups/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteOrgGroup(@PathVariable String id) {
        log.debug("REST request to delete OrgGroup : {}", id);
        return orgGroupService
            .delete(id)
            .map(result ->
                ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build()
            );
    }

    /**
     * {@code SEARCH  /_search/org-groups?query=:query} : search for the orgGroup corresponding
     * to the query.
     *
     * @param query the query of the orgGroup search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search/org-groups")
    public Mono<ResponseEntity<Flux<OrgGroupDTO>>> searchOrgGroups(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of OrgGroups for query {}", query);
        return orgGroupService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page -> PaginationUtil.generatePaginationHttpHeaders(UriComponentsBuilder.fromHttpRequest(request), page))
            .map(headers -> ResponseEntity.ok().headers(headers).body(orgGroupService.search(query, pageable)));
    }
}
