package com.example.newsgb.utils.ui

     fun setApiCode(countryCode: String): String {
        return when (countryCode) {
            "Russia" -> "ru"
            "Germany" -> "de"
            "England" -> "en"
            else -> "ru"
        }
    }
