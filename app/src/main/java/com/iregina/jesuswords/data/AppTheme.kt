package com.iregina.jesuswords.data

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** One entry of res/raw/themes.json (mirrors AppTheme in ThemeModel.swift). */
@Serializable
data class AppTheme(
    val id: String,
    val name: String,
    val subtitle: String,
    val gradientColors: List<String>,
    val accentColor: String,
    /** "serif" or "rounded". */
    val fontDesign: String,
    @SerialName("decorativeIcon_SFSymbol") val decorativeIcon: String,
    val overlayElements: List<ThemeOverlay> = emptyList(),
    val backgroundImage: String? = null,
    /** Drawable base name (without _phone/_tablet); non-null for picture themes. */
    val androidResource: String? = null,
) {
    val isPictureTheme: Boolean get() = androidResource != null
    val isRounded: Boolean get() = fontDesign == "rounded"
    val gradient: List<Color> get() = gradientColors.map(::parseHexColor)
    val accent: Color get() = parseHexColor(accentColor)

    companion object {
        const val DEFAULT_ID = "sunrise_chapel"
    }
}

/** A decorative icon drawn on gradient themes; x/y are fractions of the screen. */
@Serializable
data class ThemeOverlay(
    val icon: String,
    val size: Float,
    val opacity: Float,
    val x: Float,
    val y: Float,
    val rotationDeg: Float,
)

/** One entry of res/raw/wallpapers.json — a background for shared verse images. */
@Serializable
data class Wallpaper(
    val id: String,
    val name: String,
    val tamilName: String,
    val subtitle: String,
    val tamilSubtitle: String,
    val image: String? = null,
    val androidResource: String,
) {
    fun name(language: AppLanguage) = if (language == AppLanguage.TAMIL) tamilName else name
    fun subtitle(language: AppLanguage) = if (language == AppLanguage.TAMIL) tamilSubtitle else subtitle

    companion object {
        const val DEFAULT_ID = "wallpaper_gates_of_heaven"
    }
}

/** "#RRGGBB" or "#AARRGGBB" → Color. */
fun parseHexColor(hex: String): Color {
    val clean = hex.removePrefix("#")
    val value = clean.toLong(16)
    return when (clean.length) {
        6 -> Color(0xFF000000 or value)
        8 -> Color(value)
        else -> Color.White
    }
}
