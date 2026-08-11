package ru.murasya.state.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.time.YearMonth
import java.util.Locale
import ru.murasya.state.R
import ru.murasya.state.domain.monthName

private const val CHEVRON_SIZE = 20

@Composable
fun MonthBar(month: YearMonth, locale: Locale, onPrev: () -> Unit, onNext: () -> Unit, onPick: () -> Unit) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ArrowButton(R.drawable.ic_chevron_left, R.string.action_prev_month, onPrev)
        Spacer(Modifier.weight(1f))
        MonthPill(month, locale, onPick)
        Spacer(Modifier.weight(1f))
        ArrowButton(R.drawable.ic_chevron_right, R.string.action_next_month, onNext)
    }
}

@Composable
private fun ArrowButton(
    @DrawableRes icon: Int,
    @StringRes label: Int,
    onClick: () -> Unit,
) {
    FilledTonalIconButton(onClick = onClick) {
        Icon(painterResource(icon), stringResource(label))
    }
}

@Composable
private fun MonthPill(month: YearMonth, locale: Locale, onClick: () -> Unit) {
    val shape = MaterialTheme.shapes.large
    Row(
        modifier =
            Modifier
                .clip(shape)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .clickable(onClickLabel = stringResource(R.string.action_pick_month), onClick = onClick)
                .padding(start = 18.dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = stringResource(R.string.month_year, monthName(month.month, locale), month.year),
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Icon(
            painter = painterResource(R.drawable.ic_expand_more),
            contentDescription = null,
            modifier = Modifier.size(CHEVRON_SIZE.dp),
        )
    }
}
