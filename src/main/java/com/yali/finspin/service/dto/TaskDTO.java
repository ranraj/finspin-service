package com.yali.finspin.service.dto;

import com.yali.finspin.domain.enumeration.TaskStatus;
import com.yali.finspin.domain.enumeration.TaskType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.yali.finspin.domain.Task} entity.
 */
@Schema(description = "Task entity.\n@author The JHipster team.")
public class TaskDTO implements Serializable {

    private String id;

    private String title;

    private String description;

    private Instant createDate;

    private Instant updatedDate;

    private TaskStatus status;

    private TaskType type;

    private Long effortHrs;

    private Instant startDate;

    private Instant endDate;

    private UserDTO owner;

    private UserDTO createdBy;

    private SprintDTO sprint;

    private OrgGroupDTO orgGroup;

    private UserDTO assignedTo;

    private TaskDTO parent;

    private Set<UserDTO> watchers = new HashSet<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Instant createDate) {
        this.createDate = createDate;
    }

    public Instant getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Instant updatedDate) {
        this.updatedDate = updatedDate;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskType getType() {
        return type;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    public Long getEffortHrs() {
        return effortHrs;
    }

    public void setEffortHrs(Long effortHrs) {
        this.effortHrs = effortHrs;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public UserDTO getOwner() {
        return owner;
    }

    public void setOwner(UserDTO owner) {
        this.owner = owner;
    }

    public UserDTO getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserDTO createdBy) {
        this.createdBy = createdBy;
    }

    public SprintDTO getSprint() {
        return sprint;
    }

    public void setSprint(SprintDTO sprint) {
        this.sprint = sprint;
    }

    public OrgGroupDTO getOrgGroup() {
        return orgGroup;
    }

    public void setOrgGroup(OrgGroupDTO orgGroup) {
        this.orgGroup = orgGroup;
    }

    public UserDTO getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(UserDTO assignedTo) {
        this.assignedTo = assignedTo;
    }

    public TaskDTO getParent() {
        return parent;
    }

    public void setParent(TaskDTO parent) {
        this.parent = parent;
    }

    public Set<UserDTO> getWatchers() {
        return watchers;
    }

    public void setWatchers(Set<UserDTO> watchers) {
        this.watchers = watchers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TaskDTO)) {
            return false;
        }

        TaskDTO taskDTO = (TaskDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, taskDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TaskDTO{" +
            "id='" + getId() + "'" +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", createDate='" + getCreateDate() + "'" +
            ", updatedDate='" + getUpdatedDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", type='" + getType() + "'" +
            ", effortHrs=" + getEffortHrs() +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", owner=" + getOwner() +
            ", createdBy=" + getCreatedBy() +
            ", sprint=" + getSprint() +
            ", orgGroup=" + getOrgGroup() +
            ", assignedTo=" + getAssignedTo() +
            ", parent=" + getParent() +
            ", watchers=" + getWatchers() +
            "}";
    }
}
