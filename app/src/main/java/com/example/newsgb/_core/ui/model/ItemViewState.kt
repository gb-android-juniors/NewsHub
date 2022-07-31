package com.example.newsgb._core.ui.model

/** Класс состояний экрана
 *
 * Empty - состояние отсутствуия данных (когда пришел пустой список данных)
 * Loading - состояние первоначальной загрузки
 * Data - состояние наличия валидных данных
 * DataRefreshed - состояние наличия обновленных данных
 * Error - состояние ошибки
 *
 * */
sealed class ItemViewState {
    object Empty : ItemViewState()
    object Loading : ItemViewState()
    data class Data(val data: Article) : ItemViewState()
    data class DataRefreshed(val data: Article) : ItemViewState()
    data class Error(var message: String?) : ItemViewState()
}
