package com.robivan.newsgb._core.data.api.model

import com.robivan.newsgb.BuildConfig

object ApiKey {
    private const val DEFAULT_TOKEN_INDEX = 0
    private var index = DEFAULT_TOKEN_INDEX
    private val tokensList = BuildConfig.api_token.split("@").shuffled()
    private var validKey: String = tokensList[index]

    fun getKey(): String = validKey

    fun nextKey(): String? = if (++index < tokensList.size) {
        validKey = tokensList[index]
        getKey()
    } else null
}