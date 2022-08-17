package com.example.newsgb._core.ui.model

import com.example.newsgb._core.data.api.model.ArticleDTO

/** Класс событий от пользователя и источника данных
 *
 * Refresh - событие от пользователя на получение данных
 * DataReceived - событие от источника данных об успешном получении данных
 * ErrorReceived - событие от источника данных об получении ошибки
 *
 * */
sealed class AppEvent {
    object Refresh: AppEvent()
    data class NewDataReceived(val data: List<Article>) : AppEvent()
    data class MoreDataReceived(val data: List<Article>) : AppEvent()
    data class ErrorReceived(val message: String?): AppEvent()
}
