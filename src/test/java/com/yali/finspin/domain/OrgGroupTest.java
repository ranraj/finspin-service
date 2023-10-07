package com.yali.finspin.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.yali.finspin.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OrgGroupTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrgGroup.class);
        OrgGroup orgGroup1 = new OrgGroup();
        orgGroup1.setId("id1");
        OrgGroup orgGroup2 = new OrgGroup();
        orgGroup2.setId(orgGroup1.getId());
        assertThat(orgGroup1).isEqualTo(orgGroup2);
        orgGroup2.setId("id2");
        assertThat(orgGroup1).isNotEqualTo(orgGroup2);
        orgGroup1.setId(null);
        assertThat(orgGroup1).isNotEqualTo(orgGroup2);
    }
}
