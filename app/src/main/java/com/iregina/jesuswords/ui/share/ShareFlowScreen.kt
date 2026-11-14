package com.iregina.jesuswords.ui.share

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iregina.jesuswords.R
import com.iregina.jesuswords.data.JesusWord
import com.iregina.jesuswords.data.Wallpaper
import com.iregina.jesuswords.ui.UiState
import com.iregina.jesuswords.ui.components.CapsuleShape
import com.iregina.jesuswords.ui.components.Drawables
import com.iregina.jesuswords.ui.components.PictureBackground
import com.iregina.jesuswords.ui.components.SelectedBadge
import com.iregina.jesuswords.ui.components.blockTouchesBelow
import com.iregina.jesuswords.ui.components.plainClickable
import com.iregina.jesuswords.ui.components.rememberDrawableBitmap
import com.iregina.jesuswords.ui.components.softShadow
import com.iregina.jesuswords.ui.theme.serifFontFamily
import kotlinx.coroutines.launch

/**
 * The Share flow (ShareFlowView.swift): live preview of the real 1080×1080 image, a
 * wallpaper strip and the Share button. The preview recomposes from the selected
 * wallpaper on every tap, so it never lags behind.
 */
@Composable
fun ShareFlowScreen(
    word: JesusWord,
    state: UiState,
    onSelectWallpaper: (Wallpaper) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val language = state.language
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var wallpaperId by remember { mutableStateOf(state.wallpaper.id) }
    val wallpaper = state.content.wallpaper(wallpaperId)
    var rendering by remember { mutableStateOf(false) }

    // Full-resolution picture for this wallpaper (the tablet crop is 1086 px wide).
    val images = Drawables.backgrounds[wallpaper.androidResource]
    val background = images?.let { rememberDrawableBitmap(it.tablet, SHARE_CANVAS, SHARE_CANVAS) }
    val exportLayer = rememberGraphicsLayer()

    Box(modifier.fillMaxSize().background(Color.Black).blockTouchesBelow()) {
        // The exact 1080×1080 image, laid out off-screen at 1 px per unit and recorded
        // into a graphics layer; it is never drawn on screen.
        OffscreenShareImage(word, state, background, exportLayer)

        Column(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
            TopBar(language.pick("Share Blessing", "ஆசீர்வாதத்தைப் பகிரு"), serifFontFamily(language), onClose, language.pick("Close", "மூடு"))

            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(top = 8.dp, bottom = 20.dp),
            ) {
                LivePreview(word, state, background, waiting = images != null && background == null)

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        language.pick("Choose Wallpaper", "வால்பேப்பர் தேர்வு"),
                        style = TextStyle(fontFamily = serifFontFamily(language), fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color.White.copy(alpha = 0.9f)),
                        modifier = Modifier.padding(horizontal = 20.dp),
                    )
                    WallpaperStrip(state, wallpaperId) { chosen ->
                        wallpaperId = chosen.id
                        onSelectWallpaper(chosen) // also becomes the default
                    }
                }

                ShareButton(
                    label = language.pick("Share", "பகிரு"),
                    rendering = rendering,
                    enabled = background != null || images == null,
                ) {
                    if (rendering) return@ShareButton
                    rendering = true
                    scope.launch {
                        try {
                            val bitmap = exportLayer.toImageBitmap().asAndroidBitmap()
                            ShareSender.share(context, bitmap, word.id, language.pick("Share Blessing", "ஆசீர்வாதத்தைப் பகிரு"))
                        } finally {
                            rendering = false
                        }
                    }
                }
            }
        }
    }
}

/** Lays the share image out at exactly 1080×1080 px and records it, without drawing it. */
@Composable
private fun OffscreenShareImage(word: JesusWord, state: UiState, background: ImageBitmap?, layer: GraphicsLayer) {
    Box(
        Modifier.layout { measurable, _ ->
            val placeable = measurable.measure(Constraints())
            layout(0, 0) { placeable.place(0, 0) }
        },
    ) {
        CompositionLocalProvider(LocalDensity provides Density(1f, 1f)) {
            ShareImage(
                word = word,
                theme = state.theme,
                language = state.language,
                wallpaper = background,
                modifier = Modifier.drawWithContent { layer.record { this@drawWithContent.drawContent() } },
            )
        }
    }
}

