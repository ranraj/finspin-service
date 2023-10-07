package com.yali.finspin.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.yali.finspin.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OrgAccountTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrgAccount.class);
        OrgAccount orgAccount1 = new OrgAccount();
        orgAccount1.setId("id1");
        OrgAccount orgAccount2 = new OrgAccount();
        orgAccount2.setId(orgAccount1.getId());
        assertThat(orgAccount1).isEqualTo(orgAccount2);
        orgAccount2.setId("id2");
        assertThat(orgAccount1).isNotEqualTo(orgAccount2);
        orgAccount1.setId(null);
        assertThat(orgAccount1).isNotEqualTo(orgAccount2);
    }
}
