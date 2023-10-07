package com.yali.finspin.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrgGroupMapperTest {

    private OrgGroupMapper orgGroupMapper;

    @BeforeEach
    public void setUp() {
        orgGroupMapper = new OrgGroupMapperImpl();
    }
}
