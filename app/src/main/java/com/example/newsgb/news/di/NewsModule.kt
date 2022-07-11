package com.example.newsgb.news.di

import com.example.newsgb._core.data.api.ApiService
import com.example.newsgb.news.domain.NewsRepository
import com.example.newsgb.news.domain.NewsRepositoryImpl
import org.koin.dsl.module

val newsModule = module {

    single { provideNewsRepository(apiService = get()) }

}

private fun provideNewsRepository(apiService: ApiService) : NewsRepository{
    return NewsRepositoryImpl(apiService)
}