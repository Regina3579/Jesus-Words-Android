import Foundation
import SwiftUI

enum AppLanguage: String, CaseIterable {
    case english = "English"
    case tamil = "தமிழ்"
}

class WordsViewModel: ObservableObject {
    @Published var allWords: [JesusWord] = []
    @Published var todaysWord: JesusWord?
    @Published var history: [HistoryEntry] = []
    @Published var reminderHour: Int = 7
    @Published var reminderMinute: Int = 0
    @Published var themeManager = ThemeManager.shared
    @Published var wallpaperManager = WallpaperManager.shared
    @Published var selectedLanguage: AppLanguage = .english
    // Newest first.
    @Published private(set) var favoriteIDs: [Int] = []

    private let historyKey = "viewedWordsHistory"
    private let favoritesKey = "favoriteVerseIDs"
    private let lastDayKey = "lastShownDay"
    private let languageKey = "selectedLanguage"

    init() {
        loadLanguage()
        loadWords()
        loadHistory()
        loadTodaysWord()
        favoriteIDs = UserDefaults.standard.array(forKey: favoritesKey) as? [Int] ?? []
    }

    // MARK: - Favourites

    func isFavorite(_ word: JesusWord) -> Bool {
        favoriteIDs.contains(word.id)
    }

    func toggleFavorite(_ word: JesusWord) {
        if let index = favoriteIDs.firstIndex(of: word.id) {
            favoriteIDs.remove(at: index)
        } else {
            favoriteIDs.insert(word.id, at: 0)
        }
        UserDefaults.standard.set(favoriteIDs, forKey: favoritesKey)
    }

    var favoriteWords: [JesusWord] {
        let byID = Dictionary(allWords.map { ($0.id, $0) }, uniquingKeysWith: { first, _ in first })
        return favoriteIDs.compactMap { byID[$0] }
    }

    var selectedTheme: AppTheme {
        themeManager.selectedTheme
    }

    func selectTheme(_ theme: AppTheme) {
        themeManager.selectedThemeId = theme.id
        objectWillChange.send()
    }

    var selectedWallpaper: Wallpaper {
        wallpaperManager.selectedWallpaper
    }

    func selectWallpaper(_ wallpaper: Wallpaper) {
        wallpaperManager.selectedWallpaperId = wallpaper.id
        objectWillChange.send()
    }

    func setLanguage(_ language: AppLanguage) {
        selectedLanguage = language
        UserDefaults.standard.set(language.rawValue, forKey: languageKey)
        rescheduleNotifications()
    }

    private func loadLanguage() {
        if let saved = UserDefaults.standard.string(forKey: languageKey),
           let language = AppLanguage(rawValue: saved) {
            selectedLanguage = language
        }
    }

    func loadWords() {
        guard let url = Bundle.main.url(forResource: "jesus_words", withExtension: "json") else {
            print("Could not find jesus_words.json")
            return
        }

        do {
            let data = try Data(contentsOf: url)
            allWords = try JSONDecoder().decode([JesusWord].self, from: data)
        } catch {
            print("Error loading words: \(error)")
        }
    }

    func loadTodaysWord() {
        let dayOfYear = Calendar.current.ordinality(of: .day, in: .year, for: Date()) ?? 1
        let wordIndex = (dayOfYear - 1) % max(allWords.count, 1)

        guard !allWords.isEmpty else { return }
        todaysWord = allWords[wordIndex]

        // Add to history if not already added today
        let today = Calendar.current.startOfDay(for: Date())
        let lastShownDate = UserDefaults.standard.object(forKey: lastDayKey) as? Date

        if lastShownDate == nil || !Calendar.current.isDate(lastShownDate!, inSameDayAs: today) {
            if let word = todaysWord {
                let entry = HistoryEntry(word: word, dateShown: Date())
                history.insert(entry, at: 0)
                saveHistory()
                UserDefaults.standard.set(today, forKey: lastDayKey)
            }
        }

        // Schedule notifications with correct word for each future day
        let hour = UserDefaults.standard.integer(forKey: "reminderHour")
        let minute = UserDefaults.standard.integer(forKey: "reminderMinute")
        if hour > 0 || minute > 0 {
            NotificationManager.shared.scheduleDailyNotifications(
                hour: hour, minute: minute,
                allWords: allWords,
                language: selectedLanguage
            )
        }
    }

    func saveHistory() {
        if let data = try? JSONEncoder().encode(history) {
            UserDefaults.standard.set(data, forKey: historyKey)
        }
    }

    func loadHistory() {
        guard let data = UserDefaults.standard.data(forKey: historyKey) else { return }
        if let decoded = try? JSONDecoder().decode([HistoryEntry].self, from: data) {
            history = decoded
        }
    }

    func setReminder(hour: Int, minute: Int) {
        reminderHour = hour
        reminderMinute = minute
        UserDefaults.standard.set(hour, forKey: "reminderHour")
        UserDefaults.standard.set(minute, forKey: "reminderMinute")

        NotificationManager.shared.scheduleDailyNotifications(
            hour: hour, minute: minute,
            allWords: allWords,
            language: selectedLanguage
        )
    }

    private func rescheduleNotifications() {
        let hour = UserDefaults.standard.integer(forKey: "reminderHour")
        let minute = UserDefaults.standard.integer(forKey: "reminderMinute")
        guard hour > 0 || minute > 0 else { return }
        NotificationManager.shared.scheduleDailyNotifications(
            hour: hour, minute: minute,
            allWords: allWords,
            language: selectedLanguage
        )
    }

    var todaysDayNumber: Int {
        Calendar.current.ordinality(of: .day, in: .year, for: Date()) ?? 1
    }
}
