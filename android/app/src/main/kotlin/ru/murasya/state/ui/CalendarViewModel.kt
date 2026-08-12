package ru.murasya.state.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.murasya.state.data.CalendarDatabase
import ru.murasya.state.data.DayMark
import ru.murasya.state.domain.DayState
import ru.murasya.state.domain.paintedWith
import ru.murasya.state.domain.stateOf

private const val KEEP_ALIVE_MS = 5_000L
private val DEFAULT_BRUSH = DayState.RED

class CalendarViewModel(
    app: Application,
) : AndroidViewModel(app) {
    private val dao = CalendarDatabase.get(app).dao()
    private val writes = Mutex()
    private val edits = MutableStateFlow<Map<Long, DayState>>(emptyMap())
    private val picked = MutableStateFlow<DayState?>(null)

    val marks: StateFlow<Map<Long, DayState>> =
        combine(dao.marksFlow(), edits) { stored, edited -> stored.toStates() + edited }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(KEEP_ALIVE_MS), emptyMap())

    val brush: StateFlow<DayState> =
        combine(dao.brushFlow(), picked) { stored, choice -> choice ?: stored.toBrush() }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(KEEP_ALIVE_MS), DEFAULT_BRUSH)

    fun paint(date: LocalDate) = mark(date, stateOn(date).paintedWith(brush.value))

    fun pick(state: DayState) {
        picked.value = state
        viewModelScope.launch {
            writes.withLock { dao.putBrush(state.code) }
        }
    }

    private fun stateOn(date: LocalDate): DayState = marks.value[date.toEpochDay()] ?: DayState.WHITE

    private fun mark(date: LocalDate, state: DayState) {
        val day = date.toEpochDay()
        edits.update { it + (day to state) }
        viewModelScope.launch {
            writes.withLock {
                if (state == DayState.WHITE) dao.clear(day) else dao.put(day, state.code)
            }
        }
    }
}

private fun List<DayMark>.toStates(): Map<Long, DayState> = associate { it.date to stateOf(it.state) }

private fun List<Int>.toBrush(): DayState = firstOrNull()?.let(::stateOf) ?: DEFAULT_BRUSH
