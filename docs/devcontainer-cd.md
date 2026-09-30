# Continuous Deployment des DevContainers

Prozessdokumentation (Abschluss-Auftrag 324)

## Ziel

Nach automatischem Build und Push schlägt ein Pull Request die neue Image-Version
vor. Nach dessen Merge und lokalem Container-Rebuild verwenden `gradle.yml`
und die lokale Entwicklung diese Version.

## Ablauf (automatisch)

1. **DevContainer wird erstellt** – Definition in `.devcontainer/Dockerfile`
   (Alpine, Java 25, User 1000:1000).
2. **Bei Push auf `main`** (der `.devcontainer/Dockerfile` betrifft) baut der Workflow
   `.github/workflows/devcontainer-release.yml` das Image und pusht es nach
   `ghcr.io/timeo2342/450-tictactest-mvk-devcontainer` – getaggt mit einer
   konkreten Version (`v1.0.<run_number>-attempt.<run_attempt>`) **und** `:latest`.
3. **Nach erfolgreichem Push** wird automatisch ein Pull Request erstellt, der
   **`devcontainer.json`**, **`image-version.txt`** und vorhandene
   **CI-Image-Referenzen** auf die neue Version umstellt.

## Versionierung

Jeder Build-Versuch erhält einen eigenen Versions-Tag:

```
v1.0.<github.run_number>-attempt.<github.run_attempt>
```

`github.run_number` steigt bei neuen Workflow-Läufen; `github.run_attempt`
unterscheidet Wiederholungen desselben Laufs. Ein Re-run überschreibt dadurch
nicht mehr den vorherigen Versions-Tag. `:latest` zeigt auf den zuletzt
veröffentlichten Build. Veröffentlichungen laufen nur auf `main` und nicht parallel.

## Verwendung des Images

- **CI-Jobs** (`gradle.yml`): laufen direkt im Image aus GHCR
  (`container.image`). Der Auto-PR aktualisiert die Referenz auf die neue Version.
- **Lokale Entwicklung** (`devcontainer.json`): zieht dasselbe Image aus GHCR
  (`"image": "ghcr.io/.../...-devcontainer:..."`), statt lokal zu bauen. So haben
  CI und alle Entwickler dieselbe Umgebung.
- **Coverage-Workflows:** laufen separat auf Ubuntu mit Java 25 aus `setup-java`;
  sie verwenden nicht das DevContainer-Image.

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
- Actions-Secret **`CD_PAT`** mit Zugriff auf dieses Repository und Berechtigung
  zum Ändern von Workflow-Dateien (bei klassischen PATs: `repo` und `workflow`).

## Beteiligte Dateien

| Datei | Rolle |
|---|---|
| `.devcontainer/Dockerfile` | Image-Definition (Alpine, Java 25, User 1000:1000) |
| `.devcontainer/devcontainer.json` | Lokale Umgebung, zieht das Image aus GHCR |
| `.github/workflows/devcontainer-release.yml` | Baut/pusht Image bei Push auf main, öffnet Auto-PR |
| `.github/workflows/gradle.yml` | CI-Tests laufen im Image aus GHCR |

## Ablaufdiagramm

```
Push auf main (.devcontainer/Dockerfile geändert)
        |
        v
+-------------------------------------------+
|  devcontainer-release.yml                 |
|  1. Image bauen (Dockerfile)              |
|  2. push :v1.0.N-attempt.A + :latest     |
|  3. Referenzen in devcontainer.json +     |
|     vorhandenen CI-Image-Referenzen       |
|  4. automatischer Pull Request            |
+-------------------------------------------+
        |                         |
        v                         v
  ghcr.io (Registry)        Pull Request nach main
        |
        +--> CI-Jobs (gradle.yml)  -> nutzen das Image
        +--> Lokale Umgebung       -> nutzt das Image
```
