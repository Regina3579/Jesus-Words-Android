package com.iregina.jesuswords.ui.today

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iregina.jesuswords.R
import com.iregina.jesuswords.data.AppLanguage
import com.iregina.jesuswords.data.AppTheme
import com.iregina.jesuswords.data.JesusWord
import com.iregina.jesuswords.ui.UiState
import com.iregina.jesuswords.ui.components.AutoFitText
import com.iregina.jesuswords.ui.components.CapsuleShape
import com.iregina.jesuswords.ui.components.DoubleShadowText
import com.iregina.jesuswords.ui.components.DropShadow
import com.iregina.jesuswords.ui.components.FavoriteHeartButton
import com.iregina.jesuswords.ui.components.LatinCrossIcon
import com.iregina.jesuswords.ui.components.MaterialCardAlpha
import com.iregina.jesuswords.ui.components.TabBarSpacer
import com.iregina.jesuswords.ui.components.ThemeBackground
import com.iregina.jesuswords.ui.components.contentCard
import com.iregina.jesuswords.ui.components.plainClickable
import com.iregina.jesuswords.ui.components.softShadow
import com.iregina.jesuswords.ui.theme.lineHeightWithSpacing
import com.iregina.jesuswords.ui.theme.textShadow
import com.iregina.jesuswords.ui.theme.themeFontFamily

/**
 * The Today page (HomeView.swift). It always fits on one screen without scrolling:
 * the verse card takes the space between the title and the Share button, and the
 * quote shrinks (never below 11sp) so long Tamil verses fit too.
 */
@Composable
fun TodayScreen(
    state: UiState,
    onLanguageChange: (AppLanguage) -> Unit,
    onToggleFavorite: (JesusWord) -> Unit,
    onShare: (JesusWord) -> Unit,
    modifier: Modifier = Modifier,
    onLanguageTogglePositioned: (LayoutCoordinates) -> Unit = {},
) {
    val language = state.language
    val theme = state.theme
    Box(modifier.fillMaxSize()) {
        ThemeBackground(theme)

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)),
        ) {
            Spacer(Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth().padding(end = 24.dp, top = 16.dp),
            ) {
                LanguageToggle(
                    selected = language,
                    onSelect = onLanguageChange,
                    modifier = Modifier.onGloballyPositioned(onLanguageTogglePositioned),
                )
            }

            Spacer(Modifier.height(12.dp))
            AppTitle(theme, language)
            Spacer(Modifier.height(18.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.weight(1f).fillMaxWidth(),
            ) {
                state.todaysWord?.let { word ->
                    VerseCard(
                        word = word,
                        theme = theme,
                        language = language,
                        isFavorite = state.isFavorite(word),
                        onToggleFavorite = { onToggleFavorite(word) },
                    )
                }
            }

            state.todaysWord?.let { word ->
                Spacer(Modifier.height(18.dp))
                ShareCapsuleButton(
                    label = language.pick("Share This Blessing", "இந்த ஆசீர்வாதத்தைப் பகிரு"),
                    onClick = { onShare(word) },
                )
            }

            TabBarSpacer()
        }
    }
}

@Composable
private fun AppTitle(theme: AppTheme, language: AppLanguage) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.padding(horizontal = 20.dp),
    ) {
        LatinCrossIcon(
            height = 36.dp,
            shadows = listOf(DropShadow(0.55f, 2.dp), DropShadow(0.35f, 8.dp)),
        )
        DoubleShadowText(
            text = language.pick("Jesus Words", "இயேசுவின் வார்த்தைகள்"),
            maxLines = 1,
            minFontSize = 20.sp,
            style = TextStyle(
                fontFamily = themeFontFamily(theme, language),
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
            ),
        )
    }
}

/**
 * The verse card shared by Today and the History detail page: category badge, theme,
 * divider, quote and reference, with the heart in the top corner. It takes at most the
 * height it is given; only the quote (and if needed the theme) shrink to fit.
 */
