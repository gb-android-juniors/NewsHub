package com.example.newsgb.news.domain

interface NewsRepository {
    fun <T> getNewsByCategory(category: String, countryCode: String): Result<T>
}