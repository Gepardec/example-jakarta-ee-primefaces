package com.gepardec.notizblock.exception;

import java.io.Serial;

/**
 * Custom Exception für "Not Found" Fälle in der REST API
 * Wird geworfen wenn eine Ressource (z.B. Note) nicht gefunden wurde
 */
public class NotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Constructor mit Fehlermeldung
     * @param message Fehlermeldung
     */
    public NotFoundException(String message) {
        super(message);
    }

    /**
     * Constructor mit Fehlermeldung und Ursache
     * @param message Fehlermeldung
     * @param cause Ursache
     */
    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
