package com.example.newsgb._core.ui.model

/** Класс состояний экрана
 *
 * Empty - состояние отсутствуия данных (первоначальное или когда пришел пустой список данных)
 * Loading - состояние первоначальной загрузки
 * MoreLoading - состояние дозагрузки данных
 * Data - состояние наличия валидных данных
 * Error - состояние ошибки
 *
 * */
sealed class ViewState {
    object Empty : ViewState()
    object Loading : ViewState()
    data class MoreLoading(val data: List<Article>) : ViewState()
    data class Data(val data: List<Article>) : ViewState()
    data class Error(var message: String?) : ViewState()
}
