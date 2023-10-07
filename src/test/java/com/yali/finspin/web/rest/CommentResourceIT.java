package com.yali.finspin.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.yali.finspin.IntegrationTest;
import com.yali.finspin.domain.Comment;
import com.yali.finspin.repository.CommentRepository;
import com.yali.finspin.repository.search.CommentSearchRepository;
import com.yali.finspin.service.dto.CommentDTO;
import com.yali.finspin.service.mapper.CommentMapper;
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
 * Integration tests for the {@link CommentResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class CommentResourceIT {

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final Boolean DEFAULT_DISABLED = false;
    private static final Boolean UPDATED_DISABLED = true;

    private static final Long DEFAULT_UP_VOTE = 1L;
    private static final Long UPDATED_UP_VOTE = 2L;

    private static final Long DEFAULT_DOWN_VOTE = 1L;
    private static final Long UPDATED_DOWN_VOTE = 2L;

    private static final String DEFAULT_PERM_LINK = "AAAAAAAAAA";
    private static final String UPDATED_PERM_LINK = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/comments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/comments";

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentMapper commentMapper;

    /**
     * This repository is mocked in the com.yali.finspin.repository.search test package.
     *
     * @see com.yali.finspin.repository.search.CommentSearchRepositoryMockConfiguration
     */
    @Autowired
    private CommentSearchRepository mockCommentSearchRepository;

    @Autowired
    private WebTestClient webTestClient;

    private Comment comment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Comment createEntity() {
        Comment comment = new Comment()
            .createdDate(DEFAULT_CREATED_DATE)
            .updatedDate(DEFAULT_UPDATED_DATE)
            .content(DEFAULT_CONTENT)
            .disabled(DEFAULT_DISABLED)
            .upVote(DEFAULT_UP_VOTE)
            .downVote(DEFAULT_DOWN_VOTE)
            .permLink(DEFAULT_PERM_LINK);
        return comment;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Comment createUpdatedEntity() {
        Comment comment = new Comment()
            .createdDate(UPDATED_CREATED_DATE)
            .updatedDate(UPDATED_UPDATED_DATE)
            .content(UPDATED_CONTENT)
            .disabled(UPDATED_DISABLED)
            .upVote(UPDATED_UP_VOTE)
            .downVote(UPDATED_DOWN_VOTE)
            .permLink(UPDATED_PERM_LINK);
        return comment;
    }

    @BeforeEach
    public void initTest() {
        commentRepository.deleteAll().block();
        comment = createEntity();
    }

    @Test
    void createComment() throws Exception {
        int databaseSizeBeforeCreate = commentRepository.findAll().collectList().block().size();
        // Configure the mock search repository
        when(mockCommentSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Create the Comment
        CommentDTO commentDTO = commentMapper.toDto(comment);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(commentDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Comment in the database
        List<Comment> commentList = commentRepository.findAll().collectList().block();
        assertThat(commentList).hasSize(databaseSizeBeforeCreate + 1);
        Comment testComment = commentList.get(commentList.size() - 1);
        assertThat(testComment.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testComment.getUpdatedDate()).isEqualTo(DEFAULT_UPDATED_DATE);
        assertThat(testComment.getContent()).isEqualTo(DEFAULT_CONTENT);
        assertThat(testComment.getDisabled()).isEqualTo(DEFAULT_DISABLED);
        assertThat(testComment.getUpVote()).isEqualTo(DEFAULT_UP_VOTE);
        assertThat(testComment.getDownVote()).isEqualTo(DEFAULT_DOWN_VOTE);
        assertThat(testComment.getPermLink()).isEqualTo(DEFAULT_PERM_LINK);

        // Validate the Comment in Elasticsearch
        verify(mockCommentSearchRepository, times(1)).save(testComment);
    }

    @Test
    void createCommentWithExistingId() throws Exception {
        // Create the Comment with an existing ID
        comment.setId("existing_id");
        CommentDTO commentDTO = commentMapper.toDto(comment);

        int databaseSizeBeforeCreate = commentRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(commentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Comment in the database
        List<Comment> commentList = commentRepository.findAll().collectList().block();
        assertThat(commentList).hasSize(databaseSizeBeforeCreate);

        // Validate the Comment in Elasticsearch
        verify(mockCommentSearchRepository, times(0)).save(comment);
    }

    @Test
    void getAllComments() {
        // Initialize the database
        commentRepository.save(comment).block();

        // Get all the commentList
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
            .value(hasItem(comment.getId()))
            .jsonPath("$.[*].createdDate")
            .value(hasItem(DEFAULT_CREATED_DATE.toString()))
            .jsonPath("$.[*].updatedDate")
            .value(hasItem(DEFAULT_UPDATED_DATE.toString()))
            .jsonPath("$.[*].content")
            .value(hasItem(DEFAULT_CONTENT))
            .jsonPath("$.[*].disabled")
            .value(hasItem(DEFAULT_DISABLED.booleanValue()))
            .jsonPath("$.[*].upVote")
            .value(hasItem(DEFAULT_UP_VOTE.intValue()))
            .jsonPath("$.[*].downVote")
            .value(hasItem(DEFAULT_DOWN_VOTE.intValue()))
            .jsonPath("$.[*].permLink")
            .value(hasItem(DEFAULT_PERM_LINK));
    }

    @Test
    void getComment() {
        // Initialize the database
        commentRepository.save(comment).block();

        // Get the comment
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, comment.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(comment.getId()))
            .jsonPath("$.createdDate")
            .value(is(DEFAULT_CREATED_DATE.toString()))
            .jsonPath("$.updatedDate")
            .value(is(DEFAULT_UPDATED_DATE.toString()))
            .jsonPath("$.content")
            .value(is(DEFAULT_CONTENT))
            .jsonPath("$.disabled")
            .value(is(DEFAULT_DISABLED.booleanValue()))
            .jsonPath("$.upVote")
            .value(is(DEFAULT_UP_VOTE.intValue()))
            .jsonPath("$.downVote")
            .value(is(DEFAULT_DOWN_VOTE.intValue()))
            .jsonPath("$.permLink")
            .value(is(DEFAULT_PERM_LINK));
    }

    @Test
    void getNonExistingComment() {
        // Get the comment
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewComment() throws Exception {
        // Configure the mock search repository
        when(mockCommentSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        commentRepository.save(comment).block();

        int databaseSizeBeforeUpdate = commentRepository.findAll().collectList().block().size();

        // Update the comment
        Comment updatedComment = commentRepository.findById(comment.getId()).block();
        updatedComment
            .createdDate(UPDATED_CREATED_DATE)
            .updatedDate(UPDATED_UPDATED_DATE)
            .content(UPDATED_CONTENT)
            .disabled(UPDATED_DISABLED)
            .upVote(UPDATED_UP_VOTE)
            .downVote(UPDATED_DOWN_VOTE)
            .permLink(UPDATED_PERM_LINK);
        CommentDTO commentDTO = commentMapper.toDto(updatedComment);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, commentDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(commentDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Comment in the database
        List<Comment> commentList = commentRepository.findAll().collectList().block();
        assertThat(commentList).hasSize(databaseSizeBeforeUpdate);
        Comment testComment = commentList.get(commentList.size() - 1);
        assertThat(testComment.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testComment.getUpdatedDate()).isEqualTo(UPDATED_UPDATED_DATE);
        assertThat(testComment.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testComment.getDisabled()).isEqualTo(UPDATED_DISABLED);
        assertThat(testComment.getUpVote()).isEqualTo(UPDATED_UP_VOTE);
        assertThat(testComment.getDownVote()).isEqualTo(UPDATED_DOWN_VOTE);
        assertThat(testComment.getPermLink()).isEqualTo(UPDATED_PERM_LINK);

        // Validate the Comment in Elasticsearch
        verify(mockCommentSearchRepository).save(testComment);
    }

    @Test
    void putNonExistingComment() throws Exception {
        int databaseSizeBeforeUpdate = commentRepository.findAll().collectList().block().size();
        comment.setId(UUID.randomUUID().toString());

        // Create the Comment
        CommentDTO commentDTO = commentMapper.toDto(comment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, commentDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(commentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Comment in the database
        List<Comment> commentList = commentRepository.findAll().collectList().block();
        assertThat(commentList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Comment in Elasticsearch
        verify(mockCommentSearchRepository, times(0)).save(comment);
    }

    @Test
    void putWithIdMismatchComment() throws Exception {
        int databaseSizeBeforeUpdate = commentRepository.findAll().collectList().block().size();
        comment.setId(UUID.randomUUID().toString());

        // Create the Comment
        CommentDTO commentDTO = commentMapper.toDto(comment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(commentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Comment in the database
        List<Comment> commentList = commentRepository.findAll().collectList().block();
        assertThat(commentList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Comment in Elasticsearch
        verify(mockCommentSearchRepository, times(0)).save(comment);
    }

    @Test
    void putWithMissingIdPathParamComment() throws Exception {
        int databaseSizeBeforeUpdate = commentRepository.findAll().collectList().block().size();
        comment.setId(UUID.randomUUID().toString());

        // Create the Comment
        CommentDTO commentDTO = commentMapper.toDto(comment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(commentDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Comment in the database
        List<Comment> commentList = commentRepository.findAll().collectList().block();
        assertThat(commentList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Comment in Elasticsearch
        verify(mockCommentSearchRepository, times(0)).save(comment);
    }

    @Test
    void partialUpdateCommentWithPatch() throws Exception {
        // Initialize the database
        commentRepository.save(comment).block();

        int databaseSizeBeforeUpdate = commentRepository.findAll().collectList().block().size();

        // Update the comment using partial update
        Comment partialUpdatedComment = new Comment();
        partialUpdatedComment.setId(comment.getId());

        partialUpdatedComment
            .createdDate(UPDATED_CREATED_DATE)
            .content(UPDATED_CONTENT)
            .disabled(UPDATED_DISABLED)
            .upVote(UPDATED_UP_VOTE)
            .downVote(UPDATED_DOWN_VOTE)
            .permLink(UPDATED_PERM_LINK);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedComment.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedComment))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Comment in the database
        List<Comment> commentList = commentRepository.findAll().collectList().block();
        assertThat(commentList).hasSize(databaseSizeBeforeUpdate);
        Comment testComment = commentList.get(commentList.size() - 1);
        assertThat(testComment.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testComment.getUpdatedDate()).isEqualTo(DEFAULT_UPDATED_DATE);
        assertThat(testComment.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testComment.getDisabled()).isEqualTo(UPDATED_DISABLED);
        assertThat(testComment.getUpVote()).isEqualTo(UPDATED_UP_VOTE);
        assertThat(testComment.getDownVote()).isEqualTo(UPDATED_DOWN_VOTE);
        assertThat(testComment.getPermLink()).isEqualTo(UPDATED_PERM_LINK);
    }

    @Test
    void fullUpdateCommentWithPatch() throws Exception {
        // Initialize the database
        commentRepository.save(comment).block();

        int databaseSizeBeforeUpdate = commentRepository.findAll().collectList().block().size();

        // Update the comment using partial update
        Comment partialUpdatedComment = new Comment();
        partialUpdatedComment.setId(comment.getId());

        partialUpdatedComment
            .createdDate(UPDATED_CREATED_DATE)
            .updatedDate(UPDATED_UPDATED_DATE)
            .content(UPDATED_CONTENT)
            .disabled(UPDATED_DISABLED)
            .upVote(UPDATED_UP_VOTE)
            .downVote(UPDATED_DOWN_VOTE)
            .permLink(UPDATED_PERM_LINK);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedComment.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedComment))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Comment in the database
        List<Comment> commentList = commentRepository.findAll().collectList().block();
        assertThat(commentList).hasSize(databaseSizeBeforeUpdate);
        Comment testComment = commentList.get(commentList.size() - 1);
        assertThat(testComment.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testComment.getUpdatedDate()).isEqualTo(UPDATED_UPDATED_DATE);
        assertThat(testComment.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testComment.getDisabled()).isEqualTo(UPDATED_DISABLED);
        assertThat(testComment.getUpVote()).isEqualTo(UPDATED_UP_VOTE);
        assertThat(testComment.getDownVote()).isEqualTo(UPDATED_DOWN_VOTE);
        assertThat(testComment.getPermLink()).isEqualTo(UPDATED_PERM_LINK);
    }

    @Test
    void patchNonExistingComment() throws Exception {
        int databaseSizeBeforeUpdate = commentRepository.findAll().collectList().block().size();
        comment.setId(UUID.randomUUID().toString());

        // Create the Comment
        CommentDTO commentDTO = commentMapper.toDto(comment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, commentDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(commentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Comment in the database
        List<Comment> commentList = commentRepository.findAll().collectList().block();
        assertThat(commentList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Comment in Elasticsearch
        verify(mockCommentSearchRepository, times(0)).save(comment);
    }

    @Test
    void patchWithIdMismatchComment() throws Exception {
        int databaseSizeBeforeUpdate = commentRepository.findAll().collectList().block().size();
        comment.setId(UUID.randomUUID().toString());

        // Create the Comment
        CommentDTO commentDTO = commentMapper.toDto(comment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(commentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Comment in the database
        List<Comment> commentList = commentRepository.findAll().collectList().block();
        assertThat(commentList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Comment in Elasticsearch
        verify(mockCommentSearchRepository, times(0)).save(comment);
    }

    @Test
    void patchWithMissingIdPathParamComment() throws Exception {
        int databaseSizeBeforeUpdate = commentRepository.findAll().collectList().block().size();
        comment.setId(UUID.randomUUID().toString());

        // Create the Comment
        CommentDTO commentDTO = commentMapper.toDto(comment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(commentDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Comment in the database
        List<Comment> commentList = commentRepository.findAll().collectList().block();
        assertThat(commentList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Comment in Elasticsearch
        verify(mockCommentSearchRepository, times(0)).save(comment);
    }

    @Test
    void deleteComment() {
        // Configure the mock search repository
        when(mockCommentSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockCommentSearchRepository.deleteById(anyString())).thenReturn(Mono.empty());
        // Initialize the database
        commentRepository.save(comment).block();

        int databaseSizeBeforeDelete = commentRepository.findAll().collectList().block().size();

        // Delete the comment
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, comment.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Comment> commentList = commentRepository.findAll().collectList().block();
        assertThat(commentList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Comment in Elasticsearch
        verify(mockCommentSearchRepository, times(1)).deleteById(comment.getId());
    }

    @Test
    void searchComment() {
        // Configure the mock search repository
        when(mockCommentSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockCommentSearchRepository.count()).thenReturn(Mono.just(1L));
        // Initialize the database
        commentRepository.save(comment).block();
        when(mockCommentSearchRepository.search("id:" + comment.getId(), PageRequest.of(0, 20))).thenReturn(Flux.just(comment));

        // Search the comment
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + comment.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(comment.getId()))
            .jsonPath("$.[*].createdDate")
            .value(hasItem(DEFAULT_CREATED_DATE.toString()))
            .jsonPath("$.[*].updatedDate")
            .value(hasItem(DEFAULT_UPDATED_DATE.toString()))
            .jsonPath("$.[*].content")
            .value(hasItem(DEFAULT_CONTENT))
            .jsonPath("$.[*].disabled")
            .value(hasItem(DEFAULT_DISABLED.booleanValue()))
            .jsonPath("$.[*].upVote")
            .value(hasItem(DEFAULT_UP_VOTE.intValue()))
            .jsonPath("$.[*].downVote")
            .value(hasItem(DEFAULT_DOWN_VOTE.intValue()))
            .jsonPath("$.[*].permLink")
            .value(hasItem(DEFAULT_PERM_LINK));
    }
}
