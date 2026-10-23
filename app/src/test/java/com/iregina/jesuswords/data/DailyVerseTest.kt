package com.iregina.jesuswords.data

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class DailyVerseTest {
    @Test
    fun firstDayOfYearIsIndexZero() {
        assertEquals(0, DailyVerse.indexFor(LocalDate.of(2026, 1, 1)))
    }

    @Test
    fun lastDayOfCommonYearIsLastVerse() {
        assertEquals(364, DailyVerse.indexFor(LocalDate.of(2026, 12, 31)))
    }

    @Test
    fun leapDay366WrapsToFirstVerse() {
        assertEquals(0, DailyVerse.indexFor(LocalDate.of(2028, 12, 31)))
        assertEquals(59, DailyVerse.indexFor(LocalDate.of(2028, 2, 29)))
    }

    @Test
    fun matchesDayField() {
        // Day 296 of 2026 (23 Oct) → index 295 → the verse whose "day" is 296.
        assertEquals(295, DailyVerse.indexFor(LocalDate.of(2026, 10, 23)))
    }
}
