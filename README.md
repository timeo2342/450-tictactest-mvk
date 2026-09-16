# 450 – TicTacTest

TicTacToe-Projekt für Modul 450 mit JUnit 5, AssertJ und einem DevContainer.

## DevContainer

Die Entwicklungsumgebung ist als DevContainer definiert, damit alle im Team
die gleiche Umgebung haben.

- **Definition:** `.devcontainer/Dockerfile` und `.devcontainer/devcontainer.json`
- **Base-Image:** `eclipse-temurin:25-jdk-alpine` (Alpine-basiert, Java 25)
- **Gradle:** über den Gradle Wrapper (`./gradlew`)
- **JUnit:** als Gradle-Test-Dependency (siehe `build.gradle`)
- **Benutzer:** non-root `dev` mit UID:GID `1000:1000`
- **VS Code Extensions:** Java Extension Pack, Gradle, Red Hat Java, Java Debug, Java Test

### Voraussetzungen

- [Docker](https://www.docker.com/) (läuft und ist gestartet)
- [VS Code](https://code.visualstudio.com/) mit der Extension
  [Dev Containers](https://marketplace.visualstudio.com/items?itemName=ms-vscode-remote.remote-containers)

### Starten

1. Projekt in VS Code öffnen.
2. Befehlspalette (`F1` / `Strg+Shift+P`) → **Dev Containers: Reopen in Container**.
3. VS Code baut das Image und öffnet das Projekt im Container.
4. Im Container-Terminal die Tests ausführen:

   ```bash
   ./gradlew test
   ```

> Hinweis: Am Ende der Aufgabe `devcontainer.json` und `Dockerfile` in beide
> Repositories der 2er-Gruppe kopieren, damit alle die gleiche Umgebung haben.

## Tests

- Test-Code: `src/test/java/ch/bbw/m450/tictactoe`
- Ausführen (lokal oder im Container): `./gradlew test`
- Ausführliche Testdokumentation (GIVEN-WHEN-THEN): siehe [`TESTS.md`](TESTS.md)

Das Projekt nutzt JUnit 5 und AssertJ, inklusive Helper/Fixtures
(`TicTacToeFixtures`) und Parameterized Tests für mehrere Board-Konstellationen.

## Anwendung starten

```bash
./gradlew run
```
