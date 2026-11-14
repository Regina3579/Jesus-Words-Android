package com.iregina.jesuswords.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import com.iregina.jesuswords.data.AppLanguage
import com.iregina.jesuswords.data.AppSettings
import com.iregina.jesuswords.data.AppTheme
import com.iregina.jesuswords.data.ContentRepository
import com.iregina.jesuswords.data.HistoryEntry
import com.iregina.jesuswords.data.JesusWord
import com.iregina.jesuswords.data.Wallpaper
import com.iregina.jesuswords.ui.components.Drawables
import com.iregina.jesuswords.ui.components.ImageCache
import com.iregina.jesuswords.ui.history.VerseDetail
import com.iregina.jesuswords.ui.history.VerseDetailScreen
import com.iregina.jesuswords.ui.main.MainScreen
import com.iregina.jesuswords.ui.main.MainTab
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.time.LocalDate
import java.time.ZoneId

/**
 * Renders real screens on the JVM (Robolectric native graphics) and saves PNGs to
 * app/build/screenshots, so every CI build shows what the app looks like.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h852dp-xxhdpi")
class ScreenshotTest {
    @get:Rule
    val compose = createComposeRule()

    private val app get() = RuntimeEnvironment.getApplication()
    private val content get() = ContentRepository.get(app)
    private val today = LocalDate.of(2026, 10, 23)

    private object NoActions : MainActions {
        override fun setLanguage(language: AppLanguage) = Unit
        override fun toggleFavorite(word: JesusWord) = Unit
        override fun selectTheme(theme: AppTheme) = Unit
        override fun selectWallpaper(wallpaper: Wallpaper) = Unit
        override fun setReminder(hour: Int, minute: Int) = Unit
        override fun resetTips() = Unit
    }

    private fun millis(date: LocalDate) = date.atTime(9, 0).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

    private fun state(
        themeId: String,
        language: AppLanguage,
        date: LocalDate = today,
        favourites: List<Int> = emptyList(),
        historyDays: Int = 0,
    ): UiState {
        val history = (0 until historyDays).map { back ->
            val day = date.minusDays(back.toLong())
            HistoryEntry(content.wordFor(day)!!.id, millis(day))
        }
        return UiState(
            content = content,
            settings = AppSettings(
                language = language,
                selectedThemeId = themeId,
                favoriteVerseIds = favourites,
                history = history,
            ),
            today = date,
        )
    }

    /** Decode the theme picture up front so the shot shows it rather than the placeholder. */
    private fun preload(theme: AppTheme) {
        theme.androidResource?.let { Drawables.backgrounds[it] }?.let {
            ImageCache.loadBlocking(app, it.phone, 4000, 4000)
        }
    }

    private fun shoot(name: String, body: @Composable () -> Unit) {
        compose.setContent(body)
        compose.waitForIdle()
        // Wait for thumbnails that decode in the background.
        compose.waitUntil(20_000) { ImageCache.inFlight.get() == 0 }
        compose.waitForIdle()
        compose.onRoot().captureRoboImage("build/screenshots/$name.png")
    }

    private fun main(name: String, state: UiState, tab: MainTab = MainTab.TODAY) {
        preload(state.theme)
        shoot(name) { MainScreen(state, NoActions, initialTab = tab) }
    }

    // Today — always fits on one screen.
    @Test fun today_english_sunriseChapel() = main("01_today_en_sunrise_chapel", state("sunrise_chapel", AppLanguage.ENGLISH))

    @Test fun today_tamil_gatesOfHeaven() =
        main("02_today_ta_gates_of_heaven", state("gates_of_heaven", AppLanguage.TAMIL, favourites = listOf(296)))

    @Test fun today_english_lilyFields_rounded() = main("03_today_en_lily_fields", state("lily_fields", AppLanguage.ENGLISH))

    @Test fun today_tamil_longestVerse_starryBethlehem() =
        main("04_today_ta_longest_starry", state("starry_bethlehem", AppLanguage.TAMIL, date = LocalDate.of(2026, 8, 6)))

    @Test fun today_tamil_14Nov_sunrise() =
        main("05_today_ta_14nov_sunrise", state("sunrise_chapel", AppLanguage.TAMIL, date = LocalDate.of(2026, 11, 14)))

    // History, Favourites and the verse page.
    @Test fun history_english() =
        main("10_history_en", state("river_of_life", AppLanguage.ENGLISH, historyDays = 6, favourites = listOf(295)), MainTab.HISTORY)

    @Test fun history_empty_tamil() = main("11_history_ta_empty", state("sunrise_chapel", AppLanguage.TAMIL), MainTab.HISTORY)

    @Test fun verseDetail_tamil_longest() {
        val s = state("crown_of_glory", AppLanguage.TAMIL)
        preload(s.theme)
        val word = content.word(218)!!
        shoot("12_detail_ta_longest") {
            VerseDetailScreen(VerseDetail(word, millis(today)), s, onClose = {}, onToggleFavorite = {}, onShare = {})
        }
    }

    // Themes, Wallpapers, Settings.
    @Test fun themes_english() = main("20_themes_en", state("gates_of_heaven", AppLanguage.ENGLISH), MainTab.THEMES)

    @Test fun wallpapers_tamil() = main("21_wallpapers_ta", state("sunrise_chapel", AppLanguage.TAMIL), MainTab.WALLPAPERS)

    @Test fun settings_english() = main("22_settings_en", state("nativity_star", AppLanguage.ENGLISH), MainTab.SETTINGS)

    @Test fun settings_tamil() = main("23_settings_ta", state("sunrise_chapel", AppLanguage.TAMIL), MainTab.SETTINGS)
}
