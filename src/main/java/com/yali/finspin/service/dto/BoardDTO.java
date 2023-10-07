package com.yali.finspin.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.yali.finspin.domain.Board} entity.
 */
public class BoardDTO implements Serializable {

    private String id;

    private String title;

    private String uid;

    private Instant createdDate;

    private Instant updateDate;

    private DashboardDTO dashBoard;

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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Instant getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Instant updateDate) {
        this.updateDate = updateDate;
    }

    public DashboardDTO getDashBoard() {
        return dashBoard;
    }

    public void setDashBoard(DashboardDTO dashBoard) {
        this.dashBoard = dashBoard;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BoardDTO)) {
            return false;
        }

        BoardDTO boardDTO = (BoardDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, boardDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BoardDTO{" +
            "id='" + getId() + "'" +
            ", title='" + getTitle() + "'" +
            ", uid='" + getUid() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", updateDate='" + getUpdateDate() + "'" +
            ", dashBoard=" + getDashBoard() +
            "}";
    }
}
