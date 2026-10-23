package com.iregina.jesuswords.ui.components

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.sp
import kotlin.math.floor

/**
 * Multi-line text that keeps its font size unless a single word is wider than the line,
 * in which case it shrinks just enough (down to [minScale]) for that word to fit. Long
 * Tamil words would otherwise be split in the middle.
 */
@Composable
fun WordFittedText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    minScale: Float = 0.75f,
) {
    BoxWithConstraints(modifier) {
        val measurer = rememberTextMeasurer()
        val maxWidth = constraints.maxWidth
        val scale = remember(text, style, maxWidth) {
            if (maxWidth == Constraints.Infinity) return@remember 1f
            val widest = text.split(Regex("\\s+"))
                .filter { it.isNotEmpty() }
                .maxOfOrNull { word -> measurer.measure(word, style, maxLines = 1, softWrap = false).size.width }
                ?: 0
            if (widest <= maxWidth) 1f else (maxWidth.toFloat() / widest).coerceAtLeast(minScale)
        }
        val fitted = if (scale >= 1f) style else style.copy(
            fontSize = style.fontSize.scaledDown(scale),
            lineHeight = style.lineHeight.scaledDown(scale),
        )
        Text(text, style = fitted)
    }
}

/** Scales a sp value, rounding down to 0.5sp so the result is guaranteed to fit. */
private fun TextUnit.scaledDown(scale: Float): TextUnit =
    if (isSpecified) (floor(value * scale * 2f) / 2f).sp else this
