package ru.murasya.state.domain

enum class DayState(
    val code: Int,
) {
    WHITE(0),
    RED(1),
    YELLOW(2),
    GREEN(3),
    ;

    fun next(): DayState = entries[(ordinal + 1) % entries.size]
}

fun stateOf(code: Int): DayState = DayState.entries.firstOrNull { it.code == code } ?: DayState.WHITE
