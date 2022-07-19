package com.example.newsgb.utils.ui

import com.example.newsgb.R

enum class Category(val apiCode: String, val nameResId: Int) {
    GENERAL(apiCode = "general", nameResId = R.string.main_category_name_text),
    BUSINESS(apiCode = "business", nameResId = R.string.business_category_name_text),
    SPORT(apiCode = "sport", nameResId = R.string.sports_category_name_text),
    ENTERTAINMENT(apiCode = "entertainment", nameResId = R.string.entertainment_category_name_text),
    TECHNOLOGY(apiCode = "technology", nameResId = R.string.technology_category_name_text),
    SCIENCE(apiCode = "science", nameResId = R.string.science_category_name_text),
    HEALTH(apiCode = "health", nameResId = R.string.health_category_name_text)
}