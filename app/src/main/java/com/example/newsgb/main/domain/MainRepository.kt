package com.example.newsgb.main.domain

import com.example.newsgb._core.data.api.model.ResponseDTO

interface MainRepository {
   suspend fun getBreakingNews(page: Int, apiKey: String): Result<ResponseDTO>
}