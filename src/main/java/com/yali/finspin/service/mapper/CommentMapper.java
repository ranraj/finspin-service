package com.yali.finspin.service.mapper;

import com.yali.finspin.domain.Comment;
import com.yali.finspin.domain.Task;
import com.yali.finspin.domain.User;
import com.yali.finspin.service.dto.CommentDTO;
import com.yali.finspin.service.dto.TaskDTO;
import com.yali.finspin.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Comment} and its DTO {@link CommentDTO}.
 */
@Mapper(componentModel = "spring")
public interface CommentMapper extends EntityMapper<CommentDTO, Comment> {
    @Mapping(target = "owner", source = "owner", qualifiedByName = "userId")
    @Mapping(target = "task", source = "task", qualifiedByName = "taskId")
    CommentDTO toDto(Comment s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);

    @Named("taskId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TaskDTO toDtoTaskId(Task task);
}
