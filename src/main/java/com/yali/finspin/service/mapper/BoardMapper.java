package com.yali.finspin.service.mapper;

import com.yali.finspin.domain.Board;
import com.yali.finspin.domain.Dashboard;
import com.yali.finspin.service.dto.BoardDTO;
import com.yali.finspin.service.dto.DashboardDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Board} and its DTO {@link BoardDTO}.
 */
@Mapper(componentModel = "spring")
public interface BoardMapper extends EntityMapper<BoardDTO, Board> {
    @Mapping(target = "dashBoard", source = "dashBoard", qualifiedByName = "dashboardId")
    BoardDTO toDto(Board s);

    @Named("dashboardId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    DashboardDTO toDtoDashboardId(Dashboard dashboard);
}
