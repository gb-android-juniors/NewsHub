package com.robivan.newsgb.main.domain

import com.robivan.newsgb._core.data.api.model.ResponseDTO

interface MainRepository {
   suspend fun getBreakingNews(page: Int, apiKey: String): Result<ResponseDTO>
}