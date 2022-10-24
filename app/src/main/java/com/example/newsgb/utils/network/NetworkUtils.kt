package com.example.newsgb.utils.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData

/** В конструктор передаём контекст, наследуемся от LiveData, которая возвращает булево значение*/
class OnlineLiveData(context: Context) : LiveData<Boolean>() {
    // Массив из доступных сетей
    private val availableNetworks = mutableSetOf<Network>()

    // Получаем connectivityManager
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    // Создаём запрос
    private val request: NetworkRequest = NetworkRequest.Builder().build()

    /** Создаём колбэк, который уведомляет нас о появлении или исчезновении связи с сетью */
    private val callback = object : ConnectivityManager.NetworkCallback() {
        /** Если соединение потеряно, убираем его из массива и уведомляем подписчиков о наличии связи */
        override fun onLost(network: Network) {
            availableNetworks.remove(network)
            update(availableNetworks.isNotEmpty())
        }

        /** Если соединение восстановлено, добавляем его в массив и уведомляем подписчиков о наличии сети */
        override fun onAvailable(network: Network) {
            availableNetworks.add(network)
            update(availableNetworks.isNotEmpty())
        }
    }

    /** Регистрируем колбэк, если компонент, подписанный на LiveData, активен */
    override fun onActive() {
        connectivityManager.registerNetworkCallback(request, callback)
    }

    /** Убираем колбэк, если компонент, подписанный на LiveData, неактивен */
    override fun onInactive() {
        connectivityManager.unregisterNetworkCallback(callback)
    }

    /** Уведомляем подписчиков о наличии/отсутствии связи с сетью */
    private fun update(online: Boolean) {
        if (online != value) {
            postValue(online)
        }
    }
}

fun Fragment.isNetworkAvailable(): Boolean {
    val connectivityManager =
        (requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                return true
            }
        }
        return false
    } else @Suppress("DEPRECATION") {
        return connectivityManager.activeNetworkInfo?.isConnected == true
    }
}