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

    fun isFavorite(word: JesusWord) = word.id in favoriteSet
}

/** Everything the UI can ask for; implemented by [MainViewModel] (and fakes in tests). */
interface MainActions {
    fun setLanguage(language: AppLanguage)
    fun toggleFavorite(word: JesusWord)
    fun selectTheme(theme: AppTheme)
    fun selectWallpaper(wallpaper: Wallpaper)
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
        val date = LocalDate.now()
        today.value = date
        viewModelScope.launch {
            val word = content.filterNotNull().first().wordFor(date) ?: return@launch
            settingsRepository.recordShown(word.id, date, System.currentTimeMillis())
        }
    }

    override fun setLanguage(language: AppLanguage) {
        viewModelScope.launch { settingsRepository.setLanguage(language) }
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
