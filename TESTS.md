# TicTacToe – Testdokumentation

Die Ziele Z1 bis Z8 stehen im [Testkonzept](TESTKONZEPT.md).
Alle Testklassen liegen unter `src/test/java/ch/bbw/m450/tictactoe/`.
`TicTacToeFixtures` stellt frische Boards, Argument-Provider und geskriptete
Spieler bereit. AssertJ prüft Werte, Arrays, Ausgabe und genaue Exceptions.
Pioneer übernimmt Ein-/Ausgabe-Fixtures; jqwik ergänzt generierte Daten.

## Z1: Gewinnerkennung (`TicTacToeMainTest`)

| ID / Methode | GIVEN | WHEN | THEN |
|--------------|-------|------|------|
| T1.1 `isWin_detectsEveryLineForBothColors` | Acht Gewinnlinien, jeweils X und O | Beide Farben prüfen | Nur die Farbe der Linie gewinnt |
| T1.2 `isWin_requiresAllThreeMatchingStones` | Jede Linie mit jeweils einem leeren oder gegnerischen Feld | Gewinnerkennung | Kein Sieg |
| T1.3 `isWin_rejectsEmptyAndDrawBoards` | Leeres Board und volles Board ohne Linie | Beide Farben prüfen | Kein Sieg |

## Z2: Spielablauf (`TicTacToeMainTest`)

| ID / Methode | GIVEN | WHEN | THEN |
|--------------|-------|------|------|
| T2.1 `play_twoGreedyPlayersResultInCrossWinner` | Zwei GreedyPlayer | Vollständiges Spiel | CROSS gewinnt über 2–4–6; Endboard und Siegmeldung stimmen |
| T2.2 `play_circleCanWinAndStopsImmediately` | Geskriptete Züge | O vervollständigt 3–4–5 | CIRCLE gewinnt; kein weiterer Zug wird verlangt |
| T2.3 `play_drawUsesAllNineMoves` | Neun Züge ohne Gewinnlinie | Spiel durchführen | Rückgabe `null`, Ausgabe `it's a draw!` |
| T2.4 `play_aWinOnTheLastMoveIsNotADraw` | Sieg erst am neunten Zug | Spiel durchführen | CROSS-Sieg, keine Draw-Meldung |

## Z3: Fehlerfälle und Isolation (`TicTacToeMainTest`)

| ID / Methode | GIVEN | WHEN | THEN |
|--------------|-------|------|------|
| T3.1 `play_rejectsIdenticalPlayers` | Dieselbe Spielerinstanz für beide Farben | Spiel starten | `IllegalArgumentException`, `players must differ` |
| T3.2 `play_rejectsOutOfBoundsMoves` | Zug -1 oder 9 | Zug ausführen | `IllegalStateException` mit Position und unverändertem Board in der Ausgabe |
| T3.3 `play_rejectsOccupiedMoves` | Feld 0 ist bereits belegt | Nochmals Feld 0 wählen | `IllegalStateException`, ursprüngliche Belegung bleibt erhalten |
| T3.4 `play_givesEachPlayerAFreshSnapshotAndProtectsTheBoard` | Spieler überschreiben ihr übergebenes Board | Mehrere Züge | Jeder erhält den korrekten Snapshot/Farbwechsel; das echte Spiel endet unverfälscht |

## Z4: Ein-/Ausgabe (JUnit Pioneer)

| ID / Methode | GIVEN | WHEN | THEN |
|--------------|-------|------|------|
| T4.1 `TicTacToeMainTest.toString_formatsStonesFreeIndicesAndRows` | Board mit X, O und freien Feldern | Formatieren | Zeichen, freie Indizes, ANSI-Sequenzen und Zeilen stimmen exakt |
| T4.2 `HumanPlayerTest.play_preservesBufferedMovesAcrossTurns` | Eingaben 0, 2, 4, 6 | Spiel gegen Greedy | Keine Zeile geht verloren; CROSS gewinnt |
| T4.3 `HumanPlayerTest.play_readsMoveAndPrintsBoardAndColor` | Eingabe 8 für jede Farbe | Zug lesen | Index 8, korrektes Board und korrekte Farbe in der Aufforderung |
| T4.4 `HumanPlayerTest.play_rejectsMalformedInputWithoutRetrying` | Text, Leerzeile, führendes Leerzeichen, Zahlenüberlauf, danach 4 | Je ein Aufruf pro Eingabe | Vier `NumberFormatException`, danach 4; kein automatischer Retry |
| T4.5 `HumanPlayerTest.play_rejectsEndOfInput` | Ein gültiger Zug, danach Eingabeende | Zweiten Zug lesen | `NoSuchElementException` |
| T4.6 `HumanPlayerTest.main_runsTheInteractiveGameToCompletion` | Vier vorbereitete Eingaben | Echte `main`-Methode starten | Spiel läuft bis zur CROSS-Siegmeldung |

## Z5: GreedyPlayer und Stone

| ID / Methode | GIVEN | WHEN | THEN |
|--------------|-------|------|------|
| T5.1 `GreedyPlayerTest.play_choosesFirstFreeCellIncludingLastSlotWithoutChangingBoard` | Schrittweise belegtes Board | Zugwahl für jede Farbe | Erster freier Index, einschließlich 8; Board unverändert |
| T5.2 `GreedyPlayerTest.play_rejectsFullBoard` | Volles Board | Zugwahl | `IllegalStateException`, Board unverändert |
| T5.3 `StoneTest.opponent_returnsTheOtherColorAndIsReversible` | CROSS bzw. CIRCLE | Einmal und zweimal wechseln | Andere Farbe, danach Ausgangsfarbe |

