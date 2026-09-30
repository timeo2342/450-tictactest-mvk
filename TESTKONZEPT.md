# Testkonzept – TicTacTest

Modul 450 – TicTacToe-Projekt

> Dieses Dokument beschreibt den **IST-Zustand** des Projekts: dokumentiert ist
> nur, was heute tatsächlich im Repository vorhanden ist.

## 1. Einleitung

### 1.0 Dokumentstand und Verantwortung
Version: **1.1**, Stand: **30.09.2026**.
Projektverantwortung und Pflege: **Timeo Lutz (`timeo2342`)**.
Fachlicher Review gemäss Unterrichtsauftrag: **`bernedom`**, nach Einladung im PR.
Die CI übernimmt die automatisierte Ausführung; die Freigabe bleibt beim
Projektverantwortlichen. Ein dokumentierter Prüfablauf bedeutet nicht, dass
ein externer Review oder eine Abgabe bereits stattgefunden hat.

### 1.1 Zweck
Dieses Testkonzept beschreibt die Teststrategie, die Teststruktur und die
konkreten Testziele des TicTacToe-Projekts. Es dient als Übersicht darüber, was
getestet wird und wie die Tests organisiert sind.

### 1.2 Testgegenstand
Getestet wird die Spiellogik der Klasse `TicTacToeMain`
(`src/main/java/ch/bbw/m450/tictactoe/`), insbesondere:
- `isWin(Stone[] board, Stone color)` – Erkennung einer Gewinnstellung
- `play(TicTacToePlayer x, TicTacToePlayer o)` – vollständiger Spielablauf
- `toString(Stone[])` und `main(String[])` – Ausgabe und Konsoleneinstieg
- GreedyPlayer, HumanPlayer und PerfectPlayer – Zugwahl und Eingabe
- `Stone.opponent()` – Farbwechsel

## 2. Teststrategie

### 2.1 Testart
Unit-Tests prüfen Gewinnerkennung, Darstellung, Farbwechsel und Zugwahl.
Integrationstests prüfen Spieler und Spielschleife mit vorbereiteten Eingaben;
JUnit Pioneer stellt Ein-/Ausgabestreams bereit. Property-Tests mit jqwik
ergänzen einzelne Beispiele durch generierte Daten. Der perfekte Spieler
wird für beide Farben gegen alle legalen gegnerischen Zugfolgen geprüft.
PITest verändert Produktionscode, um die Fehlererkennung der Tests zu messen.
Die separate E2E-Suite startet `main` mit dem echten HumanPlayer und GreedyPlayer,
liest vorbereitete Konsoleneingaben und prüft die Ausgabe bis zum Resultat oder
zum erwarteten Fehler. Es werden dabei keine Spieler durch Test-Doubles ersetzt.
Der Release-Workflow führt einen System-Smoke-Test des gebauten und anschließend
erneut heruntergeladenen JARs aus. Manuelle Terminalbedienung bleibt ergänzend.

### 2.2 Verwendete Werkzeuge (IST)
| Werkzeug | Version | Zweck |
|----------|---------|-------|
| JUnit (Jupiter) | 5.14.4 | Test-Framework, Testausführung |
| AssertJ | 3.27.7 | Fluent Assertions |
| JUnit Pioneer | 2.3.0 | StdIn/StdOut-Tests |
| jqwik | 1.10.1 | Zusätzliche Property-Test-Engine |
| JaCoCo | 0.8.14 | Coverage-Grenze und Reports |
| PITest / Gradle-Plugin / JUnit-Bridge | 1.30.0 / 1.19.0 / 1.2.3 | Mutation Testing |
| Gradle (Wrapper) | 9.1.0 | Build und Testausführung (`./gradlew test`) |
| GitHub Actions | – | Qualitäts-CI bei Branch-Pushes und PRs; zusätzlicher Container-Job für `main` |

Konfiguriert in `build.gradle` (`useJUnitPlatform()`), CI in
`.github/workflows/coverage.yml` und `.github/workflows/gradle.yml`.

