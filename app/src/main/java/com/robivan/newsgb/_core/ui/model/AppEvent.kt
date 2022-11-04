package com.robivan.newsgb._core.ui.model

/** Класс событий от пользователя и источника данных
 *
 * Refresh - событие от пользователя на получение данных
 * LoadMore - событие от пользователя о необходимости дозагрузки данных
 * BookmarksClear - событие от пользователя с намерением очистить все закладки
 * BookmarkCheck - событие от пользователя с намерением добавить\удалить закладку
 * DataReceived - событие от источника данных об успешном получении данных
 * ErrorReceived - событие от источника данных об получении ошибки
 *
 * */
sealed class AppEvent {
    object Refresh: AppEvent()
    object LoadMore: AppEvent()
    object BookmarksClear: AppEvent()
    data class BookmarkCheck(val article: Article) : AppEvent()
    data class DataReceived(val data: List<Article>) : AppEvent()
    data class ErrorReceived(val message: String?): AppEvent()
}
