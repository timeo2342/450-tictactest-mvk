package ch.bbw.m450.tictactoe;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Dummy tests to verify that both JUnit 5 and AssertJ are correctly set up.
 */
class DummyTest {

	@Test
	void junitDummy() {
		// classic JUnit assertion
		assertFalse(false);
		assertTrue(true);
	}

	@Test
	void assertJDummy() {
		// fluent AssertJ assertion
		assertThat(true).isTrue();
		assertThat("tic-tac-toe").startsWith("tic").contains("tac");
	}

	@Test
	void failingDummy() {
		// this test fails on purpose (see assignment example)
		assertFalse(true);
	}
}
