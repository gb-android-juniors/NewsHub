package com.example.newsgb._core.state

import com.example.newsgb._core.data.api.NewsResponse

sealed class ViewState {
    object DefaultState : ViewState()
    object LoadingState : ViewState()
    class SuccessState(val newsResponse: NewsResponse) : ViewState()
    class ErrorState(var message: String?) : ViewState()
}
