package com.gepardec.notizblock.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gepardec.notizblock.entity.ChangeType;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Data Transfer Object für NoteHistory Entity
 * Read-only DTO für GET Requests
 */
public class NoteHistoryDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long noteId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private ChangeType changeType;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime changedAt;

    /**
     * Default Constructor
     */
    public NoteHistoryDTO() {
    }

    /**
     * All-Args Constructor
     */
    public NoteHistoryDTO(Long id, Long noteId, ChangeType changeType, LocalDateTime changedAt) {
        this.id = id;
        this.noteId = noteId;
        this.changeType = changeType;
        this.changedAt = changedAt;
    }

    // Getter und Setter

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNoteId() {
        return noteId;
    }

    public void setNoteId(Long noteId) {
        this.noteId = noteId;
    }

    public ChangeType getChangeType() {
        return changeType;
    }

    public void setChangeType(ChangeType changeType) {
        this.changeType = changeType;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NoteHistoryDTO that = (NoteHistoryDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "NoteHistoryDTO{" +
                "id=" + id +
                ", noteId=" + noteId +
                ", changeType=" + changeType +
                ", changedAt=" + changedAt +
                '}';
    }
}
