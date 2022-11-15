package com.robivan.newsgb.news.ui

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.robivan.newsgb.R
import com.robivan.newsgb._core.ui.BaseFragment
import com.robivan.newsgb._core.ui.adapter.NewsListAdapter
import com.robivan.newsgb._core.ui.adapter.RecyclerItemListener
import com.robivan.newsgb._core.ui.model.AdBanner
import com.robivan.newsgb._core.ui.model.Article
import com.robivan.newsgb._core.ui.model.ListViewState
import com.robivan.newsgb._core.ui.model.NewsListItem
import com.robivan.newsgb.article.ui.ArticleFragment
import com.robivan.newsgb.databinding.NewsFragmentTabItemBinding
import com.robivan.newsgb.utils.ui.Category
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class NewsTabItemFragment : BaseFragment<NewsFragmentTabItemBinding>() {

    private val category: Category?
        get() = if (Build.VERSION.SDK_INT >= 33) {
            requireArguments().getSerializable(ARG_CATEGORY, Category::class.java)
        } else @Suppress("DEPRECATION") {
            requireArguments().getSerializable(ARG_CATEGORY) as? Category
        }

    private val viewModel by viewModel<NewsViewModel> { parametersOf(category) }

    private val recyclerItemListener = object : RecyclerItemListener {
        override fun onItemClick(itemArticle: Article) {
            showFragment(fragment = ArticleFragment.newInstance(article = itemArticle))
        }

        override fun onBookmarkCheck(itemArticle: Article) {
            viewModel.checkBookmark(article = itemArticle)
        }
    }

    private val newsListAdapter: NewsListAdapter =
        NewsListAdapter(listener = recyclerItemListener, isMainNewsScreen = true)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initViewModel()
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    private fun initView() = with(binding) {
        mainRecycler.adapter = newsListAdapter
        swipeRefresh.setOnRefreshListener {
            viewModel.refreshData()
            swipeRefresh.isRefreshing = false
        }

        mainRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
                    viewModel.getMoreDataToList()
                }
            }
        })
    }

    private fun initViewModel() {
        viewModel.viewState.onEach { renderState(it) }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun initData() {
        category?.let { viewModel.getInitData() }
    }

    private fun renderState(state: ListViewState) {
        when (state) {
            is ListViewState.Empty -> {
                enableEmptyState(state = true)
                enableProgress(state = false)
                enableError(state = false)
                enableContent(state = false)
                enableRecyclerProgress(state = false)
            }
            is ListViewState.Loading -> {
                enableProgress(state = true)
                enableEmptyState(state = true)
                enableError(state = false)
                enableContent(state = false)
                enableRecyclerProgress(state = false)
            }
            is ListViewState.MoreLoading -> {
                enableProgress(state = state.mainProgressState)
                enableRecyclerProgress(state = state.recyclerProgressState)
                enableEmptyState(state = state.mainProgressState)
                enableError(state = false)
                enableContent(state = state.recyclerProgressState)
            }
            is ListViewState.Refreshing -> {
                enableProgress(state = true)
                enableEmptyState(state = false)
                enableError(state = false)
                enableContent(state = true)
                enableRecyclerProgress(state = false)
            }
            is ListViewState.Error -> {
                enableError(state = true)
                enableEmptyState(state = false)
                enableProgress(state = false)
                enableContent(state = false)
                enableRecyclerProgress(state = false)
                showToastMessage(message = state.message ?: getString(R.string.unknown_error))
            }
            is ListViewState.Data -> {
                enableContent(state = true)
                enableEmptyState(state = false)
                enableProgress(state = false)
                enableError(state = false)
                enableRecyclerProgress(state = false)
                initContent(data = state.data)
            }
        }
    }

    private fun initContent(data: List<Article>) {
        val resultData = insertAdToDataList(data)
        newsListAdapter.submitList(resultData)
    }

    private fun insertAdToDataList(data: List<Article>): List<NewsListItem> {
        val result = mutableListOf<NewsListItem>()
        var adItemId = 0
        for ((index, article) in data.withIndex()) {
            result.add(article)
            if ((index + 1) % 10 == 0) {
                result.add(AdBanner(id = adItemId++))
            }
        }
        return result
    }

    private fun enableEmptyState(state: Boolean) {
        binding.noNewsImage.isVisible = state
    }

    private fun enableProgress(state: Boolean) {
        binding.loader.isVisible = state
    }

    private fun enableRecyclerProgress(state: Boolean) {
        binding.recyclerLoader.isVisible = state
    }

    private fun enableContent(state: Boolean) {
        binding.mainRecycler.isVisible = state
    }

    private fun enableError(state: Boolean) {
        binding.error.isVisible = state
    }

    private fun showFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager
            .beginTransaction()
            .add(R.id.main_container, fragment)
            .setTransition(TRANSIT_FRAGMENT_FADE)
            .addToBackStack(ARTICLE_DETAILS_FRAGMENT_FROM_NEWS_LIST)
            .commit()
    }

    private fun showToastMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val ARG_CATEGORY = "arg_category"
        private const val ARTICLE_DETAILS_FRAGMENT_FROM_NEWS_LIST =
            "ArticleDetailsFragmentFromNewsList"

        @JvmStatic
        fun newInstance(category: Category?): NewsTabItemFragment =
            NewsTabItemFragment().apply {
                arguments = bundleOf(ARG_CATEGORY to category)
            }
    }

    override fun getViewBinding() = NewsFragmentTabItemBinding.inflate(layoutInflater)
}