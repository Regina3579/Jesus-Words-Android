package com.iregina.jesuswords.ui.themes

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iregina.jesuswords.R
import com.iregina.jesuswords.data.AppLanguage
import com.iregina.jesuswords.data.AppTheme
import com.iregina.jesuswords.ui.UiState
import com.iregina.jesuswords.ui.components.Drawables
import com.iregina.jesuswords.ui.components.LatinCrossIcon
import com.iregina.jesuswords.ui.components.PageHeader
import com.iregina.jesuswords.ui.components.PictureBackground
import com.iregina.jesuswords.ui.components.TabBarReservedHeight
import com.iregina.jesuswords.ui.components.ThemeBackground
import com.iregina.jesuswords.ui.components.contentCard
import com.iregina.jesuswords.ui.components.diagonalGradient
import com.iregina.jesuswords.ui.components.drawOverlays
import com.iregina.jesuswords.ui.components.rememberOverlayPainters
import com.iregina.jesuswords.ui.components.softShadow
import com.iregina.jesuswords.ui.components.symbolDrawable
import com.iregina.jesuswords.ui.theme.serifFontFamily
import com.iregina.jesuswords.ui.theme.themeFontFamily
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

/** The Themes page (ThemeView.swift): header, current theme, then a 2-column grid. */
@Composable
fun ThemesScreen(state: UiState, onSelect: (AppTheme) -> Unit, modifier: Modifier = Modifier) {
    val language = state.language
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
                    icon = R.drawable.ic_palette,
                    title = language.pick("Themes", "தீம்கள்"),
                    subtitle = language.pick("Choose your visual experience", "உங்கள் காட்சி அனுபவத்தைத் தேர்வுசெய்யுங்கள்"),
                    titleFamily = serifFontFamily(language),
                    modifier = Modifier.padding(top = 20.dp, bottom = 6.dp),
                )
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                CurrentSelectionCard(title = language.pick("Current Theme", "தற்போதைய தீம்"), language = language) {
                    ThemeCircle(state.theme)
                    TitleAndSubtitle(state.theme.name, state.theme.subtitle, language)
                }
            }
            items(state.content.themesForGrid, key = { it.id }) { theme ->
                SelectionTile(
                    isSelected = theme.id == state.theme.id,
                    onSelect = { onSelect(theme) },
                    preview = { ThemePreview(theme) },
                    label = {
                        TileLabel(
                            title = theme.name,
                            subtitle = theme.subtitle,
                            titleFamily = themeFontFamily(theme, AppLanguage.ENGLISH),
                            background = Color.White.copy(alpha = 0.08f),
                            subtitleAlpha = 0.6f,
                        )
                    },
                )
            }
        }
    }
}

/** Grid padding: 16 at the sides, clear of the status bar on top and the tab bar below. */
@Composable
fun gridPadding(): PaddingValues {
    val safe = WindowInsets.safeDrawing.asPaddingValues()
    val nav = WindowInsets.navigationBars.asPaddingValues()
    val dir = LocalLayoutDirection.current
    return PaddingValues(
        start = 16.dp + safe.calculateStartPadding(dir),
        end = 16.dp + safe.calculateEndPadding(dir),
        top = safe.calculateTopPadding(),
        bottom = TabBarReservedHeight + nav.calculateBottomPadding() + 24.dp,
    )
}

/** "Current Theme" / "Current Wallpaper" card: tick-seal title row, then the current item. */
@Composable
fun CurrentSelectionCard(title: String, language: AppLanguage, content: @Composable () -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth().contentCard().padding(18.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(painterResource(R.drawable.ic_verified), contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            Text(
                title,
                style = TextStyle(fontFamily = serifFontFamily(language), fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White),
                modifier = Modifier.padding(start = 8.dp),
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            content()
        }
    }
}

@Composable
fun TitleAndSubtitle(title: String, subtitle: String, language: AppLanguage) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(title, style = TextStyle(fontFamily = serifFontFamily(language), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White))
        Text(subtitle, maxLines = 2, style = TextStyle(fontSize = 14.sp, color = Color.White.copy(alpha = 0.7f)))
    }
}

/** 56dp circle: the theme picture, or its gradient with the decorative icon. */
@Composable
private fun ThemeCircle(theme: AppTheme) {
    val images = theme.androidResource?.let { Drawables.backgrounds[it] }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(56.dp)
            .softShadow(CircleShape, alpha = 0.2f, radius = 8.dp, y = 4.dp)
            .clip(CircleShape)
            .background(diagonalGradient(theme.gradient)),
    ) {
        if (images != null) {
            PictureBackground(images.tablet, theme.gradient)
        } else {
            DecorativeIcon(theme, size = 28)
        }
    }
}

@Composable
private fun DecorativeIcon(theme: AppTheme, size: Int, alpha: Float = 1f) {
    Icon(
        painterResource(symbolDrawable(theme.decorativeIcon) ?: R.drawable.ic_auto_awesome),
        contentDescription = null,
        tint = Color.White.copy(alpha = alpha),
        modifier = Modifier.size(size.dp),
    )
}

/** 120dp preview: picture (15% darker) or gradient + overlays + rays; icon and small cross centred. */
@Composable
private fun ThemePreview(theme: AppTheme) {
    val images = theme.androidResource?.let { Drawables.backgrounds[it] }
    Box(Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
        if (images != null) {
            PictureBackground(images.tablet, theme.gradient)
            Box(Modifier.matchParentSize().background(Color.Black.copy(alpha = 0.15f)))
        } else {
            val painters = rememberOverlayPainters(theme)
            Canvas(Modifier.matchParentSize()) {
                drawRect(diagonalGradient(theme.gradient, size))
                drawOverlays(theme, painters, scale = 0.5f, opacityScale = 2f)
                drawTileRays()
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
            DecorativeIcon(theme, size = 28, alpha = 0.9f)
            LatinCrossIcon(height = 14.dp, color = Color.White.copy(alpha = 0.6f))
        }
    }
}

/** Tile light rays: from (0.75·w, −10) every 45°, white 5%, 15 wide. */
private fun DrawScope.drawTileRays() {
    val origin = Offset(size.width * 0.75f, -10.dp.toPx())
    val length = max(size.width, size.height)
    val rays = Path()
    for (deg in 0 until 360 step 45) {
        val a = Math.toRadians(deg.toDouble())
        rays.moveTo(origin.x, origin.y)
        rays.lineTo(origin.x + (cos(a) * length).toFloat(), origin.y + (sin(a) * length).toFloat())
    }
    drawPath(rays, Color.White.copy(alpha = 0.05f), style = Stroke(width = 15.dp.toPx()))
}

/** Label strip under a tile: name 13 bold, subtitle 10, both one line. */
@Composable
fun TileLabel(
    title: String,
    subtitle: String,
    titleFamily: androidx.compose.ui.text.font.FontFamily,
    background: Color,
    subtitleAlpha: Float,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp),
        modifier = Modifier.fillMaxWidth().background(background).padding(vertical = 10.dp, horizontal = 6.dp),
    ) {
        Text(
            title, maxLines = 1, overflow = TextOverflow.Ellipsis,
            style = TextStyle(fontFamily = titleFamily, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White),
        )
        Text(
            subtitle, maxLines = 1, overflow = TextOverflow.Ellipsis,
            style = TextStyle(fontSize = 10.sp, color = Color.White.copy(alpha = subtitleAlpha)),
        )
    }
}
