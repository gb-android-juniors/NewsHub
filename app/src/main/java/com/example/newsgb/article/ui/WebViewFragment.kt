package com.example.newsgb.article.ui

import android.os.Bundle
import android.view.*
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.example.newsgb._core.ui.BaseFragment
import com.example.newsgb.databinding.ArticleFragmentBinding
import com.example.newsgb.utils.Constants.Companion.URL

class WebViewFragment : BaseFragment<ArticleFragmentBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMenu()
        initView()
    }

    /** метод инициализации меню в апбаре экрана */
    private fun initMenu() {
        (requireActivity() as AppCompatActivity).apply {
            /** привязываемся к тулбару в разметке */
            setSupportActionBar(binding.webViewToolbar)
            /** подключаем к меню системную кнопку "назад" */
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = ""
        }
        /** добавляем и инициализируем элементы меню */
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {}

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    /** инициализируем системную кнопку "назад" */
                    android.R.id.home -> {
                        requireActivity().onBackPressed()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun initView(){
        val url = arguments?.getString(URL)
        binding.webView.apply {
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
            if (url != null) {
                loadUrl(url)
                binding.loader.isVisible = false
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