package ru.murasya.state.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class DayStateTest {
    @Test
    fun aBlankDayTakesTheBrush() {
        colours().forEach { brush ->
            assertEquals(brush, DayState.WHITE.paintedWith(brush))
        }
    }

    @Test
    fun aDayPaintedItsOwnColourGoesBlank() {
        DayState.entries.forEach { state ->
            assertEquals(DayState.WHITE, state.paintedWith(state))
        }
    }

    @Test
    fun anotherColourReplacesTheOneThere() {
        colours().forEach { state ->
            colours().filter { it != state }.forEach { brush ->
                assertEquals(brush, state.paintedWith(brush))
            }
        }
    }

    @Test
    fun theBlankBrushAlwaysClears() {
        DayState.entries.forEach { state ->
            assertEquals(DayState.WHITE, state.paintedWith(DayState.WHITE))
        }
    }

    @Test
    fun theSecondTapOfOneBrushUndoesTheFirst() {
        colours().forEach { brush ->
            DayState.entries.filter { it != brush }.forEach { start ->
                assertEquals(brush, start.paintedWith(brush))
                assertEquals(DayState.WHITE, start.paintedWith(brush).paintedWith(brush))
            }
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

    private fun colours() = DayState.entries.filter { it != DayState.WHITE }
}
