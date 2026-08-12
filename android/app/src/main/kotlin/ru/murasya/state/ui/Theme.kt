package ru.murasya.state.ui

import androidx.annotation.StringRes
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import ru.murasya.state.R
import ru.murasya.state.domain.DayState

private val RED_LIGHT = Color(0xFFE8555A)
private val RED_DARK = Color(0xFFB4474C)
private val YELLOW_LIGHT = Color(0xFFF0C23C)
private val YELLOW_DARK = Color(0xFFBE9526)
private val GREEN_LIGHT = Color(0xFF43B77A)
private val GREEN_DARK = Color(0xFF2F8A60)

private const val QUICK_MS = 160

private val CALENDAR_SHAPES =
    Shapes(
        extraSmall = RoundedCornerShape(10.dp),
        small = RoundedCornerShape(14.dp),
        medium = RoundedCornerShape(20.dp),
        large = RoundedCornerShape(28.dp),
        extraLarge = RoundedCornerShape(36.dp),
    )

@Composable
fun StateTheme(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val scheme =
        if (isSystemInDarkTheme()) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    MaterialTheme(colorScheme = scheme, shapes = CALENDAR_SHAPES, content = content)
}

@Composable
fun fillOf(state: DayState): Color {
    val dark = isSystemInDarkTheme()
    return when (state) {
        DayState.WHITE -> MaterialTheme.colorScheme.surfaceContainerLowest
        DayState.RED -> if (dark) RED_DARK else RED_LIGHT
        DayState.YELLOW -> if (dark) YELLOW_DARK else YELLOW_LIGHT
        DayState.GREEN -> if (dark) GREEN_DARK else GREEN_LIGHT
    }
}

@Composable
fun inkOf(state: DayState, fill: Color): Color =
    if (state == DayState.WHITE) MaterialTheme.colorScheme.onSurface else onFill(fill)

fun onFill(fill: Color): Color = if (fill.luminance() > 0.5f) Color.Black else Color.White

@StringRes
fun nameOf(state: DayState): Int =
    when (state) {
        DayState.WHITE -> R.string.state_white
        DayState.RED -> R.string.state_red
        DayState.YELLOW -> R.string.state_yellow
        DayState.GREEN -> R.string.state_green
    }

fun <T> springySpatial(): SpringSpec<T> = spring(dampingRatio = 0.62f, stiffness = Spring.StiffnessMediumLow)

fun <T> calmEffects(): SpringSpec<T> = spring(dampingRatio = 1f, stiffness = Spring.StiffnessMedium)

fun <T> quickEffects(): TweenSpec<T> = tween(QUICK_MS)
