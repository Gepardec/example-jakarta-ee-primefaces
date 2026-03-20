# Session-Persistenz über Server-Restarts – Schritt-für-Schritt-Anleitung

Diese Anleitung zeigt, wie HTTP-Sessions in einer Jakarta EE / WildFly Anwendung
einen Server-Restart überleben können und wie du das mit der integrierten Demo-Seite
nachvollziehen kannst.

---

## Hintergrund: Wie funktioniert Session-Persistenz?

WildFly (und JBoss EAP) unterstützt **Session Passivation**:

1. Beim **Graceful Shutdown** serialisiert WildFly alle aktiven HTTP-Sessions auf Disk
   (Standardpfad: `$WILDFLY_HOME/standalone/data/wc/`).
2. Beim nächsten **Start** liest WildFly diese Dateien ein und stellt die Sessions wieder her.
3. Der Browser-Client merkt davon nichts – sein Session-Cookie (`JSESSIONID`) bleibt gültig.

**Voraussetzungen damit das funktioniert:**
- Der Bean muss `java.io.Serializable` implementieren.
- Alle Felder des Beans müssen ebenfalls serialisierbar sein (keine offenen DB-Verbindungen, keine nicht-serialisierbaren Objekte).
- WildFly muss **graceful** heruntergefahren werden (kein `kill -9`).

---

## Was wurde in der Anwendung hinzugefügt?

### 1. `UserSessionBean` (`@SessionScoped`)

```
src/main/java/com/gepardec/notizblock/bean/UserSessionBean.java
```

- Scope: `@SessionScoped` (lebt so lange wie die HTTP-Session des Browsers)
- Implementiert `Serializable` → passivierbar
- Speichert: Benutzername, Besuchszähler, Session-Erstellzeit, letzte Aktivität

### 2. Demo-Seite

```
src/main/webapp/session-demo.xhtml
```

Erreichbar unter: `http://localhost:8080/<app-context>/session-demo.xhtml`

Zeigt den aktuellen Session-Zustand und ob eine Reaktivierung stattgefunden hat.

---

## Schritt-für-Schritt-Anleitung

### Schritt 0: WildFly für Session-Persistenz konfigurieren (einmalig!)

> ⚠️ **Dieser Schritt ist Pflicht** – ohne ihn schreibt WildFly keine Sessions auf Disk,
> egal ob graceful Shutdown oder nicht. Im Shutdown-Log fehlen dann `WFLYSESS`-Einträge
> und der Shutdown dauert nur wenige Millisekunden.

WildFly nutzt **Infinispan** für Session-Management. Für persistente Sessions müssen
zwei Dinge konfiguriert werden:

#### 1. `standalone.xml` – Infinispan Cache-Container anpassen

Datei: `$WILDFLY_HOME/standalone/configuration/standalone.xml`

Im `<subsystem xmlns="urn:jboss:domain:infinispan:...">` Block den `cache-container`
mit `name="web"` suchen und folgendermaßen ersetzen:

```xml
<cache-container name="web" default-cache="passivation"
                 modules="org.wildfly.clustering.web.infinispan"
                 marshaller="PROTOSTREAM">
    <local-cache name="passivation">
        <expiration interval="0"/>
        <file-store passivation="false" purge="false"/>
    </local-cache>
    <local-cache name="sso">
        <expiration interval="0"/>
    </local-cache>
</cache-container>
```

**Was diese Konfiguration bewirkt:**
- `default-cache="passivation"` → WildFly nutzt den `passivation`-Cache für Web-Sessions
- `<file-store passivation="false" purge="false"/>` → Sessions werden auf Disk geschrieben
  und beim Start **nicht** gelöscht (`purge="false"`)
- `marshaller="PROTOSTREAM"` → Serialisierungsformat (Infinispan-intern)

#### 2. `web.xml` der Anwendung – `<distributable/>` einfügen

Datei: `src/main/webapp/WEB-INF/web.xml`

Das Tag `<distributable/>` direkt nach `<web-app ...>` einfügen:

