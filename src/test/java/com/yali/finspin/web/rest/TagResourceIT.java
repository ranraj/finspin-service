package com.yali.finspin.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.yali.finspin.IntegrationTest;
import com.yali.finspin.domain.Tag;
import com.yali.finspin.repository.TagRepository;
import com.yali.finspin.repository.search.TagSearchRepository;
import com.yali.finspin.service.TagService;
import com.yali.finspin.service.dto.TagDTO;
import com.yali.finspin.service.mapper.TagMapper;
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
 * Integration tests for the {@link TagResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TagResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/tags";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/tags";

    @Autowired
    private TagRepository tagRepository;

    @Mock
    private TagRepository tagRepositoryMock;

    @Autowired
    private TagMapper tagMapper;

    @Mock
    private TagService tagServiceMock;

    /**
     * This repository is mocked in the com.yali.finspin.repository.search test package.
     *
     * @see com.yali.finspin.repository.search.TagSearchRepositoryMockConfiguration
     */
    @Autowired
    private TagSearchRepository mockTagSearchRepository;

    @Autowired
    private WebTestClient webTestClient;

    private Tag tag;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tag createEntity() {
        Tag tag = new Tag().name(DEFAULT_NAME);
        return tag;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tag createUpdatedEntity() {
        Tag tag = new Tag().name(UPDATED_NAME);
        return tag;
    }

    @BeforeEach
    public void initTest() {
        tagRepository.deleteAll().block();
        tag = createEntity();
    }

    @Test
    void createTag() throws Exception {
        int databaseSizeBeforeCreate = tagRepository.findAll().collectList().block().size();
        // Configure the mock search repository
        when(mockTagSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Create the Tag
        TagDTO tagDTO = tagMapper.toDto(tag);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tagDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Tag in the database
        List<Tag> tagList = tagRepository.findAll().collectList().block();
        assertThat(tagList).hasSize(databaseSizeBeforeCreate + 1);
        Tag testTag = tagList.get(tagList.size() - 1);
        assertThat(testTag.getName()).isEqualTo(DEFAULT_NAME);

        // Validate the Tag in Elasticsearch
        verify(mockTagSearchRepository, times(1)).save(testTag);
    }

    @Test
    void createTagWithExistingId() throws Exception {
        // Create the Tag with an existing ID
        tag.setId("existing_id");
        TagDTO tagDTO = tagMapper.toDto(tag);

        int databaseSizeBeforeCreate = tagRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tagDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tag in the database
        List<Tag> tagList = tagRepository.findAll().collectList().block();
        assertThat(tagList).hasSize(databaseSizeBeforeCreate);

        // Validate the Tag in Elasticsearch
        verify(mockTagSearchRepository, times(0)).save(tag);
    }

    @Test
    void getAllTagsAsStream() {
        // Initialize the database
        tagRepository.save(tag).block();

        List<Tag> tagList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(TagDTO.class)
            .getResponseBody()
            .map(tagMapper::toEntity)
            .filter(tag::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(tagList).isNotNull();
        assertThat(tagList).hasSize(1);
        Tag testTag = tagList.get(0);
        assertThat(testTag.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    void getAllTags() {
        // Initialize the database
        tagRepository.save(tag).block();

        // Get all the tagList
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
            .value(hasItem(tag.getId()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTagsWithEagerRelationshipsIsEnabled() {
        when(tagServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(tagServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTagsWithEagerRelationshipsIsNotEnabled() {
        when(tagServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(tagServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getTag() {
        // Initialize the database
        tagRepository.save(tag).block();

        // Get the tag
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, tag.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(tag.getId()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME));
    }

    @Test
    void getNonExistingTag() {
        // Get the tag
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewTag() throws Exception {
        // Configure the mock search repository
        when(mockTagSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        tagRepository.save(tag).block();

        int databaseSizeBeforeUpdate = tagRepository.findAll().collectList().block().size();

        // Update the tag
        Tag updatedTag = tagRepository.findById(tag.getId()).block();
        updatedTag.name(UPDATED_NAME);
        TagDTO tagDTO = tagMapper.toDto(updatedTag);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, tagDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tagDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Tag in the database
        List<Tag> tagList = tagRepository.findAll().collectList().block();
        assertThat(tagList).hasSize(databaseSizeBeforeUpdate);
        Tag testTag = tagList.get(tagList.size() - 1);
        assertThat(testTag.getName()).isEqualTo(UPDATED_NAME);

        // Validate the Tag in Elasticsearch
        verify(mockTagSearchRepository).save(testTag);
    }

    @Test
    void putNonExistingTag() throws Exception {
        int databaseSizeBeforeUpdate = tagRepository.findAll().collectList().block().size();
        tag.setId(UUID.randomUUID().toString());

        // Create the Tag
        TagDTO tagDTO = tagMapper.toDto(tag);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, tagDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tagDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tag in the database
        List<Tag> tagList = tagRepository.findAll().collectList().block();
        assertThat(tagList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Tag in Elasticsearch
        verify(mockTagSearchRepository, times(0)).save(tag);
    }

    @Test
    void putWithIdMismatchTag() throws Exception {
        int databaseSizeBeforeUpdate = tagRepository.findAll().collectList().block().size();
        tag.setId(UUID.randomUUID().toString());

        // Create the Tag
        TagDTO tagDTO = tagMapper.toDto(tag);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tagDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tag in the database
        List<Tag> tagList = tagRepository.findAll().collectList().block();
        assertThat(tagList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Tag in Elasticsearch
        verify(mockTagSearchRepository, times(0)).save(tag);
    }

    @Test
    void putWithMissingIdPathParamTag() throws Exception {
        int databaseSizeBeforeUpdate = tagRepository.findAll().collectList().block().size();
        tag.setId(UUID.randomUUID().toString());

        // Create the Tag
        TagDTO tagDTO = tagMapper.toDto(tag);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tagDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Tag in the database
        List<Tag> tagList = tagRepository.findAll().collectList().block();
        assertThat(tagList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Tag in Elasticsearch
        verify(mockTagSearchRepository, times(0)).save(tag);
    }

    @Test
    void partialUpdateTagWithPatch() throws Exception {
        // Initialize the database
        tagRepository.save(tag).block();

        int databaseSizeBeforeUpdate = tagRepository.findAll().collectList().block().size();

        // Update the tag using partial update
        Tag partialUpdatedTag = new Tag();
        partialUpdatedTag.setId(tag.getId());

        partialUpdatedTag.name(UPDATED_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTag.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTag))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Tag in the database
        List<Tag> tagList = tagRepository.findAll().collectList().block();
        assertThat(tagList).hasSize(databaseSizeBeforeUpdate);
        Tag testTag = tagList.get(tagList.size() - 1);
        assertThat(testTag.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    void fullUpdateTagWithPatch() throws Exception {
        // Initialize the database
        tagRepository.save(tag).block();

        int databaseSizeBeforeUpdate = tagRepository.findAll().collectList().block().size();

        // Update the tag using partial update
        Tag partialUpdatedTag = new Tag();
        partialUpdatedTag.setId(tag.getId());

        partialUpdatedTag.name(UPDATED_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTag.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTag))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Tag in the database
        List<Tag> tagList = tagRepository.findAll().collectList().block();
        assertThat(tagList).hasSize(databaseSizeBeforeUpdate);
        Tag testTag = tagList.get(tagList.size() - 1);
        assertThat(testTag.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    void patchNonExistingTag() throws Exception {
        int databaseSizeBeforeUpdate = tagRepository.findAll().collectList().block().size();
        tag.setId(UUID.randomUUID().toString());

        // Create the Tag
        TagDTO tagDTO = tagMapper.toDto(tag);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, tagDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(tagDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tag in the database
        List<Tag> tagList = tagRepository.findAll().collectList().block();
        assertThat(tagList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Tag in Elasticsearch
        verify(mockTagSearchRepository, times(0)).save(tag);
    }

    @Test
    void patchWithIdMismatchTag() throws Exception {
        int databaseSizeBeforeUpdate = tagRepository.findAll().collectList().block().size();
        tag.setId(UUID.randomUUID().toString());

        // Create the Tag
        TagDTO tagDTO = tagMapper.toDto(tag);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(tagDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tag in the database
        List<Tag> tagList = tagRepository.findAll().collectList().block();
        assertThat(tagList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Tag in Elasticsearch
        verify(mockTagSearchRepository, times(0)).save(tag);
    }

    @Test
    void patchWithMissingIdPathParamTag() throws Exception {
        int databaseSizeBeforeUpdate = tagRepository.findAll().collectList().block().size();
        tag.setId(UUID.randomUUID().toString());

        // Create the Tag
        TagDTO tagDTO = tagMapper.toDto(tag);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(tagDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Tag in the database
        List<Tag> tagList = tagRepository.findAll().collectList().block();
        assertThat(tagList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Tag in Elasticsearch
        verify(mockTagSearchRepository, times(0)).save(tag);
    }

    @Test
    void deleteTag() {
        // Configure the mock search repository
        when(mockTagSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockTagSearchRepository.deleteById(anyString())).thenReturn(Mono.empty());
        // Initialize the database
        tagRepository.save(tag).block();

        int databaseSizeBeforeDelete = tagRepository.findAll().collectList().block().size();

        // Delete the tag
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, tag.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Tag> tagList = tagRepository.findAll().collectList().block();
        assertThat(tagList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Tag in Elasticsearch
        verify(mockTagSearchRepository, times(1)).deleteById(tag.getId());
    }

    @Test
    void searchTag() {
        // Configure the mock search repository
        when(mockTagSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        tagRepository.save(tag).block();
        when(mockTagSearchRepository.search("id:" + tag.getId())).thenReturn(Flux.just(tag));

        // Search the tag
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + tag.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(tag.getId()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME));
    }
}
