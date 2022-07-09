package com.example.newsgb.news.domain

import com.example.newsgb._core.data.api.NewsResponse

interface NewsRepository {
   suspend fun getBreakingNews(page: Int, countryCode: String): Result<NewsResponse>
   suspend fun getNewsByCategory(category: String, countryCode: String): Result<NewsResponse>
}