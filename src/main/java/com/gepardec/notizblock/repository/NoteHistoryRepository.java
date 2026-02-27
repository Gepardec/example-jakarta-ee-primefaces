package com.gepardec.notizblock.repository;

import com.gepardec.notizblock.entity.ChangeType;
import com.gepardec.notizblock.entity.Note;
import com.gepardec.notizblock.entity.NoteHistory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class NoteHistoryRepository {

    @Inject
    EntityManager entityManager;

    @Transactional
    public NoteHistory create(NoteHistory history) {
        entityManager.persist(history);
        // flush ist meistens nicht nötig, aber ok wenn du ID sofort brauchst
        entityManager.flush();
        return history;
    }

    @Transactional
    public NoteHistory createHistoryEntry(Note note, ChangeType changeType) {
        NoteHistory history = new NoteHistory(note, changeType);
        return create(history);
    }

    public List<NoteHistory> findByNoteId(Long noteId) {
        return entityManager.createQuery(
                        "SELECT h FROM NoteHistory h WHERE h.note.id = :noteId ORDER BY h.changedAt ASC",
                        NoteHistory.class)
                .setParameter("noteId", noteId)
                .getResultList();
    }

    /**
     * Empfohlen, wenn du im Template auf h.note.* zugreifst:
     * verhindert LazyInitializationException
     */
    public List<NoteHistory> findByNoteIdWithNote(Long noteId) {
        return entityManager.createQuery(
                        "SELECT h FROM NoteHistory h JOIN FETCH h.note WHERE h.note.id = :noteId ORDER BY h.changedAt ASC",
                        NoteHistory.class)
                .setParameter("noteId", noteId)
                .getResultList();
    }

    public List<NoteHistory> findByNote(Note note) {
        if (note == null || note.getId() == null) {
            return List.of();
        }
        return findByNoteId(note.getId());
        // oder: return findByNoteIdWithNote(note.getId());
    }

    public Long countByNoteId(Long noteId) {
        return entityManager.createQuery(
                        "SELECT COUNT(h) FROM NoteHistory h WHERE h.note.id = :noteId",
                        Long.class)
                .setParameter("noteId", noteId)
                .getSingleResult();
    }

    @Transactional
    public int deleteByNoteId(Long noteId) {
        return entityManager.createQuery(
                        "DELETE FROM NoteHistory h WHERE h.note.id = :noteId")
                .setParameter("noteId", noteId)
                .executeUpdate();
    }
}
