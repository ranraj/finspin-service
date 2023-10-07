package com.yali.finspin.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A OrgGroup.
 */
@Document(collection = "org_group")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "orggroup")
public class OrgGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("country_name")
    private String countryName;

    @Field("created_date")
    private Instant createdDate;

    @Field("update_date")
    private Instant updateDate;

    @Field("head")
    private User head;

    @Field("project")
    @JsonIgnoreProperties(value = { "orgAccount", "sprints", "orgGroups" }, allowSetters = true)
    private Project project;

    @Field("members")
    private Set<User> members = new HashSet<>();

    @Field("task")
    @JsonIgnoreProperties(
        value = { "owner", "createdBy", "sprint", "orgGroup", "assignedTo", "parent", "watchers", "comments", "tags" },
        allowSetters = true
    )
    private Set<Task> tasks = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public OrgGroup id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCountryName() {
        return this.countryName;
    }

    public OrgGroup countryName(String countryName) {
        this.setCountryName(countryName);
        return this;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public Instant getCreatedDate() {
        return this.createdDate;
    }

    public OrgGroup createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Instant getUpdateDate() {
        return this.updateDate;
    }

    public OrgGroup updateDate(Instant updateDate) {
        this.setUpdateDate(updateDate);
        return this;
    }

    public void setUpdateDate(Instant updateDate) {
        this.updateDate = updateDate;
    }

    public User getHead() {
        return this.head;
    }

    public void setHead(User user) {
        this.head = user;
    }

    public OrgGroup head(User user) {
        this.setHead(user);
        return this;
    }

    public Project getProject() {
        return this.project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public OrgGroup project(Project project) {
        this.setProject(project);
        return this;
    }

    public Set<User> getMembers() {
        return this.members;
    }

    public void setMembers(Set<User> users) {
        this.members = users;
    }

    public OrgGroup members(Set<User> users) {
        this.setMembers(users);
        return this;
    }

    public OrgGroup addMember(User user) {
        this.members.add(user);
        return this;
    }

    public OrgGroup removeMember(User user) {
        this.members.remove(user);
        return this;
    }

    public Set<Task> getTasks() {
        return this.tasks;
    }

    public void setTasks(Set<Task> tasks) {
        if (this.tasks != null) {
            this.tasks.forEach(i -> i.setOrgGroup(null));
        }
        if (tasks != null) {
            tasks.forEach(i -> i.setOrgGroup(this));
        }
        this.tasks = tasks;
    }

    public OrgGroup tasks(Set<Task> tasks) {
        this.setTasks(tasks);
        return this;
    }

    public OrgGroup addTask(Task task) {
        this.tasks.add(task);
        task.setOrgGroup(this);
        return this;
    }

    public OrgGroup removeTask(Task task) {
        this.tasks.remove(task);
        task.setOrgGroup(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrgGroup)) {
            return false;
        }
        return id != null && id.equals(((OrgGroup) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrgGroup{" +
            "id=" + getId() +
            ", countryName='" + getCountryName() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", updateDate='" + getUpdateDate() + "'" +
            "}";
    }
}
