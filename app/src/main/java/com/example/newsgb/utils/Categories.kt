package com.example.newsgb.utils

import com.example.newsgb.R

enum class Category(val categoryNameId: Int) {
    GENERAL(R.string.general_category_name_text),
    BUSINESS(R.string.business_category_name_text),
    SPORT(R.string.sports_category_name_text),
    ENTERTAINMENT(R.string.entertainment_category_name_text),
    TECHNOLOGY(R.string.technology_category_name_text),
    SCIENCE(R.string.science_category_name_text),
    HEALTH(R.string.health_category_name_text)
}