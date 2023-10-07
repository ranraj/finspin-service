package com.yali.finspin.web.rest;

import com.yali.finspin.repository.OrgAccountRepository;
import com.yali.finspin.service.OrgAccountService;
import com.yali.finspin.service.dto.OrgAccountDTO;
import com.yali.finspin.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.yali.finspin.domain.OrgAccount}.
 */
@RestController
@RequestMapping("/api")
public class OrgAccountResource {

    private final Logger log = LoggerFactory.getLogger(OrgAccountResource.class);

    private static final String ENTITY_NAME = "orgAccount";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OrgAccountService orgAccountService;

    private final OrgAccountRepository orgAccountRepository;

    public OrgAccountResource(OrgAccountService orgAccountService, OrgAccountRepository orgAccountRepository) {
        this.orgAccountService = orgAccountService;
        this.orgAccountRepository = orgAccountRepository;
    }

    /**
     * {@code POST  /org-accounts} : Create a new orgAccount.
     *
     * @param orgAccountDTO the orgAccountDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new orgAccountDTO, or with status {@code 400 (Bad Request)} if the orgAccount has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/org-accounts")
    public Mono<ResponseEntity<OrgAccountDTO>> createOrgAccount(@RequestBody OrgAccountDTO orgAccountDTO) throws URISyntaxException {
        log.debug("REST request to save OrgAccount : {}", orgAccountDTO);
        if (orgAccountDTO.getId() != null) {
            throw new BadRequestAlertException("A new orgAccount cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return orgAccountService
            .save(orgAccountDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/org-accounts/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /org-accounts/:id} : Updates an existing orgAccount.
     *
     * @param id the id of the orgAccountDTO to save.
     * @param orgAccountDTO the orgAccountDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orgAccountDTO,
     * or with status {@code 400 (Bad Request)} if the orgAccountDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the orgAccountDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/org-accounts/{id}")
    public Mono<ResponseEntity<OrgAccountDTO>> updateOrgAccount(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody OrgAccountDTO orgAccountDTO
    ) throws URISyntaxException {
        log.debug("REST request to update OrgAccount : {}, {}", id, orgAccountDTO);
        if (orgAccountDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, orgAccountDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return orgAccountRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return orgAccountService
                    .update(orgAccountDTO)
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
     * {@code PATCH  /org-accounts/:id} : Partial updates given fields of an existing orgAccount, field will ignore if it is null
     *
     * @param id the id of the orgAccountDTO to save.
     * @param orgAccountDTO the orgAccountDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orgAccountDTO,
     * or with status {@code 400 (Bad Request)} if the orgAccountDTO is not valid,
     * or with status {@code 404 (Not Found)} if the orgAccountDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the orgAccountDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/org-accounts/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<OrgAccountDTO>> partialUpdateOrgAccount(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody OrgAccountDTO orgAccountDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update OrgAccount partially : {}, {}", id, orgAccountDTO);
        if (orgAccountDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, orgAccountDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return orgAccountRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<OrgAccountDTO> result = orgAccountService.partialUpdate(orgAccountDTO);

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
     * {@code GET  /org-accounts} : get all the orgAccounts.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of orgAccounts in body.
     */
    @GetMapping("/org-accounts")
    public Mono<List<OrgAccountDTO>> getAllOrgAccounts() {
        log.debug("REST request to get all OrgAccounts");
        return orgAccountService.findAll().collectList();
    }

    /**
     * {@code GET  /org-accounts} : get all the orgAccounts as a stream.
     * @return the {@link Flux} of orgAccounts.
     */
    @GetMapping(value = "/org-accounts", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<OrgAccountDTO> getAllOrgAccountsAsStream() {
        log.debug("REST request to get all OrgAccounts as a stream");
        return orgAccountService.findAll();
    }

    /**
     * {@code GET  /org-accounts/:id} : get the "id" orgAccount.
     *
     * @param id the id of the orgAccountDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the orgAccountDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/org-accounts/{id}")
    public Mono<ResponseEntity<OrgAccountDTO>> getOrgAccount(@PathVariable String id) {
        log.debug("REST request to get OrgAccount : {}", id);
        Mono<OrgAccountDTO> orgAccountDTO = orgAccountService.findOne(id);
        return ResponseUtil.wrapOrNotFound(orgAccountDTO);
    }

    /**
     * {@code DELETE  /org-accounts/:id} : delete the "id" orgAccount.
     *
     * @param id the id of the orgAccountDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/org-accounts/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteOrgAccount(@PathVariable String id) {
        log.debug("REST request to delete OrgAccount : {}", id);
        return orgAccountService
            .delete(id)
            .map(result ->
                ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build()
            );
    }

    /**
     * {@code SEARCH  /_search/org-accounts?query=:query} : search for the orgAccount corresponding
     * to the query.
     *
     * @param query the query of the orgAccount search.
     * @return the result of the search.
     */
    @GetMapping("/_search/org-accounts")
    public Mono<List<OrgAccountDTO>> searchOrgAccounts(@RequestParam String query) {
        log.debug("REST request to search OrgAccounts for query {}", query);
        return orgAccountService.search(query).collectList();
    }
}
