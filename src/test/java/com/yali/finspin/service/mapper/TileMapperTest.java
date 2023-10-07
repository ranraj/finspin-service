package com.yali.finspin.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TileMapperTest {

    private TileMapper tileMapper;

    @BeforeEach
    public void setUp() {
        tileMapper = new TileMapperImpl();
    }
}
