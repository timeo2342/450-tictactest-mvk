# Release-Prozess

## Aktueller Umsetzungsstand

Die Versionen `v1.0.3` und `v1.0.4` sind im bestehenden Repository
`timeo2342/450-tictactest-mvk` mit startbaren JAR-Anhaengen veroeffentlicht.
Beide Dateien wurden nach der Veroeffentlichung heruntergeladen und mit einem
vollstaendigen Spiel ausgefuehrt. Es wurde kein neues Repository angelegt.
Die private Abgabe ist separat mit der Lehrperson zu klaeren, weil das bestehende
Repository ein oeffentlicher Fork ist.

| Version | Commit | Release-Workflow |
|---------|--------|------------------|
| [v1.0.3](https://github.com/timeo2342/450-tictactest-mvk/releases/tag/v1.0.3) | `bf5eae140b51bcbb17ee04efc5e9cff3eaeb58bb` | [36680240117](https://github.com/timeo2342/450-tictactest-mvk/actions/runs/36680240117) |
| [v1.0.4](https://github.com/timeo2342/450-tictactest-mvk/releases/tag/v1.0.4) | `ded430016f36362f1c18e7a059189c3451f95a0d` | [36680537271](https://github.com/timeo2342/450-tictactest-mvk/actions/runs/36680537271) |

Die SHA-256-Werte der heruntergeladenen Dateien stimmen mit den
GitHub-Asset-Digests ueberein:

- `tictactest-1.0.3.jar`: `3a066e92ba315170ffa0f0ed4576d7ceee4ec094d38d77e849dc2674e321d0c7`
- `tictactest-1.0.4.jar`: `59fb4755b534bebe7b1f7ead73c62d38a574a4ff1f6b0ea08a34b5a67d9a7637`

## Freigabe und Versionierung

Ein Projektverantwortlicher entscheidet bewusst ueber einen Release. Erst ein
gepushter Git-Tag `vMAJOR.MINOR.PATCH` startet `.github/workflows/release.yml`.
Ein normaler Push auf `main` startet keinen Anwendungs-Release.
DevContainer-Releases sind davon getrennt.

- MAJOR: inkompatible Aenderungen an Spiel-/Spieler-Schnittstellen.
- MINOR: rueckwaertskompatible neue Funktionen.
- PATCH: kompatible Fehlerkorrekturen.

Die erste regulaer veroeffentlichte Version ist mindestens `1.0.0`.
Die bereits vorhandenen Tags `v1.0.0`, `v1.0.1` und `v1.0.2` werden nicht verschoben.
Die neue Release-Reihe beginnt deshalb mit `v1.0.3`.
Gradle bezieht die Version aus `-PreleaseVersion=MAJOR.MINOR.PATCH`, im Workflow
direkt aus dem Tag. Ohne diese Property heissen lokale Builds `1.0.4-SNAPSHOT`.

## Voraussetzungen

Alle vorgesehenen Aenderungen muessen reviewed und auf `main` sein.
Java 25 und der Gradle Wrapper werden verwendet. Vor einem Tag muessen
`check` (inklusive Coverage-Grenze) und `pitest` erfolgreich sein.
Ein datierter Eintrag in `CHANGELOG.md` muss die Version mit den Abschnitten
`Added`, `Changed` und `Fixed` beschreiben; nicht zutreffende Abschnitte koennen
ausdruecklich `Keine.` enthalten. Das Changelog ist keine rohe Commit-Liste.

## Release erstellen (PowerShell)

Beispiel fuer den naechsten Release `1.0.5`: zuvor den zugehoerigen datierten
Changelog-Eintrag und die gewuenschten Aenderungen ueber einen PR integrieren.
Die bereits veroeffentlichten Tags nicht erneut erzeugen.

```powershell
git switch main
git pull --ff-only
git status --short
.\gradlew.bat clean check pitest jar "-PreleaseVersion=1.0.5"
java -jar .\build\libs\tictactest-1.0.5.jar
git tag -a v1.0.5 -m "Release v1.0.5"
git push origin v1.0.5
```

`git status --short` muss vor dem Tag leer sein. Neue Code-/Changelog-Aenderungen
vorher committen und ueber den vorgesehenen Review-Prozess nach `main` bringen.
Unter Linux/macOS `./gradlew` statt `.\gradlew.bat` verwenden.
Tags sind unveraenderlich: Bei einem Fehler korrigieren und eine neue Version
erstellen, nicht einen bereits veroeffentlichten Tag neu zuordnen.

## Automatischer Ablauf

Der Workflow validiert den Tag und verlangt, dass sein Commit zu `main` gehoert.
Er baut exakt diesen Commit, fuehrt Tests, Coverage-Grenze und PITest aus und
erzeugt `tictactest-VERSION.jar` mit `Main-Class` und `Implementation-Version`.
Die Archive haben eine feste Eintragsreihenfolge und keine variablen Zeitstempel.
Bei gleichem Quellstand, gleicher Version und gleichem JDK sind sie reproduzierbar.

Ein vollstaendiges Spiel prueft das gebaute JAR. Anschliessend entsteht ein GitHub
Release mit Tag, vollstaendigem Commit-SHA, Changelog-Auszug und JAR-Anhang.
Der Workflow laedt den Anhang erneut herunter, vergleicht ihn mit dem gebauten
JAR und startet ihn ohne erneute Kompilierung.
Alle erzeugten Reports werden auch bei Fehlern als Actions-Artefakt bereitgestellt.

Schlaegt ein Test, Gate oder Build fehl, wird kein Release erstellt.
Schlaegt erst der Download-/Startschritt nach der Veroeffentlichung fehl, ist
der Workflow rot und die Freigabe muss untersucht werden; ein bereits erstellter
Release wird nicht stillschweigend geloescht.

## Download und manueller Abnahmeschritt

Releases: https://github.com/timeo2342/450-tictactest-mvk/releases

Den JAR-Anhang der gewuenschten Version herunterladen und mit
`java -jar tictactest-VERSION.jar` starten (Java 25 erforderlich).
Ein normales Spiel bis zum Ende spielen. Ungueltige Eingaben fuehren weiterhin
zu einer Exception; dies ist bewusst unveraendertes Verhalten.
Mit `git rev-list -n 1 v1.0.3` laesst sich der zugehoerige Commit bestimmen.

## Zweiter Release

Der zweite Release `v1.0.4` korrigiert die Eingabeaufforderung von
"where to to put" zu "where to put". Die zugehoerigen Regressionen und der
Changelog wurden in [PR #12](https://github.com/timeo2342/450-tictactest-mvk/pull/12)
angepasst. Die Tags zeigen auf unterschiedliche Commits; auch die
heruntergeladenen JAR-Dateien unterscheiden sich tatsaechlich.

## Externe Voraussetzungen

GitHub Actions braucht `contents: write` fuer die Veroeffentlichung.
Auf `main` sind `compare` (PR Coverage Gate), `coverage` (Qualitaets-CI) und
`test` (DevContainer-CI) als erforderliche GitHub-Actions-Checks eingerichtet.
Der PR muss zum aktuellen `main` passen; die Regeln gelten auch fuer Administratoren.
Direkte Pushes, Force-Pushes und das Loeschen von `main` sind nicht erlaubt.
Eine menschliche Freigabe wird nicht automatisch behauptet: `bernedom` wurde
fuer [PR #11](https://github.com/timeo2342/450-tictactest-mvk/pull/11) angefragt.
Die private Abgabe und der Reviewer `bernedom` muessen mit den
Repository-Berechtigungen abgestimmt sein. Ein oeffentlicher Fork laesst sich
nicht einfach auf privat umstellen; dafuer ist ein separates privates Repository
oder eine mit der Lehrperson abgestimmte Loesung notwendig.
