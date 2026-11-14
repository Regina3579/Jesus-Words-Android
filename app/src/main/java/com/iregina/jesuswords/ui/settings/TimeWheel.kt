package com.iregina.jesuswords.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.filter
import kotlin.math.abs

private val ItemHeight = 38.dp
private const val VISIBLE_ROWS = 5

/**
 * iOS-style time wheel (DatePicker .wheel): hour, minute and — on 12-hour phones — AM/PM
 * columns that snap to the highlighted middle row.
 */
@Composable
fun TimeWheel(
    hour: Int,
    minute: Int,
    is24Hour: Boolean,
    onChange: (hour: Int, minute: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val current by rememberUpdatedState(hour to minute)
    Box(modifier.fillMaxWidth().height(ItemHeight * VISIBLE_ROWS), contentAlignment = Alignment.Center) {
        // Selection band behind the middle row.
        Box(
            Modifier
                .fillMaxWidth()
                .height(ItemHeight)
                .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(10.dp)),
        )
        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            if (is24Hour) {
                WheelColumn((0..23).map { "%02d".format(it) }, hour, 64.dp) { onChange(it, current.second) }
            } else {
                val h12 = if (hour % 12 == 0) 12 else hour % 12
                WheelColumn((1..12).map { "$it" }, h12 - 1, 64.dp) { index ->
                    val pm = current.first >= 12
                    onChange(((index + 1) % 12) + if (pm) 12 else 0, current.second)
                }
            }
            Text(":", style = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.SemiBold, color = Color.White))
            WheelColumn((0..59).map { "%02d".format(it) }, minute, 64.dp) { onChange(current.first, it) }
            if (!is24Hour) {
                WheelColumn(listOf("AM", "PM"), if (hour >= 12) 1 else 0, 72.dp) { index ->
                    val h = current.first % 12
                    onChange(h + if (index == 1) 12 else 0, current.second)
                }
            }
        }
    }
}

@Composable
private fun WheelColumn(labels: List<String>, selectedIndex: Int, width: Dp, onSelected: (Int) -> Unit) {
    val state = rememberLazyListState(initialFirstVisibleItemIndex = selectedIndex.coerceIn(0, labels.lastIndex))
    val centered by remember { derivedStateOf { state.centeredIndex() } }
    val latestOnSelected by rememberUpdatedState(onSelected)

    LaunchedEffect(state) {
        snapshotFlow { state.isScrollInProgress }
            .filter { !it }
            .collect { latestOnSelected(state.centeredIndex()) }
    }

    LazyColumn(
        state = state,
        flingBehavior = rememberSnapFlingBehavior(state, SnapPosition.Center),
        contentPadding = PaddingValues(vertical = ItemHeight * (VISIBLE_ROWS / 2)),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(width).height(ItemHeight * VISIBLE_ROWS),
    ) {
        items(labels.size) { index ->
            Box(Modifier.height(ItemHeight).padding(horizontal = 6.dp), contentAlignment = Alignment.Center) {
                Text(
                    labels[index],
                    style = TextStyle(
                        fontSize = 22.sp,
                        fontWeight = if (index == centered) FontWeight.SemiBold else FontWeight.Normal,
                        color = Color.White.copy(alpha = if (index == centered) 1f else 0.45f),
                    ),
                )
            }
        }
    }
}

/** Index of the item whose centre is nearest the middle of the wheel. */
private fun LazyListState.centeredIndex(): Int {
    val info = layoutInfo
    val middle = (info.viewportStartOffset + info.viewportEndOffset) / 2
    return info.visibleItemsInfo.minByOrNull { abs(it.offset + it.size / 2 - middle) }?.index ?: firstVisibleItemIndex
}