### 2.3 Testausführung
- Lokal: `./gradlew test` oder über die IDE (IntelliJ)
- Nur E2E: `./gradlew e2eTest` (Windows: `.\gradlew.bat e2eTest`)
- Qualitätsprüfung: `./gradlew check pitest`
- CI: Qualitätsreports bei Branch-Pushes und PRs; zusätzlicher Container-Job
  bei Push und Pull Request auf `main` (Java 25, Temurin)

## 3. Teststruktur

Alle Tests und Helper liegen unter `src/test/java/ch/bbw/m450/tictactoe/`.

| Datei | Aufgabe |
|-------|---------|
| `TicTacToeFixtures` | `boardOf`, `emptyBoard`, geskriptete Spieler und Argument-Provider; frische Boards/Spieler pro Test |
| `TicTacToeMainTest` | Alle Gewinnlinien für beide Farben, unvollständige Linien, Siege, Draw, ungültige Züge, Board-Kopierschutz und Darstellung |
| `HumanPlayerTest` | Pioneer-Ein-/Ausgabe, mehrere Eingabezeilen, Fehlerverhalten, vollständiger `main`-Aufruf |
| `TicTacToeE2ETest` | Eigener Task: vollständige Konsolenspiele, ungültige/böswillige Eingaben, EOF und zeitbegrenzte Lastfälle |
| `GreedyPlayerTest` | Erstes freies Feld einschließlich Index 8, volles Board und Unverändertheit |
| `StoneTest` | Beide Farbwechsel und Rückkehr zur Ausgangsfarbe |
| `PerfectPlayerTest` | Sofortige Siege/Blocks, Determinismus, Board-Isolation und alle legalen Gegnerzugfolgen |
| `BoardProperties` | 1.000 generierte Boards mit festem Seed und unabhängiger geometrischer Gewinnerkennung |

Parameterized Tests verwenden `@MethodSource`, `@EnumSource`, `@ValueSource`
und `@CsvSource`. Eine zentrale Gewinnlinienliste wird für beide Farben und
Negativfälle wiederverwendet. Pioneer richtet Ein-/Ausgabestreams pro Test ein
und räumt sie wieder auf. Es gibt keine global zwischen Tests geteilten Boards.
Die bisherigen reinen Dummy-Assertions werden durch fachliche Tests ersetzt.
Jupiter und jqwik laufen gemeinsam in `test`; der Tag `e2e` wird dort ausgeschlossen
und ausschließlich in `e2eTest` ausgeführt. `check` verlangt beide Tasks.
JaCoCo führt beide Ausführungsdateien zusammen; der abschließende Report von
`test` startet dafür bei Bedarf auch den E2E-Task.
PITest verwendet die deterministischen Jupiter-Klassen mit dem Muster `*Test`,
ausgenommen `TicTacToeE2ETest`. Die Lastfälle werden nicht für jeden Mutanten wiederholt.

## 4. Testziele und Testfälle

Testziele sind nummeriert und den konkreten Testfällen zugeordnet.

| Ziel | Testfälle in `TESTS.md` | Überprüfbare Anforderung |
|------|------------------------|------------------------|
| Z1: Gewinnerkennung | T1.1–T1.3 | Alle acht Linien für beide Farben; fehlende/falsche Steine und leere/Draw-Boards sind kein Sieg |
| Z2: Spielablauf | T2.1–T2.4 | CROSS/CIRCLE gewinnen, Draw nach neun Zügen, Sieg am letzten Zug hat Vorrang |
| Z3: Ungültige Spielzüge und Isolation | T3.1–T3.4 | Identische Spieler, -1/9 und belegte Felder werden abgelehnt; Spieler können das echte Board nicht verändern |
| Z4: Ein-/Ausgabe | T4.1–T4.6 | Boarddarstellung, Eingabeaufforderung, gepufferte Züge und unveränderte Exceptions |
| Z5: Einfache Spieler und Farben | T5.1–T5.3 | Greedy wählt das erste freie Feld; volles Board wirft Fehler; `opponent` wechselt die Farbe |
| Z6: Perfekter Spieler | T6.1–T6.6 | Nie verlieren bei legalen Gegnerzügen, direkte Siege/Blocks, deterministisch und ohne Seiteneffekte |
| Z7: Generierte Daten | T7.1 | Gewinnerkennung stimmt für 1.000 Boards mit unabhängigem Oracle überein |
| Z8: Qualitäts- und Release-Gates | T8.1–T8.4 | Feste Branch-Grenze, kein PR-Coverage-Rückgang, Mutation-Grenze und startbares Release-JAR |
| Z9: Konsolen-E2E | E1–E11 in Abschnitt 10 | Alle Spielausgänge, Fehler- und Lastfälle, eigene Ausführung und Test-first-Korrektur |

