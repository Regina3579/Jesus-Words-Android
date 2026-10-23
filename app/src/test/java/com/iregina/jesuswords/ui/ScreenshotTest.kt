package com.iregina.jesuswords.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import com.iregina.jesuswords.data.AppLanguage
import com.iregina.jesuswords.data.AppSettings
import com.iregina.jesuswords.data.AppTheme
import com.iregina.jesuswords.data.ContentRepository
import com.iregina.jesuswords.data.JesusWord
import com.iregina.jesuswords.data.Wallpaper
import com.iregina.jesuswords.ui.components.Drawables
import com.iregina.jesuswords.ui.components.ImageCache
import com.iregina.jesuswords.ui.main.MainScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.time.LocalDate

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

    private object NoActions : MainActions {
        override fun setLanguage(language: AppLanguage) = Unit
        override fun toggleFavorite(word: JesusWord) = Unit
        override fun selectTheme(theme: AppTheme) = Unit
        override fun selectWallpaper(wallpaper: Wallpaper) = Unit
    }

    private fun state(
        themeId: String,
        language: AppLanguage,
        date: LocalDate = LocalDate.of(2026, 10, 23),
        favourite: Boolean = false,
    ): UiState {
        val todays = content.wordFor(date)!!
        return UiState(
            content = content,
            settings = AppSettings(
                language = language,
                selectedThemeId = themeId,
                favoriteVerseIds = if (favourite) listOf(todays.id) else emptyList(),
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
        compose.onRoot().captureRoboImage("build/screenshots/$name.png")
    }

    private fun today(name: String, state: UiState) {
        preload(state.theme)
        shoot(name) { MainScreen(state, NoActions) }
    }

    @Test fun today_english_sunriseChapel() = today("01_today_en_sunrise_chapel", state("sunrise_chapel", AppLanguage.ENGLISH))

    @Test fun today_tamil_gatesOfHeaven() =
        today("02_today_ta_gates_of_heaven", state("gates_of_heaven", AppLanguage.TAMIL, favourite = true))

    @Test fun today_english_lilyFields_rounded() = today("03_today_en_lily_fields", state("lily_fields", AppLanguage.ENGLISH))

    @Test fun today_tamil_longestVerse_starryBethlehem() =
        today("04_today_ta_longest_starry", state("starry_bethlehem", AppLanguage.TAMIL, date = LocalDate.of(2026, 8, 6)))

    @Test fun today_english_riverOfLife() = today("05_today_en_river_of_life", state("river_of_life", AppLanguage.ENGLISH))
}
