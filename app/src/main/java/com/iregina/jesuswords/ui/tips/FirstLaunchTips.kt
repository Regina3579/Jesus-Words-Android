package com.iregina.jesuswords.ui.tips

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.iregina.jesuswords.R
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

private val Gold = Color(0xFFFFD166)
private val ArrowCream = Color(0xFFFFF7E0)

/**
 * First-launch tip 1 (LanguageTipView.swift): a girl angel points at the real En | த toggle,
 * which shows through a glowing capsule hole in a black 60% dim. Taps inside the hole reach
 * the toggle; taps anywhere else dismiss the tip.
 *
 * [toggle] is the toggle's bounds in this overlay's coordinates (root, in px).
 */
@Composable
fun LanguageTip(toggle: Rect, screenWidth: Float, onDismiss: () -> Unit, contentDescription: String) {
    val density = LocalDensity.current
    val inflate = with(density) { 6.dp.toPx() }
    val margin = with(density) { 6.dp.toPx() }
    val hole = toggle.inflate(inflate)

    // Art: 1086×1448 px; its empty capsule is centred at (933, 320) and 256 px wide.
    val scale = min(hole.width / 256f, (screenWidth - 2 * margin) / 1086f)
    val artW = 1086f * scale
    val artH = 1448f * scale
    val artX = (toggle.center.x - 933f * scale).coerceIn(margin, max(margin, screenWidth - artW - margin))
    val artY = toggle.center.y - 320f * scale

    SpotlightOverlay(hole = hole, circular = false, onDismiss = onDismiss, contentDescription = contentDescription) {
        Image(
            painterResource(R.drawable.tip_language_art),
            contentDescription = null,
            modifier = Modifier
                .offset { IntOffset(artX.roundToInt(), artY.roundToInt()) }
                .size(with(density) { artW.toDp() }, with(density) { artH.toDp() }),
        )
    }
}

/**
 * First-launch tip 2 (ThemesTipView): the sleeping cherub card sits above the tab bar and a
 * curved arrow ends at the real Themes tab, glowing through a circular hole.
 */
@Composable
fun ThemesTip(tab: Rect, screenWidth: Float, onDismiss: () -> Unit, contentDescription: String) {
    val density = LocalDensity.current
    fun dp(v: Float) = with(density) { v.dp.toPx() }
    val side = max(tab.width, tab.height) + dp(16f)
    val hole = Rect(Offset(tab.center.x - side / 2, tab.center.y - side / 2), Size(side, side))

    // Art: 881×555 px; the arrow leaves it at (322, 540).
    val artW = min(screenWidth - dp(16f), dp(440f))
    val scale = artW / 881f
    val artH = 555f * scale
    val artX = (screenWidth - artW) / 2
    val artY = hole.top - dp(56f) - artH
    val start = Offset(artX + 322f * scale, artY + 540f * scale)
    val end = Offset(hole.center.x, hole.top - dp(4f))

    SpotlightOverlay(hole = hole, circular = true, onDismiss = onDismiss, contentDescription = contentDescription) {
        Canvas(Modifier.fillMaxSize()) { drawCurvedArrow(start, end) }
        Image(
            painterResource(R.drawable.tip_themes_art),
            contentDescription = null,
            modifier = Modifier
                .offset { IntOffset(artX.roundToInt(), artY.roundToInt()) }
                .size(with(density) { artW.toDp() }, with(density) { artH.toDp() }),
        )
    }
}

/**
 * Black 60% dim with an even-odd hole and a golden glow around it. Four tap strips cover
 * everything except the hole, so a tap inside the hole falls through to the real control
 * underneath, and a tap anywhere else (art included) dismisses the tip.
 */
