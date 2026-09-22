# Continuous Deployment des DevContainers

Auftrag 3 – Prozessdokumentation

## Ziel

Nach automatischem Build und Push des DevContainer-Images wird dieses automatisch
in der CI/CD-Pipeline **und** lokal verwendet. Es werden nur **freigegebene**
Image-Versionen genutzt.

## Versionierungs-Konzept

Der DevContainer wird nach **Semantic Versioning** (`vMAJOR.MINOR.PATCH`) versioniert:

- **MAJOR** (`v2.0.0`): inkompatible Änderungen (z.B. Wechsel des Base-Images, neue Java-Major-Version)
- **MINOR** (`v1.1.0`): abwärtskompatible Ergänzungen (z.B. zusätzliches Tool, neue Extension)
- **PATCH** (`v1.0.1`): kleine Korrekturen (z.B. Bugfix im Dockerfile)

Die aktuell verwendete Version steht zentral in `.devcontainer/image-version.txt`.

Das Image liegt in der GitHub Container Registry unter:

```
ghcr.io/timeo2342/450-tictactest-mvk-devcontainer
```

Getaggt wird jedes Release doppelt: mit der konkreten Version (`:v1.0.0`) und mit `:latest`.

## Freigabeprozess (nur freigegebene Container werden verwendet)

Der Kern: **Ein Image gilt nur dann als freigegeben, wenn ein Git-Version-Tag gesetzt wurde.**

1. Änderungen am DevContainer (Dockerfile) werden auf einem Branch entwickelt und per PR nach `main` gemergt.
2. Erst wenn bewusst ein Tag gesetzt wird (`git tag v1.0.1 && git push origin v1.0.1`),
   startet der Release-Workflow und baut/pusht das Image.
3. Ein gewöhnlicher Branch-Push erzeugt **kein** freigegebenes Image.

So kann kein zufälliger oder unfertiger Stand zum offiziellen `:latest` werden –
das Setzen des Tags ist der bewusste Freigabe-Akt.

## Automatischer Ablauf

### 1. Release des Images (`.github/workflows/devcontainer-release.yml`)

Trigger: Push eines Tags `v*` (oder manuell via `workflow_dispatch`).

Schritte:
1. Version aus dem Tag ableiten.
2. Image aus `.devcontainer/Dockerfile` bauen.
3. Nach `ghcr.io` pushen – getaggt mit der Version **und** `:latest`.
4. `.devcontainer/image-version.txt` auf die neue Version setzen.
5. **Automatisch einen Pull Request** öffnen, der die aktualisierte Version verwendet
   (via `peter-evans/create-pull-request`).

### 2. CI-Jobs nutzen automatisch die neueste Version (`.github/workflows/gradle.yml`)

Der Test-Job läuft direkt im Image aus GHCR:

```yaml
container:
  image: ghcr.io/${{ github.repository }}-devcontainer:latest
```

Da `:latest` immer auf das zuletzt freigegebene Release zeigt, nutzen alle CI-Jobs
automatisch die neueste freigegebene Version.

### 3. Lokale Entwicklung nutzt automatisch die neueste Version (`.devcontainer/devcontainer.json`)

Statt lokal aus dem Dockerfile zu bauen, zieht die lokale Umgebung das fertige Image:

```jsonc
"image": "ghcr.io/timeo2342/450-tictactest-mvk-devcontainer:latest"
```

Beim "Reopen in Container" (VS Code) bzw. "Create Dev Container" (IntelliJ) wird
automatisch die neueste freigegebene Version gezogen.

## Release durchführen (Anleitung)

```bash
# 1. Änderungen am Dockerfile committen und nach main mergen
# 2. Version-Tag setzen und pushen
git tag v1.0.1
git push origin v1.0.1
```

Danach läuft der Release-Workflow automatisch: Image bauen → nach GHCR pushen
(v1.0.1 + latest) → Versions-Datei aktualisieren → automatischer PR.

## Übersicht der beteiligten Dateien

| Datei | Rolle |
|---|---|
| `.devcontainer/Dockerfile` | Definition des Images (Alpine, Java 25, User 1000:1000) |
| `.devcontainer/devcontainer.json` | Lokale Umgebung, zieht `:latest` aus GHCR |
| `.devcontainer/image-version.txt` | Zentrale, freigegebene Version |
| `.github/workflows/devcontainer-release.yml` | Baut/pusht Image bei Tag, öffnet Auto-PR |
| `.github/workflows/gradle.yml` | CI-Tests laufen im `:latest`-Image |

## Ablaufdiagramm

```
Entwickler setzt Tag v1.0.1
        |
        v
+-------------------------------------------+
|  Release-Workflow (Trigger: tag v*)       |
|  1. Image bauen (Dockerfile)              |
|  2. push ghcr.io :v1.0.1 + :latest        |
|  3. image-version.txt aktualisieren       |
|  4. automatischer Pull Request            |
+-------------------------------------------+
        |                         |
        v                         v
  ghcr.io (Registry)        Pull Request nach main
        |
        +--> CI-Jobs (gradle.yml)  -> nutzen :latest
        +--> Lokale Umgebung       -> nutzen :latest
```
