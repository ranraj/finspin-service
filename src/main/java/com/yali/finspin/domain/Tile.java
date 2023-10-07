package com.yali.finspin.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yali.finspin.domain.enumeration.DisplayMode;
import com.yali.finspin.domain.enumeration.DisplaySize;
import com.yali.finspin.domain.enumeration.PositionMode;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A Tile.
 */
@Document(collection = "tile")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "tile")
public class Tile implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("position_x")
    private Long positionX;

    @Field("position_y")
    private Long positionY;

    @Field("color")
    private String color;

    @Field("position_mode")
    private PositionMode positionMode;

    @Field("height")
    private Long height;

    @Field("width")
    private Long width;

    @Field("display_size")
    private DisplaySize displaySize;

    @Field("display_mode")
    private DisplayMode displayMode;

    @Field("task")
    private Task task;

    @Field("board")
    @JsonIgnoreProperties(value = { "boards", "dashBoard" }, allowSetters = true)
    private Board board;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Tile id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getPositionX() {
        return this.positionX;
    }

    public Tile positionX(Long positionX) {
        this.setPositionX(positionX);
        return this;
    }

    public void setPositionX(Long positionX) {
        this.positionX = positionX;
    }

    public Long getPositionY() {
        return this.positionY;
    }

    public Tile positionY(Long positionY) {
        this.setPositionY(positionY);
        return this;
    }

    public void setPositionY(Long positionY) {
        this.positionY = positionY;
    }

    public String getColor() {
        return this.color;
    }

    public Tile color(String color) {
        this.setColor(color);
        return this;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public PositionMode getPositionMode() {
        return this.positionMode;
    }

    public Tile positionMode(PositionMode positionMode) {
        this.setPositionMode(positionMode);
        return this;
    }

    public void setPositionMode(PositionMode positionMode) {
        this.positionMode = positionMode;
    }

    public Long getHeight() {
        return this.height;
    }

    public Tile height(Long height) {
        this.setHeight(height);
        return this;
    }

    public void setHeight(Long height) {
        this.height = height;
    }

    public Long getWidth() {
        return this.width;
    }

    public Tile width(Long width) {
        this.setWidth(width);
        return this;
    }

    public void setWidth(Long width) {
        this.width = width;
    }

    public DisplaySize getDisplaySize() {
        return this.displaySize;
    }

    public Tile displaySize(DisplaySize displaySize) {
        this.setDisplaySize(displaySize);
        return this;
    }

    public void setDisplaySize(DisplaySize displaySize) {
        this.displaySize = displaySize;
    }

    public DisplayMode getDisplayMode() {
        return this.displayMode;
    }

    public Tile displayMode(DisplayMode displayMode) {
        this.setDisplayMode(displayMode);
        return this;
    }

    public void setDisplayMode(DisplayMode displayMode) {
        this.displayMode = displayMode;
    }

    public Task getTask() {
        return this.task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Tile task(Task task) {
        this.setTask(task);
        return this;
    }

    public Board getBoard() {
        return this.board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Tile board(Board board) {
        this.setBoard(board);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tile)) {
            return false;
        }
        return id != null && id.equals(((Tile) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Tile{" +
            "id=" + getId() +
            ", positionX=" + getPositionX() +
            ", positionY=" + getPositionY() +
            ", color='" + getColor() + "'" +
            ", positionMode='" + getPositionMode() + "'" +
            ", height=" + getHeight() +
            ", width=" + getWidth() +
            ", displaySize='" + getDisplaySize() + "'" +
            ", displayMode='" + getDisplayMode() + "'" +
            "}";
    }
}
