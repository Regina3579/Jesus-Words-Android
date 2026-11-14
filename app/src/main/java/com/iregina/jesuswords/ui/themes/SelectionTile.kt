package com.iregina.jesuswords.ui.themes

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.iregina.jesuswords.ui.components.SelectedBadge
import com.iregina.jesuswords.ui.components.softShadow
import kotlinx.coroutines.launch

private val TileShape = RoundedCornerShape(16.dp)

/**
 * A theme or wallpaper tile: preview on top, label strip below, white border + green tick
 * when selected, and a springy "press" to 0.92 on tap. The tap area is clipped to the
 * tile's own rounded shape, so a cropped picture can never take taps from its neighbours.
 */
@Composable
fun SelectionTile(
    isSelected: Boolean,
    onSelect: () -> Unit,
    preview: @Composable () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val press = remember { Animatable(1f) }
    val spring = spring<Float>(dampingRatio = 0.7f, stiffness = 250f)
    Box(modifier.graphicsLayer { scaleX = press.value; scaleY = press.value }) {
        Column(
            Modifier
                .softShadow(
                    TileShape,
                    alpha = if (isSelected) 0.2f else 0.15f,
                    radius = if (isSelected) 12.dp else 6.dp,
                    y = if (isSelected) 2.dp else 4.dp,
                    shadowColor = if (isSelected) Color.White else Color.Black,
                )
                .clip(TileShape)
                .border(if (isSelected) 2.5.dp else 1.dp, Color.White.copy(alpha = if (isSelected) 1f else 0.15f), TileShape)
                .semantics { selected = isSelected }
                .clickable(role = Role.RadioButton) {
                    onSelect()
                    scope.launch {
                        press.animateTo(0.92f, spring)
                        press.animateTo(1f, spring)
                    }
                },
        ) {
            preview()
            label()
        }
        if (isSelected) SelectedBadge(Modifier.align(Alignment.TopEnd).padding(8.dp))
    }
}