```xml
<web-app ...>

    <distributable/>

    <!-- restliche Konfiguration ... -->
</web-app>
```

**Warum `<distributable/>`?**
Dieses Tag signalisiert dem Container, dass die Anwendung verteilte/passivierbare
Sessions unterstützt. Erst dadurch aktiviert WildFly den Infinispan-basierten
Session-Manager für diese Anwendung. Ohne diesen Eintrag verwendet WildFly den
Standard-In-Memory-Manager, der keine Persistenz unterstützt.

---

### Schritt 1: Anwendung deployen

```bash
mvn clean package
# WAR nach WildFly deployments-Ordner kopieren oder via Management Console deployen
cp target/notizblock.war $WILDFLY_HOME/standalone/deployments/
```

Starte WildFly falls noch nicht gestartet:

```bash
$WILDFLY_HOME/bin/standalone.sh
```

### Schritt 2: Demo-Seite aufrufen

Öffne im Browser:

```
http://localhost:8080/notizblock/session-demo.xhtml
```

Du siehst:
- **Session erstellt am**: aktueller Zeitstempel
- **Seitenbesuche**: 1
- **Gespeicherter Name**: (noch nicht gesetzt)

### Schritt 3: Name eintragen und speichern

1. Trage einen Namen in das Eingabefeld ein (z. B. `Max Muster`).
2. Klicke auf **Speichern**.
3. Notiere dir:
   - Den gespeicherten Namen
   - Die **Session erstellt am**-Zeit
   - Den aktuellen **Besuchszähler** (sollte `2` sein nach dem Speichern)

### Schritt 4: Seite neu laden (ohne Restart)

Drücke `F5` oder lade die Seite neu.

- Besuchszähler steigt auf `3`
- Name und Erstellzeit bleiben unverändert
- → Das ist normales Session-Verhalten (kein Restart)

### Schritt 5: WildFly graceful herunterfahren

> ⚠️ **Wichtig:** Nur ein **graceful Shutdown** passiviert die Sessions.
> Ein `kill -9` oder harter Kill löscht die Session-Daten unwiederbringlich.

**Option A – Management CLI:**

```bash
$WILDFLY_HOME/bin/jboss-cli.sh --connect command=":shutdown"
```

**Option B – HTTP Management API:**

```bash
curl -u admin:adminpassword -X POST \
  http://localhost:9990/management \
  -H "Content-Type: application/json" \
  -d '{"operation":"shutdown","address":[]}'
```

**Option C – Signal (graceful):**

```bash
# PID ermitteln
ps aux | grep wildfly
# Graceful shutdown via SIGTERM
kill -15 <PID>
```

Nach dem Shutdown prüfen, ob Session-Dateien auf Disk geschrieben wurden:

```bash
ls -la $WILDFLY_HOME/standalone/data/sessions/
```

Du solltest eine Datei mit der Session-ID sehen (hexadezimaler Name, z. B. `abc123...`).
Im WildFly-Log beim Shutdown sollte außerdem erscheinen:
```
WFLYSESS0001: Persisting session ...
```

### Schritt 6: WildFly neu starten

```bash
$WILDFLY_HOME/bin/standalone.sh
```

Warte bis der Server vollständig gestartet ist (Log-Ausgabe: `WildFly ... started in ... ms`).

### Schritt 7: Demo-Seite erneut aufrufen

Rufe **ohne den Browser-Tab zu schließen** dieselbe URL wieder auf:

```
http://localhost:8080/notizblock/session-demo.xhtml
```

> Falls du den Tab geschlossen hast, hat der Browser den `JSESSIONID`-Cookie verloren
> und WildFly erstellt eine neue Session. Lass den Tab geöffnet oder nutze ein
> Tool wie Postman/curl mit manuellem Cookie-Management.

**Erwartetes Ergebnis:**

| Feld | Vor Restart | Nach Restart |
|---|---|---|
| Session erstellt am | z. B. `20.03.2026 10:15:30` | **gleich** – `20.03.2026 10:15:30` |
| Gespeicherter Name | `Max Muster` | **gleich** – `Max Muster` |
| Besuchszähler | z. B. `3` | **+1** → `4` (neuer Seitenbesuch) |
| Nach Restart reaktiviert | Nein | **JA** |

