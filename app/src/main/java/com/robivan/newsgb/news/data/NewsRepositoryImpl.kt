package com.robivan.newsgb.news.data

import com.robivan.newsgb._core.data.api.ApiService
import com.robivan.newsgb._core.data.api.model.ResponseDTO
import com.robivan.newsgb.news.domain.NewsRepository
import com.robivan.newsgb.utils.Constants.Companion.STATUS_OK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class NewsRepositoryImpl(
    private val apiService: ApiService
) : NewsRepository {

    override suspend fun getNewsByCategory(
        page: Int,
        category: String,
        apiKey: String
    ): Result<ResponseDTO> {
        return try {
            val response = withContext(Dispatchers.IO) {
                apiService.getNewsList(pageNumber = page, category = category, apiKey = apiKey)
            }
            when (response.status) {
                STATUS_OK -> Result.success(value = response)
                else -> Result.failure(exception = Throwable(message = "${response.errorCode}: ${response.errorMessage}"))
            }
        } catch (ex: HttpException) {
            return Result.failure(exception = ex)
        } catch (ex: IOException) {
            return Result.failure(exception = ex)
        }
    }
}