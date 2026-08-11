package ru.murasya.state.domain

enum class DayState(
    val code: Int,
) {
    WHITE(0),
    RED(1),
    YELLOW(2),
    GREEN(3),
}

fun stateOf(code: Int): DayState = DayState.entries.firstOrNull { it.code == code } ?: DayState.WHITE

fun DayState.paintedWith(brush: DayState): DayState = if (this == brush) DayState.WHITE else brush
