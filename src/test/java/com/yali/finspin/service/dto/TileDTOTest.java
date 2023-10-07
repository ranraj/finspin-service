package com.yali.finspin.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.yali.finspin.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TileDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TileDTO.class);
        TileDTO tileDTO1 = new TileDTO();
        tileDTO1.setId("id1");
        TileDTO tileDTO2 = new TileDTO();
        assertThat(tileDTO1).isNotEqualTo(tileDTO2);
        tileDTO2.setId(tileDTO1.getId());
        assertThat(tileDTO1).isEqualTo(tileDTO2);
        tileDTO2.setId("id2");
        assertThat(tileDTO1).isNotEqualTo(tileDTO2);
        tileDTO1.setId(null);
        assertThat(tileDTO1).isNotEqualTo(tileDTO2);
    }
}
