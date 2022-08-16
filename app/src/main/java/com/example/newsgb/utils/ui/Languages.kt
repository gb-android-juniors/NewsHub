package com.example.newsgb.utils.ui

import com.example.newsgb.R

enum class Languages(val nameResId: Int, val languageCode: String?) {
    DEFAULT(nameResId = R.string.settings_option_item_name_as_system, languageCode = null),
    EN(nameResId = R.string.settings_language_english, languageCode = "en"),
    DE(nameResId = R.string.settings_language_deutsch, languageCode = "de"),
    RU(nameResId = R.string.settings_language_russian, languageCode = "ru"),
    FR(nameResId = R.string.settings_language_french, languageCode = "fr")
}