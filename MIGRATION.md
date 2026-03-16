---
marp: true
theme: default
paginate: true
backgroundColor: #fff
style: |
  section {
    font-size: 28px;
  }
  h1 {
    color: #3f51b5;
  }
  h2 {
    color: #5c6bc0;
  }
---

# Migration JSF/PrimeFaces zu Angular

**Notizblock Webanwendung**
Von Jakarta EE + JSF zu Quarkus REST API + Angular SPA

---

## Agenda

1. Ausgangssituation & Ziele
2. Zielarchitektur & Migrationsphasen
3. Phase 1-2: Projekt-Setup
4. Phase 3: REST API Migration
5. Phase 4: Angular Setup
6. Phase 5: UI Migration
7. Ergebnisse & Lessons Learned

---

## Ausgangssituation

- **Backend:** Jakarta EE 10 (WildFly) + JSF 4.0 + PrimeFaces 13.0
- **Deployment:** Monolithische WAR-Datei
- **Komponenten:** 2 JPA Entities, 2 Repositories, 2 Backing Beans, 2 XHTML-Seiten

## Migrationsziele

- Moderne REST API-basierte Architektur mit Frontend/Backend-Trennung
- Cloud-native Deployment (Quarkus + Angular SPA)
- Maximale Automatisierung mit Tools & LLM-Unterstützung

---

## Zielarchitektur

```
Angular 21 SPA (Port 4200)
  ↓ HTTP/JSON
Quarkus REST API (Port 8080)
  ↓
H2 Database
```

**Stack:** JSF + PrimeFaces → Angular 21 + Material | Jakarta EE → Quarkus + JAX-RS

---

## Migrationsphasen

1. **Analyse & Setup** - Quarkus + Angular Projekte initialisieren
2. **REST API** - DTOs, Mapper, Endpoints, OpenAPI
3. **Angular Setup** - Material, API-Client, Routing, Interceptors
4. **UI Migration** - Komponenten von JSF zu Angular Material

---

# Phase 1-2: Projekt-Setup

**Backend:** Quarkus-Projekt mit Extensions (resteasy-reactive, hibernate-orm-panache, h2, openapi)

**Frontend:** Angular 21 mit Routing & SCSS

---

# Phase 3: REST API Migration

## Implementierung

- **DTOs** mit Bean Validation (NoteDTO, NoteHistoryDTO, ErrorResponse)
- **MapStruct Mapper** für automatisches Entity-DTO Mapping
- **JAX-RS Endpoints** (GET, POST, PUT, DELETE)
- **OpenAPI/Swagger UI** automatisch generiert
- **CORS** Konfiguration für Angular

## REST API Endpoints

| Methode | Endpoint | Beschreibung |
|---------|----------|--------------|
| GET | /api/notes | Alle Notizen |
| GET/PUT/DELETE | /api/notes/{id} | CRUD Operations |
| GET | /api/notes/{id}/history | Historie |

---

# Phase 4: Angular Projekt Setup

## Setup

- **Angular Material** - UI Component Library
- **OpenAPI Generator** - Type-Safe API Client aus openapi.json
- **HTTP Interceptors** - Base URL, Error Handling
- **Core Services** - Notification (MatSnackBar), Loading
- **Routing** - Lazy Loading für Features
- **App Layout** - Header mit Material Toolbar

**Ergebnis:** Type-Safe API-Client + Routing + Interceptors + Material UI

---

# Phase 5: UI-Komponenten Migration

## Komponenten-Mapping

| JSF/PrimeFaces | Angular Material |
|----------------|------------------|
| `&lt;p:dataTable&gt;` + `&lt;p:paginator&gt;` | `&lt;mat-table&gt;` + `&lt;mat-paginator&gt;` |
| `&lt;p:dialog&gt;` | `MatDialog` Service |
| `&lt;p:inputText&gt;` | `&lt;mat-form-field&gt;` |
| `&lt;p:growl&gt;` / `&lt;p:confirmDialog&gt;` | `MatSnackBar` / Custom Dialog |

## Implementierte Komponenten

1. **Note List** - Material Table mit Paginator, Sort, CRUD Actions
2. **Note Form Dialog** - Reactive Forms mit Validation
3. **Delete Confirmation** - Custom MatDialog
4. **Note Detail** - Signal-basierter State, Routing

## Angular 21 Features

- Standalone Components (kein NgModule)
- Control Flow Syntax (`@if`, `@for`)
- Signals für reaktiven State
- inject() Function für DI
- Bundle: Initial 162 kB, Lazy Loading Features

---

# Ergebnisse & Lessons Learned

## Deliverables

**Backend:** 3 DTOs, 2 MapStruct Mapper, REST Resource, OpenAPI Spec

**Frontend:** API Client (generiert), 4 Components, 2 Interceptors, 2 Services, Routing

---

## Key Takeaways

### Erfolgsfaktoren
- **OpenAPI Generator** - Automatische API-Client-Generierung
- **MapStruct** - Kein manueller Mapping-Code
- **Quarkus Dev Mode** - Schnelles Feedback
- **Angular Material** - Konsistente UI Components

### LLM-Nutzung
- Code-Generierung für DTOs, Boilerplate, Templates
- Hohe Erfolgsrate bei strukturierten Prompts
- Beschleunigt Migration erheblich

### Fazit
- Migration von Monolith zu moderner SPA-Architektur erfolgreich
- Hoher Automatisierungsgrad durch Tools