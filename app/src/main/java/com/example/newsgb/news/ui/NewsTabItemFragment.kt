package com.example.newsgb.news.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.newsgb.R
import com.example.newsgb._core.ui.model.Article
import com.example.newsgb._core.ui.model.ListViewState
import com.example.newsgb._core.ui.store.NewsStore
import com.example.newsgb._core.ui.store.NewsStoreHolder
import com.example.newsgb.article.ui.ArticleFragment
import com.example.newsgb.databinding.NewsFragmentTabItemBinding
import com.example.newsgb._core.ui.adapter.NewsListAdapter
import com.example.newsgb._core.ui.adapter.RecyclerItemListener
import com.example.newsgb.utils.ui.Category
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class NewsTabItemFragment : Fragment() {

    /** вытягиваем из аргументов переданную категорию новостей */
    private val category: Category?
        get() = requireArguments().getSerializable(ARG_CATEGORY) as? Category

    /** переменная хранителя экземпляра NewsStore */
    private var storeHolder: NewsStoreHolder? = null

    /** экземпляр NewsStore, который получаем из MainActivity как хранителя этого экземпляра */
    private val newsStore: NewsStore by lazy {
        storeHolder?.newsStore ?: throw IllegalArgumentException()
    }

    /** во viewModel в качестве параметров передаем экземпляр NewsStore и категорию новостей */
    private val viewModel by viewModel<NewsViewModel> { parametersOf(newsStore, category) }

    private var _binding: NewsFragmentTabItemBinding? = null
    private val binding get() = _binding!!

    /** инициализируем слушатель нажатий на элементы списка
     * onItemClick - колбэк нажатия на элемент списка
     * onBookmarkCheck - колбэк нажатия на закладку на элеменете списка (пока не реализовано!)
     * */
    private val recyclerItemListener = object : RecyclerItemListener {
        override fun onItemClick(itemArticle: Article) {
            showFragment(fragment = ArticleFragment.newInstance(articleUrl = itemArticle.contentUrl))
        }

        override fun onBookmarkCheck(itemArticle: Article) {
            viewModel.checkBookmark(article = itemArticle)
        }
    }

    /** инициализируем адаптер для RecyclerView и передаем туда слушатель нажатий на элементы списка */
    private val newsListAdapter: NewsListAdapter = NewsListAdapter(listener = recyclerItemListener)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        /** инициализируем переменную хранителя экземпляра NewsStore */
        storeHolder = context as NewsStoreHolder
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NewsFragmentTabItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initViewModel()
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

    private fun initView() = with(binding) {
        mainRecycler.adapter = newsListAdapter
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
                enableEmptyState(state = false)
                enableError(state = false)
                enableContent(state = false)
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
            is ListViewState.DataRefreshed -> {
                enableContent(state = true)
                enableEmptyState(state = false)
                enableProgress(state = false)
                enableError(state = false)
                initRecycleContent(data = state.data)
            }
            else -> {}
        }
    }

    /**
     * метод инициализации контента на экране
     * */
    private fun initContent(data: List<Article>) {
        createFirstNews(data.first())
        initRecycleContent(data)
    }

    private fun initRecycleContent(data: List<Article>) {
        if (data.size > 1) {
            newsListAdapter.submitList(data.subList(1, data.size - 1))
        } else {
            newsListAdapter.submitList(listOf())
        }
    }

    /**
     * метод инициализации главной новости на экране
     * */
    private fun createFirstNews(article: Article) {
        binding.firstNewsHeader.text = article.title
        binding.firstNewsSource.text = article.sourceName
        binding.firstNewsContent.setOnClickListener {
            showFragment(fragment = ArticleFragment.newInstance(articleUrl = article.contentUrl))
        }
        Glide.with(binding.firstNewsImage)
            .load(article.imageUrl)
            .error(article.category.imgResId)
            .into(binding.firstNewsImage)
    }

    private fun enableEmptyState(state: Boolean) {
        binding.noNewsText.isVisible = state
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
        private const val ARTICLE_DETAILS_FRAGMENT_FROM_NEWS_LIST = "ArticleDetailsFragmentFromNewsList"

        @JvmStatic
        fun newInstance(category: Category?): NewsTabItemFragment =
            NewsTabItemFragment().apply {
                arguments = bundleOf(ARG_CATEGORY to category)
            }
    }
}