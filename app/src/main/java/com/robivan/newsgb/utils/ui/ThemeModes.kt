package com.robivan.newsgb.utils.ui

import com.robivan.newsgb.R

enum class ThemeModes(val nameResId: Int) {
    SYSTEM_MODE(R.string.settings_option_item_name_as_system),
    LIGHT_MODE(R.string.settings_light_theme_name),
    DARK_MODE(R.string.settings_dark_theme_name),
    BATTERY_MODE(R.string.settings_battery_theme_name)
}