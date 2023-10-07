package com.yali.finspin.web.rest;

import com.yali.finspin.repository.TileRepository;
import com.yali.finspin.service.TileService;
import com.yali.finspin.service.dto.TileDTO;
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
 * REST controller for managing {@link com.yali.finspin.domain.Tile}.
 */
@RestController
@RequestMapping("/api")
public class TileResource {

    private final Logger log = LoggerFactory.getLogger(TileResource.class);

    private static final String ENTITY_NAME = "tile";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TileService tileService;

    private final TileRepository tileRepository;

    public TileResource(TileService tileService, TileRepository tileRepository) {
        this.tileService = tileService;
        this.tileRepository = tileRepository;
    }

    /**
     * {@code POST  /tiles} : Create a new tile.
     *
     * @param tileDTO the tileDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new tileDTO, or with status {@code 400 (Bad Request)} if the tile has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/tiles")
    public Mono<ResponseEntity<TileDTO>> createTile(@RequestBody TileDTO tileDTO) throws URISyntaxException {
        log.debug("REST request to save Tile : {}", tileDTO);
        if (tileDTO.getId() != null) {
            throw new BadRequestAlertException("A new tile cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return tileService
            .save(tileDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/tiles/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /tiles/:id} : Updates an existing tile.
     *
     * @param id the id of the tileDTO to save.
     * @param tileDTO the tileDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tileDTO,
     * or with status {@code 400 (Bad Request)} if the tileDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the tileDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/tiles/{id}")
    public Mono<ResponseEntity<TileDTO>> updateTile(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody TileDTO tileDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Tile : {}, {}", id, tileDTO);
        if (tileDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tileDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return tileRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return tileService
                    .update(tileDTO)
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
     * {@code PATCH  /tiles/:id} : Partial updates given fields of an existing tile, field will ignore if it is null
     *
     * @param id the id of the tileDTO to save.
     * @param tileDTO the tileDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tileDTO,
     * or with status {@code 400 (Bad Request)} if the tileDTO is not valid,
     * or with status {@code 404 (Not Found)} if the tileDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the tileDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/tiles/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<TileDTO>> partialUpdateTile(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody TileDTO tileDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Tile partially : {}, {}", id, tileDTO);
        if (tileDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tileDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return tileRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<TileDTO> result = tileService.partialUpdate(tileDTO);

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
     * {@code GET  /tiles} : get all the tiles.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of tiles in body.
     */
    @GetMapping("/tiles")
    public Mono<ResponseEntity<List<TileDTO>>> getAllTiles(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Tiles");
        return tileService
            .countAll()
            .zipWith(tileService.findAll(pageable).collectList())
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
     * {@code GET  /tiles/:id} : get the "id" tile.
     *
     * @param id the id of the tileDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tileDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/tiles/{id}")
    public Mono<ResponseEntity<TileDTO>> getTile(@PathVariable String id) {
        log.debug("REST request to get Tile : {}", id);
        Mono<TileDTO> tileDTO = tileService.findOne(id);
        return ResponseUtil.wrapOrNotFound(tileDTO);
    }

    /**
     * {@code DELETE  /tiles/:id} : delete the "id" tile.
     *
     * @param id the id of the tileDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/tiles/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteTile(@PathVariable String id) {
        log.debug("REST request to delete Tile : {}", id);
        return tileService
            .delete(id)
            .map(result ->
                ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build()
            );
    }

    /**
     * {@code SEARCH  /_search/tiles?query=:query} : search for the tile corresponding
     * to the query.
     *
     * @param query the query of the tile search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search/tiles")
    public Mono<ResponseEntity<Flux<TileDTO>>> searchTiles(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of Tiles for query {}", query);
        return tileService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page -> PaginationUtil.generatePaginationHttpHeaders(UriComponentsBuilder.fromHttpRequest(request), page))
            .map(headers -> ResponseEntity.ok().headers(headers).body(tileService.search(query, pageable)));
    }
}
