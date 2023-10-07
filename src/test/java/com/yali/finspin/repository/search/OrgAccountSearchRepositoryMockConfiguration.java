package com.yali.finspin.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link OrgAccountSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class OrgAccountSearchRepositoryMockConfiguration {

    @MockBean
    private OrgAccountSearchRepository mockOrgAccountSearchRepository;
}
