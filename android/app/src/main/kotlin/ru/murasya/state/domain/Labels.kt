package ru.murasya.state.domain

import java.time.DayOfWeek
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

fun monthName(month: Month, locale: Locale): String =
    titled(month.getDisplayName(TextStyle.FULL_STANDALONE, locale), locale)

fun shortMonthName(month: Month, locale: Locale): String =
    titled(month.getDisplayName(TextStyle.SHORT_STANDALONE, locale), locale)

fun weekDayName(day: DayOfWeek, locale: Locale): String =
    titled(day.getDisplayName(TextStyle.SHORT_STANDALONE, locale), locale)

private fun titled(text: String, locale: Locale): String = text.replaceFirstChar { it.titlecase(locale) }
