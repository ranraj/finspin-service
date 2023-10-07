package com.yali.finspin.service.mapper;

import com.yali.finspin.domain.OrgAccount;
import com.yali.finspin.domain.Project;
import com.yali.finspin.service.dto.OrgAccountDTO;
import com.yali.finspin.service.dto.ProjectDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Project} and its DTO {@link ProjectDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProjectMapper extends EntityMapper<ProjectDTO, Project> {
    @Mapping(target = "orgAccount", source = "orgAccount", qualifiedByName = "orgAccountId")
    ProjectDTO toDto(Project s);

    @Named("orgAccountId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    OrgAccountDTO toDtoOrgAccountId(OrgAccount orgAccount);
}
