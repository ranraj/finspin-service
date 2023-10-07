package com.yali.finspin.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.yali.finspin.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OrgAccountDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrgAccountDTO.class);
        OrgAccountDTO orgAccountDTO1 = new OrgAccountDTO();
        orgAccountDTO1.setId("id1");
        OrgAccountDTO orgAccountDTO2 = new OrgAccountDTO();
        assertThat(orgAccountDTO1).isNotEqualTo(orgAccountDTO2);
        orgAccountDTO2.setId(orgAccountDTO1.getId());
        assertThat(orgAccountDTO1).isEqualTo(orgAccountDTO2);
        orgAccountDTO2.setId("id2");
        assertThat(orgAccountDTO1).isNotEqualTo(orgAccountDTO2);
        orgAccountDTO1.setId(null);
        assertThat(orgAccountDTO1).isNotEqualTo(orgAccountDTO2);
    }
}
