package com.iregina.jesuswords.data

import android.content.Context
import androidx.annotation.RawRes
import com.iregina.jesuswords.R
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.time.LocalDate

/** Everything bundled in res/raw, parsed once per process. */
class AppContent(
    val words: List<JesusWord>,
    val themes: List<AppTheme>,
    val wallpapers: List<Wallpaper>,
) {
    private val wordsById = words.associateBy { it.id }
    private val themesById = themes.associateBy { it.id }
    private val wallpapersById = wallpapers.associateBy { it.id }

    /** Grid order: picture themes first (JSON order), then the gradient themes. */
    val themesForGrid: List<AppTheme> =
        themes.filter { it.isPictureTheme } + themes.filterNot { it.isPictureTheme }

    fun word(id: Int): JesusWord? = wordsById[id]

    fun wordFor(date: LocalDate): JesusWord? = DailyVerse.wordFor(date, words)

    fun theme(id: String?): AppTheme =
        themesById[id] ?: themesById[AppTheme.DEFAULT_ID] ?: themes.first()

    fun wallpaper(id: String?): Wallpaper =
        wallpapersById[id] ?: wallpapersById[Wallpaper.DEFAULT_ID] ?: wallpapers.first()
}

object ContentRepository {
    private val json = Json { ignoreUnknownKeys = true }

    @Volatile
    private var cached: AppContent? = null

    /** Parses the bundled JSON on first use. Call off the main thread when possible. */
    fun get(context: Context): AppContent {
        cached?.let { return it }
        return synchronized(this) {
            cached ?: load(context.applicationContext).also { cached = it }
        }
    }

    private fun load(context: Context): AppContent = AppContent(
        words = json.decodeFromString(readRaw(context, R.raw.jesus_words)),
        themes = json.decodeFromString(readRaw(context, R.raw.themes)),
        wallpapers = json.decodeFromString(readRaw(context, R.raw.wallpapers)),
    )

    private fun readRaw(context: Context, @RawRes id: Int): String =
        context.resources.openRawResource(id).bufferedReader(Charsets.UTF_8).use { it.readText() }
}
