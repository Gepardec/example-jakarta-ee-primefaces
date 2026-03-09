package com.gepardec.notizblock.mapper;

import com.gepardec.notizblock.dto.NoteDTO;
import com.gepardec.notizblock.entity.Note;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * MapStruct Mapper für Konvertierung zwischen Note Entity und NoteDTO
 * MapStruct generiert die Implementierung automatisch zur Compile-Time
 */
@Mapper(componentModel = "cdi")
public interface NoteMapper {

    /**
     * Konvertiert eine Note Entity zu einem NoteDTO
     * @param entity Note Entity
     * @return NoteDTO
     */
    NoteDTO toDTO(Note entity);

    /**
     * Konvertiert ein NoteDTO zu einer Note Entity
     * @param dto NoteDTO
     * @return Note Entity
     */
    Note toEntity(NoteDTO dto);

    /**
     * Konvertiert eine Liste von Note Entities zu NoteDTOs
     * @param entities Liste von Note Entities
     * @return Liste von NoteDTOs
     */
    List<NoteDTO> toDTOList(List<Note> entities);

    /**
     * Konvertiert eine Liste von NoteDTOs zu Note Entities
     * @param dtos Liste von NoteDTOs
     * @return Liste von Note Entities
     */
    List<Note> toEntityList(List<NoteDTO> dtos);
}
