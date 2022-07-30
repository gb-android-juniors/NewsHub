package com.example.newsgb._core.ui.model

/** Класс состояний экрана
 *
 * Empty - состояние отсутствуия данных (когда пришел пустой список данных)
 * Loading - состояние первоначальной загрузки
 * MoreLoading - состояние дозагрузки данных
 * Data - состояние наличия валидных данных
 * Error - состояние ошибки
 *
 * */
sealed class ListViewState {
    object Empty : ListViewState()
    object Loading : ListViewState()
    data class RefreshData(val data: List<Article>) : ListViewState()
    data class MoreLoading(val data: List<Article>) : ListViewState()
    data class Data(val data: List<Article>) : ListViewState()
    data class Error(var message: String?) : ListViewState()
}
