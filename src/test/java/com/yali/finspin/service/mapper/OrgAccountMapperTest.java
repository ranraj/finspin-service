package com.yali.finspin.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrgAccountMapperTest {

    private OrgAccountMapper orgAccountMapper;

    @BeforeEach
    public void setUp() {
        orgAccountMapper = new OrgAccountMapperImpl();
    }
}
