# Notizblock Webanwendung

Eine moderne Full-Stack-Webanwendung für die Verwaltung von Notizen mit **Quarkus Backend** und **Angular Frontend**.

## Architektur

Das Projekt folgt einer modernen **REST-API-basierten Architektur**:

- **Backend**: Quarkus mit REST API
- **Frontend**: Angular Single-Page-Application mit Angular Material
- **Kommunikation**: REST API als Schnittstelle zwischen Frontend und Backend

## Tech-Stack

### Backend (Quarkus)

- **Quarkus 3.31** - Supersonic Subatomic Java Framework
- **Quarkus REST (RESTEasy Reactive)** - REST API Endpoints
- **JPA (Jakarta Persistence API) / Hibernate ORM** - ORM für Datenbankzugriffe
- **H2 Database** - In-Memory Datenbank für Entwicklung
- **MapStruct** - Entity-DTO Mapping
- **Bean Validation** - Validierung
- **OpenAPI / Swagger UI** - API Dokumentation
- **Maven** - Build Management

### Frontend (notizblock-angular)

- **Angular 21** - Modern TypeScript Framework
- **Angular Material** - UI Component Library
- **RxJS** - Reactive Programming
- **OpenAPI Generator** - Auto-generierter TypeScript API Client
- **npm** - Package Management

## Quick Start

```bash
# 1. Backend starten (Terminal 1)
mvn quarkus:dev

# 2. Frontend starten (Terminal 2)
cd notizblock-angular
npm install
npm start

# 3. Browser öffnen
# Angular: http://localhost:4200/
# Swagger: http://localhost:8080/notizblock/q/swagger-ui/
```

## Projektstruktur

```
example-jakarta-ee-primefaces/
├── src/                                       # Quarkus Backend
│   └── main/
│       ├── java/com/gepardec/notizblock/
│       │   ├── entity/                        # JPA Entities
│       │   │   ├── Note.java
│       │   │   ├── NoteHistory.java
│       │   │   └── ChangeType.java
│       │   ├── dto/                           # Data Transfer Objects
│       │   │   ├── NoteDTO.java
│       │   │   └── NoteHistoryDTO.java
│       │   ├── mapper/                        # MapStruct Mappers
│       │   │   ├── NoteMapper.java
│       │   │   └── NoteHistoryMapper.java
│       │   ├── repository/                    # JPA Repositories
│       │   │   ├── NoteRepository.java
│       │   │   └── NoteHistoryRepository.java
│       │   ├── resource/                      # REST API Endpoints
│       │   │   └── NoteResource.java
│       │   ├── filter/                        # HTTP Filters
│       │   │   └── CorsFilter.java
│       │   └── exception/                     # Exception Handling
│       │       ├── NoteNotFoundException.java
│       │       └── ...
│       └── resources/
│           └── application.properties         # Quarkus Konfiguration
│
├── notizblock-angular/                        # Angular Frontend
│   ├── src/
│   │   ├── app/
│   │   │   ├── api/                           # Auto-generierter API Client
│   │   │   ├── components/                    # Angular Components
│   │   │   ├── services/                      # Services
│   │   │   └── ...
│   │   └── ...
│   ├── package.json                           # npm Dependencies
│   └── angular.json                           # Angular Configuration
│
├── openapi.json                               # OpenAPI Specification
├── pom.xml                                    # Maven Backend Dependencies
└── README.md                                  # Diese Datei
```

## Features

### 1. Notizen-Verwaltung (CRUD)

**Übersichtsseite:**

- Alle Notizen in einer übersichtlichen Tabelle anzeigen
- Notizen erstellen, bearbeiten und löschen
- Sortierung und Filterung nach verschiedenen Kriterien
- Pagination für große Datensätze
- Responsive Design für mobile Geräte

**Detailansicht:**

- Vollständige Informationen zu einer Notiz
- Titel, Inhalt, Erstellungs- und Änderungsdatum
- Navigation zwischen Notizen

### 2. Automatische Änderungshistorie

**Backend Tracking:**

- Jede CRUD-Operation wird automatisch in der `NoteHistory`-Tabelle protokolliert
- Implementiert als transparenter Service im `NoteRepository`
- Änderungstypen: CREATED, UPDATED, DELETED

