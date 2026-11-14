package com.iregina.jesuswords.ui.wallpapers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.iregina.jesuswords.R
import com.iregina.jesuswords.data.Wallpaper
import com.iregina.jesuswords.ui.UiState
import com.iregina.jesuswords.ui.components.Drawables
import com.iregina.jesuswords.ui.components.PageHeader
import com.iregina.jesuswords.ui.components.PictureBackground
import com.iregina.jesuswords.ui.components.ThemeBackground
import com.iregina.jesuswords.ui.components.softShadow
import com.iregina.jesuswords.ui.theme.serifFontFamily
import com.iregina.jesuswords.ui.themes.CurrentSelectionCard
import com.iregina.jesuswords.ui.themes.SelectionTile
import com.iregina.jesuswords.ui.themes.TileLabel
import com.iregina.jesuswords.ui.themes.TitleAndSubtitle
import com.iregina.jesuswords.ui.themes.gridPadding

/** The Wallpapers page (WallpapersView.swift): the default background for shared verse images. */
@Composable
fun WallpapersScreen(state: UiState, onSelect: (Wallpaper) -> Unit, modifier: Modifier = Modifier) {
    val language = state.language
    val placeholder = state.theme.gradient
    Box(modifier.fillMaxSize()) {
        ThemeBackground(state.theme)

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = gridPadding(),
            modifier = Modifier.fillMaxSize(),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                PageHeader(
                    icon = R.drawable.ic_photo_library,
                    title = language.pick("Share Wallpapers", "பகிர் வால்பேப்பர்கள்"),
                    subtitle = language.pick("Choose the background for shared verses", "பகிரும் பட பின்னணியைத் தேர்வுசெய்யுங்கள்"),
                    titleFamily = serifFontFamily(language),
                    modifier = Modifier.padding(top = 20.dp, bottom = 6.dp),
                )
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                CurrentSelectionCard(title = language.pick("Current Wallpaper", "தற்போதைய வால்பேப்பர்"), language = language) {
                    val thumb = RoundedCornerShape(12.dp)
                    Box(
                        Modifier
                            .size(56.dp)
                            .softShadow(thumb, alpha = 0.2f, radius = 8.dp, y = 4.dp)
                            .clip(thumb),
                    ) {
                        Drawables.backgrounds[state.wallpaper.androidResource]?.let { PictureBackground(it.tablet, placeholder) }
                    }
                    TitleAndSubtitle(state.wallpaper.name(language), state.wallpaper.subtitle(language), language)
                }
            }
            items(state.content.wallpapers, key = { it.id }) { wallpaper ->
                SelectionTile(
                    isSelected = wallpaper.id == state.wallpaper.id,
                    onSelect = { onSelect(wallpaper) },
                    preview = {
                        Box(Modifier.fillMaxWidth().height(160.dp)) {
                            Drawables.backgrounds[wallpaper.androidResource]?.let { PictureBackground(it.tablet, placeholder) }
                        }
                    },
                    label = {
                        TileLabel(
                            title = wallpaper.name(language),
                            subtitle = wallpaper.subtitle(language),
                            titleFamily = serifFontFamily(language),
                            background = Color.Black.copy(alpha = 0.5f),
                            subtitleAlpha = 0.7f,
                        )
                    },
                )
            }
        }
    }
}
