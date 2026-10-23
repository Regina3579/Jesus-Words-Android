package com.iregina.jesuswords.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

/** Width : height of the Latin cross. */
const val LATIN_CROSS_ASPECT = 0.55f

/**
 * The Christian (Latin) cross from LatinCrossShape.swift — never a plus sign.
 * Bar thickness is 35% of the width and the crossbar's top edge sits at 22% of
 * the height, so the bottom arm is the longest.
 */
fun latinCrossPath(size: Size, topLeft: Offset = Offset.Zero): Path {
    val w = size.width
    val h = size.height
    val barW = w * 0.35f
    val crossbarTop = h * 0.22f
    val crossbarBottom = crossbarTop + barW
    val vLeft = (w - barW) / 2f
    val vRight = (w + barW) / 2f
    val x = topLeft.x
    val y = topLeft.y
    return Path().apply {
        moveTo(x + vLeft, y)
        lineTo(x + vRight, y)
        lineTo(x + vRight, y + crossbarTop)
        lineTo(x + w, y + crossbarTop)
        lineTo(x + w, y + crossbarBottom)
        lineTo(x + vRight, y + crossbarBottom)
        lineTo(x + vRight, y + h)
        lineTo(x + vLeft, y + h)
        lineTo(x + vLeft, y + crossbarBottom)
        lineTo(x, y + crossbarBottom)
        lineTo(x, y + crossbarTop)
        lineTo(x + vLeft, y + crossbarTop)
        close()
    }
}

object LatinCrossShape : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline =
        Outline.Generic(latinCrossPath(size))
}

/** A cross [height] tall drawn into the centre of this DrawScope area (aspect-fit). */
fun DrawScope.drawLatinCrossFit(color: Color, boxSize: Size = size, topLeft: Offset = Offset.Zero) {
    val h = minOf(boxSize.height, boxSize.width / LATIN_CROSS_ASPECT)
    val w = h * LATIN_CROSS_ASPECT
    val origin = Offset(topLeft.x + (boxSize.width - w) / 2f, topLeft.y + (boxSize.height - h) / 2f)
    drawPath(latinCrossPath(Size(w, h), origin), color)
}

/** One soft drop shadow, as in SwiftUI `.shadow(color:radius:y:)`. */
data class DropShadow(val alpha: Float, val radius: Dp, val y: Dp = 0.dp)

/**
 * Drop-in for `LatinCrossIcon(size:)`: a filled cross [height] tall, with optional
 * soft shadows (drawn with Paint.setShadowLayer; visible on Android 9+).
 */
@Composable
fun LatinCrossIcon(
    height: Dp,
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    shadows: List<DropShadow> = emptyList(),
) {
    Canvas(modifier.size(width = height * LATIN_CROSS_ASPECT, height = height)) {
        val path = latinCrossPath(size)
        shadows.forEach { s -> drawPathShadow(path, s) }
        drawPath(path, color)
    }
}

internal fun DrawScope.drawPathShadow(path: Path, shadow: DropShadow) {
    val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.TRANSPARENT
        setShadowLayer(
            shadow.radius.toPx().coerceAtLeast(0.5f),
            0f,
            shadow.y.toPx(),
            android.graphics.Color.argb((shadow.alpha * 255).toInt().coerceIn(0, 254), 0, 0, 0),
        )
    }
    drawContext.canvas.nativeCanvas.drawPath(path.asAndroidPath(), paint)
}
