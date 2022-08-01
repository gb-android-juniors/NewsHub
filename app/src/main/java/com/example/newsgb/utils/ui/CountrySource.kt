package com.example.newsgb.utils.ui

     fun setApiCode(countryCode: String): String {
        return when (countryCode) {
            "Russia", "Россия", "Rusland" -> "ru"
            "Germany", "Германия", "Deutschland" -> "de"
            "England", "Англия" -> "en"
            else -> "ru"
        }
    }
