package com.iregina.jesuswords.ui.components

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.sp
import kotlin.math.floor

/**
 * Text that shrinks to fit the space it is given (iOS `minimumScaleFactor`).
 *
 * The font starts at `style.fontSize` and steps down until the text fits the available
 * width and height, but never below [minFontSize]. It also never uses a size at which a
 * single word is wider than the line, so long Tamil words are not split mid-word.
 * Give `style.lineHeight` in `em` so the line spacing shrinks with the font.
 */
@Composable
fun AutoFitText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    minFontSize: TextUnit = 10.sp,
    maxLines: Int = Int.MAX_VALUE,
) {
    BoxWithConstraints(modifier) {
        val measurer = rememberTextMeasurer()
        val maxWidth = constraints.maxWidth
        val start = style.fontSize.takeIf { it.isSpecified } ?: 14.sp
        // Largest size at which the widest word still fits on one line.
        val wordFit = remember(text, style, maxWidth) {
            if (maxWidth == Constraints.Infinity || maxLines == 1) return@remember start
            val widest = text.split(Regex("\\s+"))
                .filter { it.isNotEmpty() }
                .maxOfOrNull { measurer.measure(it, style, maxLines = 1, softWrap = false).size.width }
                ?: 0
            if (widest <= maxWidth) start else (floor(start.value * maxWidth / widest * 2f) / 2f).sp
        }
        val max = if (wordFit.value < minFontSize.value) minFontSize else wordFit
        BasicText(
            text = text,
            style = style,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            autoSize = TextAutoSize.StepBased(minFontSize = minFontSize, maxFontSize = max, stepSize = 0.5.sp),
        )
    }
}
