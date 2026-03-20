package com.gepardec.notizblock.bean;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * SessionScoped Bean zur Demonstration von Session-Persistenz über Server-Restarts.
 *
 * Da diese Bean @SessionScoped ist und Serializable implementiert, wird sie von
 * WildFly/JBoss bei einem Graceful Shutdown passiviert (auf Disk geschrieben) und
 * beim nächsten Start wieder aktiviert (von Disk gelesen).
 *
 * Der Zustand (Username, Besuchszähler, Erstellzeit) überlebt dadurch einen Restart.
 */
@Named
@SessionScoped
public class UserSessionBean implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    /** Vom Benutzer eingegebener Name - bleibt über Restart erhalten */
    private String username = "";

    /** Zählt wie oft der Benutzer die Demo-Seite besucht hat */
    private int visitCount = 0;

    /** Zeitpunkt der Session-Erstellung */
    private LocalDateTime sessionCreatedAt;

    /** Zeitpunkt der letzten Seiten-Aktivität */
    private LocalDateTime lastActivityAt;

    /** Flag: wurde die Session nach einem Restart reaktiviert? */
    private boolean reactivatedAfterRestart = false;

    @PostConstruct
    public void init() {
        sessionCreatedAt = LocalDateTime.now();
        lastActivityAt = LocalDateTime.now();
    }

    /**
     * Wird von der Demo-Seite beim Laden aufgerufen.
     * Erhöht den Besuchszähler und prüft ob eine Reaktivierung stattgefunden hat.
     */
    public void onPageLoad() {
        visitCount++;
        lastActivityAt = LocalDateTime.now();

        // Wenn sessionCreatedAt vor dem letzten Request liegt und visitCount > 1,
        // könnte ein Restart dazwischen gewesen sein - wir markieren das.
        // (Vereinfachte Erkennung: in Produktion würde man eine persistierte
        //  Server-Startzeit vergleichen)
        if (visitCount > 1 && sessionCreatedAt != null &&
                sessionCreatedAt.isBefore(lastActivityAt.minusSeconds(5))) {
            reactivatedAfterRestart = true;
        }
    }

    public String getSessionCreatedAtFormatted() {
        return sessionCreatedAt != null ? sessionCreatedAt.format(FORMATTER) : "-";
    }

    public String getLastActivityAtFormatted() {
        return lastActivityAt != null ? lastActivityAt.format(FORMATTER) : "-";
    }

    // Getter & Setter

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getVisitCount() {
        return visitCount;
    }

    public LocalDateTime getSessionCreatedAt() {
        return sessionCreatedAt;
    }

    public LocalDateTime getLastActivityAt() {
        return lastActivityAt;
    }

    public boolean isReactivatedAfterRestart() {
        return reactivatedAfterRestart;
    }
}
