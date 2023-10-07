package com.yali.finspin.service.mapper;

import com.yali.finspin.domain.Board;
import com.yali.finspin.domain.Task;
import com.yali.finspin.domain.Tile;
import com.yali.finspin.service.dto.BoardDTO;
import com.yali.finspin.service.dto.TaskDTO;
import com.yali.finspin.service.dto.TileDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Tile} and its DTO {@link TileDTO}.
 */
@Mapper(componentModel = "spring")
public interface TileMapper extends EntityMapper<TileDTO, Tile> {
    @Mapping(target = "task", source = "task", qualifiedByName = "taskId")
    @Mapping(target = "board", source = "board", qualifiedByName = "boardId")
    TileDTO toDto(Tile s);

    @Named("taskId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TaskDTO toDtoTaskId(Task task);

    @Named("boardId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    BoardDTO toDtoBoardId(Board board);
}
