package com.gepardec.notizblock.web;

import com.gepardec.notizblock.entity.Note;
import com.gepardec.notizblock.repository.NoteRepository;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Path("/")
@Produces(MediaType.TEXT_HTML)
public class NoteResource {

    @Inject Template index;
    @Inject NoteRepository noteRepository;
    @Location("delete_confirm.html")
    Template deleteConfirm;


    @GET
    public TemplateInstance showIndex(@QueryParam("msg") String msg,
                                      @QueryParam("level") String level) {
        List<Note> notes = noteRepository.findAll();
        return index.data("notes", notes)
                .data("notesCount", notes != null ? notes.size() : 0)
                .data("msg", msg)
                .data("level", level)
                .data("form", new NoteForm());
    }
    @GET
    @Path("/notes/{id}/delete")
    public TemplateInstance confirmDelete(@PathParam("id") Long id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Notiz nicht gefunden"));
        return deleteConfirm.data("note", note);
    }

    @POST
    @Path("/notes/save")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response save(@BeanParam NoteForm form) {
        try {
            Note note = new Note(form.title, form.content);
            noteRepository.create(note);
            return redirect("/", "Notiz wurde erfolgreich erstellt", "info");
        } catch (Exception e) {
            return redirect("/", "Notiz konnte nicht gespeichert werden: " + e.getMessage(), "error");
        }
    }

    private Response redirect(String path, String msg, String level) {
        String target = path + "?level=" + enc(level) + "&msg=" + enc(msg);
        return Response.seeOther(URI.create(target)).build();
    }

    private String enc(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}
