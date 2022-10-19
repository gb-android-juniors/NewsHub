package com.example.newsgb.news.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE
import androidx.lifecycle.lifecycleScope
import com.example.newsgb.R
import com.example.newsgb._core.ui.BaseFragment
import com.example.newsgb._core.ui.adapter.NewsListAdapter
import com.example.newsgb._core.ui.adapter.RecyclerItemListener
import com.example.newsgb._core.ui.model.Article
import com.example.newsgb._core.ui.model.ListViewState
import com.example.newsgb.article.ui.ArticleFragment
import com.example.newsgb.databinding.NewsFragmentTabItemBinding
import com.example.newsgb.utils.ui.Category
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class NewsTabItemFragment : BaseFragment<NewsFragmentTabItemBinding>() {

    /** вытягиваем из аргументов переданную категорию новостей */
    private val category: Category?
        get() = requireArguments().getSerializable(ARG_CATEGORY) as? Category

    /** во viewModel в качестве параметров передаем экземпляр NewsStore и категорию новостей */
    private val viewModel by viewModel<NewsViewModel> { parametersOf(category) }

    /** инициализируем слушатель нажатий на элементы списка
     * onItemClick - колбэк нажатия на элемент списка
     * onBookmarkCheck - колбэк нажатия на закладку на элеменете списка (пока не реализовано!)
     * */
    private val recyclerItemListener = object : RecyclerItemListener {
        override fun onItemClick(itemArticle: Article) {
            showFragment(fragment = ArticleFragment.newInstance(article = itemArticle))
        }

        override fun onBookmarkCheck(itemArticle: Article) {
            viewModel.checkBookmark(article = itemArticle)
        }
    }

    /** инициализируем адаптер для RecyclerView и передаем туда слушатель нажатий на элементы списка и флаг, что это главный экран*/
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
    }

    private fun initViewModel() {
        /**подписываемся на изменения состояний экрана */
        viewModel.viewState.onEach { renderState(it) }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun initData() {
        category?.let { viewModel.getData() }
    }

    /**
     * метод обработки состояний экрана
     * */
    private fun renderState(state: ListViewState) {
        when (state) {
            is ListViewState.Empty -> {
                enableEmptyState(state = true)
                enableProgress(state = false)
                enableError(state = false)
                enableContent(state = false)
            }
            is ListViewState.Loading -> {
                enableProgress(state = true)
                enableEmptyState(state = true)
                enableError(state = false)
                enableContent(state = false)
            }
            is ListViewState.Refreshing -> {
                enableProgress(state = true)
                enableEmptyState(state = false)
                enableError(state = false)
                enableContent(state = true)
            }
            is ListViewState.Error -> {
                enableError(state = true)
                enableEmptyState(state = false)
                enableProgress(state = false)
                enableContent(state = false)
                showToastMessage(message = state.message ?: getString(R.string.unknown_error))
            }
            is ListViewState.Data -> {
                enableContent(state = true)
                enableEmptyState(state = false)
                enableProgress(state = false)
                enableError(state = false)
                initContent(data = state.data)
            }
            else -> {}
        }
    }

    private fun initContent(data: List<Article>) {
        newsListAdapter.submitList(data)
    }

    private fun enableEmptyState(state: Boolean) {
        binding.noNewsImage.isVisible = state
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