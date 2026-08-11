package ru.murasya.state.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import java.time.YearMonth
import java.util.Locale
import ru.murasya.state.R
import ru.murasya.state.domain.FIRST_YEAR
import ru.murasya.state.domain.LAST_YEAR
import ru.murasya.state.domain.MONTHS_IN_YEAR
import ru.murasya.state.domain.shortMonthName

private const val PICKER_COLUMNS = 3
private const val CURRENT_RING = 2

@Composable
fun MonthPicker(
    selected: YearMonth,
    current: YearMonth,
    locale: Locale,
    onDismiss: () -> Unit,
    onPick: (YearMonth) -> Unit,
) {
    var year by remember { mutableIntStateOf(selected.year) }
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = MaterialTheme.shapes.extraLarge, tonalElevation = 6.dp) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                YearRow(year) { year = it }
                MonthCells(year, selected, current, locale, onPick)
                Row(modifier = Modifier.fillMaxWidth()) {
                    Spacer(Modifier.weight(1f))
                    TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_close)) }
                }
            }
        }
    }
}

@Composable
private fun YearRow(year: Int, onChange: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconButton(onClick = { onChange(year - 1) }, enabled = year > FIRST_YEAR) {
            Icon(painterResource(R.drawable.ic_chevron_left), stringResource(R.string.action_prev_year))
        }
        Text(text = year.toString(), style = MaterialTheme.typography.headlineSmall)
        IconButton(onClick = { onChange(year + 1) }, enabled = year < LAST_YEAR) {
            Icon(painterResource(R.drawable.ic_chevron_right), stringResource(R.string.action_next_year))
        }
    }
}

@Composable
private fun MonthCells(
    year: Int,
    selected: YearMonth,
    current: YearMonth,
    locale: Locale,
    onPick: (YearMonth) -> Unit,
) {
    val rows = List(MONTHS_IN_YEAR) { it + 1 }.chunked(PICKER_COLUMNS)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                row.forEach { number ->
                    val item = YearMonth.of(year, number)
                    MonthCell(
                        label = shortMonthName(item.month, locale),
                        chosen = item == selected,
                        ringed = item == current && item != selected,
                        modifier = Modifier.weight(1f),
                        onClick = { onPick(item) },
                    )
                }
            }
        }
    }
}

@Composable
private fun MonthCell(label: String, chosen: Boolean, ringed: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val shape = MaterialTheme.shapes.medium
    val container =
        if (chosen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHigh
    val ink = if (chosen) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    val ring = if (ringed) MaterialTheme.colorScheme.primary else Color.Transparent
    Box(
        modifier =
            modifier
                .clip(shape)
                .background(container)
                .border(CURRENT_RING.dp, ring, shape)
                .clickable(onClick = onClick)
                .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = label, color = ink, style = MaterialTheme.typography.labelLarge, maxLines = 1)
    }
}
