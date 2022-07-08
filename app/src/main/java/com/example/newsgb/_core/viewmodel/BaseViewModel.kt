package com.example.newsgb._core.viewmodel

import androidx.lifecycle.ViewModel
import com.example.newsgb._core.data.AppState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow

/**Создадим базовую ViewModel, куда вынесем общий для всех функционал*/
abstract class BaseViewModel<T : AppState>(
    protected open val _stateFlow: MutableStateFlow<AppState> = MutableStateFlow(AppState.Success(emptyList()))
) : ViewModel() {
    /** Объявляем свой собственный скоуп
     *  В качестве аргумента передается CoroutineContext, который мы составляем через "+"
     *  из трех частей:
     *  - Dispatchers.Main говорит, что результат работы предназначен для основного потока;
     *  - SupervisorJob() позволяет всем дочерним корутинам выполняться независимо, то есть,
     *  если какая-то корутина упадёт с ошибкой, остальные будут выполнены нормально;
     *  - CoroutineExceptionHandler позволяет перехватывать и отрабатывать ошибки и краши*/

    protected val viewModelCoroutineScope = CoroutineScope(
        Dispatchers.Main
                + SupervisorJob()
                + CoroutineExceptionHandler { _, throwable -> handleError(throwable) }
    )
    /**обрабатываем ошибки в конкретной имплементации базовой ВьюМодели*/
    abstract fun handleError(error: Throwable)
    /** Метод, благодаря которому Activity подписывается на изменение данных,
    возвращает LiveData, через которую и передаются данные*/
    abstract fun getData()
    /**Единственный метод класса ViewModel, который вызывается перед уничтожением Activity*/
    override fun onCleared() {
        super.onCleared()
        cancelJob()
    }
    /**Завершаем все незавершённые корутины, потому что пользователь закрыл экран*/
    protected fun cancelJob() {
        viewModelCoroutineScope.coroutineContext.cancelChildren()
    }
}