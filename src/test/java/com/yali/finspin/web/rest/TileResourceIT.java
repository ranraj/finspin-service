package com.yali.finspin.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.yali.finspin.IntegrationTest;
import com.yali.finspin.domain.Tile;
import com.yali.finspin.domain.enumeration.DisplayMode;
import com.yali.finspin.domain.enumeration.DisplaySize;
import com.yali.finspin.domain.enumeration.PositionMode;
import com.yali.finspin.repository.TileRepository;
import com.yali.finspin.repository.search.TileSearchRepository;
import com.yali.finspin.service.dto.TileDTO;
import com.yali.finspin.service.mapper.TileMapper;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Integration tests for the {@link TileResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TileResourceIT {

    private static final Long DEFAULT_POSITION_X = 1L;
    private static final Long UPDATED_POSITION_X = 2L;

    private static final Long DEFAULT_POSITION_Y = 1L;
    private static final Long UPDATED_POSITION_Y = 2L;

    private static final String DEFAULT_COLOR = "AAAAAAAAAA";
    private static final String UPDATED_COLOR = "BBBBBBBBBB";

    private static final PositionMode DEFAULT_POSITION_MODE = PositionMode.Fixed;
    private static final PositionMode UPDATED_POSITION_MODE = PositionMode.Floating;

    private static final Long DEFAULT_HEIGHT = 1L;
    private static final Long UPDATED_HEIGHT = 2L;

    private static final Long DEFAULT_WIDTH = 1L;
    private static final Long UPDATED_WIDTH = 2L;

    private static final DisplaySize DEFAULT_DISPLAY_SIZE = DisplaySize.X1;
    private static final DisplaySize UPDATED_DISPLAY_SIZE = DisplaySize.X2;

    private static final DisplayMode DEFAULT_DISPLAY_MODE = DisplayMode.Expand;
    private static final DisplayMode UPDATED_DISPLAY_MODE = DisplayMode.Less;

    private static final String ENTITY_API_URL = "/api/tiles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/tiles";

    @Autowired
    private TileRepository tileRepository;

    @Autowired
    private TileMapper tileMapper;

    /**
     * This repository is mocked in the com.yali.finspin.repository.search test package.
     *
     * @see com.yali.finspin.repository.search.TileSearchRepositoryMockConfiguration
     */
    @Autowired
    private TileSearchRepository mockTileSearchRepository;

    @Autowired
    private WebTestClient webTestClient;

    private Tile tile;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tile createEntity() {
        Tile tile = new Tile()
            .positionX(DEFAULT_POSITION_X)
            .positionY(DEFAULT_POSITION_Y)
            .color(DEFAULT_COLOR)
            .positionMode(DEFAULT_POSITION_MODE)
            .height(DEFAULT_HEIGHT)
            .width(DEFAULT_WIDTH)
            .displaySize(DEFAULT_DISPLAY_SIZE)
            .displayMode(DEFAULT_DISPLAY_MODE);
        return tile;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tile createUpdatedEntity() {
        Tile tile = new Tile()
            .positionX(UPDATED_POSITION_X)
            .positionY(UPDATED_POSITION_Y)
            .color(UPDATED_COLOR)
            .positionMode(UPDATED_POSITION_MODE)
            .height(UPDATED_HEIGHT)
            .width(UPDATED_WIDTH)
            .displaySize(UPDATED_DISPLAY_SIZE)
            .displayMode(UPDATED_DISPLAY_MODE);
        return tile;
    }

    @BeforeEach
    public void initTest() {
        tileRepository.deleteAll().block();
        tile = createEntity();
    }

    @Test
    void createTile() throws Exception {
        int databaseSizeBeforeCreate = tileRepository.findAll().collectList().block().size();
        // Configure the mock search repository
        when(mockTileSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Create the Tile
        TileDTO tileDTO = tileMapper.toDto(tile);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tileDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Tile in the database
        List<Tile> tileList = tileRepository.findAll().collectList().block();
        assertThat(tileList).hasSize(databaseSizeBeforeCreate + 1);
        Tile testTile = tileList.get(tileList.size() - 1);
        assertThat(testTile.getPositionX()).isEqualTo(DEFAULT_POSITION_X);
        assertThat(testTile.getPositionY()).isEqualTo(DEFAULT_POSITION_Y);
        assertThat(testTile.getColor()).isEqualTo(DEFAULT_COLOR);
        assertThat(testTile.getPositionMode()).isEqualTo(DEFAULT_POSITION_MODE);
        assertThat(testTile.getHeight()).isEqualTo(DEFAULT_HEIGHT);
        assertThat(testTile.getWidth()).isEqualTo(DEFAULT_WIDTH);
        assertThat(testTile.getDisplaySize()).isEqualTo(DEFAULT_DISPLAY_SIZE);
        assertThat(testTile.getDisplayMode()).isEqualTo(DEFAULT_DISPLAY_MODE);

        // Validate the Tile in Elasticsearch
        verify(mockTileSearchRepository, times(1)).save(testTile);
    }

    @Test
    void createTileWithExistingId() throws Exception {
        // Create the Tile with an existing ID
        tile.setId("existing_id");
        TileDTO tileDTO = tileMapper.toDto(tile);

        int databaseSizeBeforeCreate = tileRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tile in the database
        List<Tile> tileList = tileRepository.findAll().collectList().block();
        assertThat(tileList).hasSize(databaseSizeBeforeCreate);

        // Validate the Tile in Elasticsearch
        verify(mockTileSearchRepository, times(0)).save(tile);
    }

    @Test
    void getAllTiles() {
        // Initialize the database
        tileRepository.save(tile).block();

        // Get all the tileList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(tile.getId()))
            .jsonPath("$.[*].positionX")
            .value(hasItem(DEFAULT_POSITION_X.intValue()))
            .jsonPath("$.[*].positionY")
            .value(hasItem(DEFAULT_POSITION_Y.intValue()))
            .jsonPath("$.[*].color")
            .value(hasItem(DEFAULT_COLOR))
            .jsonPath("$.[*].positionMode")
            .value(hasItem(DEFAULT_POSITION_MODE.toString()))
            .jsonPath("$.[*].height")
            .value(hasItem(DEFAULT_HEIGHT.intValue()))
            .jsonPath("$.[*].width")
            .value(hasItem(DEFAULT_WIDTH.intValue()))
            .jsonPath("$.[*].displaySize")
            .value(hasItem(DEFAULT_DISPLAY_SIZE.toString()))
            .jsonPath("$.[*].displayMode")
            .value(hasItem(DEFAULT_DISPLAY_MODE.toString()));
    }

    @Test
    void getTile() {
        // Initialize the database
        tileRepository.save(tile).block();

        // Get the tile
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, tile.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(tile.getId()))
            .jsonPath("$.positionX")
            .value(is(DEFAULT_POSITION_X.intValue()))
            .jsonPath("$.positionY")
            .value(is(DEFAULT_POSITION_Y.intValue()))
            .jsonPath("$.color")
            .value(is(DEFAULT_COLOR))
            .jsonPath("$.positionMode")
            .value(is(DEFAULT_POSITION_MODE.toString()))
            .jsonPath("$.height")
            .value(is(DEFAULT_HEIGHT.intValue()))
            .jsonPath("$.width")
            .value(is(DEFAULT_WIDTH.intValue()))
            .jsonPath("$.displaySize")
            .value(is(DEFAULT_DISPLAY_SIZE.toString()))
            .jsonPath("$.displayMode")
            .value(is(DEFAULT_DISPLAY_MODE.toString()));
    }

    @Test
    void getNonExistingTile() {
        // Get the tile
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewTile() throws Exception {
        // Configure the mock search repository
        when(mockTileSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        tileRepository.save(tile).block();

        int databaseSizeBeforeUpdate = tileRepository.findAll().collectList().block().size();

        // Update the tile
        Tile updatedTile = tileRepository.findById(tile.getId()).block();
        updatedTile
            .positionX(UPDATED_POSITION_X)
            .positionY(UPDATED_POSITION_Y)
            .color(UPDATED_COLOR)
            .positionMode(UPDATED_POSITION_MODE)
            .height(UPDATED_HEIGHT)
            .width(UPDATED_WIDTH)
            .displaySize(UPDATED_DISPLAY_SIZE)
            .displayMode(UPDATED_DISPLAY_MODE);
        TileDTO tileDTO = tileMapper.toDto(updatedTile);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, tileDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tileDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Tile in the database
        List<Tile> tileList = tileRepository.findAll().collectList().block();
        assertThat(tileList).hasSize(databaseSizeBeforeUpdate);
        Tile testTile = tileList.get(tileList.size() - 1);
        assertThat(testTile.getPositionX()).isEqualTo(UPDATED_POSITION_X);
        assertThat(testTile.getPositionY()).isEqualTo(UPDATED_POSITION_Y);
        assertThat(testTile.getColor()).isEqualTo(UPDATED_COLOR);
        assertThat(testTile.getPositionMode()).isEqualTo(UPDATED_POSITION_MODE);
        assertThat(testTile.getHeight()).isEqualTo(UPDATED_HEIGHT);
        assertThat(testTile.getWidth()).isEqualTo(UPDATED_WIDTH);
        assertThat(testTile.getDisplaySize()).isEqualTo(UPDATED_DISPLAY_SIZE);
        assertThat(testTile.getDisplayMode()).isEqualTo(UPDATED_DISPLAY_MODE);

        // Validate the Tile in Elasticsearch
        verify(mockTileSearchRepository).save(testTile);
    }

    @Test
    void putNonExistingTile() throws Exception {
        int databaseSizeBeforeUpdate = tileRepository.findAll().collectList().block().size();
        tile.setId(UUID.randomUUID().toString());

        // Create the Tile
        TileDTO tileDTO = tileMapper.toDto(tile);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, tileDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tile in the database
        List<Tile> tileList = tileRepository.findAll().collectList().block();
        assertThat(tileList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Tile in Elasticsearch
        verify(mockTileSearchRepository, times(0)).save(tile);
    }

    @Test
    void putWithIdMismatchTile() throws Exception {
        int databaseSizeBeforeUpdate = tileRepository.findAll().collectList().block().size();
        tile.setId(UUID.randomUUID().toString());

        // Create the Tile
        TileDTO tileDTO = tileMapper.toDto(tile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tile in the database
        List<Tile> tileList = tileRepository.findAll().collectList().block();
        assertThat(tileList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Tile in Elasticsearch
        verify(mockTileSearchRepository, times(0)).save(tile);
    }

    @Test
    void putWithMissingIdPathParamTile() throws Exception {
        int databaseSizeBeforeUpdate = tileRepository.findAll().collectList().block().size();
        tile.setId(UUID.randomUUID().toString());

        // Create the Tile
        TileDTO tileDTO = tileMapper.toDto(tile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tileDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Tile in the database
        List<Tile> tileList = tileRepository.findAll().collectList().block();
        assertThat(tileList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Tile in Elasticsearch
        verify(mockTileSearchRepository, times(0)).save(tile);
    }

    @Test
    void partialUpdateTileWithPatch() throws Exception {
        // Initialize the database
        tileRepository.save(tile).block();

        int databaseSizeBeforeUpdate = tileRepository.findAll().collectList().block().size();

        // Update the tile using partial update
        Tile partialUpdatedTile = new Tile();
        partialUpdatedTile.setId(tile.getId());

        partialUpdatedTile
            .color(UPDATED_COLOR)
            .positionMode(UPDATED_POSITION_MODE)
            .displaySize(UPDATED_DISPLAY_SIZE)
            .displayMode(UPDATED_DISPLAY_MODE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTile.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTile))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Tile in the database
        List<Tile> tileList = tileRepository.findAll().collectList().block();
        assertThat(tileList).hasSize(databaseSizeBeforeUpdate);
        Tile testTile = tileList.get(tileList.size() - 1);
        assertThat(testTile.getPositionX()).isEqualTo(DEFAULT_POSITION_X);
        assertThat(testTile.getPositionY()).isEqualTo(DEFAULT_POSITION_Y);
        assertThat(testTile.getColor()).isEqualTo(UPDATED_COLOR);
        assertThat(testTile.getPositionMode()).isEqualTo(UPDATED_POSITION_MODE);
        assertThat(testTile.getHeight()).isEqualTo(DEFAULT_HEIGHT);
        assertThat(testTile.getWidth()).isEqualTo(DEFAULT_WIDTH);
        assertThat(testTile.getDisplaySize()).isEqualTo(UPDATED_DISPLAY_SIZE);
        assertThat(testTile.getDisplayMode()).isEqualTo(UPDATED_DISPLAY_MODE);
    }

    @Test
    void fullUpdateTileWithPatch() throws Exception {
        // Initialize the database
        tileRepository.save(tile).block();

        int databaseSizeBeforeUpdate = tileRepository.findAll().collectList().block().size();

        // Update the tile using partial update
        Tile partialUpdatedTile = new Tile();
        partialUpdatedTile.setId(tile.getId());

        partialUpdatedTile
            .positionX(UPDATED_POSITION_X)
            .positionY(UPDATED_POSITION_Y)
            .color(UPDATED_COLOR)
            .positionMode(UPDATED_POSITION_MODE)
            .height(UPDATED_HEIGHT)
            .width(UPDATED_WIDTH)
            .displaySize(UPDATED_DISPLAY_SIZE)
            .displayMode(UPDATED_DISPLAY_MODE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTile.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTile))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Tile in the database
        List<Tile> tileList = tileRepository.findAll().collectList().block();
        assertThat(tileList).hasSize(databaseSizeBeforeUpdate);
        Tile testTile = tileList.get(tileList.size() - 1);
        assertThat(testTile.getPositionX()).isEqualTo(UPDATED_POSITION_X);
        assertThat(testTile.getPositionY()).isEqualTo(UPDATED_POSITION_Y);
        assertThat(testTile.getColor()).isEqualTo(UPDATED_COLOR);
        assertThat(testTile.getPositionMode()).isEqualTo(UPDATED_POSITION_MODE);
        assertThat(testTile.getHeight()).isEqualTo(UPDATED_HEIGHT);
        assertThat(testTile.getWidth()).isEqualTo(UPDATED_WIDTH);
        assertThat(testTile.getDisplaySize()).isEqualTo(UPDATED_DISPLAY_SIZE);
        assertThat(testTile.getDisplayMode()).isEqualTo(UPDATED_DISPLAY_MODE);
    }

    @Test
    void patchNonExistingTile() throws Exception {
        int databaseSizeBeforeUpdate = tileRepository.findAll().collectList().block().size();
        tile.setId(UUID.randomUUID().toString());

        // Create the Tile
        TileDTO tileDTO = tileMapper.toDto(tile);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, tileDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(tileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tile in the database
        List<Tile> tileList = tileRepository.findAll().collectList().block();
        assertThat(tileList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Tile in Elasticsearch
        verify(mockTileSearchRepository, times(0)).save(tile);
    }

    @Test
    void patchWithIdMismatchTile() throws Exception {
        int databaseSizeBeforeUpdate = tileRepository.findAll().collectList().block().size();
        tile.setId(UUID.randomUUID().toString());

        // Create the Tile
        TileDTO tileDTO = tileMapper.toDto(tile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(tileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tile in the database
        List<Tile> tileList = tileRepository.findAll().collectList().block();
        assertThat(tileList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Tile in Elasticsearch
        verify(mockTileSearchRepository, times(0)).save(tile);
    }

    @Test
    void patchWithMissingIdPathParamTile() throws Exception {
        int databaseSizeBeforeUpdate = tileRepository.findAll().collectList().block().size();
        tile.setId(UUID.randomUUID().toString());

        // Create the Tile
        TileDTO tileDTO = tileMapper.toDto(tile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(tileDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Tile in the database
        List<Tile> tileList = tileRepository.findAll().collectList().block();
        assertThat(tileList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Tile in Elasticsearch
        verify(mockTileSearchRepository, times(0)).save(tile);
    }

    @Test
    void deleteTile() {
        // Configure the mock search repository
        when(mockTileSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockTileSearchRepository.deleteById(anyString())).thenReturn(Mono.empty());
        // Initialize the database
        tileRepository.save(tile).block();

        int databaseSizeBeforeDelete = tileRepository.findAll().collectList().block().size();

        // Delete the tile
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, tile.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Tile> tileList = tileRepository.findAll().collectList().block();
        assertThat(tileList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Tile in Elasticsearch
        verify(mockTileSearchRepository, times(1)).deleteById(tile.getId());
    }

    @Test
    void searchTile() {
        // Configure the mock search repository
        when(mockTileSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockTileSearchRepository.count()).thenReturn(Mono.just(1L));
        // Initialize the database
        tileRepository.save(tile).block();
        when(mockTileSearchRepository.search("id:" + tile.getId(), PageRequest.of(0, 20))).thenReturn(Flux.just(tile));

        // Search the tile
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + tile.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(tile.getId()))
            .jsonPath("$.[*].positionX")
            .value(hasItem(DEFAULT_POSITION_X.intValue()))
            .jsonPath("$.[*].positionY")
            .value(hasItem(DEFAULT_POSITION_Y.intValue()))
            .jsonPath("$.[*].color")
            .value(hasItem(DEFAULT_COLOR))
            .jsonPath("$.[*].positionMode")
            .value(hasItem(DEFAULT_POSITION_MODE.toString()))
            .jsonPath("$.[*].height")
            .value(hasItem(DEFAULT_HEIGHT.intValue()))
            .jsonPath("$.[*].width")
            .value(hasItem(DEFAULT_WIDTH.intValue()))
            .jsonPath("$.[*].displaySize")
            .value(hasItem(DEFAULT_DISPLAY_SIZE.toString()))
            .jsonPath("$.[*].displayMode")
            .value(hasItem(DEFAULT_DISPLAY_MODE.toString()));
    }
}
