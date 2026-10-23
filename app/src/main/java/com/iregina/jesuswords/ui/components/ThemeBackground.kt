package com.iregina.jesuswords.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.iregina.jesuswords.data.AppTheme
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

/** True on tablets (smallest width ≥ 600dp): use the `_tablet` (≈3:4) image crops. */
@Composable
fun isTabletLayout(): Boolean = LocalConfiguration.current.smallestScreenWidthDp >= 600

/**
 * The full-screen background of every page (ThemeBackgroundView in BackgroundView.swift).
 *
 * Picture themes: the image crop-filled inside a clipped box, so it can never change the
 * size of the content around it, plus a black 15% overlay.
 * Gradient themes: diagonal gradient, decorative overlay icons, a big Latin-cross
 * watermark and soft light rays.
 */
@Composable
fun ThemeBackground(theme: AppTheme, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize().clipToBounds()) {
        val images = theme.androidResource?.let { Drawables.backgrounds[it] }
        if (images != null) {
            PictureBackground(
                resId = if (isTabletLayout()) images.tablet else images.phone,
                placeholder = theme.gradient,
            )
            Box(Modifier.matchParentSize().background(Color.Black.copy(alpha = 0.15f)))
        } else {
            key(theme.id) { GradientBackground(theme) }
        }
    }
}

/** A drawable crop-filled into the available space, decoded at that size. */
@Composable
fun PictureBackground(resId: Int, placeholder: List<Color>, modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier.fillMaxSize().clipToBounds()) {
        val bitmap = rememberDrawableBitmap(resId, constraints.maxWidth, constraints.maxHeight)
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Box(Modifier.fillMaxSize().background(diagonalGradient(placeholder)))
        }
    }
}

@Composable
private fun GradientBackground(theme: AppTheme) {
    val painters: List<Painter?> = theme.overlayElements.map { overlay ->
        symbolDrawable(overlay.icon)?.let { rememberVectorPainter(ImageVector.vectorResource(it)) }
    }
    val colors = theme.gradient
    Canvas(Modifier.fillMaxSize()) {
        drawRect(diagonalGradient(colors, size))
        drawOverlays(theme, painters)
        drawCrossWatermark()
        drawLightRays()
    }
}

/** SwiftUI LinearGradient from .topLeading to .bottomTrailing. */
fun diagonalGradient(colors: List<Color>, size: Size? = null): Brush =
    if (size == null) {
        Brush.linearGradient(colors)
    } else {
        Brush.linearGradient(colors, start = Offset.Zero, end = Offset(size.width, size.height))
    }

/** Each overlay icon centred at (x·width, y·height), white at its opacity, rotated. */
fun DrawScope.drawOverlays(theme: AppTheme, painters: List<Painter?>, scale: Float = 1f) {
    theme.overlayElements.forEachIndexed { index, overlay ->
        val side = overlay.size.dp.toPx() * scale
        val center = Offset(size.width * overlay.x, size.height * overlay.y)
        val color = Color.White.copy(alpha = overlay.opacity)
        val topLeft = Offset(center.x - side / 2f, center.y - side / 2f)
        rotate(overlay.rotationDeg, pivot = center) {
            val painter = painters.getOrNull(index)
            if (painter == null) {
                drawLatinCrossFit(color, Size(side, side), topLeft)
            } else {
                translate(topLeft.x, topLeft.y) {
                    with(painter) { draw(Size(side, side), colorFilter = ColorFilter.tint(color)) }
                }
            }
        }
    }
}

/** A 200dp Latin cross, white 4%, rotated −15°, in the centre. */
fun DrawScope.drawCrossWatermark(scale: Float = 1f) {
    val h = 200.dp.toPx() * scale
    val w = h * LATIN_CROSS_ASPECT
    rotate(-15f, pivot = center) {
        drawPath(latinCrossPath(Size(w, h), Offset(center.x - w / 2f, center.y - h / 2f)), Color.White.copy(alpha = 0.04f))
    }
}

/** 12 lines from (0.75·w, −50) every 30°, white 3%, 40 wide. */
fun DrawScope.drawLightRays(scale: Float = 1f) {
    val origin = Offset(size.width * 0.75f, -50.dp.toPx() * scale)
    val length = max(size.width, size.height) * 1.5f
    val rays = Path()
    for (deg in 0 until 360 step 30) {
        val angle = Math.toRadians(deg.toDouble())
        rays.moveTo(origin.x, origin.y)
        rays.lineTo(origin.x + (cos(angle) * length).toFloat(), origin.y + (sin(angle) * length).toFloat())
    }
    drawPath(rays, Color.White.copy(alpha = 0.03f), style = Stroke(width = 40.dp.toPx() * scale))
}
