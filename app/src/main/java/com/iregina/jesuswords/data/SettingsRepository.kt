package com.iregina.jesuswords.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException
import java.time.LocalDate

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/** A day's verse in History; [dateShown] is epoch milliseconds. */
@Serializable
data class HistoryEntry(val verseId: Int, val dateShown: Long)

/** Everything the app persists (brief §13). All of it stays on the device. */
data class AppSettings(
    val hasCompletedSetup: Boolean = false,
    val language: AppLanguage = AppLanguage.ENGLISH,
    val selectedThemeId: String = AppTheme.DEFAULT_ID,
    val selectedWallpaperId: String = Wallpaper.DEFAULT_ID,
    val reminderEnabled: Boolean = false,
    val reminderHour: Int = DEFAULT_REMINDER_HOUR,
    val reminderMinute: Int = DEFAULT_REMINDER_MINUTE,
    /** Newest first, at most one entry per day. */
    val history: List<HistoryEntry> = emptyList(),
    val lastShownDay: LocalDate? = null,
    /** Newest first. */
    val favoriteVerseIds: List<Int> = emptyList(),
    val hasSeenLanguageTip: Boolean = false,
    val hasSeenThemesTip: Boolean = false,
) {
    companion object {
        const val DEFAULT_REMINDER_HOUR = 7
        const val DEFAULT_REMINDER_MINUTE = 0
    }
}

class SettingsRepository(context: Context) {
    private val store = context.applicationContext.settingsDataStore

    val settings: Flow<AppSettings> = store.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map(::toSettings)

    suspend fun current(): AppSettings = settings.first()

    suspend fun setHasCompletedSetup(done: Boolean = true) = store.edit { it[Keys.HAS_COMPLETED_SETUP] = done }

    suspend fun setLanguage(language: AppLanguage) = store.edit { it[Keys.LANGUAGE] = language.key }

    suspend fun setTheme(themeId: String) = store.edit { it[Keys.THEME] = themeId }

    suspend fun setWallpaper(wallpaperId: String) = store.edit { it[Keys.WALLPAPER] = wallpaperId }

    suspend fun setReminder(enabled: Boolean, hour: Int, minute: Int) = store.edit {
        it[Keys.REMINDER_ENABLED] = enabled
        it[Keys.REMINDER_HOUR] = hour
        it[Keys.REMINDER_MINUTE] = minute
    }

    /** Adds the verse at the front of the favourites, or removes it if already saved. */
    suspend fun toggleFavorite(verseId: Int) = store.edit { prefs ->
        val ids = decodeIds(prefs[Keys.FAVORITES])
        val updated = if (verseId in ids) ids - verseId else listOf(verseId) + ids
        prefs[Keys.FAVORITES] = json.encodeToString(updated)
    }

    /**
     * Adds today's verse to the top of History once per day (tracked by lastShownDay).
     * Returns true if an entry was added.
     */
    suspend fun recordShown(verseId: Int, today: LocalDate, nowMillis: Long): Boolean {
        var added = false
        store.edit { prefs ->
            if (prefs[Keys.LAST_SHOWN_DAY] != today.toString()) {
                val history = decodeHistory(prefs[Keys.HISTORY])
                prefs[Keys.HISTORY] = json.encodeToString(listOf(HistoryEntry(verseId, nowMillis)) + history)
                prefs[Keys.LAST_SHOWN_DAY] = today.toString()
                added = true
            }
        }
        return added
    }

    suspend fun setSeenLanguageTip(seen: Boolean = true) = store.edit { it[Keys.SEEN_LANGUAGE_TIP] = seen }

    suspend fun setSeenThemesTip(seen: Boolean = true) = store.edit { it[Keys.SEEN_THEMES_TIP] = seen }

    /** "Show Tips Again": both tips play once more. */
    suspend fun resetTips() = store.edit {
        it[Keys.SEEN_LANGUAGE_TIP] = false
        it[Keys.SEEN_THEMES_TIP] = false
    }

    private fun toSettings(prefs: Preferences) = AppSettings(
        hasCompletedSetup = prefs[Keys.HAS_COMPLETED_SETUP] ?: false,
        language = AppLanguage.fromKey(prefs[Keys.LANGUAGE]),
        selectedThemeId = prefs[Keys.THEME] ?: AppTheme.DEFAULT_ID,
        selectedWallpaperId = prefs[Keys.WALLPAPER] ?: Wallpaper.DEFAULT_ID,
        reminderEnabled = prefs[Keys.REMINDER_ENABLED] ?: false,
        reminderHour = prefs[Keys.REMINDER_HOUR] ?: AppSettings.DEFAULT_REMINDER_HOUR,
        reminderMinute = prefs[Keys.REMINDER_MINUTE] ?: AppSettings.DEFAULT_REMINDER_MINUTE,
        history = decodeHistory(prefs[Keys.HISTORY]),
        lastShownDay = prefs[Keys.LAST_SHOWN_DAY]?.let { runCatching { LocalDate.parse(it) }.getOrNull() },
        favoriteVerseIds = decodeIds(prefs[Keys.FAVORITES]),
        hasSeenLanguageTip = prefs[Keys.SEEN_LANGUAGE_TIP] ?: false,
        hasSeenThemesTip = prefs[Keys.SEEN_THEMES_TIP] ?: false,
    )

    private fun decodeIds(raw: String?): List<Int> =
        raw?.let { runCatching { json.decodeFromString<List<Int>>(it) }.getOrNull() } ?: emptyList()

    private fun decodeHistory(raw: String?): List<HistoryEntry> =
        raw?.let { runCatching { json.decodeFromString<List<HistoryEntry>>(it) }.getOrNull() } ?: emptyList()

    private object Keys {
        val HAS_COMPLETED_SETUP = booleanPreferencesKey("hasCompletedSetup")
        val LANGUAGE = stringPreferencesKey("selectedLanguage")
        val THEME = stringPreferencesKey("selectedThemeId")
        val WALLPAPER = stringPreferencesKey("selectedWallpaperId")
        val REMINDER_ENABLED = booleanPreferencesKey("reminderEnabled")
        val REMINDER_HOUR = intPreferencesKey("reminderHour")
        val REMINDER_MINUTE = intPreferencesKey("reminderMinute")
        val HISTORY = stringPreferencesKey("history")
        val LAST_SHOWN_DAY = stringPreferencesKey("lastShownDay")
        val FAVORITES = stringPreferencesKey("favoriteVerseIDs")
        val SEEN_LANGUAGE_TIP = booleanPreferencesKey("hasSeenLanguageTip")
        val SEEN_THEMES_TIP = booleanPreferencesKey("hasSeenThemesTip")
    }

    private companion object {
        val json = Json { ignoreUnknownKeys = true }
    }
}
