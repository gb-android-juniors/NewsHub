package com.example.newsgb._core.ui.model

/** Класс состояний экрана
 *
 * Empty - состояние отсутствуия данных (когда пришел пустой список данных)
 * Loading - состояние первоначальной загрузки
 * MoreLoading - состояние дозагрузки данных
 * Data - состояние наличия валидных данных
 * DataRefreshed - состояние наличия обновленных данных
 * Error - состояние ошибки
 *
 * */
sealed class ListViewState {
    object Empty : ListViewState()
    object Loading : ListViewState()
    object Refreshing : ListViewState()
    data class MoreLoading(val data: List<Article>) : ListViewState()
    data class Data(val data: List<Article>) : ListViewState()
    data class DataRefreshed(val data: List<Article>) : ListViewState()
    data class Error(var message: String?) : ListViewState()
}
