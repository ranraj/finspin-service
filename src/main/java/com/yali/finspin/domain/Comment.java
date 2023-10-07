package com.yali.finspin.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A Comment.
 */
@Document(collection = "comment")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "comment")
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("created_date")
    private Instant createdDate;

    @Field("updated_date")
    private Instant updatedDate;

    @Field("content")
    private String content;

    @Field("disabled")
    private Boolean disabled;

    @Field("up_vote")
    private Long upVote;

    @Field("down_vote")
    private Long downVote;

    @Field("perm_link")
    private String permLink;

    @Field("owner")
    private User owner;

    @Field("task")
    @JsonIgnoreProperties(
        value = { "owner", "createdBy", "sprint", "orgGroup", "assignedTo", "parent", "watchers", "comments", "tags" },
        allowSetters = true
    )
    private Task task;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Comment id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Instant getCreatedDate() {
        return this.createdDate;
    }

    public Comment createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Instant getUpdatedDate() {
        return this.updatedDate;
    }

    public Comment updatedDate(Instant updatedDate) {
        this.setUpdatedDate(updatedDate);
        return this;
    }

    public void setUpdatedDate(Instant updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getContent() {
        return this.content;
    }

    public Comment content(String content) {
        this.setContent(content);
        return this;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getDisabled() {
        return this.disabled;
    }

    public Comment disabled(Boolean disabled) {
        this.setDisabled(disabled);
        return this;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public Long getUpVote() {
        return this.upVote;
    }

    public Comment upVote(Long upVote) {
        this.setUpVote(upVote);
        return this;
    }

    public void setUpVote(Long upVote) {
        this.upVote = upVote;
    }

    public Long getDownVote() {
        return this.downVote;
    }

    public Comment downVote(Long downVote) {
        this.setDownVote(downVote);
        return this;
    }

    public void setDownVote(Long downVote) {
        this.downVote = downVote;
    }

    public String getPermLink() {
        return this.permLink;
    }

    public Comment permLink(String permLink) {
        this.setPermLink(permLink);
        return this;
    }

    public void setPermLink(String permLink) {
        this.permLink = permLink;
    }

    public User getOwner() {
        return this.owner;
    }

    public void setOwner(User user) {
        this.owner = user;
    }

    public Comment owner(User user) {
        this.setOwner(user);
        return this;
    }

    public Task getTask() {
        return this.task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Comment task(Task task) {
        this.setTask(task);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Comment)) {
            return false;
        }
        return id != null && id.equals(((Comment) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Comment{" +
            "id=" + getId() +
            ", createdDate='" + getCreatedDate() + "'" +
            ", updatedDate='" + getUpdatedDate() + "'" +
            ", content='" + getContent() + "'" +
            ", disabled='" + getDisabled() + "'" +
            ", upVote=" + getUpVote() +
            ", downVote=" + getDownVote() +
            ", permLink='" + getPermLink() + "'" +
            "}";
    }
}
