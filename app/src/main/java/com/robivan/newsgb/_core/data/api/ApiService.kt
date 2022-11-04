package com.robivan.newsgb._core.data.api

import com.robivan.newsgb.App
import com.robivan.newsgb._core.data.api.model.ResponseDTO
import com.robivan.newsgb.utils.Constants
import com.robivan.newsgb.utils.Constants.Companion.INITIAL_PAGE
import com.robivan.newsgb.utils.Constants.Companion.SEARCH_ENDPOINT
import com.robivan.newsgb.utils.Constants.Companion.TOP_HEADLINES_ENDPOINT
import com.robivan.newsgb.utils.verifySearchLanguage
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET(TOP_HEADLINES_ENDPOINT)
    suspend fun getNewsList(
        @Query("country") countryCode: String = App.countryCode,
        @Query("category") category: String = DEFAULT_CATEGORY,
        @Query("page") pageNumber: Int = INITIAL_PAGE,
        @Query("apiKey") apiKey: String,
        @Query("pageSize") pageSize: Int = Constants.DEFAULT_API_PAGE_SIZE
    ): ResponseDTO

    @GET(SEARCH_ENDPOINT)
    suspend fun searchNewsByPhrase(
        @Query("q") phrase: String,
        @Query("page") pageNumber: Int = INITIAL_PAGE,
        @Query("apiKey") apiKey: String,
        @Query("language") language: String = verifySearchLanguage(App.countryCode),
        @Query("pageSize") pageSize: Int = Constants.DEFAULT_API_PAGE_SIZE
    ): ResponseDTO

    companion object {
        fun getInstance(): com.robivan.newsgb._core.data.api.ApiService {
            return Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(OkHttpClient.Builder().build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build()
                .create(com.robivan.newsgb._core.data.api.ApiService::class.java)
        }

        private const val DEFAULT_CATEGORY = "general"
    }
}

