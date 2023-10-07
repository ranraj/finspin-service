package com.yali.finspin.service.mapper;

import com.yali.finspin.domain.OrgAccount;
import com.yali.finspin.domain.Organisation;
import com.yali.finspin.domain.User;
import com.yali.finspin.service.dto.OrgAccountDTO;
import com.yali.finspin.service.dto.OrganisationDTO;
import com.yali.finspin.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link OrgAccount} and its DTO {@link OrgAccountDTO}.
 */
@Mapper(componentModel = "spring")
public interface OrgAccountMapper extends EntityMapper<OrgAccountDTO, OrgAccount> {
    @Mapping(target = "owner", source = "owner", qualifiedByName = "userId")
    @Mapping(target = "org", source = "org", qualifiedByName = "organisationId")
    OrgAccountDTO toDto(OrgAccount s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);

    @Named("organisationId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    OrganisationDTO toDtoOrganisationId(Organisation organisation);
}
