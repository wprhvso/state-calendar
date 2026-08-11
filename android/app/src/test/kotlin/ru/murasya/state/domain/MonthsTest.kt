package ru.murasya.state.domain

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MonthsTest {
    @Test
    fun pagesAndMonthsAreTheSameThing() {
        listOf(YearMonth.of(1970, 1), YearMonth.of(2026, 8), YearMonth.of(2100, 12)).forEach { month ->
            assertEquals(month, monthOfPage(pageOfMonth(month)))
        }
    }

    @Test
    fun theFirstPageIsJanuaryOfTheFirstYear() {
        assertEquals(YearMonth.of(FIRST_YEAR, 1), monthOfPage(0))
        assertEquals(YearMonth.of(LAST_YEAR, MONTHS_IN_YEAR), monthOfPage(MONTH_PAGES - 1))
    }

    @Test
    fun pagesOutsideTheRangeAreClamped() {
        assertEquals(YearMonth.of(FIRST_YEAR, 1), monthOfPage(-5))
        assertEquals(YearMonth.of(LAST_YEAR, MONTHS_IN_YEAR), monthOfPage(MONTH_PAGES + 5))
        assertEquals(0, pageOfMonth(YearMonth.of(1900, 3)))
        assertEquals(MONTH_PAGES - 1, pageOfMonth(YearMonth.of(2200, 3)))
    }

    @Test
    fun theWeekStartsWhereTheLocaleSaysItDoes() {
        assertEquals(DayOfWeek.MONDAY, weekDays(DayOfWeek.MONDAY).first())
        assertEquals(DayOfWeek.SUNDAY, weekDays(DayOfWeek.MONDAY).last())
        assertEquals(DayOfWeek.SATURDAY, weekDays(DayOfWeek.SUNDAY).last())
        assertEquals(WEEK_DAYS, weekDays(DayOfWeek.SUNDAY).size)
    }

    @Test
    fun theGridIsAlwaysSixWeeksLong() {
        listOf(YearMonth.of(2026, 2), YearMonth.of(2026, 8), YearMonth.of(2032, 2)).forEach { month ->
            assertEquals(GRID_WEEKS * WEEK_DAYS, monthGrid(month, DayOfWeek.MONDAY).size)
        }
    }

    @Test
    fun theFirstOfTheMonthLandsUnderItsWeekday() {
        val august = YearMonth.of(2026, 8)
        val fromMonday = monthGrid(august, DayOfWeek.MONDAY)
        assertNull(fromMonday[4])
        assertEquals(LocalDate.of(2026, 8, 1), fromMonday[5])

        val fromSunday = monthGrid(august, DayOfWeek.SUNDAY)
        assertEquals(LocalDate.of(2026, 8, 1), fromSunday[6])
    }

    @Test
    fun theGridHoldsEveryDayOfTheMonthAndNothingElse() {
        val february = YearMonth.of(2024, 2)
        val days = monthGrid(february, DayOfWeek.MONDAY).filterNotNull()
        assertEquals(february.lengthOfMonth(), days.size)
        assertEquals(LocalDate.of(2024, 2, 1), days.first())
        assertEquals(LocalDate.of(2024, 2, 29), days.last())
    }

    @Test
    fun unmarkedDaysCountAsBlank() {
        val month = YearMonth.of(2026, 8)
        val counts = stateCounts(month, emptyMap())
        assertEquals(month.lengthOfMonth(), counts[DayState.WHITE])
        assertEquals(0, counts[DayState.RED])
        assertEquals(0, counts[DayState.YELLOW])
        assertEquals(0, counts[DayState.GREEN])
    }

    @Test
    fun countsIgnoreOtherMonths() {
        val month = YearMonth.of(2026, 8)
        val marks =
            mapOf(
                LocalDate.of(2026, 8, 1).toEpochDay() to DayState.RED,
                LocalDate.of(2026, 8, 2).toEpochDay() to DayState.RED,
                LocalDate.of(2026, 8, 3).toEpochDay() to DayState.GREEN,
                LocalDate.of(2026, 9, 1).toEpochDay() to DayState.YELLOW,
            )
        val counts = stateCounts(month, marks)
        assertEquals(2, counts[DayState.RED])
        assertEquals(1, counts[DayState.GREEN])
        assertEquals(0, counts[DayState.YELLOW])
        assertEquals(month.lengthOfMonth() - 3, counts[DayState.WHITE])
    }

    @Test
    fun weekendsAreSaturdayAndSunday() {
        assertEquals(listOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY), DayOfWeek.entries.filter { isWeekend(it) })
    }
}
