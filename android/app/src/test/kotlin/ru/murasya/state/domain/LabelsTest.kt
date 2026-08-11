package ru.murasya.state.domain

import java.time.DayOfWeek
import java.time.Month
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Test

class LabelsTest {
    @Test
    fun monthsReadAsNamesNotNumbers() {
        assertEquals("August", monthName(Month.AUGUST, Locale.US))
        assertEquals("Aug", shortMonthName(Month.AUGUST, Locale.US))
    }

    @Test
    fun namesStartWithACapitalInEveryLocale() {
        val russian = Locale.forLanguageTag("ru")
        listOf(
            monthName(Month.AUGUST, russian),
            shortMonthName(Month.AUGUST, russian),
            weekDayName(DayOfWeek.MONDAY, russian),
        ).forEach { name ->
            assertEquals(name.take(1).uppercase(russian), name.take(1))
        }
    }

    @Test
    fun weekDaysAreShortEnoughForAColumn() {
        DayOfWeek.entries.forEach { day ->
            assertEquals(true, weekDayName(day, Locale.US).length <= 4)
        }
    }
}
