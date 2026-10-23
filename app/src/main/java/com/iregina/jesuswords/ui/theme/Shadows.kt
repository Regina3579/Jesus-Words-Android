package com.iregina.jesuswords.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** SwiftUI-style `.shadow(color: .black.opacity(alpha), radius:, y:)` for text. */
@Composable
@ReadOnlyComposable
fun textShadow(alpha: Float = 0.6f, radius: Dp = 3.dp, y: Dp = 0.dp): Shadow {
    val density = LocalDensity.current
    return with(density) {
        Shadow(
            color = Color.Black.copy(alpha = alpha),
            offset = Offset(0f, y.toPx()),
            blurRadius = radius.toPx(),
        )
    }
}