## Z6: PerfectPlayer

| ID / Methode | GIVEN | WHEN | THEN |
|--------------|-------|------|------|
| T6.1 `play_neverLosesAgainstAnyLegalOpponentSequence` | Leeres Board; PerfectPlayer als X und als O | Alle legalen Gegnerzugfolgen bis Spielende durchlaufen | Gegner gewinnt niemals; jeder gewählte Zug ist frei und gültig |
| T6.2 `play_prefersImmediateWinOverBlockingOrWinningLater` | Eigener sofortiger Sieg und gegnerische Drohung | Zug wählen | Sofort gewinnen |
| T6.3 `play_blocksImmediateOpponentWin` | Gegner droht unmittelbar zu gewinnen | Zug wählen | Drohung abwehren |
| T6.4 `play_deterministicallyChoosesLowestIndexAmongEqualWins` | Mehrere gleichwertige Siege | Wiederholt und mit neuer Instanz wählen | Immer kleinster Index |
| T6.5 `play_leavesBoardUnchanged` | Teilweise belegtes Board | Zug berechnen | Gültiges freies Feld; Originalboard unverändert |
| T6.6 `play_rejectsFullBoardWithoutChangingIt` | Volles Board | Zugwahl | `IllegalStateException`, Originalboard unverändert |

Diese Methoden liegen in `PerfectPlayerTest`. Die exhaustive Prüfung folgt
beim perfekten Spieler seiner deterministischen Entscheidung, beim Gegner
dagegen jedem legalen Zug. Sie behauptet nicht, alle beliebigen Minimax-internen
Spielerentscheidungen seien gleichwertig.

## Z7: Zusätzliches Framework jqwik

**T7.1 `BoardProperties.winDetectionAgreesWithIndependentGeometricOracle`**

- GIVEN: 1.000 Boards aus `.`, `X`, `O`, reproduzierbarer Seed `4502026`.
- WHEN: Gewinnerkennung für beide Farben mit separater geometrischer Referenz vergleichen.
- THEN: Gleiche Resultate; das untersuchte Board bleibt unverändert.

Die Property läuft automatisch unter `test` und `check`. PITest verwendet
gezielt die deterministischen Jupiter-Tests; der zusätzliche jqwik-Lauf bleibt
eine unabhängige Ergänzung.

## Z8: Automatisierte Gates und Artefakte

| ID | GIVEN / WHEN | THEN |
|----|--------------|------|
| T8.1 Coverage | `./gradlew check` misst alle Produktionsklassen | Unter 90 % Branch-Coverage schlägt der Build fehl |
| T8.2 PR-Gate | Aktuellen `main` und PR-Head getrennt messen | Niedrigere Line Coverage ergibt FAIL, sonst PASS; Werte und Differenz sind sichtbar |
| T8.3 Mutation Testing | `./gradlew pitest` verändert Produktionscode | Mindestens 80 % Mutation Score; HTML/XML dokumentieren getötete und verbleibende Mutanten |
| T8.4 Release-Smoke | Release-Tag baut ein JAR und lädt es nach Veröffentlichung wieder herunter | Identische Bytes; beide JARs spielen ohne erneutes Kompilieren bis zur Siegmeldung |

Das PR-Gate vergleicht Brüche ohne vorherige Rundung. Fehlende, ungültige oder
leere Coverage-Zähler sind Fehler, keine stillschweigenden 0-%-Werte.

## Ausführung und Nachweise

```powershell
.\gradlew.bat check pitest
```

HTML-Berichte: `build/reports/tests/test/index.html`,
`build/reports/jacoco/test/html/index.html`, `build/reports/pitest/index.html`.
XML-Ergebnisse stehen unter `build/test-results/test/` sowie neben den
Coverage-/Mutation-Berichten. GitHub Actions lädt die erzeugten Reports als
Artefakte hoch. Erfolgreiche lokale Läufe sind keine Behauptung, dass ein
Release, ein externer Review oder die Teams-Abgabe schon erfolgt ist.

### Lokales Prüfprotokoll vom 30.09.2026

Geprüft wurde der lokale, noch nicht veröffentlichte Arbeitsstand mit
Microsoft OpenJDK 25.0.2 und Gradle 9.1.0 unter Windows.

| Nachweis | Ergebnis |
|----------|----------|
| `check pitest jar "-PreleaseVersion=1.0.3"` | Erfolgreich; 150 Testausführungen ohne Fehler oder übersprungene Fälle |
| jqwik-Property | 1.000 generierte Boards, Seed `4502026` |
| JaCoCo | 94/94 Branches (100 %), 69/70 Zeilen (98,57 %) |
| PITest | 75/75 Mutanten getötet (100 %) |
| Ausführbares JAR | Eingaben 0, 2, 4, 6 führen bis zur CROSS-Siegmeldung |
| Reproduzierbarkeit | Erneute Kompilierung und Archivierung mit gleicher Version ergeben identisches SHA-256 |
| PR-Vergleichsskript | Gleichheit/Verbesserung akzeptiert; kleinster Rückgang sowie fehlende, negative oder leere Zähler abgelehnt |

Die Reports beschreiben diesen Arbeitsstand, nicht den unveränderten Stand auf
GitHub. Veröffentlichung, Download eines echten Release-Anhangs und Container-CI
müssen nach Übernahme der Änderungen noch auf GitHub durchlaufen werden.
