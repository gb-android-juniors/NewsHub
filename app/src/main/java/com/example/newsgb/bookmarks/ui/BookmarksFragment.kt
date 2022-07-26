package com.example.newsgb.bookmarks.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.example.newsgb.R
import com.example.newsgb._core.ui.adapter.NewsListAdapter
import com.example.newsgb._core.ui.adapter.RecyclerItemListener
import com.example.newsgb._core.ui.model.Article
import com.example.newsgb._core.ui.model.ListViewState
import com.example.newsgb._core.ui.store.NewsStore
import com.example.newsgb._core.ui.store.NewsStoreHolder
import com.example.newsgb.article.ui.ArticleDetailsFragment
import com.example.newsgb.databinding.BookmarksFragmentBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class BookmarksFragment : Fragment() {

    private var _binding: BookmarksFragmentBinding? = null
    private val binding get() = _binding!!

    /** переменная хранителя экземпляра NewsStore */
    private var storeHolder: NewsStoreHolder? = null

    /** экземпляр NewsStore, который получаем из MainActivity как хранителя этого экземпляра */
    private val newsStore: NewsStore by lazy {
        storeHolder?.newsStore ?: throw IllegalArgumentException()
    }

    private val viewModel by viewModel<BookmarksViewModel> { parametersOf(newsStore)}

    /** инициализируем слушатель нажатий на элементы списка
     * onItemClick - колбэк нажатия на элемент списка
     * onBookmarkCheck - колбэк нажатия на закладку на элеменете списка (пока не реализовано!)
     * */
    private val recyclerItemListener = object : RecyclerItemListener {
        override fun onItemClick(itemArticle: Article) {
            showArticleDetailsFragment(fragment = ArticleDetailsFragment.newInstance(articleUrl = itemArticle.contentUrl))
        }

        override fun onBookmarkCheck(itemArticle: Article) {
            if (itemArticle.isChecked) {
                viewModel.saveToDB(itemArticle)
            } else {
                viewModel.deleteBookmark(itemArticle)
            }
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        /** инициализируем переменную хранителя экземпляра NewsStore */
        storeHolder = context as NewsStoreHolder
    }

    /** инициализируем адаптер для RecyclerView и передаем туда слушатель нажатий на элементы списка */
    private val bookmarksListAdapter: NewsListAdapter = NewsListAdapter(listener = recyclerItemListener)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BookmarksFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        initContentView()
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDetach() {
        super.onDetach()
        storeHolder = null
    }

    private fun initData() {
        viewModel.renderData()
    }

    private fun initContentView() {
        with(binding) {
            bookmarksRecycler.adapter = bookmarksListAdapter
            clearAllBookmarks.setOnClickListener { viewModel.clearBookmarks() }
            swipeRefreshLayoutBookmarks.setOnRefreshListener {
                initData()
                swipeRefreshLayoutBookmarks.isRefreshing = false
            }
        }
    }

    private fun initViewModel() {
        /**подписываемся на изменения состояний экрана */
        viewModel.stateFlow.onEach { renderState(it) }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    /**
     * метод обработки состояний экрана
     * */
    private fun renderState(listViewState: ListViewState) {
        when (listViewState) {
            is ListViewState.Data -> {
                enableProgress(state = false)
                enableContent(state = true)
                setDataToAdapter(articleList = listViewState.data)
            }
            is ListViewState.Loading -> {
                enableProgress(state = true)
                enableEmptyState(state = false)
                enableContent(state = false)
            }
            is ListViewState.Empty -> {
                enableProgress(state = false)
                enableEmptyState(state = true)
                enableContent(state = false)
            }
            else -> {}
        }
    }

    /**
     * метод инициализации списка закладок на экране
     * */
    private fun setDataToAdapter(articleList: List<Article>) {
        bookmarksListAdapter.submitList(articleList)
    }

    private fun enableContent(state: Boolean) {
        binding.bookmarksRecycler.isVisible = state
    }

    private fun enableProgress(state: Boolean) {
        binding.loader.isVisible = state
    }

    private fun enableEmptyState(state: Boolean) {
        binding.emptyBookmarksWarning.isVisible = state
    }

    private fun showArticleDetailsFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager
            .beginTransaction()
            .add(R.id.main_container, fragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .addToBackStack(ARTICLE_DETAILS_FRAGMENT_FROM_BOOKMARKS)
            .commit()
    }

    companion object {
        private const val ARTICLE_DETAILS_FRAGMENT_FROM_BOOKMARKS = "ArticleDetailsFragmentFromBookmarks"

        fun newInstance() = BookmarksFragment()
    }
}