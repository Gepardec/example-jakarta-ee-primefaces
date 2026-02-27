package com.gepardec.notizblock.repository;

import com.gepardec.notizblock.entity.ChangeType;
import com.gepardec.notizblock.entity.Note;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class NoteRepository {

    @Inject
    EntityManager entityManager;

    @Inject
    NoteHistoryRepository historyRepository;

    public List<Note> findAll() {
        return entityManager.createQuery(
                        "SELECT n FROM Note n ORDER BY n.createdAt DESC", Note.class)
                .getResultList();
    }

    public Optional<Note> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Note.class, id));
    }

    @Transactional
    public Note create(Note note) {
        entityManager.persist(note);
        entityManager.flush();
        historyRepository.createHistoryEntry(note, ChangeType.CREATED);
        return note;
    }

    @Transactional
    public Note update(Note note) {
        Note merged = entityManager.merge(note);
        entityManager.flush();
        historyRepository.createHistoryEntry(merged, ChangeType.UPDATED);
        return merged;
    }

    @Transactional
    public void delete(Long id) {
        findById(id).ifPresent(note -> {
            historyRepository.deleteByNoteId(id);

            Note managed = note;
            if (!entityManager.contains(managed)) {
                managed = entityManager.merge(managed);
            }
            entityManager.remove(managed);

            // Optional: history DELETED loggen (wenn du das willst)
            // historyRepository.createHistoryEntry(managed, ChangeType.DELETED); // Achtung: managed ist danach removed
        });
    }

    @Transactional
    public void delete(Note note) {
        if (note != null && note.getId() != null) {
            delete(note.getId());
        }
    }

    public Long count() {
        return entityManager.createQuery("SELECT COUNT(n) FROM Note n", Long.class)
                .getSingleResult();
    }
}
