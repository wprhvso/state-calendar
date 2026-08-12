package ru.murasya.state.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.hypot
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import ru.murasya.state.domain.DayState

private const val WASH_MS = 400
private const val WASH_HALF = 0.5f
private const val POP_SCALE = 1.07f
private const val RIM_ALPHA = 0.5f
private const val HALO_ALPHA = 0.45f

private val RIM_WIDTH = 2.dp
private val HALO_WIDTH = 1.5.dp
private val HALO_REACH = (CELL_GAP / 2).dp

@Immutable
data class Stain(
    val state: DayState,
    val from: Offset,
    val reach: Float,
)

@Stable
class DayInk(
    state: DayState,
) {
    private val spread = Animatable(1f)
    private val pop = Animatable(1f)

    var origin by mutableStateOf(Offset.Unspecified)

    var under by mutableStateOf(state)
        private set
    var over by mutableStateOf(state)
        private set
    var face by mutableStateOf(state)
        private set
    var source by mutableStateOf(Offset.Unspecified)
        private set
    var stain by mutableStateOf<Stain?>(null)
        private set

    val flood: Float get() = spread.value
    val lift: Float get() = pop.value

    suspend fun washTo(next: DayState) {
        if (next == over) return
        val caught = if (spread.value < 1f) Stain(over, source, spread.value) else null
        spread.snapTo(0f)
        stain = caught
        source = origin
        over = next
        coroutineScope {
            launch {
                pop.snapTo(POP_SCALE)
                pop.animateTo(1f, springySpatial())
            }
            spread.animateTo(1f, tween(WASH_MS, easing = LinearOutSlowInEasing)) {
                if (value >= WASH_HALF) face = next
            }
        }
        under = next
        stain = null
    }
}

@Composable
fun rememberDayInk(state: DayState): DayInk {
    val ink = remember { DayInk(state) }
    LaunchedEffect(state) { ink.washTo(state) }
    return ink
}

fun DrawScope.washDay(ink: DayInk, under: Color, blot: Color, over: Color) {
    val flood = ink.flood
    if (flood >= 1f) {
        drawRect(over)
        return
    }
    drawRect(under)
    ink.stain?.let { old ->
        val mark = spot(old.from)
        drawCircle(color = blot, radius = farCorner(mark, size) * old.reach, center = mark)
    }
    val from = spot(ink.source)
    val reach = farCorner(from, size) * flood
    drawCircle(color = over, radius = reach, center = from)
    drawCircle(
        color = onFill(over),
        radius = reach,
        center = from,
        alpha = RIM_ALPHA * (1f - flood),
        style = Stroke(RIM_WIDTH.toPx()),
    )
}

fun DrawScope.haloDay(ink: DayInk, tint: Color, shape: Shape) {
    val flood = ink.flood
    if (flood >= 1f) return
    val out = HALO_REACH.toPx() * flood
    drawRoundRect(
        color = tint,
        topLeft = Offset(-out, -out),
        size = Size(size.width + out * 2f, size.height + out * 2f),
        cornerRadius = CornerRadius(cornerOf(shape) + out),
        alpha = HALO_ALPHA * (1f - flood),
        style = Stroke(HALO_WIDTH.toPx()),
    )
}

private fun DrawScope.cornerOf(shape: Shape): Float {
    val outline = shape.createOutline(size, layoutDirection, this)
    return if (outline is Outline.Rounded) outline.roundRect.topLeftCornerRadius.x else 0f
}

private fun DrawScope.spot(at: Offset): Offset = if (at.isSpecified) at else center

private fun farCorner(from: Offset, size: Size): Float {
    val left = from.x
    val top = from.y
    val right = size.width - from.x
    val bottom = size.height - from.y
    return maxOf(maxOf(hypot(left, top), hypot(right, top)), maxOf(hypot(left, bottom), hypot(right, bottom)))
}