## 5. Testfälle im Detail (GIVEN-WHEN-THEN)

Die GIVEN-WHEN-THEN-Beschreibungen und konkreten Methodennamen stehen in
[`TESTS.md`](TESTS.md). Die IDs sind den oben nummerierten Testzielen zugeordnet.

## 6. Abgrenzung (nicht getestet)

Aktuell nicht durch Tests abgedeckt (IST-Zustand):
- Interaktive Terminalbedienung (HumanPlayer wird mit vorbereiteten Eingaben getestet)
- Ungültige Board-Grössen ausserhalb der Helper-Prüfung
- Umfassende Leistungs-/Lasttests, grafische UI und externe Persistenz; begrenzte Eingabelastfälle sind in Abschnitt 10 beschrieben

## 7. Testorganisation und Ablauf

| Zeitpunkt | Zuständigkeit | Tätigkeit |
|-----------|---------------|-----------|
| Bei jeder Änderung | Entwickler | Betroffene Tests lokal ausführen und vor dem Commit `check` starten |
| Bei jedem Branch-Push / PR | GitHub Actions | Tests, Coverage und PITest ausführen; Reports archivieren |
| Beim Öffnen/Aktualisieren eines PR auf `main` | PR Coverage Gate | Line Coverage von aktuellem `main` und PR-Head neu messen und vergleichen |
| Vor dem Merge | Projektverantwortlicher / Reviewer | Diff und Reports prüfen; Review gemäss Unterrichtsauftrag anfragen |
| Vor einem Release-Tag | Projektverantwortlicher | Changelog, Version, grünen Build und vollständigen Stand auf `main` sicherstellen |
| Nach einem Release-Tag | Release-Workflow | JAR bauen, starten, veröffentlichen, erneut herunterladen und starten |
| Zur Abgabe | Projektverantwortlicher | Private Bereitstellung, Reviewer-Zugriff und PR-Link in Teams sicherstellen |

Die Planung ist ereignisgesteuert; persönliche Abgabetermine werden über Teams
geführt und nicht aus den Datumsangaben der Unterrichtsunterlagen abgeleitet.

## 8. Bestehens-, Abbruch- und Wiederaufnahmekriterien

- Ein Test besteht nur, wenn alle Assertions erfüllt sind. Nicht erwartete
  Exceptions und fehlgeschlagene Assertions sind Fehler.
- `check` verlangt mindestens **90 % Branch-Coverage** über alle
  Produktionsklassen. Es gibt keine ausgenommenen Klassen, um die Quote zu erhöhen.
- Das PR-Gate verlangt, dass die **Line Coverage** nicht unter der des aktuellen
  `main` liegt. Der Vergleich erfolgt ohne Rundung; Darstellung und Grenzwert
  sind bewusst getrennt.
- Mutationsergebnisse werden zusätzlich ausgewertet; die konfigurierte
  Mindestquote beträgt **80 %**. Überlebende oder nicht abgedeckte
  Mutanten sind konkrete Hinweise auf verbleibende Testlücken.
- Bei Compilerfehlern, fehlender Laufzeit, fehlgeschlagenen Tests/Gates oder
  ungültigen Reportdaten wird die betroffene Pipeline abgebrochen, nicht als
  bestanden gewertet. Ein nachfolgender Release darf dann nicht entstehen.
- Nach einer Korrektur wird der betroffene Lauf erneut vollständig ausgeführt.
  Ein absichtlich fehlschlagender Demonstrationstest gehört nicht dauerhaft auf `main`.

