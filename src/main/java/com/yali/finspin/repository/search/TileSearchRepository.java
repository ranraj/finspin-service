package com.yali.finspin.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.yali.finspin.domain.Tile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link Tile} entity.
 */
public interface TileSearchRepository extends ReactiveElasticsearchRepository<Tile, String>, TileSearchRepositoryInternal {}

interface TileSearchRepositoryInternal {
    Flux<Tile> search(String query, Pageable pageable);
}

class TileSearchRepositoryInternalImpl implements TileSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    TileSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<Tile> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        return reactiveElasticsearchTemplate.search(nativeSearchQuery, Tile.class).map(SearchHit::getContent);
    }
}