@Composable
private fun SpotlightOverlay(
    hole: Rect,
    circular: Boolean,
    onDismiss: () -> Unit,
    contentDescription: String,
    art: @Composable () -> Unit,
) {
    BoxWithConstraints(
        Modifier
            .fillMaxSize()
            .semantics {
                role = Role.Button
                this.contentDescription = contentDescription
                onClick { onDismiss(); true }
            },
    ) {
        val w = constraints.maxWidth.toFloat()
        val h = constraints.maxHeight.toFloat()
        Canvas(Modifier.fillMaxSize()) {
            val dim = Path().apply {
                fillType = PathFillType.EvenOdd
                addRect(Rect(Offset.Zero, size))
                addHole(hole, circular)
            }
            drawPath(dim, Color.Black.copy(alpha = 0.6f))
            drawGlow(hole, circular)
        }
        art()
        val top = hole.top.coerceIn(0f, h)
        val bottom = hole.bottom.coerceIn(0f, h)
        val left = hole.left.coerceIn(0f, w)
        val right = hole.right.coerceIn(0f, w)
        TapStrip(Rect(0f, 0f, w, top), onDismiss)
        TapStrip(Rect(0f, bottom, w, h), onDismiss)
        TapStrip(Rect(0f, top, left, bottom), onDismiss)
        TapStrip(Rect(right, top, w, bottom), onDismiss)
    }
}

/** An invisible tap target covering exactly [area] (px). */
@Composable
private fun TapStrip(area: Rect, onTap: () -> Unit) {
    if (area.width <= 0f || area.height <= 0f) return
    val density = LocalDensity.current
    Box(
        Modifier
            .offset { IntOffset(area.left.roundToInt(), area.top.roundToInt()) }
            .size(with(density) { area.width.toDp() }, with(density) { area.height.toDp() })
            .pointerInput(Unit) { detectTapGestures { onTap() } },
    )
}

private fun Path.addHole(hole: Rect, circular: Boolean) {
    if (circular) {
        addOval(hole)
    } else {
        addRoundRect(RoundRect(hole, CornerRadius(hole.height / 2, hole.height / 2)))
    }
}

/** Golden 2.5dp outline with two soft glows (r10 at 90%, r18 at 60%). */
private fun DrawScope.drawGlow(hole: Rect, circular: Boolean) {
    val path = Path().apply { addHole(hole, circular) }
    val canvas = drawContext.canvas.nativeCanvas
    listOf(18.dp to 0.6f, 10.dp to 0.9f).forEach { (radius, alpha) ->
        val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
            style = android.graphics.Paint.Style.STROKE
            strokeWidth = 2.5.dp.toPx()
            color = Gold.toArgb()
            setShadowLayer(radius.toPx(), 0f, 0f, Gold.copy(alpha = alpha).toArgb())
        }
        canvas.drawPath(path.asAndroidPath(), paint)
    }
}

/** Curved arrow (quadratic, control = (start.x − 10, end.y − 10)) with a 14dp arrowhead. */
private fun DrawScope.drawCurvedArrow(start: Offset, end: Offset) {
    val control = Offset(start.x - 10.dp.toPx(), end.y - 10.dp.toPx())
    val path = Path().apply {
        moveTo(start.x, start.y)
        quadraticTo(control.x, control.y, end.x, end.y)
        val angle = atan2(end.y - control.y, end.x - control.x)
        val len = 14.dp.toPx()
        val spread = Math.PI.toFloat() / 6
        moveTo(end.x, end.y)
        lineTo(end.x - len * cos(angle - spread), end.y - len * sin(angle - spread))
        moveTo(end.x, end.y)
        lineTo(end.x - len * cos(angle + spread), end.y - len * sin(angle + spread))
    }
    val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        style = android.graphics.Paint.Style.STROKE
        strokeWidth = 3.5.dp.toPx()
        strokeCap = android.graphics.Paint.Cap.ROUND
        strokeJoin = android.graphics.Paint.Join.ROUND
        color = ArrowCream.toArgb()
        setShadowLayer(6.dp.toPx(), 0f, 0f, Gold.toArgb())
    }
    drawContext.canvas.nativeCanvas.drawPath(path.asAndroidPath(), paint)
}

