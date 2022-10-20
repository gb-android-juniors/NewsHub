package com.example.newsgb._core.ui.model

/** Класс команд приложения
 *
 * LoadData - команда на загрузку данных
 * CheckBookmark - команда на сохранение(удаление) статьи в закладки
 * Error - команда на отображение ошибки
 *
 * */
sealed class AppEffect {
    data class LoadData(val isRefreshing: Boolean) : AppEffect()
    object Refresh : AppEffect()
    object ClearBookmarks : AppEffect()
    data class CheckBookmark(val dataItem: Article) : AppEffect()
    data class Error(val message: String?) : AppEffect()
}