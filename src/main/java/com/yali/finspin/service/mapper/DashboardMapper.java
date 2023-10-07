package com.yali.finspin.service.mapper;

import com.yali.finspin.domain.Dashboard;
import com.yali.finspin.service.dto.DashboardDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Dashboard} and its DTO {@link DashboardDTO}.
 */
@Mapper(componentModel = "spring")
public interface DashboardMapper extends EntityMapper<DashboardDTO, Dashboard> {}
