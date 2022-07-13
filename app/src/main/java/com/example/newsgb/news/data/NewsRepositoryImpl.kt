package com.example.newsgb.news.data

import com.example.newsgb._core.data.api.ApiService
import com.example.newsgb._core.data.api.model.ResponseDTO
import com.example.newsgb.news.domain.NewsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class NewsRepositoryImpl(
    private val apiService: ApiService
) : NewsRepository {

    override suspend fun getBreakingNews(page: Int): Result<ResponseDTO> {
        return try {
            val response = withContext(Dispatchers.IO) {
                apiService.getBreakingNews(pageNumber = page)
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

    override suspend fun getNewsByCategory(
        category: String,
        countryCode: String,
    ): Result<ResponseDTO> {
        TODO("Not yet implemented")
    }

    companion object {
        private const val STATUS_OK = "ok"
    }
}