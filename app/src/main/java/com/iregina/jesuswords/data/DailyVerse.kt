package com.iregina.jesuswords.data

import java.time.LocalDate

/**
 * The one daily rule shared by the app, the notifications and the widget:
 * `index = (dayOfYear − 1) % 365`, dayOfYear 1-based in the device's local calendar.
 * In a leap year day 366 wraps to index 0.
 */
object DailyVerse {
    const val VERSE_COUNT = 365

    fun indexFor(date: LocalDate, count: Int = VERSE_COUNT): Int =
        (date.dayOfYear - 1) % count.coerceAtLeast(1)

    fun wordFor(date: LocalDate, words: List<JesusWord>): JesusWord? =
        if (words.isEmpty()) null else words[indexFor(date, words.size)]
}
