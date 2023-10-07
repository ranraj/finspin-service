package com.yali.finspin.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SprintMapperTest {

    private SprintMapper sprintMapper;

    @BeforeEach
    public void setUp() {
        sprintMapper = new SprintMapperImpl();
    }
}
