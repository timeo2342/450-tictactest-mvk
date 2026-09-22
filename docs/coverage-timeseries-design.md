# Design: Coverage Time-Series auf GitHub Pages

Auftrag 2 / 3.1 – Design (noch keine Implementierung)

Modul 450 – TicTacTest · 2er-Gruppe

## 1. Ziel

Die Test Coverage des `main`-Branches soll über die Zeit sichtbar gemacht werden.
Nach jeder Änderung auf `main` wird der aktuelle Coverage-Wert gemessen, zur
Historie hinzugefügt und als Diagramm (Time-Series) über GitHub Pages
veröffentlicht.

## 2. Beantwortung der Leitfragen

### 2.1 Woher erhalten wir den aktuellen Coverage-Wert?

Aus dem JaCoCo-Report, der bereits in Auftrag 1 erzeugt wird. JaCoCo schreibt
neben dem HTML- auch einen **XML-Report** (`build/reports/jacoco/test/jacocoTestReport.xml`).
Der XML-Report ist maschinenlesbar und enthält die Zähler, aus denen sich die
Coverage berechnen lässt.

Der Wert wird im GitHub-Actions-Workflow aus dem XML extrahiert (z.B. mit einer
fertigen Action wie `jacoco-report` oder durch Auslesen der `<counter>`-Elemente).

### 2.2 Welche Coverage-Metrik speichern wir?

Wir speichern die **Line Coverage** in Prozent (eine Zahl mit einer Nachkommastelle,
z.B. `75.0`). Line Coverage ist einfach zu verstehen und für ein kleines Projekt
aussagekräftig. Branch Coverage wäre eine Alternative, aber für die Time-Series
beschränken wir uns bewusst auf **eine** Metrik, damit die Kurve eindeutig ist.

### 2.3 Wo und in welchem Format speichern wir historische Daten?

- **Format:** CSV (eine Zeile pro Messung), weil es einfach anzuhängen, zu lesen
  und zu versionieren ist.
- **Ort:** Eine Datei `coverage-history.csv` auf einem eigenen Branch `gh-pages`
  (dem Publishing-Branch von GitHub Pages). So bleiben Produktivcode und
  veröffentlichte Daten getrennt.

Aufbau der CSV:

```
Datum,Commit,Coverage
2026-09-01,abc123,72.4
2026-09-02,def456,74.1
2026-09-03,ghi789,75.0
```

### 2.4 Wie fügen wir neue Messwerte zu bestehenden Daten hinzu?

Der Workflow (läuft auf `main`):
1. checkt den `gh-pages`-Branch aus (dort liegt die bestehende `coverage-history.csv`),
2. liest den neuen Coverage-Wert aus dem JaCoCo-XML,
3. **hängt eine neue Zeile an** (`Datum, Commit-SHA, Coverage`),
4. committet und pusht die aktualisierte CSV zurück auf `gh-pages`.

Wichtig: Es wird **angehängt**, nie überschrieben – so bleibt die Historie erhalten.

### 2.5 Wie erzeugen wir daraus eine Time-Series?

Aus der CSV wird ein Diagramm erzeugt. Zwei mögliche Varianten:
- **Client-seitig (bevorzugt):** Eine statische `index.html` auf `gh-pages` lädt die
  CSV und zeichnet mit einer JS-Chart-Bibliothek (z.B. Chart.js) ein Liniendiagramm.
  Vorteil: Die Grafik ist immer aktuell, sobald die CSV aktualisiert wird – kein
  erneutes Rendern nötig.
- **Server-/Build-seitig (Alternative):** Der Workflow rendert bei jedem Lauf ein
  PNG/SVG aus der CSV und legt es auf `gh-pages`.

Wir wählen die **client-seitige** Variante (Chart.js), weil sie am wenigsten
Build-Logik braucht.

### 2.6 Wie veröffentlichen wir die Visualisierung auf GitHub Pages?