**Historie-Ansicht:**

- Timeline aller Änderungen einer Notiz
- Zeitstempel und Änderungstyp für jede Operation
- Chronologische Darstellung (älteste zuerst)

### 3. REST API

**Vollständige API-Abdeckung:**

- CRUD-Operationen für Notizen
- Historie-Abfragen
- OpenAPI/Swagger Dokumentation
- JSON-basierte Kommunikation
- Bean Validation für Request-Validierung

### 4. Modern Stack

**Angular Frontend:**

- Komponentenbasierte SPA-Architektur
- Angular Material für konsistente UI
- Reactive Programming mit RxJS
- Auto-generierter TypeScript API Client
- Hot-Reload Development

## Installation & Deployment

### Voraussetzungen

**Backend:**

- Java 17 oder höher
- Maven 3.8+

**Frontend:**

- Node.js 18+ und npm 9+
- Angular CLI (optional, wird über npx verwendet)

### Entwicklungsmodus

#### 1. Backend starten

```bash
# Terminal 1: Backend im Dev-Modus
mvn quarkus:dev
```

Das Backend startet mit:

- **REST API**: `http://localhost:8080/notizblock/api/notes`
- **Swagger UI**: `http://localhost:8080/notizblock/q/swagger-ui/`
- **Dev UI**: `http://localhost:8080/notizblock/q/dev/`
- **Live-Reload** für Java-Code

#### 2. Frontend starten

```bash
# Terminal 2: Frontend starten
cd notizblock-angular

# Dependencies installieren (nur beim ersten Mal)
npm install

# Angular Dev-Server starten
npm start
```

Das Angular Frontend läuft auf `http://localhost:4200/`

**Wichtig**: Der Angular Dev-Server nutzt einen Proxy, um API-Aufrufe an das Backend weiterzuleiten (siehe
`proxy.conf.json`).

### Produktion

#### Backend Build

```bash
# JAR Build
mvn clean package

# Starten
java -jar target/quarkus-app/quarkus-run.jar
```

#### Frontend Build

```bash
cd notizblock-angular

# Production Build
npm run build
```

Die Build-Artefakte befinden sich in `notizblock-angular/dist/`.

### Zugriff auf die Anwendung

**Frontend:**

```
http://localhost:4200/                         # Angular App
```

**Backend & API:**

```
http://localhost:8080/notizblock/api/notes              # REST Endpoints
http://localhost:8080/notizblock/q/swagger-ui/         # Swagger UI
http://localhost:8080/notizblock/q/dev/                # Quarkus Dev UI
```

## REST API

Das Backend stellt eine vollständige REST API bereit.

### API Endpoints

| Methode  | Endpoint                             | Beschreibung              |
|----------|--------------------------------------|---------------------------|
| `GET`    | `/notizblock/api/notes`              | Alle Notizen abrufen      |
| `GET`    | `/notizblock/api/notes/{id}`         | Einzelne Notiz abrufen    |
| `POST`   | `/notizblock/api/notes`              | Neue Notiz erstellen      |
| `PUT`    | `/notizblock/api/notes/{id}`         | Notiz aktualisieren       |
| `DELETE` | `/notizblock/api/notes/{id}`         | Notiz löschen             |
| `GET`    | `/notizblock/api/notes/{id}/history` | Änderungshistorie abrufen |

### API Dokumentation

- **OpenAPI Spec**: `openapi.json` im Projekt-Root
- **Swagger UI**: `http://localhost:8080/notizblock/q/swagger-ui/`
- Interaktive API-Dokumentation zum Testen aller Endpoints

### Angular API Client

Der Angular TypeScript API Client wird automatisch aus der OpenAPI-Spezifikation generiert:

```bash
cd notizblock-angular

# API Client manuell neu generieren
npm run generate-api
```

Der generierte Code befindet sich in `notizblock-angular/src/app/api/`.

**Automatische Generierung**: Der API Client wird bei `npm install` automatisch generiert (siehe `postinstall` Script in
`package.json`).

## Konfiguration

### Backend (application.properties)

