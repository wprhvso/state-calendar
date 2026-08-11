package ru.murasya.state.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import ru.murasya.state.R
import ru.murasya.state.domain.DayState

private const val DOT_SIZE = 14

@Composable
fun MonthSummary(counts: Map<DayState, Int>, showToday: Boolean, onToday: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DayState.entries.forEach { state ->
            CountChip(state = state, count = counts[state] ?: 0, modifier = Modifier.weight(1f))
        }
        AnimatedVisibility(
            visible = showToday,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut(),
        ) {
            TodayButton(onToday)
        }
    }
}

@Composable
private fun CountChip(state: DayState, count: Int, modifier: Modifier = Modifier) {
    val label = stringResource(nameOf(state))
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = modifier.semantics { contentDescription = label },
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Dot(state)
            Text(text = count.toString(), style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun Dot(state: DayState) {
    Box(
        modifier =
            Modifier
                .size(DOT_SIZE.dp)
                .clip(CircleShape)
                .background(fillOf(state))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
    )
}

@Composable
private fun TodayButton(onClick: () -> Unit) {
    FilledTonalIconButton(onClick = onClick) {
        Icon(painterResource(R.drawable.ic_today), stringResource(R.string.action_today))
    }
}
