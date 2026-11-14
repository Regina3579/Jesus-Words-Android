package com.iregina.jesuswords.data

import com.iregina.jesuswords.ui.components.Drawables
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** Sanity checks on res/raw so a bad data edit fails the build, not the app. */
class BundledDataTest {
    private val json = Json { ignoreUnknownKeys = true }
    private fun raw(name: String) = File("src/main/res/raw/$name.json").readText()

    private val words: List<JesusWord> = json.decodeFromString(raw("jesus_words"))
    private val themes: List<AppTheme> = json.decodeFromString(raw("themes"))
    private val wallpapers: List<Wallpaper> = json.decodeFromString(raw("wallpapers"))

    @Test
    fun has365VersesInDayOrder() {
        assertEquals(365, words.size)
        words.forEachIndexed { index, word -> assertEquals(index + 1, word.day) }
        assertEquals(words.size, words.map { it.id }.toSet().size)
    }

    @Test
    fun everyVerseHasAKnownCategoryAndTamilText() {
        words.forEach { word ->
            assertNotNull("category ${word.category}", word.verseCategory)
            assertTrue("tamilQuote missing for ${word.id}", !word.tamilQuote.isNullOrBlank())
        }
    }

    @Test
    fun themesAndWallpapers() {
        assertEquals(30, themes.size)
        assertEquals(18, themes.count { it.isPictureTheme })
        assertTrue(themes.any { it.id == AppTheme.DEFAULT_ID })
        assertEquals(23, wallpapers.size)
        assertTrue(wallpapers.any { it.id == Wallpaper.DEFAULT_ID })
        themes.forEach { assertEquals(4, it.gradientColors.size) }
    }

    @Test
    fun everyImageNameHasADrawable() {
        val names = themes.mapNotNull { it.androidResource } + wallpapers.map { it.androidResource }
        names.forEach { assertNotNull("no drawable for $it", Drawables.backgrounds[it]) }
    }
}
