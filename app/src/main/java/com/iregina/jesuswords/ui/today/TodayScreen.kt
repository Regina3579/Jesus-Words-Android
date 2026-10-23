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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iregina.jesuswords.R
import com.iregina.jesuswords.data.AppLanguage
import com.iregina.jesuswords.data.AppTheme
import com.iregina.jesuswords.data.JesusWord
import com.iregina.jesuswords.ui.UiState
import com.iregina.jesuswords.ui.components.CapsuleShape
import com.iregina.jesuswords.ui.components.DropShadow
import com.iregina.jesuswords.ui.components.FavoriteHeartButton
import com.iregina.jesuswords.ui.components.LatinCrossIcon
import com.iregina.jesuswords.ui.components.ThemeBackground
import com.iregina.jesuswords.ui.components.WordFittedText
import com.iregina.jesuswords.ui.components.contentCard
import com.iregina.jesuswords.ui.components.plainClickable
import com.iregina.jesuswords.ui.components.softShadow
import com.iregina.jesuswords.ui.theme.lineHeightWithSpacing
import com.iregina.jesuswords.ui.theme.textShadow
import com.iregina.jesuswords.ui.theme.themeFontFamily

/** The Today page (HomeView.swift). */
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
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
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

            AppTitle(theme, language)

            state.todaysWord?.let { word ->
                VerseCard(
                    word = word,
                    theme = theme,
                    language = language,
                    isFavorite = state.isFavorite(word),
                    onToggleFavorite = { onToggleFavorite(word) },
                )
                ShareBlessingButton(language) { onShare(word) }
            }

            Spacer(Modifier.height(100.dp))
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
 * White title text with the double shadow used on iOS (black 60% r2 + black 40% r10),
 * readable on both bright and dark backgrounds.
 */
@Composable
fun DoubleShadowText(text: String, style: TextStyle, modifier: Modifier = Modifier) {
    Box(modifier) {
        Text(text, style = style.copy(shadow = textShadow(0.4f, 10.dp)))
        Text(text, style = style.copy(shadow = textShadow(0.6f, 2.dp)))
    }
}

@Composable
private fun VerseCard(
    word: JesusWord,
    theme: AppTheme,
    language: AppLanguage,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
) {
    // Fade in and scale up from 0.95 over 0.8 s when Today first appears.
    var appeared by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { appeared = true }
    val progress by animateFloatAsState(
        targetValue = if (appeared) 1f else 0f,
        animationSpec = tween(800, easing = EaseOut),
        label = "verseCardAppear",
    )

    val family = themeFontFamily(theme, language)
    val shadow = textShadow(0.6f, 3.dp)
    Box(
        Modifier
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .contentCard(fillAlpha = 0.18f, cornerRadius = 24.dp)
                .padding(28.dp),
        ) {
            // Category badge
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
                    style = TextStyle(
                        fontFamily = family, fontWeight = FontWeight.SemiBold, fontSize = 14.sp,
                        color = Color.White, shadow = shadow,
                    ),
                )
            }

            Text(
                word.theme(language),
                style = TextStyle(
                    fontFamily = family, fontWeight = FontWeight.Bold, fontSize = 18.sp,
                    color = Color.White, textAlign = TextAlign.Center, shadow = shadow,
                ),
            )

            Box(Modifier.width(60.dp).height(2.dp).background(Color.White.copy(alpha = 0.3f)))

            WordFittedText(
                "“${word.quote(language)}”",
                style = TextStyle(
                    fontFamily = family, fontWeight = FontWeight.Medium, fontSize = 20.sp,
                    lineHeight = lineHeightWithSpacing(family, 20f, 6f),
                    color = Color.White, textAlign = TextAlign.Center, shadow = shadow,
                ),
                modifier = Modifier.padding(horizontal = 8.dp),
            )

            Text(
                "— ${word.reference(language)}",
                style = TextStyle(
                    fontFamily = family, fontWeight = FontWeight.SemiBold, fontStyle = FontStyle.Italic,
                    fontSize = 16.sp, color = Color.White.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center, shadow = shadow,
                ),
            )
        }

        FavoriteHeartButton(
            isFavorite = isFavorite,
            onToggle = onToggleFavorite,
            size = 24.dp,
            modifier = Modifier.align(Alignment.TopEnd).padding(10.dp),
        )
    }
}

@Composable
private fun ShareBlessingButton(language: AppLanguage, onClick: () -> Unit) {
    val style = TextStyle(
        fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold, fontSize = 16.sp,
        color = Color.White, shadow = textShadow(0.5f, 2.dp),
    )
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .softShadow(CapsuleShape, alpha = 0.3f, radius = 8.dp, y = 3.dp)
            .background(Color.Black.copy(alpha = 0.38f), CapsuleShape)
            .border(1.2.dp, Color.White.copy(alpha = 0.55f), CapsuleShape)
            .semantics { role = Role.Button }
            .plainClickable(onClick = onClick)
            .padding(horizontal = 28.dp, vertical = 14.dp),
    ) {
        Icon(painterResource(R.drawable.ic_image), contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
        Text(language.pick("Share This Blessing", "இந்த ஆசீர்வாதத்தைப் பகிரு"), style = style)
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
