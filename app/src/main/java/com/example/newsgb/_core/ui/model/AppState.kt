package com.example.newsgb._core.ui.model

/** Класс состояний приложения
 *
 * Empty - состояние отсутствуия данных (первоначальное или когда пришел пустой список данных)
 * Loading - состояние первоначальной загрузки
 * MoreLoading - состояние дозагрузки данных
 * BookmarkChecking - состояние добавления\удаления статьи в закладки
 * Data - состояние наличия валидных данных
 * Error - состояние ошибки
 *
 * */
sealed class AppState {
    object Empty : AppState()
    object Loading : AppState()
    data class MoreLoading(val data: List<Article>) : AppState()
    data class BookmarkChecking(val data: List<Article>) : AppState()
    data class Data(val data: List<Article>) : AppState()
    data class Error(var message: String?) : AppState()
}