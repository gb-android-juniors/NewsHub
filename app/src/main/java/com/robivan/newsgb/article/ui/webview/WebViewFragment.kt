package com.robivan.newsgb.article.ui.webview

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import com.robivan.newsgb.R
import com.robivan.newsgb._core.ui.BaseFragment
import com.robivan.newsgb.databinding.ArticleFragmentBinding
import com.robivan.newsgb.utils.Constants.Companion.URL
import com.robivan.newsgb.utils.getBaseUrl
import com.robivan.newsgb.utils.getShareNewsIntent

class WebViewFragment : BaseFragment<ArticleFragmentBinding>() {

    private val contentUrl: String by lazy { arguments?.getString(URL).toString() }
    private val webViewCallback = object : WebViewCallback {

        override fun pageLoadingStarted() {
            showLoading()
        }

        override fun pageLoadingFinished() {
            showWebContent()
        }

        override fun pageErrorHandled(failingUrl: String?, errorCode: Int) {
            handleError(failingUrl, errorCode)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMenu()
        initView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.webView.destroy()
    }

    private fun initMenu() {
        (requireActivity() as AppCompatActivity).apply {
            setSupportActionBar(binding.webViewToolbar)
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                subtitle = getBaseUrl(contentUrl)
            }
        }
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {}

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    android.R.id.home -> {
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun initView() = with(binding) {
        if (contentUrl.isNotEmpty()) {
            share.setOnClickListener { getShareNewsIntent(contentUrl)?.let { startActivity(it) } }
            webView.apply {
                @Suppress("SetJavaScriptEnabled")
                settings.javaScriptEnabled = true
                webViewClient = ArticleWebViewClient(callback = webViewCallback)
                loadUrl(contentUrl)
            }
        } else {
            showError(getString(R.string.unknown_error))
        }
    }

    private fun handleError(failingUrl: String?, errorCode: Int) {
        failingUrl?.let { url ->
            if (url == contentUrl) {
                val errorMessage = when (errorCode) {
                    WebViewClient.ERROR_TIMEOUT -> getString(R.string.error_timeout)
                    WebViewClient.ERROR_TOO_MANY_REQUESTS -> getString(R.string.error_too_many_requests)
                    WebViewClient.ERROR_CONNECT -> getString(R.string.error_connect)
                    WebViewClient.ERROR_FAILED_SSL_HANDSHAKE -> getString(R.string.error_failed_ssl_handshake)
                    WebViewClient.ERROR_HOST_LOOKUP -> getString(R.string.error_host_lookup)
                    WebViewClient.ERROR_REDIRECT_LOOP -> getString(R.string.error_redirect_loop)
                    WebViewClient.ERROR_UNSUPPORTED_SCHEME -> getString(R.string.error_unsupported_scheme)
                    WebViewClient.ERROR_IO -> getString(R.string.error_io)
                    else -> null
                }
                errorMessage?.let {
                    showError(message = it)
                }
            }
        }
    }

    private fun showWebContent() = with(binding) {
        loader.isVisible = false
        webView.isVisible = true
        errorMessage.isVisible = false
    }

    private fun showLoading() = with(binding) {
        loader.isVisible = true
        webView.isVisible = true
        errorMessage.isVisible = false
    }

    private fun showError(message: String) = with(binding) {
        loader.isVisible = false
        webView.apply {
            isVisible = false
            removeAllViews()
            destroy()
        }
        errorMessage.apply {
            isVisible = true
            text = message
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(url: String) =
            WebViewFragment().apply {
                arguments = Bundle().apply {
                    putString(URL, url)
                }
            }
    }

    override fun getViewBinding() = ArticleFragmentBinding.inflate(layoutInflater)
}