package com.robivan.newsgb.article.ui.webview

interface WebViewCallback {

    fun pageLoadingStarted()
    fun pageLoadingFinished()
    fun pageErrorHandled(failingUrl: String?, errorCode: Int)
}