@Composable
fun VerseCard(
    word: JesusWord,
    theme: AppTheme,
    language: AppLanguage,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier,
    animateIn: Boolean = true,
    sizes: VerseCardSizes = VerseCardSizes.Today,
    footer: String? = null,
) {
    // Fade in and scale up from 0.95 over 0.8 s when the card first appears.
    var appeared by remember { mutableStateOf(!animateIn) }
    LaunchedEffect(Unit) { appeared = true }
    val progress by animateFloatAsState(
        targetValue = if (appeared) 1f else 0f,
        animationSpec = tween(800, easing = EaseOut),
        label = "verseCardAppear",
    )

    val family = themeFontFamily(theme, language)
    val shadow = textShadow(0.6f, 3.dp)
    Box(
        modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .graphicsLayer {
                alpha = progress
                scaleX = 0.95f + 0.05f * progress
                scaleY = 0.95f + 0.05f * progress
            },
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(sizes.spacing),
            modifier = Modifier
                .fillMaxWidth()
                .contentCard(fillAlpha = sizes.fillAlpha, cornerRadius = 24.dp)
                .padding(horizontal = 28.dp, vertical = sizes.verticalPadding),
        ) {
            if (sizes.bigEmoji) {
                Text(word.categoryEmoji, style = TextStyle(fontSize = 44.sp))
                Text(
                    word.categoryName(language),
                    maxLines = 1,
                    style = TextStyle(
                        fontFamily = family, fontWeight = FontWeight.SemiBold, fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.8f), shadow = shadow,
                    ),
                )
            } else {
                CategoryBadge(word, language, family)
            }

            AutoFitText(
                word.theme(language),
                maxLines = 2,
                minFontSize = 13.sp,
                style = TextStyle(
                    fontFamily = family, fontWeight = FontWeight.Bold, fontSize = sizes.themeSize,
                    color = Color.White, textAlign = TextAlign.Center, shadow = shadow,
                ),
            )

            Box(Modifier.width(60.dp).height(2.dp).background(Color.White.copy(alpha = 0.3f)))

            AutoFitText(
                "“${word.quote(language)}”",
                minFontSize = 11.sp,
                style = TextStyle(
                    fontFamily = family, fontWeight = FontWeight.Medium, fontSize = sizes.quoteSize,
                    lineHeight = lineHeightWithSpacing(family, sizes.quoteSize.value, sizes.quoteLineSpacing),
                    color = Color.White, textAlign = TextAlign.Center, shadow = shadow,
                ),
                modifier = Modifier.weight(1f, fill = false).padding(horizontal = 8.dp),
            )

            AutoFitText(
                "— ${word.reference(language)}",
                maxLines = 1,
                minFontSize = 11.sp,
                style = TextStyle(
                    fontFamily = family, fontWeight = FontWeight.SemiBold, fontStyle = FontStyle.Italic,
                    fontSize = sizes.referenceSize, color = Color.White.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center, shadow = shadow,
                ),
            )

            footer?.let {
                Text(
                    it,
                    style = TextStyle(fontSize = 12.sp, color = Color.White.copy(alpha = 0.6f), textAlign = TextAlign.Center),
                )
            }
        }

        FavoriteHeartButton(
            isFavorite = isFavorite,
            onToggle = onToggleFavorite,
            size = 24.dp,
            modifier = Modifier.align(Alignment.TopEnd).padding(10.dp),
        )
    }
}

/** Type sizes of the verse card: Today (HomeView) or the History detail page (VerseDetailView). */
data class VerseCardSizes(
    val themeSize: androidx.compose.ui.unit.TextUnit,
    val quoteSize: androidx.compose.ui.unit.TextUnit,
    val quoteLineSpacing: Float,
    val referenceSize: androidx.compose.ui.unit.TextUnit,
    val spacing: Dp,
    val verticalPadding: Dp,
    val fillAlpha: Float,
    val bigEmoji: Boolean,
) {
    companion object {
        val Today = VerseCardSizes(18.sp, 20.sp, 6f, 16.sp, 16.dp, 28.dp, 0.18f, bigEmoji = false)
        val Detail = VerseCardSizes(24.sp, 22.sp, 8f, 18.sp, 16.dp, 28.dp, MaterialCardAlpha, bigEmoji = true)
    }
}

@Composable
private fun CategoryBadge(word: JesusWord, language: AppLanguage, family: FontFamily) {
    val shadow = textShadow(0.6f, 3.dp)
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(Color.White.copy(alpha = 0.25f), CapsuleShape)
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(word.categoryEmoji, style = TextStyle(fontSize = 14.sp, shadow = shadow))
        Text(
            word.categoryName(language),
            maxLines = 1,
            style = TextStyle(
                fontFamily = family, fontWeight = FontWeight.SemiBold, fontSize = 14.sp,
                color = Color.White, shadow = shadow,
            ),
        )
    }
}

/** "Share This Blessing" / "Share as Image" capsule: image icon + 16 semibold, dark backplate. */
@Composable
fun ShareCapsuleButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val style = TextStyle(
        fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold, fontSize = 16.sp,
        color = Color.White, shadow = textShadow(0.5f, 2.dp),
    )
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(horizontal = 20.dp)
            .softShadow(CapsuleShape, alpha = 0.3f, radius = 8.dp, y = 3.dp)
            .background(Color.Black.copy(alpha = 0.38f), CapsuleShape)
            .border(1.2.dp, Color.White.copy(alpha = 0.55f), CapsuleShape)
            .semantics { role = Role.Button }
            .plainClickable(onClick = onClick)
            .padding(horizontal = 28.dp, vertical = 14.dp),
    ) {
        Icon(painterResource(R.drawable.ic_image), contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
        Text(label, style = style, maxLines = 1)
    }
}

/** The En | த capsule (LanguageToggle in HomeView.swift). */
@Composable
fun LanguageToggle(
    selected: AppLanguage,
    onSelect: (AppLanguage) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .softShadow(CapsuleShape, alpha = 0.35f, radius = 6.dp, y = 2.dp)
            .background(Color.Black.copy(alpha = 0.45f), CapsuleShape)
            .border(1.2.dp, Color.White.copy(alpha = 0.55f), CapsuleShape),
    ) {
        ToggleSegment("En", selected == AppLanguage.ENGLISH) { onSelect(AppLanguage.ENGLISH) }
        ToggleSegment("த", selected == AppLanguage.TAMIL) { onSelect(AppLanguage.TAMIL) }
    }
}

@Composable
private fun ToggleSegment(label: String, isSelected: Boolean, onClick: () -> Unit) {
    val fill by animateColorAsState(
        targetValue = if (isSelected) Color.White.copy(alpha = 0.28f) else Color.Transparent,
        animationSpec = tween(200),
        label = "toggleFill",
    )
    Text(
        text = label,
        style = TextStyle(
            fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White,
            shadow = textShadow(0.5f, 2.dp),
        ),
        modifier = Modifier
            .background(fill, CapsuleShape)
            .semantics {
                role = Role.Tab
                selected = isSelected
            }
            .plainClickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp),
    )
}
