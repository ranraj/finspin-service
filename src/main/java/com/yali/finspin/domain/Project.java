package com.yali.finspin.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A Project.
 */
@Document(collection = "project")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "project")
public class Project implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull(message = "must not be null")
    @Field("string")
    private String string;

    @Field("created_date")
    private Instant createdDate;

    @Field("update_date")
    private Instant updateDate;

    @Field("orgAccount")
    @JsonIgnoreProperties(value = { "owner", "org", "projects" }, allowSetters = true)
    private OrgAccount orgAccount;

    @Field("sprint")
    @JsonIgnoreProperties(value = { "project", "tasks" }, allowSetters = true)
    private Set<Sprint> sprints = new HashSet<>();

    @Field("orgGroup")
    @JsonIgnoreProperties(value = { "head", "project", "members", "tasks" }, allowSetters = true)
    private Set<OrgGroup> orgGroups = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Project id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getString() {
        return this.string;
    }

    public Project string(String string) {
        this.setString(string);
        return this;
    }

    public void setString(String string) {
        this.string = string;
    }

    public Instant getCreatedDate() {
        return this.createdDate;
    }

    public Project createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Instant getUpdateDate() {
        return this.updateDate;
    }

    public Project updateDate(Instant updateDate) {
        this.setUpdateDate(updateDate);
        return this;
    }

    public void setUpdateDate(Instant updateDate) {
        this.updateDate = updateDate;
    }

    public OrgAccount getOrgAccount() {
        return this.orgAccount;
    }

    public void setOrgAccount(OrgAccount orgAccount) {
        this.orgAccount = orgAccount;
    }

    public Project orgAccount(OrgAccount orgAccount) {
        this.setOrgAccount(orgAccount);
        return this;
    }

    public Set<Sprint> getSprints() {
        return this.sprints;
    }

    public void setSprints(Set<Sprint> sprints) {
        if (this.sprints != null) {
            this.sprints.forEach(i -> i.setProject(null));
        }
        if (sprints != null) {
            sprints.forEach(i -> i.setProject(this));
        }
        this.sprints = sprints;
    }

    public Project sprints(Set<Sprint> sprints) {
        this.setSprints(sprints);
        return this;
    }

    public Project addSprint(Sprint sprint) {
        this.sprints.add(sprint);
        sprint.setProject(this);
        return this;
    }

    public Project removeSprint(Sprint sprint) {
        this.sprints.remove(sprint);
        sprint.setProject(null);
        return this;
    }

    public Set<OrgGroup> getOrgGroups() {
        return this.orgGroups;
    }

    public void setOrgGroups(Set<OrgGroup> orgGroups) {
        if (this.orgGroups != null) {
            this.orgGroups.forEach(i -> i.setProject(null));
        }
        if (orgGroups != null) {
            orgGroups.forEach(i -> i.setProject(this));
        }
        this.orgGroups = orgGroups;
    }

    public Project orgGroups(Set<OrgGroup> orgGroups) {
        this.setOrgGroups(orgGroups);
        return this;
    }

    public Project addOrgGroup(OrgGroup orgGroup) {
        this.orgGroups.add(orgGroup);
        orgGroup.setProject(this);
        return this;
    }

    public Project removeOrgGroup(OrgGroup orgGroup) {
        this.orgGroups.remove(orgGroup);
        orgGroup.setProject(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Project)) {
            return false;
        }
        return id != null && id.equals(((Project) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Project{" +
            "id=" + getId() +
            ", string='" + getString() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", updateDate='" + getUpdateDate() + "'" +
            "}";
    }
}
