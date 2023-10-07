package com.yali.finspin.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A OrgAccount.
 */
@Document(collection = "org_account")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "orgaccount")
public class OrgAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("name")
    private String name;

    @Field("blocked")
    private Boolean blocked;

    @Field("owner")
    private User owner;

    @Field("org")
    @JsonIgnoreProperties(value = { "orgAccounts" }, allowSetters = true)
    private Organisation org;

    @Field("project")
    @JsonIgnoreProperties(value = { "orgAccount", "sprints", "orgGroups" }, allowSetters = true)
    private Set<Project> projects = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public OrgAccount id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public OrgAccount name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getBlocked() {
        return this.blocked;
    }

    public OrgAccount blocked(Boolean blocked) {
        this.setBlocked(blocked);
        return this;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }

    public User getOwner() {
        return this.owner;
    }

    public void setOwner(User user) {
        this.owner = user;
    }

    public OrgAccount owner(User user) {
        this.setOwner(user);
        return this;
    }

    public Organisation getOrg() {
        return this.org;
    }

    public void setOrg(Organisation organisation) {
        this.org = organisation;
    }

    public OrgAccount org(Organisation organisation) {
        this.setOrg(organisation);
        return this;
    }

    public Set<Project> getProjects() {
        return this.projects;
    }

    public void setProjects(Set<Project> projects) {
        if (this.projects != null) {
            this.projects.forEach(i -> i.setOrgAccount(null));
        }
        if (projects != null) {
            projects.forEach(i -> i.setOrgAccount(this));
        }
        this.projects = projects;
    }

    public OrgAccount projects(Set<Project> projects) {
        this.setProjects(projects);
        return this;
    }

    public OrgAccount addProject(Project project) {
        this.projects.add(project);
        project.setOrgAccount(this);
        return this;
    }

    public OrgAccount removeProject(Project project) {
        this.projects.remove(project);
        project.setOrgAccount(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrgAccount)) {
            return false;
        }
        return id != null && id.equals(((OrgAccount) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrgAccount{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", blocked='" + getBlocked() + "'" +
            "}";
    }
}