## 9. Umgebung, Nachweise und verbleibende Risiken

Java 25, Gradle Wrapper 9.1.0 und die in `build.gradle` festgelegten Bibliotheken
bilden die lokale Umgebung. Der DevContainer verwendet Alpine/Temurin und den
Benutzer `dev` (1000:1000); der Container-CI-Job benötigt intern Root-Schreibrechte.
Weitere Qualitäts- und Release-Jobs verwenden Ubuntu mit Temurin 25.
Es werden keine Mockingframeworks eingesetzt.

Nachweise sind die JUnit-XML-Ergebnisse, HTML-Testberichte, JaCoCo-HTML/XML,
PITest-HTML/XML und die GitHub-Job-Zusammenfassungen. Berichte liegen unter
`build/reports/` und `build/test-results/` sowie als Actions-Artefakte.
Sie gelten jeweils nur für den zugehörigen Commit und Lauf.

Hohe Coverage beweist keine Fehlerfreiheit. Der erschöpfende Gegnerbaum des
PerfectPlayer prüft legale Züge; beliebige beschädigte oder bereits widersprüchliche
Boards sind kein unterstützter Spieleingang. Terminaldarstellung und
Docker-/GitHub-Berechtigungen hängen zusätzlich von der jeweiligen Umgebung ab.

## 10. E2E-Auftrag vom 30.09.2026

### 10.1 Umfang und Durchführung

`TicTacToeE2ETest` verwendet JUnit Pioneer `@StdIo`/`StdOut`; bei generierten
Eingaben wird innerhalb dieses Fixtures ein Pioneer-`StdIn` eingesetzt.
Die Erweiterung stellt die ursprünglichen Streams nach jedem Fall wieder her.
Die Tests laufen seriell und starten die echte `main`-Methode, keine Shell.
Der zusätzliche JAR-Smoke-Test des Release-Workflows bleibt unabhängig erhalten.

Alle E2E-Fälle haben `@Timeout(10)` mit separatem Ausführungsthread.
Der Gradle-Task hat zusätzlich ein Zwei-Minuten-Limit als äußere Absicherung.
Die Fälle mit großen Eingaben bleiben bewusst endlich, damit bei einer
Regression nicht absichtlich unbegrenzter Speicher verbraucht wird.

### 10.2 Testfälle: erwartetes und tatsächliches Verhalten

Lokaler Stand nach der Korrektur am 30.09.2026: 28 E2E-Ausführungen erfolgreich,
keine Fehler, keine übersprungenen Fälle. Die tatsächlichen Ergebnisse stammen
aus `build/test-results/e2eTest/`, nicht aus einem behaupteten neuen GitHub-Lauf.

| ID | Eingabe / Fall | Erwartetes Verhalten | Tatsächliches Verhalten |
|----|----------------|----------------------|-------------------------|
| E1 | `0, 2, 4, 6` | X gewinnt; Endbrett `XOXOXOX..`; vier Aufforderungen | Entspricht Erwartung |
| E2 | `8, 7, 3` | O gewinnt; Endbrett `OOOX...XX`; drei Aufforderungen | Entspricht Erwartung |
| E3 | `1, 3, 4, 6, 8` | Draw; volles Endbrett `OXOXXOXOX`; fünf Aufforderungen | Zunächst Endbrett fehlend; nach Test-first-Korrektur korrekt |
| E4 | Leerzeile, Text, Leerzeichen, Dezimalzahl, Überlauf (8 Varianten) | `NumberFormatException`; genau eine Aufforderung, kein Retry | Entspricht Erwartung |
| E5 | `-1`, `9`, Integer-Minimum/-Maximum | `IllegalStateException`; keine Brettänderung und kein Ergebnis | Entspricht Erwartung |
| E6 | `0, 1`: Computer hat Feld 1 bereits belegt | `IllegalStateException`; Brett bleibt `XO.......` | Entspricht Erwartung |
| E7 | Shell-/HTML-/SQL-artiger Text, NUL, ANSI-Steuerfolge, Pfadtext (7 Varianten) | Zahlparser lehnt ab; keine erfolgreiche Spielausgabe und kein Echo des Inputs auf stdout | Entspricht Erwartung |
| E8 | EOF sofort bzw. nach erstem Spielerzug (2 Fälle) | `NoSuchElementException`; kein Endlosschleifen-Retry, kein erfundenes Ergebnis | Entspricht Erwartung |
| E9 | 1.048.576 Ziffern in einer Eingabezeile | `NumberFormatException` innerhalb von 10 s; nur erste Aufforderung | Entspricht Erwartung |
| E10 | 100.000 ungültige Eingabezeilen | Abbruch bei erster ungültiger Zeile innerhalb von 10 s; keine Ausgabeflut | Entspricht Erwartung |
| E11 | X-Sieg mit danach angehängter ungültiger Eingabe | Spiel endet beim Sieg; zusätzliche Eingabe wird nicht verarbeitet | Entspricht Erwartung |

