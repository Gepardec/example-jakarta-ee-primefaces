package com.gepardec.notizblock.resource;

import com.gepardec.notizblock.dto.ErrorResponse;
import com.gepardec.notizblock.dto.NoteDTO;
import com.gepardec.notizblock.dto.NoteHistoryDTO;
import com.gepardec.notizblock.entity.Note;
import com.gepardec.notizblock.entity.NoteHistory;
import com.gepardec.notizblock.exception.NotFoundException;
import com.gepardec.notizblock.mapper.NoteHistoryMapper;
import com.gepardec.notizblock.mapper.NoteMapper;
import com.gepardec.notizblock.repository.NoteHistoryRepository;
import com.gepardec.notizblock.repository.NoteRepository;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

/**
 * JAX-RS REST Resource für Notizen-Verwaltung
 * Konvertiert vom JSF Backing Bean (NoteBean) zu REST API
 */
@Path("/api/notes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Notes", description = "Notizen-Verwaltung API")
public class NoteResource {

    @Inject
    NoteRepository noteRepository;

    @Inject
    NoteHistoryRepository noteHistoryRepository;

    @Inject
    NoteMapper noteMapper;

    @Inject
    NoteHistoryMapper noteHistoryMapper;

    /**
     * Gibt alle Notizen zurück (sortiert nach Erstellungsdatum, neueste zuerst)
     * @return Liste aller Notizen als DTOs
     */
    @GET
    @Operation(summary = "Alle Notizen abrufen", description = "Gibt eine Liste aller Notizen zurück, sortiert nach Erstellungsdatum (neueste zuerst)")
    @APIResponse(responseCode = "200", description = "Erfolg - Liste der Notizen")
    public List<NoteDTO> listAll() {
        List<Note> notes = noteRepository.findAll();
        return noteMapper.toDTOList(notes);
    }

    /**
     * Ruft eine einzelne Notiz anhand ihrer ID ab
     * @param id Die ID der Notiz
     * @return Die Notiz als DTO
     */
    @GET
    @Path("/{id}")
    @Operation(summary = "Eine Notiz abrufen", description = "Gibt eine einzelne Notiz anhand ihrer ID zurück")
    @APIResponse(responseCode = "200", description = "Notiz gefunden")
    @APIResponse(responseCode = "404", description = "Notiz nicht gefunden")
    public Response getById(@Parameter(description = "Notiz-ID") @PathParam("id") Long id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Notiz mit ID " + id + " nicht gefunden"));

        return Response.ok(noteMapper.toDTO(note)).build();
    }

    /**
     * Erstellt eine neue Notiz
     * @param noteDTO Die zu erstellende Notiz (ohne ID)
     * @return Die erstellte Notiz mit generierter ID
     */
    @POST
    @Operation(summary = "Neue Notiz erstellen", description = "Erstellt eine neue Notiz und gibt sie mit generierter ID zurück")
    @APIResponse(responseCode = "201", description = "Notiz erfolgreich erstellt")
    @APIResponse(responseCode = "400", description = "Ungültige Eingabedaten (Validierungsfehler)")
    public Response create(@Valid NoteDTO noteDTO) {
        try {
            // DTO zu Entity konvertieren
            Note note = noteMapper.toEntity(noteDTO);

            // Notiz speichern (Repository erstellt auch History-Eintrag)
            Note createdNote = noteRepository.create(note);

            // Entity zurück zu DTO konvertieren
            NoteDTO createdDTO = noteMapper.toDTO(createdNote);

            return Response.status(Response.Status.CREATED)
                    .entity(createdDTO)
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(500, "Fehler beim Erstellen der Notiz: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Aktualisiert eine bestehende Notiz
     * @param id Die ID der zu aktualisierenden Notiz
     * @param noteDTO Die aktualisierten Notiz-Daten
     * @return Die aktualisierte Notiz
     */
    @PUT
    @Path("/{id}")
    @Operation(summary = "Notiz aktualisieren", description = "Aktualisiert eine bestehende Notiz")
    @APIResponse(responseCode = "200", description = "Notiz erfolgreich aktualisiert")
    @APIResponse(responseCode = "404", description = "Notiz nicht gefunden")
    @APIResponse(responseCode = "400", description = "Ungültige Eingabedaten")
    public Response update(@Parameter(description = "Notiz-ID") @PathParam("id") Long id,
                           @Valid NoteDTO noteDTO) {
        try {
            // Prüfen ob Notiz existiert
            noteRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Notiz mit ID " + id + " nicht gefunden"));

            // DTO zu Entity konvertieren und ID setzen
            Note note = noteMapper.toEntity(noteDTO);
            note.setId(id);

            // Notiz aktualisieren (Repository erstellt auch History-Eintrag)
            Note updatedNote = noteRepository.update(note);

            // Entity zurück zu DTO konvertieren
            NoteDTO updatedDTO = noteMapper.toDTO(updatedNote);

            return Response.ok(updatedDTO).build();
        } catch (NotFoundException e) {
            throw e; // Wird von NotFoundExceptionMapper behandelt
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(500, "Fehler beim Aktualisieren der Notiz: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Löscht eine Notiz anhand ihrer ID
     * @param id Die ID der zu löschenden Notiz
     * @return 204 No Content bei Erfolg
     */
    @DELETE
    @Path("/{id}")
    @Operation(summary = "Notiz löschen", description = "Löscht eine Notiz anhand ihrer ID")
    @APIResponse(responseCode = "204", description = "Notiz erfolgreich gelöscht")
    @APIResponse(responseCode = "404", description = "Notiz nicht gefunden")
    public Response delete(@Parameter(description = "Notiz-ID") @PathParam("id") Long id) {
        try {
            // Prüfen ob Notiz existiert
            noteRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Notiz mit ID " + id + " nicht gefunden"));

            // Notiz löschen (Repository löscht auch History-Einträge)
            noteRepository.delete(id);

            return Response.noContent().build();
        } catch (NotFoundException e) {
            throw e; // Wird von NotFoundExceptionMapper behandelt
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(500, "Fehler beim Löschen der Notiz: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Gibt die Änderungshistorie einer Notiz zurück
     * @param id Die ID der Notiz
     * @return Liste aller History-Einträge für diese Notiz
     */
    @GET
    @Path("/{id}/history")
    @Operation(summary = "Notiz-Historie abrufen", description = "Gibt alle Änderungen einer Notiz zurück (sortiert nach Zeitstempel)")
    @APIResponse(responseCode = "200", description = "Historie erfolgreich abgerufen")
    @APIResponse(responseCode = "404", description = "Notiz nicht gefunden")
    public Response getHistory(@Parameter(description = "Notiz-ID") @PathParam("id") Long id) {
        // Prüfen ob Notiz existiert
        noteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Notiz mit ID " + id + " nicht gefunden"));

        // History-Einträge abrufen
        List<NoteHistory> historyList = noteHistoryRepository.findByNoteId(id);

        // Zu DTOs konvertieren
        List<NoteHistoryDTO> historyDTOs = noteHistoryMapper.toDTOList(historyList);

        return Response.ok(historyDTOs).build();
    }
}
