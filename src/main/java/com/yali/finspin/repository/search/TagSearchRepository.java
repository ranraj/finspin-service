package com.yali.finspin.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.yali.finspin.domain.Tag;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link Tag} entity.
 */
public interface TagSearchRepository extends ReactiveElasticsearchRepository<Tag, String>, TagSearchRepositoryInternal {}

interface TagSearchRepositoryInternal {
    Flux<Tag> search(String query);
}

class TagSearchRepositoryInternalImpl implements TagSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    TagSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<Tag> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return reactiveElasticsearchTemplate.search(nativeSearchQuery, Tag.class).map(SearchHit::getContent);
    }
}
