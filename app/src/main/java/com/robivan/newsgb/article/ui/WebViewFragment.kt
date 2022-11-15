package com.robivan.newsgb.article.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import com.robivan.newsgb._core.ui.BaseFragment
import com.robivan.newsgb.databinding.ArticleFragmentBinding
import com.robivan.newsgb.utils.Constants.Companion.URL
import com.robivan.newsgb.utils.getBaseUrl
import com.robivan.newsgb.utils.getShareNewsIntent

class WebViewFragment : BaseFragment<ArticleFragmentBinding>() {

    private val contentUrl: String by lazy { arguments?.getString(URL).toString() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMenu()
        initView()
    }

    private fun initMenu() {
        (requireActivity() as AppCompatActivity).apply {
            setSupportActionBar(binding.webViewToolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.subtitle = getBaseUrl(contentUrl)
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
        webView.apply {
            webViewClient = WebViewClient()
            @Suppress("SetJavaScriptEnabled")
            settings.javaScriptEnabled = true
            if (contentUrl.isNotEmpty()) {
                loadUrl(contentUrl)
                loader.isVisible = false
                share.setOnClickListener { getShareNewsIntent(contentUrl)?.let { startActivity(it) } }
            }
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