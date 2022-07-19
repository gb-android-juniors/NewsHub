package com.example.newsgb.article.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.newsgb.databinding.ArticleFragmentBinding
import com.example.newsgb.utils.Constants.Companion.URL

class ArticleFragment : Fragment() {

    private var _binding: ArticleFragmentBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ArticleFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initWebView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initWebView(){
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
            ArticleFragment().apply {
                arguments = Bundle().apply {
                    putString(URL, url)
                }
            }
    }

}