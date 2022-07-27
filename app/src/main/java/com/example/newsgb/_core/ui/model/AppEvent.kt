package com.example.newsgb._core.ui.model

/** Класс событий от пользователя и источника данных
 *
 * Refresh - событие от пользователя на получение данных
 * DataReceived - событие от источника данных об успешном получении данных
 * ErrorReceived - событие от источника данных об получении ошибки
 *
 * */
sealed class AppEvent {
    object Refresh: AppEvent()
    object LoadMore: AppEvent()
    // добавить событие, когда юзер помещает статью в закладки
    data class DataReceived(val data: List<Article>) : AppEvent()
    data class ErrorReceived(val message: String?): AppEvent()
}
