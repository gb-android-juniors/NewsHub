package com.example.newsgb.search.data

import com.example.newsgb._core.data.api.ApiService
import com.example.newsgb._core.data.api.model.ResponseDTO
import com.example.newsgb.search.domain.SearchRepository
import com.example.newsgb.utils.Constants.Companion.STATUS_OK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class SearchRepositoryImpl(private val apiService: ApiService) : SearchRepository {

    override suspend fun getNewsByPhrase(page: Int, phrase: String, token: String): Result<ResponseDTO> {
        return try {
            val response = withContext(Dispatchers.IO) {
                apiService.searchNewsByPhrase(pageNumber = page, phrase = phrase, apiKey = token)
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