---

## Troubleshooting

### Session wird nach Restart nicht wiederhergestellt

**Mögliche Ursachen:**

1. **Infinispan-Cache oder `<distributable/>` nicht konfiguriert** ← häufigste Ursache
   → Symptom: Kein einziger `WFLYSESS`-Eintrag im Shutdown-Log, Shutdown dauert < 100ms.
   → Lösung: Beide Teile aus Schritt 0 ausführen – `standalone.xml` UND `web.xml`.
   → Beides wird benötigt: ohne `<distributable/>` ignoriert WildFly den Infinispan-Cache
     für diese Anwendung.

2. **Harter Kill statt graceful Shutdown**
   → Sicherstellen, dass `jboss-cli.sh --connect command=":shutdown"` verwendet wird.

2. **Browser-Tab war geschlossen**
   → Der `JSESSIONID`-Cookie ist weg. WildFly kann die Session nicht zuordnen.

3. **Session-Timeout überschritten**
   → Konfiguriert in `web.xml` auf 30 Minuten. Wenn der Restart länger dauert
   als der Timeout, ist die Session abgelaufen.
   Für Tests den Timeout erhöhen:
   ```xml
   <session-config>
       <session-timeout>120</session-timeout>
   </session-config>
   ```

4. **Bean ist nicht Serializable**
   → WildFly loggt einen Fehler wie `Failed to passivate session`.
   Sicherstellen, dass alle `@SessionScoped` Beans `implements Serializable` haben.

5. **Feld nicht serialisierbar**
   → Felder die nicht serialisiert werden sollen mit `transient` markieren.
   Felder die serialisiert werden müssen sicherstellen, dass deren Typen
   ebenfalls `Serializable` sind.

### Session-Dateien prüfen

```bash
# Wo WildFly Sessions speichert (nach Aktivierung von persistent-sessions):
ls -la $WILDFLY_HOME/standalone/data/sessions/

# Inhalt einer Session-Datei (binär, schwer lesbar, nur zur Prüfung ob vorhanden):
file $WILDFLY_HOME/standalone/data/sessions/<session-id>
```

### Passivierung im WildFly-Log beobachten

Im WildFly-Log (`standalone/log/server.log`) beim Shutdown nach Einträgen suchen:

```
WFLYSESS0001: Session passivated
```

Und beim Start:

```
WFLYSESS0002: Session activated
```

---

## Warum die Datenbank-Daten trotzdem weg sind

Die App nutzt eine **H2 in-memory Datenbank** (`jdbc:h2:mem:notizblockdb`).
In-Memory bedeutet: die Daten leben nur im RAM des laufenden Prozesses.
Nach einem Restart sind alle Notizen weg – unabhängig von der Session-Persistenz.

Die `@SessionScoped` Bean speichert bewusst **keine Datenbankentitäten**, sondern
nur einfache Strings und Zahlen, die problemlos serialisiert werden können.

Für persistente Daten wäre eine File-basierte oder externe Datenbank nötig:
```
jdbc:h2:file:~/notizblockdb    (H2 file-based, einfach für Tests)
jdbc:postgresql://...          (PostgreSQL, produktionstauglich)
```

---

## Zusammenfassung

```
Browser               WildFly                        Disk
  │                      │                             │
  │── GET /session-demo ─►│                             │
  │                      │── Session erstellen          │
  │◄── Response ─────────│   (UserSessionBean.init())  │
  │                      │                             │
  │── Shutdown ──────────►│                             │
  │                      │── Session passivieren ──────►│
  │                      │   (serialisieren)            │
  │                      X                             │
  │                      │ (Restart)                   │
  │                      │◄── Session aktivieren ───────│
  │                      │    (deserialisieren)         │
  │                      │                             │
  │── GET /session-demo ─►│                             │
  │                      │── onPageLoad(): visitCount++ │
  │◄── Response ─────────│   (Name noch da!)            │
```
