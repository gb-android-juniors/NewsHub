package com.example.newsgb._core.ui.model

import com.example.newsgb._core.data.api.model.ArticleDTO

/** Класс состояний приложения
 *
 * Default - состояние не определено
 * Loading - состояние загрузки
 * Success - состояние наличия валидных данных
 * Error - состояние ошибки
 *
 * */
sealed class AppState {
    object Default : AppState()
    object Loading : AppState()
    data class Success(val data: List<Article>) : AppState()
    data class Error(var message: String?) : AppState()
}