package com.yali.finspin.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.yali.finspin.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OrgGroupDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrgGroupDTO.class);
        OrgGroupDTO orgGroupDTO1 = new OrgGroupDTO();
        orgGroupDTO1.setId("id1");
        OrgGroupDTO orgGroupDTO2 = new OrgGroupDTO();
        assertThat(orgGroupDTO1).isNotEqualTo(orgGroupDTO2);
        orgGroupDTO2.setId(orgGroupDTO1.getId());
        assertThat(orgGroupDTO1).isEqualTo(orgGroupDTO2);
        orgGroupDTO2.setId("id2");
        assertThat(orgGroupDTO1).isNotEqualTo(orgGroupDTO2);
        orgGroupDTO1.setId(null);
        assertThat(orgGroupDTO1).isNotEqualTo(orgGroupDTO2);
    }
}
