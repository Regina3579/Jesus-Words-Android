package com.iregina.jesuswords.ui.main

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.iregina.jesuswords.data.JesusWord
import com.iregina.jesuswords.ui.history.HistoryScreen
import com.iregina.jesuswords.ui.history.VerseDetail
import com.iregina.jesuswords.ui.history.VerseDetailScreen
import com.iregina.jesuswords.ui.settings.SettingsScreen
import com.iregina.jesuswords.ui.themes.ThemesScreen
import com.iregina.jesuswords.ui.wallpapers.WallpapersScreen
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
import com.iregina.jesuswords.ui.components.plainClickable
import com.iregina.jesuswords.ui.components.softShadow
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
fun MainScreen(state: UiState, actions: MainActions, initialTab: MainTab = MainTab.TODAY) {
    val tabs = MainTab.entries
    val pagerState = rememberPagerState(initialPage = initialTab.ordinal) { tabs.size }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var detail by remember { mutableStateOf<VerseDetail?>(null) }

    // The Share flow arrives in milestone 6.
    val share: (JesusWord) -> Unit = {
        Toast.makeText(
            context,
            state.language.pick("Sharing is coming soon", "பகிர்வு விரைவில் வரும்"),
            Toast.LENGTH_SHORT,
        ).show()
    }
    fun goTo(tab: MainTab) {
        scope.launch { pagerState.animateScrollToPage(tab.ordinal) }
    }

    // Back closes the verse page first, then returns to Today, then leaves the app.
    BackHandler(enabled = detail != null || pagerState.currentPage != MainTab.TODAY.ordinal) {
        if (detail != null) detail = null else goTo(MainTab.TODAY)
    }

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
                    onShare = share,
                )
                MainTab.HISTORY -> HistoryScreen(
                    state = state,
                    onToggleFavorite = actions::toggleFavorite,
                    onOpen = { detail = it },
                )
                MainTab.THEMES -> ThemesScreen(state, onSelect = actions::selectTheme)
                MainTab.WALLPAPERS -> WallpapersScreen(state, onSelect = actions::selectWallpaper)
                MainTab.SETTINGS -> SettingsScreen(
                    state = state,
                    onSaveReminder = actions::setReminder,
                    onShowTipsAgain = {
                        actions.resetTips()
                        goTo(MainTab.TODAY)
                    },
                )
            }
        }

        FloatingTabBar(
            selected = tabs[pagerState.currentPage],
            language = state.language,
            onSelect = ::goTo,
            modifier = Modifier.align(Alignment.BottomCenter),
        )

        // Verse page from History / Favourites, sliding up over everything (iOS .sheet).
        // Keeps the last verse on screen while the page slides away.
        val lastDetail = remember { arrayOfNulls<VerseDetail>(1) }
        if (detail != null) lastDetail[0] = detail
        AnimatedVisibility(
            visible = detail != null,
            enter = slideInVertically(tween(300)) { it } + fadeIn(tween(300)),
            exit = slideOutVertically(tween(250)) { it } + fadeOut(tween(250)),
        ) {
            lastDetail[0]?.let {
                VerseDetailScreen(
                    detail = it,
                    state = state,
                    onClose = { detail = null },
                    onToggleFavorite = actions::toggleFavorite,
                    onShare = share,
                )
            }
        }
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
