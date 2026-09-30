# Release-Prozess

## Aktueller Umsetzungsstand

Build und Workflow sind vorbereitet. Die Veroeffentlichung erfolgt im bestehenden
Repository `timeo2342/450-tictactest-mvk`; ein neues Repository wird nicht angelegt.
Zwei Releases mit unterschiedlichen Code-Staenden und startbaren JAR-Anhaengen
sind Teil der Abnahme. Die private Abgabe ist separat mit der Lehrperson zu
klaeren, weil das bestehende Repository ein oeffentlicher Fork ist.

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

```powershell
git switch main
git pull --ff-only
git status --short
.\gradlew.bat clean check pitest jar "-PreleaseVersion=1.0.3"
java -jar .\build\libs\tictactest-1.0.3.jar
git tag -a v1.0.3 -m "Release v1.0.3"
git push origin v1.0.3
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

Nach dem ersten Release eine kleine tatsaechliche Aenderung vornehmen, zum Beispiel
eine Korrektur der Eingabeaufforderung. Einen neuen Changelog-Eintrag fuer `1.0.4`
mit dem tatsaechlichen Freigabedatum ergaenzen, committen/reviewen und die Schritte
mit `1.0.4` wiederholen. Die beiden Tags muessen auf unterschiedliche Staende
zeigen und beide Releases einen herunterladbaren, startbaren JAR-Anhang besitzen.

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
