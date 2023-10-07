package com.yali.finspin.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.yali.finspin.domain.OrgGroup;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link OrgGroup} entity.
 */
public interface OrgGroupSearchRepository extends ReactiveElasticsearchRepository<OrgGroup, String>, OrgGroupSearchRepositoryInternal {}

interface OrgGroupSearchRepositoryInternal {
    Flux<OrgGroup> search(String query, Pageable pageable);
}

class OrgGroupSearchRepositoryInternalImpl implements OrgGroupSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    OrgGroupSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<OrgGroup> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        return reactiveElasticsearchTemplate.search(nativeSearchQuery, OrgGroup.class).map(SearchHit::getContent);
    }
}
