package com.example.newsgb._core.data

import com.example.newsgb._core.data.db.entity.ArticleEntity

sealed class AppState {
    data class Success(val data: List<ArticleEntity>?) : AppState()
    data class Error(val error: Throwable) : AppState()
    data class Loading(val progress: Int?) : AppState()
}
