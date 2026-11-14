package com.iregina.jesuswords.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iregina.jesuswords.R
import com.iregina.jesuswords.ui.theme.textShadow

/** Height the floating tab bar covers above the navigation bar (bar ≈ 62 + bottom gap 20 + air). */
val TabBarReservedHeight = 96.dp

/** Empty space at the end of a page so nothing hides behind the floating tab bar. */
@Composable
fun TabBarSpacer() {
    Column {
        Spacer(Modifier.height(TabBarReservedHeight))
        Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
    }
}

/**
 * White title with the iOS double shadow (black 60% r2 + black 40% r10). With [minFontSize]
 * it stays on [maxLines] lines and shrinks to fit (e.g. the long Tamil title).
 */
@Composable
fun DoubleShadowText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
    minFontSize: TextUnit? = null,
) {
    val autoSize = minFontSize?.let {
        TextAutoSize.StepBased(minFontSize = it, maxFontSize = style.fontSize, stepSize = 0.5.sp)
    }
    Box(modifier, contentAlignment = Alignment.Center) {
        BasicText(text, style = style.copy(shadow = textShadow(0.4f, 10.dp)), maxLines = maxLines, autoSize = autoSize)
        BasicText(text, style = style.copy(shadow = textShadow(0.6f, 2.dp)), maxLines = maxLines, autoSize = autoSize)
    }
}

/** Page header: optional icon, big title, subtitle (ThemeView / HistoryView / SettingsView). */
@Composable
fun PageHeader(
    title: String,
    subtitle: String,
    titleFamily: FontFamily,
    modifier: Modifier = Modifier,
    icon: Int? = null,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxWidth().padding(horizontal = 24.dp),
    ) {
        icon?.let {
            Icon(painterResource(it), contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
        }
        DoubleShadowText(
            text = title,
            maxLines = 1,
            minFontSize = 20.sp,
            style = TextStyle(
                fontFamily = titleFamily, fontWeight = FontWeight.Bold, fontSize = 32.sp,
                color = Color.White, textAlign = TextAlign.Center,
            ),
        )
        BasicText(
            subtitle,
            style = TextStyle(
                fontSize = 15.sp, color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center, shadow = textShadow(0.5f, 2.dp),
            ),
        )
    }
}

/** Green circle with a white tick, shown on the selected theme / wallpaper tile. */
@Composable
fun SelectedBadge(modifier: Modifier = Modifier, size: Dp = 22.dp) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size + 4.dp)
            .softShadow(CircleShape, alpha = 0.3f, radius = 4.dp)
            .background(Color.White, CircleShape)
            .padding(2.dp)
            .background(Color(0xFF34C759), CircleShape),
    ) {
        Icon(painterResource(R.drawable.ic_check), contentDescription = null, tint = Color.White, modifier = Modifier.size(size * 0.7f))
    }
}

/**
 * Lets content stick out by [horizontal] on the end side and [vertical] above and below
 * without making its row taller (SwiftUI negative padding).
 */
fun Modifier.overhang(horizontal: Dp = 0.dp, vertical: Dp = 0.dp): Modifier = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    val h = horizontal.roundToPx()
    val v = vertical.roundToPx()
    layout((placeable.width - h).coerceAtLeast(0), (placeable.height - 2 * v).coerceAtLeast(0)) {
        placeable.place(0, -v)
    }
}
