# 450 – TicTacTest

TicTacToe-Projekt für Modul 450 mit JUnit, AssertJ und einem DevContainer.

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
3. VS Code lädt die festgelegte Image-Version aus GHCR und öffnet das unter
   `/workspace` eingebundene Projekt im Container.
4. Im Container-Terminal die Tests ausführen:

   ```bash
   ./gradlew test
   ```

> Hinweis: Am Ende der Aufgabe `devcontainer.json` und `Dockerfile` in beide
> Repositories der 2er-Gruppe kopieren, damit alle die gleiche Umgebung haben.

## Tests

- Test-Code: `src/test/java/ch/bbw/m450/tictactoe`
- Ausführen (lokal oder im Container): `./gradlew test`
- Qualitätsprüfung inklusive mindestens 90 % Branch-Coverage: `./gradlew check`
- Mutation Testing: `./gradlew pitest`
- Ausführliche Testdokumentation (GIVEN-WHEN-THEN): siehe [`TESTS.md`](TESTS.md)
- Teststrategie, Zuständigkeiten und Kriterien: [`TESTKONZEPT.md`](TESTKONZEPT.md)

Das Projekt nutzt JUnit und AssertJ, inklusive Helper/Fixtures und parameterisierter
Tests. JUnit Pioneer prüft Ein-/Ausgaben; jqwik ergänzt Property-Tests.
JaCoCo misst die Abdeckung aller Produktionsklassen, PITest prüft die
Fehlererkennung der Tests durch Mutationen.

Reports liegen unter `build/reports/tests/test/`, `build/reports/jacoco/test/`
und `build/reports/pitest/`. Die Qualitäts-CI lädt die erzeugten Reports auch bei
einem fehlgeschlagenen Lauf als Artefakt hoch.

## Spieler

`HumanPlayer` liest Konsoleneingaben, `GreedyPlayer` wählt das erste freie Feld.
Der zusätzliche `PerfectPlayer` verwendet Minimax und kann bei legalen Zügen
weder als CROSS noch als CIRCLE verlieren. Die bestehende `main`-Methode startet
weiterhin HumanPlayer gegen GreedyPlayer. Über
`TicTacToeMain.play(new HumanPlayer(), new PerfectPlayer())` kann der perfekte
Gegner verwendet werden; zwei perfekte Spieler erreichen ein Unentschieden.

## Anwendung starten

```bash
./gradlew run
```

Unter Windows: `.\gradlew.bat run` bzw. `.\gradlew.bat test`.
`JAVA_HOME` muss auf ein vorhandenes JDK 25 zeigen.
Ungültige Eingaben und Eingabeende brechen das Spiel weiterhin mit einer Exception ab.

## Releases

```powershell
.\gradlew.bat clean check pitest jar "-PreleaseVersion=1.0.3"
java -jar .\build\libs\tictactest-1.0.3.jar
```

Der [Release-Prozess](RELEASE.md) beschreibt Freigabe, semantische Versionierung
und Git-Tags. Nur ein Release-Tag startet die Veröffentlichung der Anwendung;
ein Push auf `main` reicht nicht. Änderungen stehen im [Changelog](CHANGELOG.md).
Veröffentlichte JARs: https://github.com/timeo2342/450-tictactest-mvk/releases

## CI-Gates

`check` erzwingt **90 % Branch-Coverage**. Unabhängig davon vergleicht der
Workflow `PR Coverage Gate` die **Line Coverage** des PR-Heads mit dem aktuellen
`main` und lehnt jede Verschlechterung ab. Beide Werte, die Differenz und
PASS/FAIL stehen im Workflow-Log und in der Job-Zusammenfassung. Der Vergleich
läuft nur für Pull Requests auf `main` oder bei manueller Ausführung.
Die [Coverage-Zeitreihe](https://timeo2342.github.io/450-tictactest-mvk/)
zeigt die Entwicklung auf `main`.