```properties
# Application Context Path
quarkus.http.root-path=/notizblock
# Datasource (H2 In-Memory für Development)
quarkus.datasource.db-kind=h2
quarkus.datasource.jdbc.url=jdbc:h2:mem:notizblockdb;DB_CLOSE_DELAY=-1
quarkus.datasource.username=sa
quarkus.datasource.password=
# Hibernate ORM
quarkus.hibernate-orm.schema-management.strategy=drop-and-create
quarkus.hibernate-orm.log.sql=true
# OpenAPI / Swagger UI
quarkus.swagger-ui.always-include=true
```

**Hinweis**: Die H2 In-Memory Datenbank wird bei jedem Neustart neu erstellt. Für produktive Umgebungen sollte eine
persistente Datenbank (PostgreSQL, MySQL) konfiguriert werden.

### Frontend (Angular)

- **API Base Path**: Konfiguriert in `notizblock-angular/proxy.conf.json`
- **Proxy**: Leitet `/notizblock/api` an `http://localhost:8080` weiter

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

## Entwicklungs-Workflow

### Backend-Entwicklung

1. **Backend im Dev-Modus starten**: `mvn quarkus:dev`
2. Code ändern - Quarkus lädt automatisch neu
3. API testen mit Swagger UI: `http://localhost:8080/notizblock/q/swagger-ui/`

### Frontend-Entwicklung (Angular)

1. **Backend starten**: `mvn quarkus:dev` (Terminal 1)
2. **Frontend starten**: `cd notizblock-angular && npm start` (Terminal 2)
3. Im Browser öffnen: `http://localhost:4200/`
4. Änderungen in Angular-Code werden automatisch neu geladen

### OpenAPI Schema aktualisieren

Wenn Backend-API geändert wurde:

```bash
# 1. Backend starten
mvn quarkus:dev

# 2. OpenAPI Spec exportieren (aus Swagger UI oder Dev UI)
# Oder: http://localhost:8080/notizblock/q/openapi

# 3. openapi.json im Root aktualisieren

# 4. Angular API Client neu generieren
cd notizblock-angular
npm run generate-api
```

## Technische Details

### Architektur-Patterns

**Repository Pattern:**

- Trennung von Business-Logik und Datenzugriff
- `NoteRepository` und `NoteHistoryRepository` kapseln alle DB-Operationen

**DTO Pattern (Data Transfer Objects):**

- Entkopplung von Entities und API
- MapStruct für automatisches Entity-DTO Mapping
- Vermeidet Lazy-Loading-Probleme bei der Serialisierung

**REST Resource Pattern:**

- JAX-RS Resources (`NoteResource`) für HTTP Endpoints
- Bean Validation für Request-Validierung
- Exception Mapper für einheitliche Fehlerbehandlung

**Component-Based UI:**

- Angular Komponentenbasierte SPA-Architektur
- Wiederverwendbare Components und Services
- Reactive Forms für Formularverwaltung

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

### Validierung

- **Jakarta Bean Validation** in DTOs und Entities (@NotBlank, @Size, etc.)
- **Server-Side Validation** in REST Endpoints
- **Client-Side Validation** in Angular Forms
- Einheitliche Fehlerbehandlung mit Exception Mapper

### Transaktionsverwaltung

- Transaktionen via `@Transactional` im Repository
- Automatisches Rollback bei Exceptions
- Optimistic Locking für Concurrency Control

### Dependency Injection

- `@ApplicationScoped` - Repositories und Services (Singleton)
- `@RequestScoped` - REST Resources (Pro Request)
- Automatische Dependency Injection via `@Inject`

## Deployment-Strategien

### Variante 1: Quarkus JAR

```bash
mvn clean package
java -jar target/quarkus-app/quarkus-run.jar
```

Backend läuft auf `http://localhost:8080/notizblock/api/`

### Variante 2: Docker Container (Backend + Angular)

**Backend als Container:**

```bash
# Quarkus Container Build
mvn clean package -Dquarkus.container-image.build=true

# Container starten
docker run -p 8080:8080 quarkus/notizblock:1.0-SNAPSHOT
```

**Frontend als Static Files:**

```bash
# Angular Production Build
cd notizblock-angular
npm run build

# Statische Dateien deployen (z.B. Nginx, Apache)
# Die Dateien befinden sich in dist/notizblock-angular/browser/
```

### Variante 3: Quarkus Native Image

Für minimalen Speicher-Footprint und schnellsten Start:

