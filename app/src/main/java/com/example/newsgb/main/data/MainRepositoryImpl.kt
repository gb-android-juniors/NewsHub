package com.example.newsgb.main.data

import com.example.newsgb._core.data.api.ApiService
import com.example.newsgb._core.data.api.model.ResponseDTO
import com.example.newsgb.main.domain.MainRepository
import com.example.newsgb.utils.PrivateSharedPreferences
import com.example.newsgb.utils.ui.setApiCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class MainRepositoryImpl(
    private val apiService: ApiService
) : MainRepository {




    override suspend fun getBreakingNews(page: Int): Result<ResponseDTO> {
        return try {
            val response = withContext(Dispatchers.IO) {
                apiService.getNewsList(pageNumber = page/*, countryCode =
                setApiCode(PrivateSharedPreferences(context = this).read())*/)
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

    companion object {
        private const val STATUS_OK = "ok"
    }
}