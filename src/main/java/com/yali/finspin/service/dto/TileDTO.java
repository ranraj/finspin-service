package com.yali.finspin.service.dto;

import com.yali.finspin.domain.enumeration.DisplayMode;
import com.yali.finspin.domain.enumeration.DisplaySize;
import com.yali.finspin.domain.enumeration.PositionMode;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.yali.finspin.domain.Tile} entity.
 */
public class TileDTO implements Serializable {

    private String id;

    private Long positionX;

    private Long positionY;

    private String color;

    private PositionMode positionMode;

    private Long height;

    private Long width;

    private DisplaySize displaySize;

    private DisplayMode displayMode;

    private TaskDTO task;

    private BoardDTO board;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getPositionX() {
        return positionX;
    }

    public void setPositionX(Long positionX) {
        this.positionX = positionX;
    }

    public Long getPositionY() {
        return positionY;
    }

    public void setPositionY(Long positionY) {
        this.positionY = positionY;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public PositionMode getPositionMode() {
        return positionMode;
    }

    public void setPositionMode(PositionMode positionMode) {
        this.positionMode = positionMode;
    }

    public Long getHeight() {
        return height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }

    public Long getWidth() {
        return width;
    }

    public void setWidth(Long width) {
        this.width = width;
    }

    public DisplaySize getDisplaySize() {
        return displaySize;
    }

    public void setDisplaySize(DisplaySize displaySize) {
        this.displaySize = displaySize;
    }

    public DisplayMode getDisplayMode() {
        return displayMode;
    }

    public void setDisplayMode(DisplayMode displayMode) {
        this.displayMode = displayMode;
    }

    public TaskDTO getTask() {
        return task;
    }

    public void setTask(TaskDTO task) {
        this.task = task;
    }

    public BoardDTO getBoard() {
        return board;
    }

    public void setBoard(BoardDTO board) {
        this.board = board;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TileDTO)) {
            return false;
        }

        TileDTO tileDTO = (TileDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, tileDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TileDTO{" +
            "id='" + getId() + "'" +
            ", positionX=" + getPositionX() +
            ", positionY=" + getPositionY() +
            ", color='" + getColor() + "'" +
            ", positionMode='" + getPositionMode() + "'" +
            ", height=" + getHeight() +
            ", width=" + getWidth() +
            ", displaySize='" + getDisplaySize() + "'" +
            ", displayMode='" + getDisplayMode() + "'" +
            ", task=" + getTask() +
            ", board=" + getBoard() +
            "}";
    }
}
