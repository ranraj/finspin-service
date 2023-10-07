package com.yali.finspin.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link DashboardSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class DashboardSearchRepositoryMockConfiguration {

    @MockBean
    private DashboardSearchRepository mockDashboardSearchRepository;
}
