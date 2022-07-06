package com.example.newsgb._core.di

import com.example.newsgb._core.data.remote.ApiService
import org.koin.dsl.module

val appModule = module {
    single { ApiService.getInstance() }
}