package com.example.newsgb.utils.ui

import com.example.newsgb.R

enum class Countries(val nameResId: Int, val countryCode: String) {
    RUSSIA(nameResId = R.string.country_russia, countryCode = "ru"),
    GERMANY(nameResId = R.string.country_germany, countryCode = "de"),
    AUSTRALIA(nameResId = R.string.country_australia, countryCode = "au")
}