package com.iregina.jesuswords.ui.main

import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iregina.jesuswords.R
import com.iregina.jesuswords.data.AppLanguage
import com.iregina.jesuswords.ui.MainActions
import com.iregina.jesuswords.ui.UiState
import com.iregina.jesuswords.ui.components.ThemeBackground
import com.iregina.jesuswords.ui.components.plainClickable
import com.iregina.jesuswords.ui.components.softShadow
import com.iregina.jesuswords.ui.theme.textShadow
import com.iregina.jesuswords.ui.theme.themeFontFamily
import com.iregina.jesuswords.ui.today.DoubleShadowText
import com.iregina.jesuswords.ui.today.TodayScreen
import kotlinx.coroutines.launch

/** The five swipeable pages, in tab order. */
enum class MainTab(@DrawableRes val icon: Int, val english: String, val tamil: String) {
    TODAY(R.drawable.ic_home, "Today", "இன்று"),
    HISTORY(R.drawable.ic_schedule, "History", "வரலாறு"),
    THEMES(R.drawable.ic_palette, "Themes", "தீம்கள்"),
    WALLPAPERS(R.drawable.ic_photo_library, "Wallpapers", "வால்பேப்பர்"),
    SETTINGS(R.drawable.ic_settings, "Settings", "அமைப்புகள்");

    fun label(language: AppLanguage) = language.pick(english, tamil)
}

/** The main screen (ContentView.swift): a HorizontalPager with a floating custom tab bar. */
@Composable
fun MainScreen(state: UiState, actions: MainActions) {
    val tabs = MainTab.entries
    val pagerState = rememberPagerState { tabs.size }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Box(Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            beyondViewportPageCount = 1,
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            when (tabs[page]) {
                MainTab.TODAY -> TodayScreen(
                    state = state,
                    onLanguageChange = actions::setLanguage,
                    onToggleFavorite = actions::toggleFavorite,
                    onShare = {
                        // The Share flow arrives in milestone 6.
                        Toast.makeText(
                            context,
                            state.language.pick("Sharing is coming soon", "பகிர்வு விரைவில் வரும்"),
                            Toast.LENGTH_SHORT,
                        ).show()
                    },
                )
                else -> ComingSoonPage(state, tabs[page])
            }
        }

        FloatingTabBar(
            selected = tabs[pagerState.currentPage],
            language = state.language,
            onSelect = { tab -> scope.launch { pagerState.animateScrollToPage(tab.ordinal) } },
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

/**
 * Rounded (25) translucent bar: inner padding h10 v12, outer padding h16 and bottom 20
 * above the navigation bar. Selected item white, others white 50%.
 */
@Composable
fun FloatingTabBar(
    selected: MainTab,
    language: AppLanguage,
    onSelect: (MainTab) -> Unit,
    modifier: Modifier = Modifier,
    onTabPositioned: (MainTab, LayoutCoordinates) -> Unit = { _, _ -> },
) {
    val shape = RoundedCornerShape(25.dp)
    Row(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(start = 16.dp, end = 16.dp, bottom = 20.dp)
            .fillMaxWidth()
            .softShadow(shape, alpha = 0.2f, radius = 10.dp, y = 5.dp)
            // iOS uses .ultraThinMaterial; a dark translucent plate keeps the white
            // labels readable on every theme without a backdrop blur.
            .background(Color.Black.copy(alpha = 0.40f), shape)
            .border(0.5.dp, Color.White.copy(alpha = 0.18f), shape)
            .padding(horizontal = 10.dp, vertical = 12.dp),
    ) {
        MainTab.entries.forEach { tab ->
            TabItem(
                tab = tab,
                label = tab.label(language),
                isSelected = tab == selected,
                onClick = { onSelect(tab) },
                modifier = Modifier
                    .weight(1f)
                    .onGloballyPositioned { onTabPositioned(tab, it) },
            )
        }
    }
}

@Composable
private fun TabItem(tab: MainTab, label: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier) {
    val tint by animateColorAsState(
        targetValue = if (isSelected) Color.White else Color.White.copy(alpha = 0.5f),
        animationSpec = tween(200),
        label = "tabTint",
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .semantics {
                role = Role.Tab
                this.selected = isSelected
            }
            .plainClickable(onClick = onClick),
    ) {
        Icon(painterResource(tab.icon), contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
        BasicText(
            text = label,
            maxLines = 1,
            color = { tint },
            style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center),
            // Single line that may shrink to 70% (iOS minimumScaleFactor 0.7).
            autoSize = TextAutoSize.StepBased(minFontSize = 7.sp, maxFontSize = 10.sp, stepSize = 0.25.sp),
        )
    }
}

/** Pages that later milestones fill in; they already draw the theme background. */
@Composable
private fun ComingSoonPage(state: UiState, tab: MainTab) {
    Box(Modifier.fillMaxSize()) {
        ThemeBackground(state.theme)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.align(Alignment.Center).padding(horizontal = 24.dp),
        ) {
            DoubleShadowText(
                text = tab.label(state.language),
                style = TextStyle(
                    fontFamily = themeFontFamily(state.theme, state.language),
                    fontWeight = FontWeight.Bold, fontSize = 32.sp, color = Color.White,
                ),
            )
            Text(
                state.language.pick("Coming in the next update", "அடுத்த புதுப்பிப்பில் வரும்"),
                style = TextStyle(fontSize = 15.sp, color = Color.White.copy(alpha = 0.85f), shadow = textShadow()),
            )
        }
    }
}
