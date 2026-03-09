# Notizblock Webanwendung

Eine vollständige Quarkus Webanwendung für die Verwaltung von Notizen mit JSF und PrimeFaces, inklusive Detailansicht
und automatischer Änderungshistorie.

## Tech-Stack

- **Quarkus 3.31** - Supersonic Subatomic Java Framework
- **JSF (JavaServer Faces) via Apache MyFaces** - MVC Framework für die UI (wird von `quarkus-primefaces` unter der Haube mitgeliefert und ermöglicht den Einsatz von JSF in Quarkus)
- **PrimeFaces (Quarkus Extension)** - Die `quarkus-primefaces` Extension ermöglicht darüber hinaus die Verwendung der PrimeFaces UI-Komponentenbibliothek mit Quarkus
- **JPA (Jakarta Persistence API) / Hibernate ORM** - ORM für Datenbankzugriffe (via `quarkus-hibernate-orm`)
- **CDI (Contexts and Dependency Injection)** - Dependency Injection (integriert in Quarkus)
- **H2 Database** - In-Memory Datenbank (via `quarkus-jdbc-h2`)
- **Maven** - Build Management

## Projektstruktur

```
notizblock/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/gepardec/notizblock/
│   │   │       ├── entity/
│   │   │       │   ├── Note.java              # JPA Entity
│   │   │       │   ├── NoteHistory.java       # Historie-Entity
│   │   │       │   └── ChangeType.java        # Enum (CREATED, UPDATED, DELETED)
│   │   │       ├── repository/
│   │   │       │   ├── NoteRepository.java    # Note CRUD + Auto-History
│   │   │       │   └── NoteHistoryRepository.java  # Historie-Abfragen
│   │   │       └── bean/
│   │   │           ├── NoteBean.java          # JSF Backing Bean (Übersicht)
│   │   │           └── NoteDetailBean.java    # JSF Backing Bean (Detail)
│   │   └── resources/
│   │       ├── META-INF/
│   │       │   ├── web.xml                    # JSF Servlet Config
│   │       │   └── resources/
│   │       │       ├── resources/
│   │       │       │   └── components/
│   │       │       │       ├── layout/
│   │       │       │       │   └── template.xhtml     # Facelets Template
│   │       │       │       ├── noteTable.xhtml        # DataTable Komponente
│   │       │       │       ├── noteFormDialog.xhtml   # Erstellen/Bearbeiten Dialog
│   │       │       │       └── deleteConfirmDialog.xhtml  # Lösch-Bestätigung
│   │       │       ├── index.xhtml            # Übersichtsseite
│   │       │       └── detail.xhtml           # Detailseite mit Historie
│   │       └── application.properties         # Quarkus Konfiguration
└── pom.xml                                    # Maven Dependencies
```

## Features

### 1. Notizen-Verwaltung (CRUD)

#### Übersichtsseite (index.xhtml)

- **Alle Notizen anzeigen** in interaktiver PrimeFaces DataTable
    - Spalten: Titel, Inhalt (gekürzt), Erstellungsdatum
    - Pagination (5, 10, 20 Einträge pro Seite)
    - Sortierung und Filterung nach Titel

- **Notiz erstellen**
    - Button "Neue Notiz" öffnet Dialog
    - Validierung für Titel und Inhalt
    - AJAX-Update der Liste nach dem Speichern

- **Notiz bearbeiten**
    - Stift-Icon öffnet Dialog mit vorausgefüllten Daten
    - AJAX-Update nach dem Speichern

- **Notiz löschen**
    - Mülleimer-Icon zeigt Bestätigungsdialog
    - AJAX-Update nach dem Löschen

- **Detail anzeigen**
    - Lupen-Icon navigiert zur Detailseite
    - Übergabe der Notiz-ID als Query-Parameter

### 2. Detailseite (detail.xhtml)

Die Detailseite zeigt vollständige Informationen zu einer Notiz:

- **Titel** - Vollständiger Titel
- **Inhalt** - Kompletter Inhalt (ohne Kürzung)
- **Erstellungsdatum** - Wann die Notiz erstellt wurde
- **Letztes Änderungsdatum** - Zeitpunkt der letzten Bearbeitung
- **Navigation** - "Zurück zur Liste" Button

