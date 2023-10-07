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
 * A Dashboard.
 */
@Document(collection = "dashboard")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "dashboard")
public class Dashboard implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("name")
    private String name;

    @Field("created_date")
    private Instant createdDate;

    @Field("update_date")
    private Instant updateDate;

    @Field("board")
    @JsonIgnoreProperties(value = { "boards", "dashBoard" }, allowSetters = true)
    private Set<Board> boards = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Dashboard id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Dashboard name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getCreatedDate() {
        return this.createdDate;
    }

    public Dashboard createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Instant getUpdateDate() {
        return this.updateDate;
    }

    public Dashboard updateDate(Instant updateDate) {
        this.setUpdateDate(updateDate);
        return this;
    }

    public void setUpdateDate(Instant updateDate) {
        this.updateDate = updateDate;
    }

    public Set<Board> getBoards() {
        return this.boards;
    }

    public void setBoards(Set<Board> boards) {
        if (this.boards != null) {
            this.boards.forEach(i -> i.setDashBoard(null));
        }
        if (boards != null) {
            boards.forEach(i -> i.setDashBoard(this));
        }
        this.boards = boards;
    }

    public Dashboard boards(Set<Board> boards) {
        this.setBoards(boards);
        return this;
    }

    public Dashboard addBoard(Board board) {
        this.boards.add(board);
        board.setDashBoard(this);
        return this;
    }

    public Dashboard removeBoard(Board board) {
        this.boards.remove(board);
        board.setDashBoard(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Dashboard)) {
            return false;
        }
        return id != null && id.equals(((Dashboard) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Dashboard{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", updateDate='" + getUpdateDate() + "'" +
            "}";
    }
}
