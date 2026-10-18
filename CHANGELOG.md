# Changelog

Benutzerrelevante Aenderungen der Anwendung. DevContainer-Versionen werden
unabhaengig davon verwaltet. Ein Eintrag allein veroeffentlicht keinen Release.

## [Unreleased]

### Added
- Eigener Gradle-Task `e2eTest` fuer vollstaendige Konsolenspiele mit JUnit Pioneer.
- E2E-Faelle fuer Siege beider Farben, Unentschieden, fehlerhafte und boeswillige
  Eingaben, Eingabeende sowie grosse Eingaben mit festen Zeitlimits.
- E2E-Ausfuehrung in `check` und den CI-Pipelines mit eigenem HTML/XML-Bericht.

### Changed
- Ungueltige Konsoleneingaben werden mit einer Meldung abgelehnt und erneut
  abgefragt, ohne einen Spielzug zu verbrauchen. Das gilt fuer Text, Zahlen
  ausserhalb von 0-8 und belegte Felder.
- Leerzeichen um eine eingegebene Zahl werden akzeptiert.
- Die E2E-Fehlerfaelle pruefen nun die Fortsetzung bis zum regulaeren Spielende.

### Fixed
- Auch bei Unentschieden wird das vollstaendige Endbrett nach dem letzten Zug
  ausgegeben; der Fehler wurde zuerst durch einen fehlschlagenden E2E-Test belegt.

## [1.0.4] - 2026-09-30

### Added
- Keine.

### Changed
- Die lokalen Snapshot-Builds verwenden die Versionsnummer `1.0.4-SNAPSHOT`.

### Fixed
- Die Eingabeaufforderung zeigt "where to put" statt "where to to put".
- Die Ein-/Ausgabe-Assertions sichern die korrigierte Aufforderung fuer beide Farben ab.

## [1.0.3] - 2026-09-30

### Added
- Perfekt spielender, deterministischer Minimax-Spieler `PerfectPlayer`.
- Versioniertes, direkt mit Java 25 startbares JAR und taggesteuerter Release-Prozess.
- Pioneer-Ein-/Ausgabetests, Property-Tests und automatisiertes Mutation Testing.
- Verbindliche 90-Prozent-Branch-Coverage und ein Coverage-Vergleich fuer Pull Requests.

### Changed
- Testdaten und parametrisierte Tests decken beide Spielfarben ab.
- CI stellt Test-, Coverage- und Mutation-Reports als Artefakte bereit.
- Test- und Release-Dokumentation beschreibt Qualitaetsanforderungen und Freigabe.

### Fixed
- Gradle gibt Konsoleneingaben an die Anwendung weiter.
- Mehrere vorbereitete Eingabezeilen bleiben ueber Spielzuege hinweg erhalten.
- Der DevContainer oeffnet den korrekt eingebundenen Projektordner.
- Fehler beim Abruf der Coverage-Historie brechen die Veroeffentlichung ab.
- Wiederholte DevContainer-Builds verwenden unterschiedliche Versions-Tags.
