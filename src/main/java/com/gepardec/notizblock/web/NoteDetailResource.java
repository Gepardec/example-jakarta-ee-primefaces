package com.gepardec.notizblock.web;

import com.gepardec.notizblock.entity.Note;
import com.gepardec.notizblock.entity.NoteHistory;
import com.gepardec.notizblock.repository.NoteHistoryRepository;
import com.gepardec.notizblock.repository.NoteRepository;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/notes")
@Produces(MediaType.TEXT_HTML)
public class NoteDetailResource {

    @Inject Template note_detail; // templates/note_detail.html
    @Inject NoteRepository noteRepository;
    @Inject NoteHistoryRepository historyRepository;

    @GET
    @Path("/{id}")
    public TemplateInstance show(@PathParam("id") Long id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Notiz mit ID " + id + " wurde nicht gefunden"));

        List<NoteHistory> history = historyRepository.findByNote(note);

        return note_detail
                .data("note", note)
                .data("history", history)
                .data("historyCount", history != null ? history.size() : 0);
    }
}
