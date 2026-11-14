package com.iregina.jesuswords.data

/** The two app languages. [key] is the value persisted in DataStore. */
enum class AppLanguage(val key: String) {
    ENGLISH("english"),
    TAMIL("tamil");

    val isTamil: Boolean get() = this == TAMIL

    /** Picks the English or Tamil variant of a piece of UI text. */
    fun pick(english: String, tamil: String): String = if (this == TAMIL) tamil else english

    companion object {
        fun fromKey(key: String?): AppLanguage = entries.firstOrNull { it.key == key } ?: ENGLISH
    }
}