/** Square preview, min(width − 40, 480), corner 24: the same layout scaled down. */
@Composable
private fun LivePreview(word: JesusWord, state: UiState, background: ImageBitmap?, waiting: Boolean) {
    BoxWithConstraints(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        val side = minOf(maxWidth - 40.dp, 480.dp)
        val shape = RoundedCornerShape(24.dp)
        BoxWithConstraints(
            Modifier
                .size(side)
                .softShadow(shape, alpha = 0.4f, radius = 20.dp, y = 8.dp)
                .clip(shape),
        ) {
            val scale = constraints.maxWidth / SHARE_CANVAS.toFloat()
            CompositionLocalProvider(LocalDensity provides Density(scale, 1f)) {
                Box(Modifier.layout { measurable, _ ->
                    val placeable = measurable.measure(Constraints())
                    layout(placeable.width, placeable.height) { placeable.place(0, 0) }
                }) {
                    ShareImage(word, state.theme, state.language, background, waitingForWallpaper = waiting)
                }
            }
        }
    }
}

@Composable
private fun TopBar(title: String, family: androidx.compose.ui.text.font.FontFamily, onClose: () -> Unit, closeLabel: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 16.dp),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(36.dp)
                .background(Color.White.copy(alpha = 0.15f), CircleShape)
                .semantics {
                    role = Role.Button
                    contentDescription = closeLabel
                }
                .plainClickable(onClick = onClose),
        ) {
            Icon(painterResource(R.drawable.ic_close), contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
        }
        Text(
            title,
            textAlign = TextAlign.Center,
            maxLines = 1,
            style = TextStyle(fontFamily = family, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.White),
            modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
        )
        Spacer(Modifier.size(36.dp))
    }
}

/** 72×96 thumbnails with names; each one only reacts to taps inside its own bounds. */
@Composable
private fun WallpaperStrip(state: UiState, selectedId: String, onSelect: (Wallpaper) -> Unit) {
    val language = state.language
    val initial = state.content.wallpapers.indexOfFirst { it.id == selectedId }.coerceAtLeast(0)
    LazyRow(
        state = rememberLazyListState(initialFirstVisibleItemIndex = initial),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 20.dp),
    ) {
        items(state.content.wallpapers, key = { it.id }) { wallpaper ->
            val isSelected = wallpaper.id == selectedId
            val thumbShape = RoundedCornerShape(10.dp)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .width(72.dp)
                    .semantics {
                        role = Role.RadioButton
                        selected = isSelected
                    }
                    .plainClickable { onSelect(wallpaper) },
            ) {
                Box(
                    Modifier
                        .size(width = 72.dp, height = 96.dp)
                        .clip(thumbShape)
                        .border(if (isSelected) 2.5.dp else 1.dp, Color.White.copy(alpha = if (isSelected) 1f else 0.2f), thumbShape),
                ) {
                    Drawables.backgrounds[wallpaper.androidResource]?.let { PictureBackground(it.tablet, state.theme.gradient) }
                    if (isSelected) SelectedBadge(Modifier.align(Alignment.TopEnd).padding(4.dp), size = 16.dp)
                }
                Text(
                    wallpaper.name(language),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = Color.White.copy(alpha = if (isSelected) 1f else 0.6f),
                    ),
                )
            }
        }
    }
}

@Composable
private fun ShareButton(label: String, rendering: Boolean, enabled: Boolean, onClick: () -> Unit) {
    val stroke by animateColorAsState(Color.White.copy(alpha = if (enabled) 0.4f else 0.2f), label = "shareStroke")
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .padding(top = 8.dp)
            .fillMaxWidth()
            .height(56.dp)
            .background(Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.25f), Color.White.copy(alpha = 0.15f))), CapsuleShape)
            .border(1.dp, stroke, CapsuleShape)
            .semantics { role = Role.Button }
            .plainClickable(enabled = enabled && !rendering, onClick = onClick),
    ) {
        if (rendering || !enabled) {
            CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(22.dp))
        } else {
            Icon(painterResource(R.drawable.ic_ios_share), contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            Text(label, style = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = Color.White))
        }
    }
}
