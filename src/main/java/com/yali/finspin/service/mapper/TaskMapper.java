package com.yali.finspin.service.mapper;

import com.yali.finspin.domain.OrgGroup;
import com.yali.finspin.domain.Sprint;
import com.yali.finspin.domain.Task;
import com.yali.finspin.domain.User;
import com.yali.finspin.service.dto.OrgGroupDTO;
import com.yali.finspin.service.dto.SprintDTO;
import com.yali.finspin.service.dto.TaskDTO;
import com.yali.finspin.service.dto.UserDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Task} and its DTO {@link TaskDTO}.
 */
@Mapper(componentModel = "spring")
public interface TaskMapper extends EntityMapper<TaskDTO, Task> {
    @Mapping(target = "owner", source = "owner", qualifiedByName = "userId")
    @Mapping(target = "createdBy", source = "createdBy", qualifiedByName = "userId")
    @Mapping(target = "sprint", source = "sprint", qualifiedByName = "sprintId")
    @Mapping(target = "orgGroup", source = "orgGroup", qualifiedByName = "orgGroupId")
    @Mapping(target = "assignedTo", source = "assignedTo", qualifiedByName = "userId")
    @Mapping(target = "parent", source = "parent", qualifiedByName = "taskId")
    @Mapping(target = "watchers", source = "watchers", qualifiedByName = "userIdSet")
    TaskDTO toDto(Task s);

    @Mapping(target = "removeWatcher", ignore = true)
    Task toEntity(TaskDTO taskDTO);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);

    @Named("userIdSet")
    default Set<UserDTO> toDtoUserIdSet(Set<User> user) {
        return user.stream().map(this::toDtoUserId).collect(Collectors.toSet());
    }

    @Named("sprintId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SprintDTO toDtoSprintId(Sprint sprint);

    @Named("orgGroupId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    OrgGroupDTO toDtoOrgGroupId(OrgGroup orgGroup);

    @Named("taskId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TaskDTO toDtoTaskId(Task task);
}
