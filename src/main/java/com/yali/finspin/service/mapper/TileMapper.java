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
@Mapper(componentModel = "spring",nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface TileMapper extends EntityMapper<TileDTO, Tile> {
    @Mapping(target = "task", source = "task", qualifiedByName = "taskId")
    @Mapping(target = "board", source = "board", qualifiedByName = "boardId")
    TileDTO toDto(Tile s);

    @Named("taskId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", defaultExpression="java(\"\")", source = "title")
    @Mapping(target = "description", defaultExpression="java(\"\")", source = "description")
    @Mapping(target = "createDate", source = "createDate")
    @Mapping(target = "updatedDate", source = "updatedDate")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "effortHrs",source = "effortHrs")
    @Mapping(target = "owner", source = "owner")
    @Mapping(target = "createdBy",source = "createdBy")
    @Mapping(target = "sprint",source = "sprint")
    @Mapping(target = "parent",source = "parent")
    TaskDTO toDtoTaskId(Task task);

    @Named("boardId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    BoardDTO toDtoBoardId(Board board);
}
