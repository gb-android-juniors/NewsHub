package com.robivan.newsgb.article.ui

import android.os.Build.VERSION
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.robivan.newsgb.R
import com.robivan.newsgb._core.ui.BaseFragment
import com.robivan.newsgb._core.ui.model.Article
import com.robivan.newsgb._core.ui.model.ItemViewState
import com.robivan.newsgb.databinding.DetailsFragmentBinding
import com.robivan.newsgb.utils.formatApiStringToDate
import com.robivan.newsgb.utils.getShareNewsIntent
import com.robivan.newsgb.utils.setBookmarkIconColor
import com.yandex.mobile.ads.banner.AdSize
import com.yandex.mobile.ads.common.AdRequest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class ArticleFragment : BaseFragment<DetailsFragmentBinding>() {

    private val article: Article by lazy {
        if (VERSION.SDK_INT >= 33) {
            requireArguments().getParcelable(ARG_ARTICLE_DATA, Article::class.java) as Article
        } else @Suppress("DEPRECATION") {
            requireArguments().getParcelable<Article>(ARG_ARTICLE_DATA) as Article
        }

    }

    private val viewModel: ArticleViewModel by viewModel { parametersOf(article) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMenu()
        initViewModel()
        initAdBanner()
    }

    private fun initMenu() {
        (requireActivity() as AppCompatActivity).apply {
            setSupportActionBar(binding.detailsToolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
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

    private fun initViewModel() {
        viewModel.viewState.onEach { renderState(it) }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun initAdBanner() = with(binding) {
        bannerAdView.setAdUnitId(BANNER_AD_ID)
        bannerAdView.setAdSize(AdSize.flexibleSize(FLEX_BANNER_WIDTH, FLEX_BANNER_HEIGHT))
        val adRequest = AdRequest.Builder().build()
        bannerAdView.loadAd(adRequest)
    }

    private fun renderState(state: ItemViewState) {
        when (state) {
            is ItemViewState.Loading -> {
                enableProgress(state = true)
                enableError(state = false)
                enableContent(state = false)
            }
            is ItemViewState.Error -> {
                enableError(state = true)
                enableProgress(state = false)
                enableContent(state = false)
                showToastMessage(state.message ?: getString(R.string.unknown_error))
            }
            is ItemViewState.Data -> {
                enableContent(state = true)
                enableProgress(state = false)
                enableError(state = false)
                initContent(state.data)
            }
            is ItemViewState.Refreshing -> {
                enableProgress(state = true)
                enableContent(state = true)
                enableError(state = false)
            }
            else -> {}
        }
    }

    private fun initContent(article: Article) = with(binding) {
        share.setOnClickListener { getShareNewsIntent(article.contentUrl)?.let { startActivity(it) } }
        articleHeaderText.text = article.title
        articleSourceName.text = article.sourceName
        publicationDate.text = article.publishedDate.formatApiStringToDate()
        descriptionText.text = article.description
        detailsButton.setOnClickListener {
            showArticleFragment(WebViewFragment.newInstance(article.contentUrl))
        }
        bookmarkIcon.apply {
            setBookmarkIconColor(
                context = requireContext(),
                bookmarkImage = this,
                isChecked = article.isChecked
            )
            setOnClickListener { viewModel.checkBookmark(article = article) }
        }

        Glide.with(articleImage)
            .load(article.imageUrl)
            .error(article.category.imgResId)
            .into(articleImage)
    }

    private fun enableProgress(state: Boolean) {
        binding.loader.isVisible = state
    }

    private fun enableContent(state: Boolean) {
        binding.content.isVisible = state
    }

    private fun enableError(state: Boolean) {
        binding.error.isVisible = state
    }

    private fun showToastMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showArticleFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .add(R.id.main_container, fragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .addToBackStack(null)
            .commit()
    }

    companion object {
        private const val ARG_ARTICLE_DATA = "arg_article_data"
        private const val BANNER_AD_ID = "R-M-1994249-1"
        private const val FLEX_BANNER_WIDTH = 300
        private const val FLEX_BANNER_HEIGHT = 160


        @JvmStatic
        fun newInstance(article: Article): ArticleFragment =
            ArticleFragment().apply {
                arguments = bundleOf(ARG_ARTICLE_DATA to article)
            }
    }

    override fun getViewBinding() = DetailsFragmentBinding.inflate(layoutInflater)
}