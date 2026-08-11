package ru.murasya.state.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class DayStateTest {
    @Test
    fun theCycleRunsWhiteRedYellowGreen() {
        assertEquals(DayState.RED, DayState.WHITE.next())
        assertEquals(DayState.YELLOW, DayState.RED.next())
        assertEquals(DayState.GREEN, DayState.YELLOW.next())
    }

    @Test
    fun greenWrapsBackToWhite() {
        assertEquals(DayState.WHITE, DayState.GREEN.next())
    }

    @Test
    fun fourTapsLeaveTheDayAsItWas() {
        DayState.entries.forEach { start ->
            var state = start
            repeat(DayState.entries.size) { state = state.next() }
            assertEquals(start, state)
        }
    }

    @Test
    fun codesSurviveARoundTrip() {
        DayState.entries.forEach { state ->
            assertEquals(state, stateOf(state.code))
        }
    }

    @Test
    fun anUnknownCodeReadsAsBlank() {
        assertEquals(DayState.WHITE, stateOf(-1))
        assertEquals(DayState.WHITE, stateOf(99))
    }
}