### 3. Änderungshistorie

**Automatisches Tracking:**

- Jede CRUD-Operation (Create, Update, Delete) wird automatisch in der `NoteHistory`-Tabelle protokolliert
- Implementiert als transparenter Service im `NoteRepository`

**Timeline-Darstellung:**

- Vertikale Timeline mit allen Änderungen (älteste zuerst)
- Farbcodierte Icons:
    - 🟢 Grün: Notiz erstellt
    - 🟠 Orange: Notiz bearbeitet
- Zeitstempel für jede Änderung
- CSS-basierte Timeline (kein externes Plugin erforderlich)

### 4. Komponentenbasierte Architektur

**Facelets Template** (`layout/template.xhtml`):

- Wiederverwendbares Layout mit Header, Content-Bereich und Footer
- Konsistentes Design über alle Seiten
- Responsive Navigation

**Wiederverwendbare UI-Komponenten**:

- `noteTable.xhtml` - DataTable mit allen Action-Buttons
- `noteFormDialog.xhtml` - Erstellen/Bearbeiten Dialog
- `deleteConfirmDialog.xhtml` - Lösch-Bestätigung

**Vorteile**:

- DRY-Prinzip (Don't Repeat Yourself)
- Einfachere Wartung
- Konsistente UI

## Installation & Start

### Voraussetzungen

- **Java 17** oder höher
- **Maven 3.8+**

### Build

```bash
# Projekt klonen oder entpacken
cd notizblock

# Maven Build
mvn clean package
```

### Entwicklungsmodus (Dev Mode)

Quarkus bietet einen Entwicklungsmodus mit Live-Reload:

```bash
mvn quarkus:dev
```

Dies startet die Anwendung mit:

- **Live-Reload** - Änderungen an Code und Ressourcen werden automatisch übernommen
- **Dev UI** - Verfügbar unter `http://localhost:8080/notizblock/q/dev/`
- **Detaillierte Fehlermeldungen**

### Produktion

```bash
mvn clean package
java -jar target/quarkus-app/quarkus-run.jar
```

## Anwendung aufrufen

Nach erfolgreichem Start:

```
http://localhost:8080/notizblock/           # Übersichtsseite
http://localhost:8080/notizblock/detail?id=1   # Detailseite (Beispiel mit ID 1)
```

## Konfiguration

Die gesamte Konfiguration erfolgt über `application.properties`:

```properties
# Application Context Path
quarkus.http.root-path=/notizblock

# Datasource Configuration (H2 In-Memory)
quarkus.datasource.db-kind=h2
quarkus.datasource.jdbc.url=jdbc:h2:mem:notizblockdb

# Hibernate ORM
quarkus.hibernate-orm.schema-management.strategy=drop-and-create
```

Die H2 In-Memory Datenbank wird automatisch konfiguriert – keine manuelle DataSource-Einrichtung nötig.

## Datenmodell

### Note Entity

| Feld        | Typ           | Beschreibung                                        |
|-------------|---------------|-----------------------------------------------------|
| `id`        | Long          | Primary Key, auto-generiert                         |
| `title`     | String        | Titel der Notiz (max. 255 Zeichen)                  |
| `content`   | String        | Inhalt der Notiz (max. 5000 Zeichen)                |
| `createdAt` | LocalDateTime | Erstellungsdatum (automatisch via @PrePersist)      |
| `updatedAt` | LocalDateTime | Letztes Änderungsdatum (automatisch via @PreUpdate) |

### NoteHistory Entity

| Feld         | Typ           | Beschreibung                         |
|--------------|---------------|--------------------------------------|
| `id`         | Long          | Primary Key, auto-generiert          |
| `note`       | Note          | ManyToOne-Relation zur Notiz         |
| `changeType` | ChangeType    | Enum: CREATED, UPDATED, DELETED      |
| `changedAt`  | LocalDateTime | Zeitpunkt der Änderung (automatisch) |

### ChangeType Enum

```java
CREATED   // Notiz wurde erstellt
        UPDATED   // Notiz wurde bearbeitet
```

Jeder Wert hat:

- `displayName` - Anzeigetext für die UI
- `icon` - PrimeIcons CSS-Klasse

## Technische Details

### Architektur-Patterns

**Repository Pattern:**

- Trennung von Business-Logik und Datenzugriff
- `NoteRepository` und `NoteHistoryRepository` kapseln alle DB-Operationen

**Backing Bean Pattern:**

- `NoteBean` (@ViewScoped) für Übersichtsseite
- `NoteDetailBean` (@ViewScoped) für Detailseite mit ViewParam-Support

**Component-Based UI:**

- Facelets Template für konsistentes Layout
- `<ui:composition>` und `<ui:include>` für Komponenten-Wiederverwendung

### Automatisches History-Tracking

Implementierung in `NoteRepository`:

```java

@Transactional
public Note create(Note note) {
    entityManager.persist(note);
    entityManager.flush();
    historyRepository.createHistoryEntry(note, ChangeType.CREATED);
    return note;
}
```

Bei jeder Operation (`create`, `update`, `delete`) wird automatisch ein History-Eintrag erstellt.

### ViewParameter-Verarbeitung

Die Detailseite nutzt JSF ViewParams für die ID-Übergabe:

```xml

<f:metadata>
    <f:viewParam name="id" value="#{noteDetailBean.id}" required="true"/>
    <f:viewAction action="#{noteDetailBean.init}"/>
</f:metadata>
```

**Wichtig**: Die `init()`-Methode wird über `<f:viewAction>` aufgerufen (nicht `@PostConstruct`), damit der
ViewParameter bereits gebunden ist.

### Validierung

- **Jakarta Bean Validation** Annotations in der Entity (@NotBlank, etc.)
- **JSF Required-Validierung** in der UI (required="true")
- **Client-Side Validation** durch PrimeFaces
- **Server-Side Validation** vor dem Persistieren

### Transaktionsverwaltung

- Transaktionen via `@Transactional` im Repository
- Automatisches Rollback bei Exceptions

### CDI Scopes

- `@ApplicationScoped` - Repositories (Singleton)
- `@ViewScoped` - Backing Beans (Pro View-Instanz)
- Automatische Dependency Injection via `@Inject`

## UI/UX Features

### PrimeFaces Komponenten

- `p:dataTable` - Datentabelle mit Pagination, Sortierung, Filterung
- `p:dialog` - Modale Dialoge für CRUD-Operationen
- `p:growl` - Toast-Benachrichtigungen
- `p:card` - Karten-Layout für Timeline
- `p:button` - Navigation ohne AJAX
- `p:commandButton` - AJAX-fähige Buttons

### Responsive Design

- Viewport Meta-Tag für mobile Geräte
- PrimeFaces responsive grid system
- CSS-basierte Timeline passt sich an

### AJAX-Updates

Alle Operationen nutzen AJAX für bessere UX:

- `update=":mainForm:notesTable"` - Aktualisiert nur die Tabelle
- `process="@this"` - Verarbeitet nur den Button
- `oncomplete` - Callback nach erfolgreichem Update

## Troubleshooting

### Hibernate DDL-Fehler

Die `application.properties` nutzt `drop-and-create` für Development. Die Datenbank wird bei jedem Neustart neu erstellt.

### "Keine Notiz-ID angegeben" beim Öffnen der Detailseite

- Stelle sicher, dass die URL den `id`-Parameter enthält: `detail.xhtml?id=1`
- Prüfe, dass `<f:viewAction>` in der detail.xhtml vorhanden ist
- Die `init()`-Methode darf NICHT `@PostConstruct` haben

### Timeline wird nicht angezeigt

- CSS-Styles müssen im `<ui:define name="head">` Block sein
- Browser-Cache leeren nach CSS-Änderungen

## Best Practices

1. **Transaktionen**: Alle DB-Operationen in `@Transactional`-Methoden
2. **Error Handling**: Try-Catch in Bean-Methoden mit FacesMessage
3. **Lazy Loading**: `@ManyToOne(fetch = FetchType.LAZY)` für bessere Performance
4. **Validation**: Validierung auf Entity- UND UI-Ebene
5. **Komponenten**: Wiederverwendbare XHTML-Komponenten für DRY
6. **Separation of Concerns**: Repository → Service-Logik, Bean → UI-Logik

## Erweiterungsmöglichkeiten

- **Benutzer-Authentifizierung**: Login/Logout mit Quarkus Security
- **Tags/Kategorien**: Notizen kategorisieren und filtern
- **Volltextsuche**: Suche im Inhalt aller Notizen
- **Export/Import**: JSON oder XML Export
- **Anhänge**: Dateien an Notizen anhängen
- **Rich-Text Editor**: CKEditor oder TinyMCE Integration
- **REST API**: Quarkus RESTEasy Endpoints für externe Clients
- **Persistente DB**: PostgreSQL oder MySQL statt H2 (via `quarkus-jdbc-postgresql` etc.)

## Migration von Jakarta EE (JSF) nach Quarkus – TODO-Checkliste

Die folgende Checkliste gibt einen schnellen Überblick über die notwendigen Schritte, wenn man ein bestehendes JSF-Frontend aus einer Jakarta EE Anwendung (z.B. WildFly) nach Quarkus migrieren möchte.

### Die Schlüsselrolle von Apache MyFaces

Das „Arbeitstier" hinter JSF in Quarkus ist die **Quarkus Extension von Apache MyFaces** (`quarkus-myfaces`). Sie stellt die Implementierung des JSF-Standards (Jakarta Faces) für Quarkus bereit und macht den Einsatz von JSF in Quarkus überhaupt erst möglich.

Im konkreten Fall dieser Anwendung wird **`quarkus-primefaces`** verwendet – diese Extension liefert Apache MyFaces bereits als transitive Abhängigkeit mit und stellt darüber hinaus die PrimeFaces UI-Komponentenbibliothek in Quarkus-kompatibler Form bereit. Man benötigt also nur eine einzige Dependency:

```xml
<dependency>
    <groupId>io.quarkiverse.primefaces</groupId>
    <artifactId>quarkus-primefaces</artifactId>
</dependency>
```

### TODO-Liste

- [ ] **Dependencies austauschen**
  - `jakarta.jakartaee-api` (provided) entfernen
  - `primefaces` (mit `jakarta` Classifier) entfernen
  - Stattdessen `quarkus-primefaces` hinzufügen (bringt MyFaces + PrimeFaces mit)
  - Quarkus-spezifische Dependencies hinzufügen: `quarkus-hibernate-orm`, `quarkus-jdbc-h2`, `quarkus-hibernate-validator`
  - Quarkus BOM im `<dependencyManagement>` einbinden

- [ ] **Build-Konfiguration anpassen**
  - `<packaging>war</packaging>` entfernen (Quarkus baut standardmäßig ein JAR)
  - `maven-war-plugin` entfernen
  - `wildfly-maven-plugin` durch `quarkus-maven-plugin` ersetzen

- [ ] **XHTML-Dateien verschieben**
  - Von `src/main/webapp/` nach `src/main/resources/META-INF/resources/`
  - Facelets-Seiten (`index.xhtml`, `detail.xhtml`, etc.) sowie Composite Components und Templates

- [ ] **web.xml verschieben**
  - Von `src/main/webapp/WEB-INF/web.xml` nach `src/main/resources/META-INF/web.xml`

- [ ] **Konfigurationsdateien ersetzen**
  - `persistence.xml` entfernen → Konfiguration über `application.properties` (`quarkus.datasource.*`, `quarkus.hibernate-orm.*`)
  - `beans.xml` entfernen → CDI ist in Quarkus standardmäßig aktiv
  - WildFly-spezifische DataSource-Dateien (`*-ds.xml`) entfernen → Konfiguration über `application.properties`

- [ ] **EntityManager-Injection anpassen**
  - `@PersistenceContext(unitName = "...")` durch `@Inject` ersetzen
  - `private` Sichtbarkeit auf package-private ändern (Quarkus CDI erfordert dies)

- [ ] **Anwendung testen**
  - `mvn quarkus:dev` starten und alle JSF-Seiten durchklicken
  - AJAX-Updates, Dialoge, Navigation und Validierung prüfen
  - Sicherstellen, dass PrimeFaces-Komponenten korrekt gerendert werden

## Lizenz

Dieses Projekt ist ein Beispielprojekt für Lernzwecke.
