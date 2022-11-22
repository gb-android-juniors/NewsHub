package com.robivan.newsgb.article.ui.webview

import android.graphics.Bitmap
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.webkit.*
import androidx.annotation.RequiresApi

class ArticleWebViewClient(private val callback: WebViewCallback) : WebViewClient() {

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        callback.pageLoadingStarted()
        if (view?.progress!! > MIN_PROGRESS)
            callback.pageLoadingFinished()
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        callback.pageLoadingFinished()
    }


    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onReceivedError(
        view: WebView?,
        errorCode: Int,
        description: String?,
        failingUrl: String?
    ) {
        if (VERSION.SDK_INT < 23) {
            super.onReceivedError(view, errorCode, description, failingUrl)
            callback.pageErrorHandled(failingUrl, errorCode)
        }
    }

    @RequiresApi(VERSION_CODES.M)
    override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) {
        if (VERSION.SDK_INT >= 23) {
            super.onReceivedError(view, request, error)
            error?.errorCode?.let { callback.pageErrorHandled(request?.url.toString(), it) }
        }
    }

    companion object {
        const val MIN_PROGRESS = 20
    }
}