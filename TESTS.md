# TicTacToe – Testdokumentation

Modul 450 – Erste Tests

## Setup

Das Projekt wurde mit **JUnit 5 (Jupiter)** und **AssertJ** aufgesetzt.

- `build.gradle`: Dependencies `org.junit.jupiter:junit-jupiter` und `org.assertj:assertj-core`
- Testausführung über `useJUnitPlatform()`

## Link zum Test-Code auf GitHub

- **Test-Code:** https://github.com/timeo2342/450-tictactest-mvk/tree/main/src/test/java/ch/bbw/m450/tictactoe
- **Repository:** https://github.com/timeo2342/450-tictactest-mvk
- **CI (Actions):** https://github.com/timeo2342/450-tictactest-mvk/actions

## Tests nach dem GIVEN-WHEN-THEN Pattern

### Dummy-Tests (JUnit & AssertJ)

#### 1. `DummyTest.junitDummy` (JUnit)
- **GIVEN:** Ein einfacher boolescher Wert
- **WHEN:** Klassische JUnit-Assertions werden ausgeführt (`assertFalse(false)`, `assertTrue(true)`)
- **THEN:** Die Assertions sind erfüllt, der Test ist grün

#### 2. `DummyTest.assertJDummy` (AssertJ)
- **GIVEN:** Ein boolescher Wert und ein String `"tic-tac-toe"`
- **WHEN:** Fluent AssertJ-Assertions werden ausgeführt (`assertThat(...).isTrue()`, `startsWith`, `contains`)
- **THEN:** Alle Bedingungen sind erfüllt, der Test ist grün

### TicTacToe-Tests (Spiellogik)

#### 3. `isWin_detectsWinningRow`
- **GIVEN:** Ein Spielbrett mit drei Kreuzen (X) in der obersten Reihe (Felder 0, 1, 2)
- **WHEN:** `isWin(board, CROSS)` wird aufgerufen
- **THEN:** Das Ergebnis ist `true` (CROSS gewinnt durch die Reihe)

#### 4. `isWin_detectsWinningColumn`
- **GIVEN:** Ein Spielbrett mit drei Kreisen (O) in der linken Spalte (Felder 0, 3, 6)
- **WHEN:** `isWin(board, CIRCLE)` wird aufgerufen
- **THEN:** Das Ergebnis ist `true` (CIRCLE gewinnt durch die Spalte)

#### 5. `isWin_returnsFalseForEmptyBoard`
- **GIVEN:** Ein komplett leeres Spielbrett
- **WHEN:** `isWin(board, ...)` wird für beide Farben aufgerufen
- **THEN:** Das Ergebnis ist jeweils `false` (niemand gewinnt)

#### 6. `play_twoGreedyPlayersResultInCrossWinner`
- **GIVEN:** Zwei GreedyPlayer (beide spielen immer das oberste freie Feld)
- **WHEN:** Ein vollständiges Spiel wird gespielt (`play(x, o)`)
- **THEN:** Der Startspieler CROSS gewinnt (Diagonale 0-4-8)

#### 7. `play_rejectsIdenticalPlayers`
- **GIVEN:** Eine einzige Spieler-Instanz, die für beide Seiten verwendet wird
- **WHEN:** `play(player, player)` mit derselben Instanz aufgerufen wird
- **THEN:** Eine `IllegalArgumentException` mit der Meldung "players must differ" wird geworfen

## Screenshots

### Alle Tests erfolgreich
> **TODO:** Screenshot einfügen, der alle grünen Tests zeigt.

### Fehlschlagender Test
> **TODO:** Screenshot einfügen, der einen fehlschlagenden Test zeigt (z.B. `assertFalse(true)`).
