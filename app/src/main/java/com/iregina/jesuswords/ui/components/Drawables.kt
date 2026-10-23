package com.iregina.jesuswords.ui.components

import com.iregina.jesuswords.R

/**
 * Compile-time map from the `androidResource` names in themes.json / wallpapers.json
 * to the drawables (phone portrait + tablet 3:4 crops). Explicit references keep
 * the release resource shrinker from removing images that are only named in JSON.
 */
data class BackgroundImages(val phone: Int, val tablet: Int)

object Drawables {
    val backgrounds: Map<String, BackgroundImages> = mapOf(
        "bg_crown_of_glory" to BackgroundImages(R.drawable.bg_crown_of_glory_phone, R.drawable.bg_crown_of_glory_tablet),
        "bg_crystal_paradise" to BackgroundImages(R.drawable.bg_crystal_paradise_phone, R.drawable.bg_crystal_paradise_tablet),
        "bg_easter_dawn" to BackgroundImages(R.drawable.bg_easter_dawn_phone, R.drawable.bg_easter_dawn_tablet),
        "bg_friends_on_high" to BackgroundImages(R.drawable.bg_friends_on_high_phone, R.drawable.bg_friends_on_high_tablet),
        "bg_garden_of_grace" to BackgroundImages(R.drawable.bg_garden_of_grace_phone, R.drawable.bg_garden_of_grace_tablet),
        "bg_gates_of_heaven" to BackgroundImages(R.drawable.bg_gates_of_heaven_phone, R.drawable.bg_gates_of_heaven_tablet),
        "bg_gates_of_heaven_alt" to BackgroundImages(R.drawable.bg_gates_of_heaven_alt_phone, R.drawable.bg_gates_of_heaven_alt_tablet),
        "bg_good_shepherd" to BackgroundImages(R.drawable.bg_good_shepherd_phone, R.drawable.bg_good_shepherd_tablet),
        "bg_holy_spirit_glow" to BackgroundImages(R.drawable.bg_holy_spirit_glow_phone, R.drawable.bg_holy_spirit_glow_tablet),
        "bg_lavender_grace" to BackgroundImages(R.drawable.bg_lavender_grace_phone, R.drawable.bg_lavender_grace_tablet),
        "bg_little_blessings" to BackgroundImages(R.drawable.bg_little_blessings_phone, R.drawable.bg_little_blessings_tablet),
        "bg_nativity_star" to BackgroundImages(R.drawable.bg_nativity_star_phone, R.drawable.bg_nativity_star_tablet),
        "bg_new_jerusalem" to BackgroundImages(R.drawable.bg_new_jerusalem_phone, R.drawable.bg_new_jerusalem_tablet),
        "bg_pentecost_light" to BackgroundImages(R.drawable.bg_pentecost_light_phone, R.drawable.bg_pentecost_light_tablet),
        "bg_rainbow_promise" to BackgroundImages(R.drawable.bg_rainbow_promise_phone, R.drawable.bg_rainbow_promise_tablet),
        "bg_resurrection_glory" to BackgroundImages(R.drawable.bg_resurrection_glory_phone, R.drawable.bg_resurrection_glory_tablet),
        "bg_river_of_life" to BackgroundImages(R.drawable.bg_river_of_life_phone, R.drawable.bg_river_of_life_tablet),
        "bg_rose_of_sharon" to BackgroundImages(R.drawable.bg_rose_of_sharon_phone, R.drawable.bg_rose_of_sharon_tablet),
        "bg_sacred_heart_cathedral" to BackgroundImages(R.drawable.bg_sacred_heart_cathedral_phone, R.drawable.bg_sacred_heart_cathedral_tablet),
        "bg_smiling_skies" to BackgroundImages(R.drawable.bg_smiling_skies_phone, R.drawable.bg_smiling_skies_tablet),
        "bg_stacked_joy" to BackgroundImages(R.drawable.bg_stacked_joy_phone, R.drawable.bg_stacked_joy_tablet),
        "bg_star_of_bethlehem" to BackgroundImages(R.drawable.bg_star_of_bethlehem_phone, R.drawable.bg_star_of_bethlehem_tablet),
        "bg_still_waters" to BackgroundImages(R.drawable.bg_still_waters_phone, R.drawable.bg_still_waters_tablet),
        "bg_tree_of_life" to BackgroundImages(R.drawable.bg_tree_of_life_phone, R.drawable.bg_tree_of_life_tablet),
    )
}