```bash
mvn clean package -Pnative
./target/notizblock-runner
```

**Hinweis**: Benötigt GraalVM Native Image.

### Variante 4: Angular in Quarkus integrieren

Die Angular Build-Artefakte können in `src/main/resources/META-INF/resources/` kopiert werden, um eine
Single-JAR-Deployment zu ermöglichen.

## Troubleshooting

### Backend-Probleme

**Hibernate DDL-Fehler:**

- Die H2-Datenbank wird bei jedem Start neu erstellt (`drop-and-create`)
- Für Produktion: Persistente DB verwenden und `schema-management.strategy` ändern

**CORS-Fehler:**

- CORS wird durch `CorsFilter.java` konfiguriert
- Prüfen: Allowed origins in `src/main/java/com/gepardec/notizblock/filter/CorsFilter.java`

**Port bereits belegt:**

```bash
# Port ändern in application.properties
quarkus.http.port=8081
```

### Frontend-Probleme (Angular)

**API Client nicht gefunden:**

```bash
cd notizblock-angular
npm run generate-api
```

**Backend nicht erreichbar:**

- Prüfen: Backend läuft auf `http://localhost:8080`
- Prüfen: `proxy.conf.json` ist korrekt konfiguriert

**Angular Proxy Fehler:**

```json
// notizblock-angular/proxy.conf.json prüfen
{
  "/notizblock": {
    "target": "http://localhost:8080",
    "secure": false
  }
}
```

## Best Practices

### Backend

1. **Transaktionen**: Alle DB-Operationen in `@Transactional`-Methoden
2. **DTOs verwenden**: Niemals JPA Entities direkt in REST API zurückgeben
3. **Validation**: Bean Validation in DTOs UND Entities
4. **Error Handling**: Exception Mapper für einheitliche Fehlerantworten
5. **Separation of Concerns**: Repository → Data Access, Resource → HTTP Layer

### Frontend

1. **Services für API-Zugriff**: Keine direkten HTTP-Calls in Components
2. **RxJS Subscriptions**: Immer unsubscribe oder async pipe verwenden
3. **Type Safety**: TypeScript Types nutzen, `any` vermeiden
4. **Component Design**: Kleine, wiederverwendbare Components
5. **Error Handling**: Globale Error Interceptors
6. **Lazy Loading**: Lazy Loading für Module und Routes
7. **State Management**: Reactive State Management mit Services oder NgRx

## Erweiterungsmöglichkeiten

### Backend

- **Benutzer-Authentifizierung**: Quarkus OIDC oder JWT mit Keycloak
- **Datenbank Migration**: H2 → PostgreSQL/MySQL/MariaDB
- **Hibernate Search**: Volltextsuche mit Lucene/Elasticsearch
- **Caching**: Redis Cache mit Quarkus Cache Extension
- **Messaging**: Kafka/AMQP für Event-Driven Architecture
- **Observability**: Micrometer Metrics, OpenTelemetry Tracing

### Frontend (Angular)

- **PWA Support**: Service Workers für Offline-Funktionalität
- **State Management**: NgRx oder Akita für komplexe State-Logik
- **Internationalisierung**: i18n für mehrsprachige UI
- **Rich-Text Editor**: Quill oder TinyMCE Integration
- **File Upload**: Drag & Drop für Anhänge
- **Real-Time Updates**: WebSocket oder Server-Sent Events

### Features

- **Tags/Kategorien**: Notizen organisieren und filtern
- **Kollaboration**: Notizen teilen und gemeinsam bearbeiten
- **Export/Import**: JSON, PDF, Markdown Export
- **Markdown Support**: Markdown-Editor mit Preview
- **Attachments**: Datei-Uploads mit S3-Integration
- **Mobile App**: Ionic oder React Native basierend auf derselben API

---

## Lizenz & Projekt-Info

Dieses Projekt ist ein **Beispielprojekt für Lern- und Demonstrationszwecke**.

### Projektziel

Demonstration einer modernen Full-Stack-Architektur mit:

- Quarkus Backend mit REST API
- Angular Frontend als moderne SPA
- OpenAPI-basierte API-Entwicklung mit auto-generiertem TypeScript Client
- Best Practices für Enterprise Java Development

### Autor

Gepardec - Enterprise Java Experts
