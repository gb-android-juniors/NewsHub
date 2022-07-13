package com.example.newsgb._core.ui.model

sealed class ViewState {
    object Default : ViewState()
    object Loading : ViewState()
    data class Success(val data: List<Article>) : ViewState()
    data class Error(var message: String?) : ViewState()
}
