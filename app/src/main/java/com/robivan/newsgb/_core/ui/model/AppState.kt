package com.robivan.newsgb._core.ui.model

/** Класс состояний приложения
 *
 * Empty - состояние отсутствуия данных (первоначальное или когда пришел пустой список данных)
 * Loading - состояние первоначальной загрузки
 * MoreLoading - состояние дозагрузки данных
 * Refreshing - состояние обновления данных (на экране прогресс плюс пока еще не обновленные данные)
 * BookmarkChecking - состояние добавления\удаления статьи в закладки
 * BookmarksClearing - состояние очистки закладок
 * Data - состояние наличия валидных данных
 * Error - состояние ошибки
 *
 * */
sealed class AppState {
    object Empty : AppState()
    object Loading : AppState()
    data class MoreLoading(val data: List<Article>) : AppState()
    data class Refreshing(val data: List<Article>) : AppState()
    data class BookmarkChecking(val data: List<Article>) : AppState()
    data class BookmarksClearing(val data: List<Article>) : AppState()
    data class Data(val data: List<Article>) : AppState()
    data class Error(var message: String?) : AppState()
}