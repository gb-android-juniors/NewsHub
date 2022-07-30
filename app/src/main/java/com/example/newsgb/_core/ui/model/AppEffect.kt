package com.example.newsgb._core.ui.model

/** Класс команд приложения
 *
 * LoadData - команда на загрузку данных
 * Error - команда на отображение ошибки
 *
 * */
sealed class AppEffect {
    object LoadData : AppEffect()

    // добавить команду для добавления удаления статьи из закладок
    data class CheckBookmark(val dataItem: Article) : AppEffect()
    data class Error(val message: String?) : AppEffect()

}