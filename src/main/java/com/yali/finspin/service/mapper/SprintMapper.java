package com.yali.finspin.service.mapper;

import com.yali.finspin.domain.Project;
import com.yali.finspin.domain.Sprint;
import com.yali.finspin.service.dto.ProjectDTO;
import com.yali.finspin.service.dto.SprintDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Sprint} and its DTO {@link SprintDTO}.
 */
@Mapper(componentModel = "spring")
public interface SprintMapper extends EntityMapper<SprintDTO, Sprint> {
    @Mapping(target = "project", source = "project", qualifiedByName = "projectId")
    SprintDTO toDto(Sprint s);

    @Named("projectId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProjectDTO toDtoProjectId(Project project);
}
