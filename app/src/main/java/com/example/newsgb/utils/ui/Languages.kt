package com.example.newsgb.utils.ui

import com.example.newsgb.R

enum class Languages(val nameResId: Int, val languageCode: String) {
    ENGLISH(nameResId = R.string.settings_language_english, languageCode = "en"),
    DEUTSCH(nameResId = R.string.settings_language_deutsch, languageCode = "de"),
    RUSSIAN(nameResId = R.string.settings_language_russian, languageCode = "ru")
}