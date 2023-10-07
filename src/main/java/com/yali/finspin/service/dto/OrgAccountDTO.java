package com.yali.finspin.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.yali.finspin.domain.OrgAccount} entity.
 */
public class OrgAccountDTO implements Serializable {

    private String id;

    private String name;

    private Boolean blocked;

    private UserDTO owner;

    private OrganisationDTO org;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getBlocked() {
        return blocked;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }

    public UserDTO getOwner() {
        return owner;
    }

    public void setOwner(UserDTO owner) {
        this.owner = owner;
    }

    public OrganisationDTO getOrg() {
        return org;
    }

    public void setOrg(OrganisationDTO org) {
        this.org = org;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrgAccountDTO)) {
            return false;
        }

        OrgAccountDTO orgAccountDTO = (OrgAccountDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, orgAccountDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrgAccountDTO{" +
            "id='" + getId() + "'" +
            ", name='" + getName() + "'" +
            ", blocked='" + getBlocked() + "'" +
            ", owner=" + getOwner() +
            ", org=" + getOrg() +
            "}";
    }
}
