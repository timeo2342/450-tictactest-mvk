# Continuous Deployment des DevContainers

Prozessdokumentation (Abschluss-Auftrag 324)

## Ziel

Nach automatischem Build und Push des DevContainer-Images wird dieses automatisch
in der CI/CD-Pipeline **und** lokal verwendet.

## Ablauf (automatisch)

1. **DevContainer wird erstellt** – Definition in `.devcontainer/Dockerfile`
   (Alpine, Java 25, User 1000:1000).
2. **Bei Push auf `main`** (der `.devcontainer/**` betrifft) baut der Workflow
   `.github/workflows/devcontainer-release.yml` das Image und pusht es nach
   `ghcr.io/timeo2342/450-tictactest-mvk-devcontainer` – getaggt mit einer
   konkreten Version (`v1.0.<run_number>`) **und** `:latest`.
3. **Nach erfolgreichem Push** wird automatisch ein Pull Request erstellt, der
   **`devcontainer.json`** und **alle CI-Workflows** auf die neue Version umstellt.

## Versionierung

Jeder Build erhält eine eindeutige, aufsteigende Version:

```
v1.0.<github.run_number>
```

`github.run_number` steigt bei jedem Workflow-Lauf monoton – so ist jede Version
eindeutig und nachvollziehbar. Zusätzlich zeigt `:latest` immer auf das zuletzt
gebaute Image.

## Verwendung des Images

- **CI-Jobs** (`gradle.yml`): laufen direkt im Image aus GHCR
  (`container.image`). Der Auto-PR aktualisiert die Referenz auf die neue Version.
- **Lokale Entwicklung** (`devcontainer.json`): zieht dasselbe Image aus GHCR
  (`"image": "ghcr.io/.../...-devcontainer:..."`), statt lokal zu bauen. So haben
  CI und alle Entwickler dieselbe Umgebung.

## Der automatische Pull Request

Nach Build & Push aktualisiert der Workflow per Suchen-und-Ersetzen jede
Image-Referenz (`<image>:<tag>`) in `.devcontainer/` und `.github/workflows/` auf
die neue Version und öffnet damit einen PR
(`peter-evans/create-pull-request`). So wird der Versionswechsel als
überprüfbarer, reviewbarer Schritt sichtbar.

## Voraussetzungen (GitHub-Einstellungen)

Damit die Automatik funktioniert, unter **Settings → Actions → General →
Workflow permissions**:

- **Read and write permissions** aktiv (für Image-Push und Branch-Push)
- **Allow GitHub Actions to create and approve pull requests** aktiv (für den Auto-PR)

## Beteiligte Dateien

| Datei | Rolle |
|---|---|
| `.devcontainer/Dockerfile` | Image-Definition (Alpine, Java 25, User 1000:1000) |
| `.devcontainer/devcontainer.json` | Lokale Umgebung, zieht das Image aus GHCR |
| `.github/workflows/devcontainer-release.yml` | Baut/pusht Image bei Push auf main, öffnet Auto-PR |
| `.github/workflows/gradle.yml` | CI-Tests laufen im Image aus GHCR |

## Ablaufdiagramm

```
Push auf main (.devcontainer/** geändert)
        |
        v
+-------------------------------------------+
|  devcontainer-release.yml                 |
|  1. Image bauen (Dockerfile)              |
|  2. push ghcr.io :v1.0.N + :latest        |
|  3. Referenzen in devcontainer.json +     |
|     allen CI-Workflows aktualisieren      |
|  4. automatischer Pull Request            |
+-------------------------------------------+
        |                         |
        v                         v
  ghcr.io (Registry)        Pull Request nach main
        |
        +--> CI-Jobs (gradle.yml)  -> nutzen das Image
        +--> Lokale Umgebung       -> nutzt das Image
```
