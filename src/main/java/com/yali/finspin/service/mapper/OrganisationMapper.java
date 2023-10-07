package com.yali.finspin.service.mapper;

import com.yali.finspin.domain.Organisation;
import com.yali.finspin.service.dto.OrganisationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Organisation} and its DTO {@link OrganisationDTO}.
 */
@Mapper(componentModel = "spring")
public interface OrganisationMapper extends EntityMapper<OrganisationDTO, Organisation> {}
