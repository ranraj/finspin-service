package com.yali.finspin.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.yali.finspin.IntegrationTest;
import com.yali.finspin.domain.Board;
import com.yali.finspin.repository.BoardRepository;
import com.yali.finspin.repository.search.BoardSearchRepository;
import com.yali.finspin.service.dto.BoardDTO;
import com.yali.finspin.service.mapper.BoardMapper;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link BoardResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class BoardResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_UID = "AAAAAAAAAA";
    private static final String UPDATED_UID = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATE_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATE_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/boards";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/boards";

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private BoardMapper boardMapper;

    /**
     * This repository is mocked in the com.yali.finspin.repository.search test package.
     *
     * @see com.yali.finspin.repository.search.BoardSearchRepositoryMockConfiguration
     */
    @Autowired
    private BoardSearchRepository mockBoardSearchRepository;

    @Autowired
    private WebTestClient webTestClient;

    private Board board;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Board createEntity() {
        Board board = new Board().title(DEFAULT_TITLE).uid(DEFAULT_UID).createdDate(DEFAULT_CREATED_DATE).updateDate(DEFAULT_UPDATE_DATE);
        return board;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Board createUpdatedEntity() {
        Board board = new Board().title(UPDATED_TITLE).uid(UPDATED_UID).createdDate(UPDATED_CREATED_DATE).updateDate(UPDATED_UPDATE_DATE);
        return board;
    }

    @BeforeEach
    public void initTest() {
        boardRepository.deleteAll().block();
        board = createEntity();
    }

    @Test
    void createBoard() throws Exception {
        int databaseSizeBeforeCreate = boardRepository.findAll().collectList().block().size();
        // Configure the mock search repository
        when(mockBoardSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Create the Board
        BoardDTO boardDTO = boardMapper.toDto(board);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(boardDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Board in the database
        List<Board> boardList = boardRepository.findAll().collectList().block();
        assertThat(boardList).hasSize(databaseSizeBeforeCreate + 1);
        Board testBoard = boardList.get(boardList.size() - 1);
        assertThat(testBoard.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testBoard.getUid()).isEqualTo(DEFAULT_UID);
        assertThat(testBoard.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testBoard.getUpdateDate()).isEqualTo(DEFAULT_UPDATE_DATE);

        // Validate the Board in Elasticsearch
        verify(mockBoardSearchRepository, times(1)).save(testBoard);
    }

    @Test
    void createBoardWithExistingId() throws Exception {
        // Create the Board with an existing ID
        board.setId("existing_id");
        BoardDTO boardDTO = boardMapper.toDto(board);

        int databaseSizeBeforeCreate = boardRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(boardDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Board in the database
        List<Board> boardList = boardRepository.findAll().collectList().block();
        assertThat(boardList).hasSize(databaseSizeBeforeCreate);

        // Validate the Board in Elasticsearch
        verify(mockBoardSearchRepository, times(0)).save(board);
    }

    @Test
    void getAllBoards() {
        // Initialize the database
        boardRepository.save(board).block();

        // Get all the boardList
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
            .value(hasItem(board.getId()))
            .jsonPath("$.[*].title")
            .value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].uid")
            .value(hasItem(DEFAULT_UID))
            .jsonPath("$.[*].createdDate")
            .value(hasItem(DEFAULT_CREATED_DATE.toString()))
            .jsonPath("$.[*].updateDate")
            .value(hasItem(DEFAULT_UPDATE_DATE.toString()));
    }

    @Test
    void getBoard() {
        // Initialize the database
        boardRepository.save(board).block();

        // Get the board
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, board.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(board.getId()))
            .jsonPath("$.title")
            .value(is(DEFAULT_TITLE))
            .jsonPath("$.uid")
            .value(is(DEFAULT_UID))
            .jsonPath("$.createdDate")
            .value(is(DEFAULT_CREATED_DATE.toString()))
            .jsonPath("$.updateDate")
            .value(is(DEFAULT_UPDATE_DATE.toString()));
    }

    @Test
    void getNonExistingBoard() {
        // Get the board
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewBoard() throws Exception {
        // Configure the mock search repository
        when(mockBoardSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        boardRepository.save(board).block();

        int databaseSizeBeforeUpdate = boardRepository.findAll().collectList().block().size();

        // Update the board
        Board updatedBoard = boardRepository.findById(board.getId()).block();
        updatedBoard.title(UPDATED_TITLE).uid(UPDATED_UID).createdDate(UPDATED_CREATED_DATE).updateDate(UPDATED_UPDATE_DATE);
        BoardDTO boardDTO = boardMapper.toDto(updatedBoard);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, boardDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(boardDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Board in the database
        List<Board> boardList = boardRepository.findAll().collectList().block();
        assertThat(boardList).hasSize(databaseSizeBeforeUpdate);
        Board testBoard = boardList.get(boardList.size() - 1);
        assertThat(testBoard.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testBoard.getUid()).isEqualTo(UPDATED_UID);
        assertThat(testBoard.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testBoard.getUpdateDate()).isEqualTo(UPDATED_UPDATE_DATE);

        // Validate the Board in Elasticsearch
        verify(mockBoardSearchRepository).save(testBoard);
    }

    @Test
    void putNonExistingBoard() throws Exception {
        int databaseSizeBeforeUpdate = boardRepository.findAll().collectList().block().size();
        board.setId(UUID.randomUUID().toString());

        // Create the Board
        BoardDTO boardDTO = boardMapper.toDto(board);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, boardDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(boardDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Board in the database
        List<Board> boardList = boardRepository.findAll().collectList().block();
        assertThat(boardList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Board in Elasticsearch
        verify(mockBoardSearchRepository, times(0)).save(board);
    }

    @Test
    void putWithIdMismatchBoard() throws Exception {
        int databaseSizeBeforeUpdate = boardRepository.findAll().collectList().block().size();
        board.setId(UUID.randomUUID().toString());

        // Create the Board
        BoardDTO boardDTO = boardMapper.toDto(board);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(boardDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Board in the database
        List<Board> boardList = boardRepository.findAll().collectList().block();
        assertThat(boardList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Board in Elasticsearch
        verify(mockBoardSearchRepository, times(0)).save(board);
    }

    @Test
    void putWithMissingIdPathParamBoard() throws Exception {
        int databaseSizeBeforeUpdate = boardRepository.findAll().collectList().block().size();
        board.setId(UUID.randomUUID().toString());

        // Create the Board
        BoardDTO boardDTO = boardMapper.toDto(board);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(boardDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Board in the database
        List<Board> boardList = boardRepository.findAll().collectList().block();
        assertThat(boardList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Board in Elasticsearch
        verify(mockBoardSearchRepository, times(0)).save(board);
    }

    @Test
    void partialUpdateBoardWithPatch() throws Exception {
        // Initialize the database
        boardRepository.save(board).block();

        int databaseSizeBeforeUpdate = boardRepository.findAll().collectList().block().size();

        // Update the board using partial update
        Board partialUpdatedBoard = new Board();
        partialUpdatedBoard.setId(board.getId());

        partialUpdatedBoard.title(UPDATED_TITLE).createdDate(UPDATED_CREATED_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedBoard.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedBoard))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Board in the database
        List<Board> boardList = boardRepository.findAll().collectList().block();
        assertThat(boardList).hasSize(databaseSizeBeforeUpdate);
        Board testBoard = boardList.get(boardList.size() - 1);
        assertThat(testBoard.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testBoard.getUid()).isEqualTo(DEFAULT_UID);
        assertThat(testBoard.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testBoard.getUpdateDate()).isEqualTo(DEFAULT_UPDATE_DATE);
    }

    @Test
    void fullUpdateBoardWithPatch() throws Exception {
        // Initialize the database
        boardRepository.save(board).block();

        int databaseSizeBeforeUpdate = boardRepository.findAll().collectList().block().size();

        // Update the board using partial update
        Board partialUpdatedBoard = new Board();
        partialUpdatedBoard.setId(board.getId());

        partialUpdatedBoard.title(UPDATED_TITLE).uid(UPDATED_UID).createdDate(UPDATED_CREATED_DATE).updateDate(UPDATED_UPDATE_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedBoard.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedBoard))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Board in the database
        List<Board> boardList = boardRepository.findAll().collectList().block();
        assertThat(boardList).hasSize(databaseSizeBeforeUpdate);
        Board testBoard = boardList.get(boardList.size() - 1);
        assertThat(testBoard.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testBoard.getUid()).isEqualTo(UPDATED_UID);
        assertThat(testBoard.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testBoard.getUpdateDate()).isEqualTo(UPDATED_UPDATE_DATE);
    }

    @Test
    void patchNonExistingBoard() throws Exception {
        int databaseSizeBeforeUpdate = boardRepository.findAll().collectList().block().size();
        board.setId(UUID.randomUUID().toString());

        // Create the Board
        BoardDTO boardDTO = boardMapper.toDto(board);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, boardDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(boardDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Board in the database
        List<Board> boardList = boardRepository.findAll().collectList().block();
        assertThat(boardList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Board in Elasticsearch
        verify(mockBoardSearchRepository, times(0)).save(board);
    }

    @Test
    void patchWithIdMismatchBoard() throws Exception {
        int databaseSizeBeforeUpdate = boardRepository.findAll().collectList().block().size();
        board.setId(UUID.randomUUID().toString());

        // Create the Board
        BoardDTO boardDTO = boardMapper.toDto(board);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(boardDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Board in the database
        List<Board> boardList = boardRepository.findAll().collectList().block();
        assertThat(boardList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Board in Elasticsearch
        verify(mockBoardSearchRepository, times(0)).save(board);
    }

    @Test
    void patchWithMissingIdPathParamBoard() throws Exception {
        int databaseSizeBeforeUpdate = boardRepository.findAll().collectList().block().size();
        board.setId(UUID.randomUUID().toString());

        // Create the Board
        BoardDTO boardDTO = boardMapper.toDto(board);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(boardDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Board in the database
        List<Board> boardList = boardRepository.findAll().collectList().block();
        assertThat(boardList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Board in Elasticsearch
        verify(mockBoardSearchRepository, times(0)).save(board);
    }

    @Test
    void deleteBoard() {
        // Configure the mock search repository
        when(mockBoardSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockBoardSearchRepository.deleteById(anyString())).thenReturn(Mono.empty());
        // Initialize the database
        boardRepository.save(board).block();

        int databaseSizeBeforeDelete = boardRepository.findAll().collectList().block().size();

        // Delete the board
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, board.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Board> boardList = boardRepository.findAll().collectList().block();
        assertThat(boardList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Board in Elasticsearch
        verify(mockBoardSearchRepository, times(1)).deleteById(board.getId());
    }

    @Test
    void searchBoard() {
        // Configure the mock search repository
        when(mockBoardSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockBoardSearchRepository.count()).thenReturn(Mono.just(1L));
        // Initialize the database
        boardRepository.save(board).block();
        when(mockBoardSearchRepository.search("id:" + board.getId(), PageRequest.of(0, 20))).thenReturn(Flux.just(board));

        // Search the board
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + board.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(board.getId()))
            .jsonPath("$.[*].title")
            .value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].uid")
            .value(hasItem(DEFAULT_UID))
            .jsonPath("$.[*].createdDate")
            .value(hasItem(DEFAULT_CREATED_DATE.toString()))
            .jsonPath("$.[*].updateDate")
            .value(hasItem(DEFAULT_UPDATE_DATE.toString()));
    }
}
