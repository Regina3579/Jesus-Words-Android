package com.iregina.jesuswords.data

import kotlinx.serialization.Serializable

/** One daily verse from res/raw/jesus_words.json (mirrors JesusWord.swift). */
@Serializable
data class JesusWord(
    val id: Int,
    val day: Int,
    val category: String,
    val quote: String,
    val reference: String,
    val theme: String,
    val tamilQuote: String? = null,
    val tamilTheme: String? = null,
    val tamilReference: String? = null,
) {
    /** Tamil text when the language is Tamil, falling back to English if it is missing. */
    fun quote(language: AppLanguage): String =
        if (language.isTamil) tamilQuote?.takeIf { it.isNotBlank() } ?: quote else quote

    fun theme(language: AppLanguage): String =
        if (language.isTamil) tamilTheme?.takeIf { it.isNotBlank() } ?: theme else theme

    fun reference(language: AppLanguage): String =
        if (language.isTamil) tamilReference?.takeIf { it.isNotBlank() } ?: reference else reference

    val verseCategory: VerseCategory? get() = VerseCategory.fromKey(category)

    fun categoryName(language: AppLanguage): String =
        verseCategory?.let { language.pick(it.english, it.tamil) } ?: category

    val categoryEmoji: String get() = verseCategory?.emoji ?: "✝️"
}

/** The 8 topics, with the display names and emoji from JesusWord.swift. */
enum class VerseCategory(val key: String, val english: String, val tamil: String, val emoji: String) {
    DONT_BE_AFRAID("DontBeAfraid", "Don't Be Afraid", "பயப்படாதே", "🛡️"),
    DONT_BE_SAD("DontBeSad", "Don't Be Sad", "கவலைப்படாதே", "☀️"),
    YOU_CAN_DO_IT("YouCanDoIt", "You Can Do It", "உன்னால் முடியும்", "💪"),
    I_AM_THERE_FOR_YOU("IAmThereForYou", "I Am There For You", "நான் உனக்காக இருக்கிறேன்", "🤲"),
    DONT_WORRY("DontWorry", "Don't Worry", "கவலைப்படாதே", "🕊️"),
    YOU_ARE_BLESSED("YouAreBlessed", "You Are Blessed", "நீ ஆசீர்வதிக்கப்பட்டவன்", "✨"),
    PRAY_YOU_WILL_GET("PrayYouWillGet", "Pray & Receive", "ஜெபி, பெறுவாய்", "🙏"),
    PARABLE_MORAL("ParableMoral", "Parable Wisdom", "உவமை ஞானம்", "📖");

    fun name(language: AppLanguage) = language.pick(english, tamil)

    companion object {
        fun fromKey(key: String): VerseCategory? = entries.firstOrNull { it.key == key }

        /** Fixed section order for Favourites (FavoritesView.swift). */
        val favouritesOrder = listOf(
            DONT_BE_AFRAID, DONT_WORRY, DONT_BE_SAD, I_AM_THERE_FOR_YOU,
            YOU_CAN_DO_IT, YOU_ARE_BLESSED, PRAY_YOU_WILL_GET, PARABLE_MORAL,
        )
    }
}
