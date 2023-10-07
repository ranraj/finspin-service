package com.yali.finspin.service.mapper;

import com.yali.finspin.domain.Tag;
import com.yali.finspin.domain.Task;
import com.yali.finspin.service.dto.TagDTO;
import com.yali.finspin.service.dto.TaskDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Tag} and its DTO {@link TagDTO}.
 */
@Mapper(componentModel = "spring")
public interface TagMapper extends EntityMapper<TagDTO, Tag> {
    @Mapping(target = "tasks", source = "tasks", qualifiedByName = "taskIdSet")
    TagDTO toDto(Tag s);

    @Mapping(target = "removeTask", ignore = true)
    Tag toEntity(TagDTO tagDTO);

    @Named("taskId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TaskDTO toDtoTaskId(Task task);

    @Named("taskIdSet")
    default Set<TaskDTO> toDtoTaskIdSet(Set<Task> task) {
        return task.stream().map(this::toDtoTaskId).collect(Collectors.toSet());
    }
}
