package com.yali.finspin.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.yali.finspin.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BoardDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(BoardDTO.class);
        BoardDTO boardDTO1 = new BoardDTO();
        boardDTO1.setId("id1");
        BoardDTO boardDTO2 = new BoardDTO();
        assertThat(boardDTO1).isNotEqualTo(boardDTO2);
        boardDTO2.setId(boardDTO1.getId());
        assertThat(boardDTO1).isEqualTo(boardDTO2);
        boardDTO2.setId("id2");
        assertThat(boardDTO1).isNotEqualTo(boardDTO2);
        boardDTO1.setId(null);
        assertThat(boardDTO1).isNotEqualTo(boardDTO2);
    }
}
