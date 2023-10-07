package com.yali.finspin.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yali.finspin.domain.enumeration.TaskStatus;
import com.yali.finspin.domain.enumeration.TaskType;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Task entity.\n@author The JHipster team.
 */
@Document(collection = "task")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "task")
public class Task implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("title")
    private String title;

    @Field("description")
    private String description;

    @Field("create_date")
    private Instant createDate;

    @Field("updated_date")
    private Instant updatedDate;

    @Field("status")
    private TaskStatus status;

    @Field("type")
    private TaskType type;

    @Field("effort_hrs")
    private Long effortHrs;

    @Field("start_date")
    private Instant startDate;

    @Field("end_date")
    private Instant endDate;

    @Field("owner")
    private User owner;

    @Field("createdBy")
    private User createdBy;

    @Field("sprint")
    @JsonIgnoreProperties(value = { "project", "tasks" }, allowSetters = true)
    private Sprint sprint;

    @Field("orgGroup")
    @JsonIgnoreProperties(value = { "head", "project", "members", "tasks" }, allowSetters = true)
    private OrgGroup orgGroup;

    @Field("assignedTo")
    private User assignedTo;

    @Field("parent")
    @JsonIgnoreProperties(
        value = { "owner", "createdBy", "sprint", "orgGroup", "assignedTo", "parent", "watchers", "comments", "tags" },
        allowSetters = true
    )
    private Task parent;

    @Field("watchers")
    private Set<User> watchers = new HashSet<>();

    @Field("comment")
    @JsonIgnoreProperties(value = { "owner", "task" }, allowSetters = true)
    private Set<Comment> comments = new HashSet<>();

    @Field("tags")
    @JsonIgnoreProperties(value = { "tasks" }, allowSetters = true)
    private Set<Tag> tags = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Task id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Task title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public Task description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getCreateDate() {
        return this.createDate;
    }

    public Task createDate(Instant createDate) {
        this.setCreateDate(createDate);
        return this;
    }

    public void setCreateDate(Instant createDate) {
        this.createDate = createDate;
    }

    public Instant getUpdatedDate() {
        return this.updatedDate;
    }

    public Task updatedDate(Instant updatedDate) {
        this.setUpdatedDate(updatedDate);
        return this;
    }

    public void setUpdatedDate(Instant updatedDate) {
        this.updatedDate = updatedDate;
    }

    public TaskStatus getStatus() {
        return this.status;
    }

    public Task status(TaskStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskType getType() {
        return this.type;
    }

    public Task type(TaskType type) {
        this.setType(type);
        return this;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    public Long getEffortHrs() {
        return this.effortHrs;
    }

    public Task effortHrs(Long effortHrs) {
        this.setEffortHrs(effortHrs);
        return this;
    }

    public void setEffortHrs(Long effortHrs) {
        this.effortHrs = effortHrs;
    }

    public Instant getStartDate() {
        return this.startDate;
    }

    public Task startDate(Instant startDate) {
        this.setStartDate(startDate);
        return this;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return this.endDate;
    }

    public Task endDate(Instant endDate) {
        this.setEndDate(endDate);
        return this;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public User getOwner() {
        return this.owner;
    }

    public void setOwner(User user) {
        this.owner = user;
    }

    public Task owner(User user) {
        this.setOwner(user);
        return this;
    }

    public User getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(User user) {
        this.createdBy = user;
    }

    public Task createdBy(User user) {
        this.setCreatedBy(user);
        return this;
    }

    public Sprint getSprint() {
        return this.sprint;
    }

    public void setSprint(Sprint sprint) {
        this.sprint = sprint;
    }

    public Task sprint(Sprint sprint) {
        this.setSprint(sprint);
        return this;
    }

    public OrgGroup getOrgGroup() {
        return this.orgGroup;
    }

    public void setOrgGroup(OrgGroup orgGroup) {
        this.orgGroup = orgGroup;
    }

    public Task orgGroup(OrgGroup orgGroup) {
        this.setOrgGroup(orgGroup);
        return this;
    }

    public User getAssignedTo() {
        return this.assignedTo;
    }

    public void setAssignedTo(User user) {
        this.assignedTo = user;
    }

    public Task assignedTo(User user) {
        this.setAssignedTo(user);
        return this;
    }

    public Task getParent() {
        return this.parent;
    }

    public void setParent(Task task) {
        this.parent = task;
    }

    public Task parent(Task task) {
        this.setParent(task);
        return this;
    }

    public Set<User> getWatchers() {
        return this.watchers;
    }

    public void setWatchers(Set<User> users) {
        this.watchers = users;
    }

    public Task watchers(Set<User> users) {
        this.setWatchers(users);
        return this;
    }

    public Task addWatcher(User user) {
        this.watchers.add(user);
        return this;
    }

    public Task removeWatcher(User user) {
        this.watchers.remove(user);
        return this;
    }

    public Set<Comment> getComments() {
        return this.comments;
    }

    public void setComments(Set<Comment> comments) {
        if (this.comments != null) {
            this.comments.forEach(i -> i.setTask(null));
        }
        if (comments != null) {
            comments.forEach(i -> i.setTask(this));
        }
        this.comments = comments;
    }

    public Task comments(Set<Comment> comments) {
        this.setComments(comments);
        return this;
    }

    public Task addComment(Comment comment) {
        this.comments.add(comment);
        comment.setTask(this);
        return this;
    }

    public Task removeComment(Comment comment) {
        this.comments.remove(comment);
        comment.setTask(null);
        return this;
    }

    public Set<Tag> getTags() {
        return this.tags;
    }

    public void setTags(Set<Tag> tags) {
        if (this.tags != null) {
            this.tags.forEach(i -> i.removeTask(this));
        }
        if (tags != null) {
            tags.forEach(i -> i.addTask(this));
        }
        this.tags = tags;
    }

    public Task tags(Set<Tag> tags) {
        this.setTags(tags);
        return this;
    }

    public Task addTag(Tag tag) {
        this.tags.add(tag);
        tag.getTasks().add(this);
        return this;
    }

    public Task removeTag(Tag tag) {
        this.tags.remove(tag);
        tag.getTasks().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Task)) {
            return false;
        }
        return id != null && id.equals(((Task) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Task{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", createDate='" + getCreateDate() + "'" +
            ", updatedDate='" + getUpdatedDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", type='" + getType() + "'" +
            ", effortHrs=" + getEffortHrs() +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            "}";
    }
}
