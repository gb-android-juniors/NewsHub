package com.example.newsgb._core.ui.model



/** Класс состояний экрана со списком'
 *
 * Empty - состояние отсутствуия данных (первоначальное или когда пришел пустой список данных)
 * Loading - состояние первоначальной загрузки
 * Data - состояние наличия валидных данных
 * Error - состояние ошибки
 *
 * */
sealed class ListViewState {
    object Empty : ListViewState()
    object Loading : ListViewState()
    data class Data(val data: List<Article>) : ListViewState()
    data class Error(var message: String?) : ListViewState()
}
