package com.iregina.jesuswords.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.iregina.jesuswords.data.AppContent
import com.iregina.jesuswords.data.AppLanguage
import com.iregina.jesuswords.data.AppSettings
import com.iregina.jesuswords.data.AppTheme
import com.iregina.jesuswords.data.ContentRepository
import com.iregina.jesuswords.data.JesusWord
import com.iregina.jesuswords.data.SettingsRepository
import com.iregina.jesuswords.data.Wallpaper
import com.iregina.jesuswords.reminders.ReminderScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

/** A verse as shown in History: the verse and when it was shown (epoch millis). */
data class ShownVerse(val word: JesusWord, val shownAtMillis: Long)

/** Everything the screens draw from: bundled content + saved settings + today's date. */
data class UiState(
    val content: AppContent,
    val settings: AppSettings,
    val today: LocalDate,
) {
    val language: AppLanguage get() = settings.language
    val theme: AppTheme = content.theme(settings.selectedThemeId)
    val wallpaper: Wallpaper = content.wallpaper(settings.selectedWallpaperId)
    val todaysWord: JesusWord? = content.wordFor(today)
    private val favoriteSet = settings.favoriteVerseIds.toSet()

    /** History newest first, resolved to verses (entries for unknown ids are skipped). */
    val history: List<ShownVerse> = settings.history.mapNotNull { entry ->
        content.word(entry.verseId)?.let { ShownVerse(it, entry.dateShown) }
    }

    /** Favourites newest first. */
    val favoriteWords: List<JesusWord> = settings.favoriteVerseIds.mapNotNull(content::word)

    fun isFavorite(word: JesusWord) = word.id in favoriteSet
}

/** Everything the UI can ask for; implemented by [MainViewModel] (and fakes in tests). */
interface MainActions {
    fun setLanguage(language: AppLanguage)
    fun toggleFavorite(word: JesusWord)
    fun selectTheme(theme: AppTheme)
    fun selectWallpaper(wallpaper: Wallpaper)

    /** Saves the reminder time, turns reminders on and schedules the next 7 days. */
    fun setReminder(hour: Int, minute: Int)

    /** "Show Tips Again": both first-launch tips play once more. */
    fun resetTips()
}

/** The app-wide state holder (WordsViewModel.swift). */
class MainViewModel(app: Application) : AndroidViewModel(app), MainActions {
    private val settingsRepository = SettingsRepository(app)
    private val content = MutableStateFlow<AppContent?>(null)
    private val today = MutableStateFlow(LocalDate.now())

    val state: StateFlow<UiState?> =
        combine(content.filterNotNull(), settingsRepository.settings, today) { c, s, t -> UiState(c, s, t) }
            .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    init {
        viewModelScope.launch(Dispatchers.Default) {
            content.value = ContentRepository.get(app)
        }
    }

    /**
     * Called whenever the app comes to the foreground: picks up a new day and adds
     * today's verse to History (at most once per day).
     */
    fun onAppForeground() {
        val app = getApplication<Application>()
        val date = LocalDate.now()
        today.value = date
        ReminderScheduler.clearDelivered(app)
        viewModelScope.launch {
            ReminderScheduler.rescheduleFromSettings(app)
            val word = content.filterNotNull().first().wordFor(date) ?: return@launch
            settingsRepository.recordShown(word.id, date, System.currentTimeMillis())
        }
    }

    override fun setLanguage(language: AppLanguage) {
        viewModelScope.launch {
            settingsRepository.setLanguage(language)
            ReminderScheduler.rescheduleFromSettings(getApplication<Application>())
        }
    }

    override fun setReminder(hour: Int, minute: Int) {
        viewModelScope.launch {
            settingsRepository.setReminder(enabled = true, hour = hour, minute = minute)
            ReminderScheduler.rescheduleFromSettings(getApplication<Application>())
        }
    }

    override fun resetTips() {
        viewModelScope.launch { settingsRepository.resetTips() }
    }

    override fun toggleFavorite(word: JesusWord) {
        viewModelScope.launch { settingsRepository.toggleFavorite(word.id) }
    }

    override fun selectTheme(theme: AppTheme) {
        viewModelScope.launch { settingsRepository.setTheme(theme.id) }
    }

    override fun selectWallpaper(wallpaper: Wallpaper) {
        viewModelScope.launch { settingsRepository.setWallpaper(wallpaper.id) }
    }
}
