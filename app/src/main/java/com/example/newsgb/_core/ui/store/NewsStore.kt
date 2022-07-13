package com.example.newsgb._core.ui.store

import com.example.newsgb._core.ui.model.AppEvent
import com.example.newsgb._core.ui.model.AppState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Класс-хранилище текущего состояния приложения (вместе с данными)
 *
 * Создается в активити и, соответственно, имеет жизненный цикл активитии.
 * Экземпляр класса, созданный в активити, переается фрагментам,
 * таким образом фрагменты всегда знают текущее состояние приложения, даже после пересоздания,
 * и могут в любой момент подписаться на него
 *
 * */

class NewsStore : CoroutineScope by MainScope() {

    private val _storeState = MutableStateFlow<AppState>(AppState.Default)
    val storeState: StateFlow<AppState> = _storeState.asStateFlow()

    fun dispatch(event: AppEvent) {
        when(event) {
            AppEvent.Refresh -> _storeState.value = AppState.Loading
            is AppEvent.DataReceived -> _storeState.value = AppState.Success(data = event.data)
            is AppEvent.ErrorReceived -> _storeState.value = AppState.Error(message = event.message)
        }
    }
}