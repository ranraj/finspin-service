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
 * A Board.
 */
@Document(collection = "board")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "board")
public class Board implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("title")
    private String title;

    @Field("uid")
    private String uid;

    @Field("created_date")
    private Instant createdDate;

    @Field("update_date")
    private Instant updateDate;

    @Field("board")
    @JsonIgnoreProperties(value = { "task", "board" }, allowSetters = true)
    private Set<Tile> boards = new HashSet<>();

    @Field("dashBoard")
    @JsonIgnoreProperties(value = { "boards" }, allowSetters = true)
    private Dashboard dashBoard;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Board id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Board title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUid() {
        return this.uid;
    }

    public Board uid(String uid) {
        this.setUid(uid);
        return this;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Instant getCreatedDate() {
        return this.createdDate;
    }

    public Board createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Instant getUpdateDate() {
        return this.updateDate;
    }

    public Board updateDate(Instant updateDate) {
        this.setUpdateDate(updateDate);
        return this;
    }

    public void setUpdateDate(Instant updateDate) {
        this.updateDate = updateDate;
    }

    public Set<Tile> getBoards() {
        return this.boards;
    }

    public void setBoards(Set<Tile> tiles) {
        if (this.boards != null) {
            this.boards.forEach(i -> i.setBoard(null));
        }
        if (tiles != null) {
            tiles.forEach(i -> i.setBoard(this));
        }
        this.boards = tiles;
    }

    public Board boards(Set<Tile> tiles) {
        this.setBoards(tiles);
        return this;
    }

    public Board addBoard(Tile tile) {
        this.boards.add(tile);
        tile.setBoard(this);
        return this;
    }

    public Board removeBoard(Tile tile) {
        this.boards.remove(tile);
        tile.setBoard(null);
        return this;
    }

    public Dashboard getDashBoard() {
        return this.dashBoard;
    }

    public void setDashBoard(Dashboard dashboard) {
        this.dashBoard = dashboard;
    }

    public Board dashBoard(Dashboard dashboard) {
        this.setDashBoard(dashboard);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Board)) {
            return false;
        }
        return id != null && id.equals(((Board) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Board{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", uid='" + getUid() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", updateDate='" + getUpdateDate() + "'" +
            "}";
    }
}
