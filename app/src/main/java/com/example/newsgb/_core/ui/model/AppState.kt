package com.example.newsgb._core.ui.model

/** Класс состояний приложения
 *
 * Empty - состояние отсутствуия данных (первоначальное или когда пришел пустой список данных)
 * Loading - состояние первоначальной загрузки
 * MoreLoading - состояние дозагрузки данных
 * Data - состояние наличия валидных данных
 * Error - состояние ошибки
 *
 * */
sealed class AppState {
    object Empty : AppState()
    object Loading : AppState()
    data class MoreLoading(val data: List<Article>) : AppState()
    // добавить событие, отражающее процесс добавления статьи в закладки
    data class BookmarkCheckedData(val data: List<Article>) : AppState()
    data class Data(val data: List<Article>) : AppState()
    data class Error(var message: String?) : AppState()
}