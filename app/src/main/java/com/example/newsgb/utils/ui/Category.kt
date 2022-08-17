package com.example.newsgb.utils.ui

import com.example.newsgb.R

enum class Category(val apiCode: String, val nameResId: Int, val imgResId: Int) {
    GENERAL(apiCode = "general", nameResId = R.string.main_category_name_text, imgResId = R.drawable.category_general),
    BUSINESS(apiCode = "business", nameResId = R.string.business_category_name_text, imgResId = R.drawable.category_business),
    SPORT(apiCode = "sport", nameResId = R.string.sports_category_name_text, imgResId = R.drawable.category_sport),
    ENTERTAINMENT(apiCode = "entertainment", nameResId = R.string.entertainment_category_name_text, imgResId = R.drawable.category_entertainment),
    TECHNOLOGY(apiCode = "technology", nameResId = R.string.technology_category_name_text, imgResId = R.drawable.category_technology),
    SCIENCE(apiCode = "science", nameResId = R.string.science_category_name_text, imgResId = R.drawable.category_science),
    HEALTH(apiCode = "health", nameResId = R.string.health_category_name_text, imgResId = R.drawable.category_health),
    BOOKMARKS(apiCode = "", nameResId = R.string.bookmarks, imgResId = R.drawable.category_general),
    SEARCH(apiCode = "", nameResId = R.string.search, imgResId = R.drawable.category_general)
}