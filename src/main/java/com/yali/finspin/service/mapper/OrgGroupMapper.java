package com.yali.finspin.service.mapper;

import com.yali.finspin.domain.OrgGroup;
import com.yali.finspin.domain.Project;
import com.yali.finspin.domain.User;
import com.yali.finspin.service.dto.OrgGroupDTO;
import com.yali.finspin.service.dto.ProjectDTO;
import com.yali.finspin.service.dto.UserDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link OrgGroup} and its DTO {@link OrgGroupDTO}.
 */
@Mapper(componentModel = "spring")
public interface OrgGroupMapper extends EntityMapper<OrgGroupDTO, OrgGroup> {
    @Mapping(target = "head", source = "head", qualifiedByName = "userId")
    @Mapping(target = "project", source = "project", qualifiedByName = "projectId")
    @Mapping(target = "members", source = "members", qualifiedByName = "userIdSet")
    OrgGroupDTO toDto(OrgGroup s);

    @Mapping(target = "removeMember", ignore = true)
    OrgGroup toEntity(OrgGroupDTO orgGroupDTO);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);

    @Named("userIdSet")
    default Set<UserDTO> toDtoUserIdSet(Set<User> user) {
        return user.stream().map(this::toDtoUserId).collect(Collectors.toSet());
    }

    @Named("projectId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProjectDTO toDtoProjectId(Project project);
}
