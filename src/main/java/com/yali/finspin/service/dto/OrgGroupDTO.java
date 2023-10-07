package com.yali.finspin.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.yali.finspin.domain.OrgGroup} entity.
 */
public class OrgGroupDTO implements Serializable {

    private String id;

    private String countryName;

    private Instant createdDate;

    private Instant updateDate;

    private UserDTO head;

    private ProjectDTO project;

    private Set<UserDTO> members = new HashSet<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Instant getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Instant updateDate) {
        this.updateDate = updateDate;
    }

    public UserDTO getHead() {
        return head;
    }

    public void setHead(UserDTO head) {
        this.head = head;
    }

    public ProjectDTO getProject() {
        return project;
    }

    public void setProject(ProjectDTO project) {
        this.project = project;
    }

    public Set<UserDTO> getMembers() {
        return members;
    }

    public void setMembers(Set<UserDTO> members) {
        this.members = members;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrgGroupDTO)) {
            return false;
        }

        OrgGroupDTO orgGroupDTO = (OrgGroupDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, orgGroupDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrgGroupDTO{" +
            "id='" + getId() + "'" +
            ", countryName='" + getCountryName() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", updateDate='" + getUpdateDate() + "'" +
            ", head=" + getHead() +
            ", project=" + getProject() +
            ", members=" + getMembers() +
            "}";
    }
}
