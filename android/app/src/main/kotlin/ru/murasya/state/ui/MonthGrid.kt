package ru.murasya.state.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import ru.murasya.state.R
import ru.murasya.state.domain.DayState
import ru.murasya.state.domain.WEEK_DAYS
import ru.murasya.state.domain.monthGrid

const val CELL_GAP = 6

private const val PRESSED_SCALE = 0.9f
private const val TODAY_RING = 2
private const val BLANK_EDGE = 1

@Composable
fun MonthGrid(
    month: YearMonth,
    firstDay: DayOfWeek,
    today: LocalDate,
    marks: Map<Long, DayState>,
    cell: Dp,
    onPaint: (LocalDate) -> Unit,
) {
    val weeks = remember(month, firstDay) { monthGrid(month, firstDay).chunked(WEEK_DAYS) }
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(CELL_GAP.dp),
    ) {
        weeks.forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(CELL_GAP.dp, Alignment.CenterHorizontally),
            ) {
                week.forEach { date ->
                    if (date == null) {
                        Spacer(Modifier.size(cell))
                    } else {
                        DayCell(
                            date = date,
                            state = marks[date.toEpochDay()] ?: DayState.WHITE,
                            today = date == today,
                            size = cell,
                            onPaint = onPaint,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(date: LocalDate, state: DayState, today: Boolean, size: Dp, onPaint: (LocalDate) -> Unit) {
    val press = remember { MutableInteractionSource() }
    val pressed by press.collectIsPressedAsState()
    val scale by animateFloatAsState(
        if (pressed) PRESSED_SCALE else 1f,
        animationSpec = springySpatial(),
        label = "dayScale",
    )
    val fill by animateColorAsState(fillOf(state), animationSpec = calmEffects(), label = "dayFill")
    val shape = MaterialTheme.shapes.small
    val edge = edgeOf(today, state)
    val label = stringResource(nameOf(state))
    Box(
        modifier =
            Modifier
                .size(size)
                .scale(scale)
                .clip(shape)
                .background(fill)
                .border(edge.width, edge.color, shape)
                .clickable(
                    interactionSource = press,
                    indication = null,
                    onClickLabel = stringResource(R.string.action_paint),
                    onClick = { onPaint(date) },
                ).semantics { stateDescription = label },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = if (today) FontWeight.Bold else null,
            color = inkOf(state, fill),
        )
    }
}

private data class Edge(
    val width: Dp,
    val color: Color,
)

@Composable
private fun edgeOf(today: Boolean, state: DayState): Edge =
    when {
        today -> Edge(TODAY_RING.dp, MaterialTheme.colorScheme.primary)
        state == DayState.WHITE -> Edge(BLANK_EDGE.dp, MaterialTheme.colorScheme.outlineVariant)
        else -> Edge(BLANK_EDGE.dp, Color.Transparent)
    }