Die Fehler-Exceptions bleiben absichtlich Teil des bisherigen Verhaltens.
Es wird keine automatische Wiederholung ungültiger Eingaben eingeführt.
Die End-to-End-Prüfungen ersetzen keinen Nachweis gegen beliebige
Denial-of-Service-Angriffe: Eine unbegrenzt lange Eingabe kann weiterhin Speicher
binden, und ein offener Eingabestream ohne Daten/EOF wartet auf Benutzereingabe.
Die Test-Timeouts sind keine neuen Produktionslimits. Diese Risiken bleiben
ausdrücklich dokumentiert; Fuzzing und die weiteren Zusatzaufträge sind optional
und werden nicht als umgesetzt ausgewiesen.

### 10.3 Gefundener Fehler und Test-first-Nachweis

**E2E-01: Letzter Zug bei Unentschieden nicht sichtbar.**
Die Spielschleife zeigte das Brett vor jeder menschlichen Eingabe und bei
Siegen am Ende. Bei Draw gab sie nur `it's a draw!` aus. Dadurch blieb im
zuletzt sichtbaren Brett Feld 8 frei, obwohl der letzte Zug bereits gespielt war.

**Rotphase:** Zuerst wurde `main_drawShowsTheFinalBoardAndResult` hinzugefügt
und ohne Produktionsänderung ausgeführt:
`.\gradlew.bat e2eTest --tests "*TicTacToeE2ETest.main_drawShowsTheFinalBoardAndResult"`.
Ergebnis: ein ausgeführter, fehlgeschlagener Test. Erwartet war das volle
Endbrett plus Draw-Meldung; tatsächlich fehlte das Endbrett.

**Korrektur:** Der Draw-Zweig gibt jetzt wie der Sieg-Zweig `toString(board)`
vor dem Resultat aus. Die bestehende Integrationsassertion wurde entsprechend
angepasst; Zugregeln und Eingabeverhalten bleiben unverändert.

**Grünphase:** Derselbe E2E-Fall und anschließend die komplette E2E-Suite
bestehen. Der gemeinsame Lauf `check e2eTest pitest` ist erfolgreich:
150 bisherige Testausführungen plus 28 separate E2E-Ausführungen,
94/94 Branches, 69/70 Zeilen und 75/75 getötete Mutanten.

### 10.4 CI und Veröffentlichung

Qualitäts-CI und DevContainer-CI rufen `e2eTest` ausdrücklich auf; `check`
bindet den Task ebenfalls ein. Der Release-Workflow verlangt ihn vor einem
zukünftigen Release. Die vorhandenen Artifact-Schritte archivieren auch
`build/reports/tests/e2eTest/` und `build/test-results/e2eTest/`.
Die gemeinsame Coverage-Auswertung bleibt an den bisherigen Reportpfaden,
sodass PR-Vergleich und Coverage-Zeitreihe kompatibel bleiben.

Diese Erweiterung wird auf einem separaten Entwicklungsbranch bereitgestellt.
Es wird dafür kein Release, PR oder Review-Aufruf erstellt. Die bestehenden
Release-JARs bleiben unverändert; die Branch-CI führt die neue Suite beim Push aus.
