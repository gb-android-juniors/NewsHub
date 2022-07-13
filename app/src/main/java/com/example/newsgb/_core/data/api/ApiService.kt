package com.example.newsgb._core.data.api

import com.example.newsgb._core.data.api.model.ResponseDTO
import com.example.newsgb.utils.Constants
import com.example.newsgb.utils.Constants.Companion.API_KEY
import com.example.newsgb.utils.Constants.Companion.TOP_HEADLINES_ENDPOINT
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET(TOP_HEADLINES_ENDPOINT)
    suspend fun getBreakingNews(
        @Query("country") countryCode: String = "ru",
        @Query("page") pageNumber: Int = 1,
        @Query("apiKey") apiKey: String = API_KEY
    ): ResponseDTO

    companion object {
        fun getInstance(): ApiService {
            return Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(OkHttpClient.Builder().build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build()
                .create(ApiService::class.java)
        }
    }
}

