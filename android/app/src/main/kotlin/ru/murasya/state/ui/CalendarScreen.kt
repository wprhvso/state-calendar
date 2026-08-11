package ru.murasya.state.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.Locale
import kotlin.math.abs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.murasya.state.domain.DayState
import ru.murasya.state.domain.GRID_WEEKS
import ru.murasya.state.domain.MONTH_PAGES
import ru.murasya.state.domain.WEEK_DAYS
import ru.murasya.state.domain.isWeekend
import ru.murasya.state.domain.monthOfPage
import ru.murasya.state.domain.pageOfMonth
import ru.murasya.state.domain.stateCounts
import ru.murasya.state.domain.weekDayName
import ru.murasya.state.domain.weekDays

private const val WEEK_ROW_HEIGHT = 30
private const val WEEK_ROW_SPACING = 10
private const val NEAR_PAGES = 2
private const val PAGE_SPACING = 16

@Composable
fun CalendarScreen(viewModel: CalendarViewModel = viewModel()) {
    val marks by viewModel.marks.collectAsStateWithLifecycle()
    val locale = remember { Locale.getDefault() }
    val firstDay = remember(locale) { WeekFields.of(locale).firstDayOfWeek }
    var today by remember { mutableStateOf(LocalDate.now()) }
    LifecycleEventEffect(Lifecycle.Event.ON_START) { today = LocalDate.now() }

    val pager =
        rememberPagerState(initialPage = pageOfMonth(YearMonth.from(today)), pageCount = { MONTH_PAGES })
    val scope = rememberCoroutineScope()
    val month = monthOfPage(pager.targetPage)
    val counts = remember(month, marks) { stateCounts(month, marks) }
    var picking by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            MonthBar(
                month = month,
                locale = locale,
                onPrev = { scope.goTo(pager, pager.targetPage - 1) },
                onNext = { scope.goTo(pager, pager.targetPage + 1) },
                onPick = { picking = true },
            )
        },
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
        ) {
            CalendarBody(
                pager = pager,
                firstDay = firstDay,
                locale = locale,
                today = today,
                marks = marks,
                onCycle = viewModel::cycle,
                onReset = viewModel::reset,
                modifier = Modifier.weight(1f),
            )
            MonthSummary(
                counts = counts,
                showToday = month != YearMonth.from(today),
                onToday = { scope.goTo(pager, pageOfMonth(YearMonth.from(today))) },
            )
        }
    }

    if (picking) {
        MonthPicker(
            selected = month,
            current = YearMonth.from(today),
            locale = locale,
            onDismiss = { picking = false },
            onPick = { picked ->
                picking = false
                scope.goTo(pager, pageOfMonth(picked))
            },
        )
    }
}

@Composable
private fun CalendarBody(
    pager: PagerState,
    firstDay: DayOfWeek,
    locale: Locale,
    today: LocalDate,
    marks: Map<Long, DayState>,
    onCycle: (LocalDate) -> Unit,
    onReset: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier) {
        val cell = cellSize(maxWidth, maxHeight)
        Column(modifier = Modifier.fillMaxSize()) {
            WeekHeader(firstDay, locale, cell)
            Spacer(Modifier.height(WEEK_ROW_SPACING.dp))
            HorizontalPager(
                state = pager,
                modifier = Modifier.weight(1f),
                pageSpacing = PAGE_SPACING.dp,
                verticalAlignment = Alignment.Top,
                key = { page -> page },
            ) { page ->
                MonthGrid(
                    month = monthOfPage(page),
                    firstDay = firstDay,
                    today = today,
                    marks = marks,
                    cell = cell,
                    onCycle = onCycle,
                    onReset = onReset,
                )
            }
        }
    }
}

@Composable
private fun WeekHeader(firstDay: DayOfWeek, locale: Locale, cell: Dp) {
    Row(
        modifier = Modifier.fillMaxWidth().height(WEEK_ROW_HEIGHT.dp),
        horizontalArrangement = Arrangement.spacedBy(CELL_GAP.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        weekDays(firstDay).forEach { day ->
            Text(
                text = weekDayName(day, locale),
                modifier = Modifier.width(cell),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium,
                color = weekDayInk(day),
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun weekDayInk(day: DayOfWeek): Color =
    if (isWeekend(day)) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

private fun cellSize(width: Dp, height: Dp): Dp {
    val byWidth = (width - CELL_GAP.dp * (WEEK_DAYS - 1)) / WEEK_DAYS
    val taken = WEEK_ROW_HEIGHT.dp + WEEK_ROW_SPACING.dp + CELL_GAP.dp * (GRID_WEEKS - 1)
    val byHeight = (height - taken) / GRID_WEEKS
    return min(byWidth, byHeight).coerceAtLeast(0.dp)
}

private fun CoroutineScope.goTo(pager: PagerState, page: Int) {
    val target = page.coerceIn(0, MONTH_PAGES - 1)
    launch {
        if (abs(target - pager.currentPage) <= NEAR_PAGES) {
            pager.animateScrollToPage(target)
        } else {
            pager.scrollToPage(target)
        }
    }
}