GitHub Pages wird so konfiguriert, dass es den Branch `gh-pages` (Ordner `/`)
als Quelle nutzt. Auf `gh-pages` liegen:
- `index.html` (die Chart.js-Seite),
- `coverage-history.csv` (die Daten).

Die Seite ist dann unter `https://timeo2342.github.io/450-tictactest-mvk/`
erreichbar.

### 2.7 Wann wird die Seite aktualisiert?

Nur **bei Änderungen auf `main`** (Trigger `push` auf `main`). Feature-Branches
lösen keine Aktualisierung der Historie aus – die Time-Series bildet bewusst nur
die Entwicklung von `main` ab.

## 3. Architekturdiagramm

```
   Entwickler
       |  push auf main
       v
+---------------------------------------------------+
|              GitHub Actions (main)                |
|                                                   |
|  1. ./gradlew test jacocoTestReport               |
|        -> jacocoTestReport.xml                    |
|  2. Coverage-Wert aus XML extrahieren             |
|  3. gh-pages auschecken                           |
|  4. neue Zeile an coverage-history.csv anhaengen  |
|  5. commit + push nach gh-pages                   |
+---------------------------------------------------+
                       |
                       v
        +------------------------------+
        |   Branch: gh-pages           |
        |   - index.html (Chart.js)    |
        |   - coverage-history.csv     |
        +------------------------------+
                       |
                       v  (GitHub Pages Publishing)
        +------------------------------+
        |        GitHub Pages          |
        |  timeo2342.github.io/        |
        |     450-tictactest-mvk/      |
        |  -> Liniendiagramm (Zeit)    |
        +------------------------------+
                       |
                       v
                   Betrachter (Browser)
```

## 4. Datenfluss (kurz)

JaCoCo-XML → Coverage-Wert → neue CSV-Zeile (`gh-pages`) → Chart.js liest CSV →
Liniendiagramm auf GitHub Pages.

## 5. Bewusste Abgrenzungen (Design-Entscheide)

- **Eine Metrik (Line Coverage):** hält die Time-Series eindeutig.
- **CSV statt Datenbank:** minimaler Aufwand, versionierbar, für die Datenmenge
  völlig ausreichend.
- **`gh-pages`-Branch als Datenspeicher:** trennt Daten/Visualisierung sauber vom
  Produktivcode und ist der Standard-Publishing-Branch von GitHub Pages.
- **Nur `main` aktualisiert die Historie:** die Kurve soll den Projektzustand
  abbilden, nicht einzelne Experimente auf Feature-Branches.
- **Noch keine Implementierung:** dieses Dokument beschreibt nur den Entwurf
  (gemäss Auftrag 3.1).

## 6. Umsetzung (Auftrag 2.5 – implementiert)

Das Design ist im Workflow `.github/workflows/coverage-pages.yml` umgesetzt.

Ablauf bei jedem Push auf `main`:

1. `./gradlew test jacocoTestReport` erzeugt `jacocoTestReport.xml`.
2. Ein Python-Skript liest den `LINE`-Counter aus dem XML und berechnet die
   Line Coverage in Prozent.
3. Die bestehende `coverage-history.csv` wird vom `gh-pages`-Branch geholt und um
   eine Zeile (`Datum,Commit,Coverage`) ergänzt (Historie bleibt erhalten).
4. `docs/pages/index.html` (Chart.js) wird zusammen mit der CSV nach `gh-pages`
   veröffentlicht (`peaceiris/actions-gh-pages`).

**Ergebnis:** Die Time-Series ist unter
`https://timeo2342.github.io/450-tictactest-mvk/` erreichbar.

### Einmalige Einrichtung (GitHub Pages)

Damit die Seite erreichbar wird, muss GitHub Pages einmalig konfiguriert werden:
- Repo → **Settings → Pages**
- **Source:** "Deploy from a branch"
- **Branch:** `gh-pages`, Ordner `/ (root)` → Save

Ab dann aktualisiert jeder Push auf `main` die Daten und die Seite automatisch.
