package com.yali.finspin.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.yali.finspin.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TileTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Tile.class);
        Tile tile1 = new Tile();
        tile1.setId("id1");
        Tile tile2 = new Tile();
        tile2.setId(tile1.getId());
        assertThat(tile1).isEqualTo(tile2);
        tile2.setId("id2");
        assertThat(tile1).isNotEqualTo(tile2);
        tile1.setId(null);
        assertThat(tile1).isNotEqualTo(tile2);
    }
}
