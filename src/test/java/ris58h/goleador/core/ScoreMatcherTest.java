package ris58h.goleador.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScoreMatcherTest {

    private void testFind(String text, int expectedScoreLeft, int expectedScoreRight) {
        Score score = ScoreMatcher.find(text);
        assertNotNull(score);
        assertEquals(expectedScoreLeft, score.left);
        assertEquals(expectedScoreRight, score.right);
    }

    @Test
    void hyphenSeparator() {
        testFind("19:32 BAYERN 2-1 LEVERKUSEN", 2, 1);
    }

    @Test
    void whitespaceSeparator() {
        testFind("12:08 INT 0 0 TOT", 0, 0);
    }

    @Test
    void noisyInput() {
        testFind("#8 90:00 INT 2 1 TOT", 2, 1);
    }

    @Test
    void symbolsAround() {
        testFind("3:22 BAYERN |3-1) B LEVERKUSEN 4", 3, 1);
    }

    @Test
    void scoreAtTheBegin() {
        testFind("2-0 som text", 2, 0);
    }

    @Test
    void scoreAtTheEnd() {
        testFind("some text 2-0", 2, 0);
    }
}