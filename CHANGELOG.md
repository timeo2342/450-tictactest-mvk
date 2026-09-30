# Changelog

Benutzerrelevante Aenderungen der Anwendung. DevContainer-Versionen werden
unabhaengig davon verwaltet. Ein Eintrag allein veroeffentlicht keinen Release.

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
