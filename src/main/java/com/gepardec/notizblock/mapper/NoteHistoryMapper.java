package com.gepardec.notizblock.mapper;

import com.gepardec.notizblock.dto.NoteHistoryDTO;
import com.gepardec.notizblock.entity.NoteHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * MapStruct Mapper für Konvertierung zwischen NoteHistory Entity und NoteHistoryDTO
 * MapStruct generiert die Implementierung automatisch zur Compile-Time
 */
@Mapper(componentModel = "cdi")
public interface NoteHistoryMapper {

    /**
     * Konvertiert eine NoteHistory Entity zu einem NoteHistoryDTO
     * Mappt note.id zu noteId
     * @param entity NoteHistory Entity
     * @return NoteHistoryDTO
     */
    @Mapping(source = "note.id", target = "noteId")
    NoteHistoryDTO toDTO(NoteHistory entity);

    /**
     * Konvertiert eine Liste von NoteHistory Entities zu NoteHistoryDTOs
     * @param entities Liste von NoteHistory Entities
     * @return Liste von NoteHistoryDTOs
     */
    List<NoteHistoryDTO> toDTOList(List<NoteHistory> entities);
}
