package com.example.newsgb.utils.ui

import com.example.newsgb.R

enum class Category(val apiCode: String, val nameResId: Int, val imgResId: Int) {
    GENERAL(apiCode = "general", nameResId = R.string.main_category_name_text, imgResId = R.drawable.general),
    BUSINESS(apiCode = "business", nameResId = R.string.business_category_name_text, imgResId = R.drawable.business),
    SPORT(apiCode = "sport", nameResId = R.string.sports_category_name_text, imgResId = R.drawable.sport),
    ENTERTAINMENT(apiCode = "entertainment", nameResId = R.string.entertainment_category_name_text, imgResId = R.drawable.entertainment),
    TECHNOLOGY(apiCode = "technology", nameResId = R.string.technology_category_name_text, imgResId = R.drawable.technology),
    SCIENCE(apiCode = "science", nameResId = R.string.science_category_name_text, imgResId = R.drawable.science),
    HEALTH(apiCode = "health", nameResId = R.string.health_category_name_text, imgResId = R.drawable.health)
}