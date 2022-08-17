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

    /**
     * Метод обработки событий
     * При событии Refresh переключаемся в состояние загрузки (AppState.Loading)
     * При событии ErrorReceived переключаемся в состояние ошибки (AppState.Error) и передаем сообщение об ошибке
     * При событии NewDataReceived переключаемся в состояние успеха (AppState.Success) и передаем новые полученные данные
     * При событии MoreDataReceived переключаемся в состояние успеха (AppState.Success) прибавляя к старым данным вновь полученные данные
     * */
    fun dispatch(event: AppEvent) {
        val oldState = storeState.value
        when(event) {
            AppEvent.Refresh -> _storeState.value = AppState.Loading
            is AppEvent.ErrorReceived -> _storeState.value = AppState.Error(message = event.message)
            is AppEvent.NewDataReceived -> _storeState.value = AppState.Success(data = event.data)
            is AppEvent.MoreDataReceived -> {
                if(oldState is AppState.Success) {
                    _storeState.value = AppState.Success(data = oldState.data + event.data)
                }
            }
        }
    }
}