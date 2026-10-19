package com.securityanalyzer.app

import org.junit.Assert.assertEquals
import org.junit.Test

class ScoringTest {
    @Test fun emptyIsPerfect() = assertEquals(100, Scoring.score(emptyList()))

    @Test fun penaltiesAdd() {
        val f = listOf(Finding(Severity.HIGH, "a", "b"), Finding(Severity.MEDIUM, "c", "d"))
        assertEquals(60, Scoring.score(f))
    }

    @Test fun neverBelowZero() {
        val f = List(10) { Finding(Severity.HIGH, "a", "b") }
        assertEquals(0, Scoring.score(f))
    }
}
