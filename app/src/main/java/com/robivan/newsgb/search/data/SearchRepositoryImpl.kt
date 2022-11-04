package com.robivan.newsgb.search.data

import com.robivan.newsgb._core.data.api.ApiService
import com.robivan.newsgb._core.data.api.model.ResponseDTO
import com.robivan.newsgb.search.domain.SearchRepository
import com.robivan.newsgb.utils.Constants.Companion.STATUS_OK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class SearchRepositoryImpl(private val apiService: ApiService) : SearchRepository {

    override suspend fun getNewsByPhrase(
        page: Int,
        phrase: String,
        apiKey: String
    ): Result<ResponseDTO> {
        return try {
            val response = withContext(Dispatchers.IO) {
                apiService.searchNewsByPhrase(pageNumber = page, phrase = phrase, apiKey = apiKey)
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