package com.example.newsgb.utils.ui

import com.example.newsgb.R

enum class ThemeModes(val resIdName: Int) {
    SYSTEM_MODE(R.string.settings_system_theme_name),
    LIGHT_MODE(R.string.settings_light_theme_name),
    DARK_MODE(R.string.settings_dark_theme_name),
    BATTERY_MODE(R.string.settings_battery_theme_name)
}