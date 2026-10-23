package com.iregina.jesuswords.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A soft drop shadow behind [shape], like SwiftUI `.shadow(color: .black.opacity(alpha),
 * radius:, y:)`. Uses Paint.setShadowLayer, which is drawn on Android 9+.
 */
fun Modifier.softShadow(shape: Shape, alpha: Float, radius: Dp, y: Dp = 0.dp): Modifier = drawBehind {
    val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.TRANSPARENT
        setShadowLayer(
            radius.toPx().coerceAtLeast(0.5f),
            0f,
            y.toPx(),
            android.graphics.Color.argb((alpha * 255).toInt().coerceIn(0, 254), 0, 0, 0),
        )
    }
    val canvas = drawContext.canvas.nativeCanvas
    when (val outline = shape.createOutline(size, layoutDirection, this)) {
        is Outline.Rectangle -> canvas.drawRect(0f, 0f, size.width, size.height, paint)
        is Outline.Rounded -> {
            val r = outline.roundRect
            canvas.drawRoundRect(r.left, r.top, r.right, r.bottom, r.topLeftCornerRadius.x, r.topLeftCornerRadius.y, paint)
        }
        is Outline.Generic -> canvas.drawPath(outline.path.asAndroidPath(), paint)
    }
}

/**
 * The shared content card: translucent black fill, 1dp white 28% stroke, rounded corners.
 * SwiftUI draws the card's shadow (black 25%, r20, y+10) from the translucent fill, so the
 * visible shadow is proportionally faint; this mirrors that.
 */
fun Modifier.contentCard(
    fillAlpha: Float = 0.22f,
    cornerRadius: Dp = 20.dp,
    strokeAlpha: Float = 0.28f,
): Modifier {
    val shape = RoundedCornerShape(cornerRadius)
    return this
        .softShadow(shape, alpha = 0.25f * (fillAlpha + 0.1f), radius = 20.dp, y = 10.dp)
        .background(Color.Black.copy(alpha = fillAlpha), shape)
        .border(1.dp, Color.White.copy(alpha = strokeAlpha), shape)
}

/** Capsule (fully rounded) shape, as in SwiftUI `Capsule()`. */
val CapsuleShape = RoundedCornerShape(CornerSize(50))

/** A tap target without the Material ripple (iOS buttons have none). */
fun Modifier.plainClickable(enabled: Boolean = true, onClick: () -> Unit): Modifier = composed {
    clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        enabled = enabled,
        onClick = onClick,
    )
}
