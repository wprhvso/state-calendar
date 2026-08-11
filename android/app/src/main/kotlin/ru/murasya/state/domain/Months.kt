package ru.murasya.state.domain

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

const val FIRST_YEAR = 1970
const val LAST_YEAR = 2100
const val WEEK_DAYS = 7
const val GRID_WEEKS = 6
const val MONTHS_IN_YEAR = 12
const val MONTH_PAGES = (LAST_YEAR - FIRST_YEAR + 1) * MONTHS_IN_YEAR

private val FIRST_MONTH: YearMonth = YearMonth.of(FIRST_YEAR, 1)

fun monthOfPage(page: Int): YearMonth = FIRST_MONTH.plusMonths(page.coerceIn(0, MONTH_PAGES - 1).toLong())

fun pageOfMonth(month: YearMonth): Int =
    ((month.year - FIRST_YEAR) * MONTHS_IN_YEAR + month.monthValue - 1).coerceIn(0, MONTH_PAGES - 1)

fun weekDays(first: DayOfWeek): List<DayOfWeek> = List(WEEK_DAYS) { first.plus(it.toLong()) }

fun isWeekend(day: DayOfWeek): Boolean = day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY

fun monthGrid(month: YearMonth, first: DayOfWeek): List<LocalDate?> {
    val lead = (month.atDay(1).dayOfWeek.value - first.value + WEEK_DAYS) % WEEK_DAYS
    val length = month.lengthOfMonth()
    return List(GRID_WEEKS * WEEK_DAYS) { cell ->
        val day = cell - lead + 1
        if (day in 1..length) month.atDay(day) else null
    }
}

fun stateCounts(month: YearMonth, marks: Map<Long, DayState>): Map<DayState, Int> {
    val counted =
        List(month.lengthOfMonth()) { marks[month.atDay(it + 1).toEpochDay()] ?: DayState.WHITE }
            .groupingBy { it }
            .eachCount()
    return DayState.entries.associateWith { counted[it] ?: 0 }
}
