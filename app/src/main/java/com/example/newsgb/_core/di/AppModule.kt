package com.example.newsgb._core.di

import androidx.room.Room
import com.example.newsgb._core.data.api.ApiService
import com.example.newsgb._core.data.db.BookmarkDataBase
import com.example.newsgb._core.data.db.DbUtils.DB_NAME
import com.example.newsgb._core.utils.Constants.Companion.BASE_URL
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    single { provideApiService() }
    single { Room.databaseBuilder(androidApplication(), BookmarkDataBase::class.java, DB_NAME).build() }
    single { get<BookmarkDataBase>().bookmarkDao() }
}

private fun provideApiService() : ApiService {
    return Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(OkHttpClient.Builder().build())
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()
        .create(ApiService::class.java)
}

