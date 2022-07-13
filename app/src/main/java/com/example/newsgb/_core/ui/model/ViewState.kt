package com.example.newsgb._core.ui.model

/** Класс состояний экрана
 *
 * Default - состояние не определено
 * Loading - состояние загрузки
 * Success - состояние наличия валидных данных
 * Error - состояние ошибки
 *
 * */
sealed class ViewState {
    object Default : ViewState()
    object Loading : ViewState()
    data class Success(val data: List<Article>) : ViewState()
    data class Error(var message: String?) : ViewState()
}
