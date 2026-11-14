package com.iregina.jesuswords.ui.components

import androidx.annotation.DrawableRes
import com.iregina.jesuswords.R

/**
 * Maps the SF Symbol names used by the iOS themes to bundled Material Symbols
 * (Rounded, filled). Returns null for "cross.fill", which is drawn as the Latin cross.
 */
@DrawableRes
fun symbolDrawable(sfSymbol: String): Int? = when (sfSymbol) {
    "cross.fill" -> null
    "sparkle", "sparkles" -> R.drawable.ic_auto_awesome
    "mountain.2.fill" -> R.drawable.ic_landscape
    "star.fill" -> R.drawable.ic_star
    "leaf.fill" -> R.drawable.ic_eco
    "water.waves" -> R.drawable.ic_waves
    "wind" -> R.drawable.ic_air
    "bird.fill" -> R.drawable.ic_raven
    "sun.max.fill" -> R.drawable.ic_sunny
    "sun.haze.fill", "sunrise.fill" -> R.drawable.ic_wb_twilight
    "camera.macro" -> R.drawable.ic_local_florist
    "tree.fill" -> R.drawable.ic_park
    "flame.fill" -> R.drawable.ic_local_fire_department
    "cloud.fill" -> R.drawable.ic_cloud
    "crown.fill" -> R.drawable.ic_crown
    "drop.fill" -> R.drawable.ic_water_drop
    "building.columns.fill" -> R.drawable.ic_account_balance
    "rainbow" -> R.drawable.ic_looks
    else -> R.drawable.ic_auto_awesome
}
