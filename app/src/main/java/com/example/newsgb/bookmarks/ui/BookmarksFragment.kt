package com.example.newsgb.bookmarks.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.newsgb._core.ui.adapter.NewsListAdapter
import com.example.newsgb._core.ui.adapter.RecyclerItemListener
import com.example.newsgb._core.ui.model.AppState
import com.example.newsgb._core.ui.model.Article
import com.example.newsgb.databinding.BookmarksFragmentBinding
import com.example.newsgb.utils.ui.Category
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class BookmarksFragment : Fragment() {

    private var _binding: BookmarksFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<BookmarksViewModel>()

    /** инициализируем слушатель нажатий на элементы списка
     * onItemClick - колбэк нажатия на элемент списка
     * onBookmarkCheck - колбэк нажатия на закладку на элеменете списка (пока не реализовано!)
     * */
    private val recyclerItemListener = object : RecyclerItemListener {
        override fun onItemClick() {
            Toast.makeText(requireContext(), "CLICK", Toast.LENGTH_SHORT).show()
        }

        override fun onBookmarkCheck() {
            TODO("Not yet implemented")
        }

    }

    /** инициализируем адаптер для RecyclerView и передаем туда слушатель нажатий на элементы списка */
    private val bookmarksListAdapter: NewsListAdapter by lazy { NewsListAdapter(listener = recyclerItemListener) }

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

    private fun initData() {
        viewModel.renderData()
    }

    private fun initContentView() {
        binding.bookmarksRecycler.adapter = bookmarksListAdapter
        binding.clearAllBookmarks.setOnClickListener { viewModel.clearBookmarks() }

        //TODO тестовый код, чтобы проверить добавление закладки в БД и их удаление
        //начала тестового блока
        binding.emptyBookmarksImage.setOnClickListener {
            val testArticle = Article(
                category = Category.GENERAL,
                sourceName = "BBC News",
                author = "https://www.facebook.com/bbcnews",
                title = "UK heatwave: Country may have hottest day",
                description = "Met Office issues first red heat warning over extreme temperatures and danger to life in much of England.",
                contentUrl = "https://www.bbc.com/news/uk-62201793",
                imageUrl = "https://ichef.bbci.co.uk/news/1024/branded_news/14FE/production/_125947350_59e7b3b4-4959-4d53-9622-ba4109af496e.jpg",
                publishedDate = "2022-07-18T08:21:28Z",
                content = "By Owen Amos &amp; Adam Durbin BBC News\\r\\nMedia caption, Watch: The forecast for the UK's heatwave\\r\\nThe UK could have its hottest day on record this week, with temperatures forecast to hit up to ",
                isChecked = false
            )
            val list = listOf(testArticle, testArticle, testArticle, testArticle)
            for (article in list) {
                viewModel.saveToDB(article)
            }
        }
        //конец тестового блока
    }

    private fun initViewModel() {
        if (binding.bookmarksRecycler.adapter != null) {
            throw IllegalStateException("The ViewModel should be initialised first")
        }
        /**подписываемся на изменения состояний экрана */
        viewModel.stateFlow.onEach { renderState(it) }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    /**
     * метод обработки состояний экрана
     * */
    private fun renderState(appState: AppState) {
        when (appState) {
            is AppState.Data -> {
                enableProgress(state = false)
                enableContent(state = true)
                setDataToAdapter(articleList = appState.data)
            }
            is AppState.Loading -> {
                enableProgress(state = true)
                enableEmptyState(state = false)
                enableContent(state = false)
            }
            is AppState.Empty -> {
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

    companion object {
        fun newInstance() = BookmarksFragment()
    }
}