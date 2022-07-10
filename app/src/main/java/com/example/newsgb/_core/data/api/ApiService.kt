package com.example.newsgb._core.data.api

import com.example.newsgb._core.utils.Constants.Companion.API_KEY
import com.example.newsgb._core.utils.Constants.Companion.TOP_HEADLINES
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET(TOP_HEADLINES)
    suspend fun getBreakingNews(
        @Query("country")
        countryCode: String = "ru",
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apiKey: String = API_KEY,
    ): NewsResponse
}

