package com.example.newsgb.article.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.newsgb.R
import com.example.newsgb._core.ui.model.Article
import com.example.newsgb._core.ui.model.ItemViewState
import com.example.newsgb._core.ui.store.NewsStore
import com.example.newsgb._core.ui.store.NewsStoreHolder
import com.example.newsgb.databinding.DetailsFragmentBinding
import com.example.newsgb.utils.formatApiStringToDate
import com.example.newsgb.utils.setBookmarkIconColor
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class ArticleDetailsFragment : Fragment() {
    /** переменная хранителя экземпляра NewsStore */
    private var storeHolder: NewsStoreHolder? = null

    /** экземпляр NewsStore, который получаем из MainActivity как хранителя этого экземпляра */
    private val newsStore: NewsStore by lazy {
        storeHolder?.newsStore ?: throw IllegalArgumentException()
    }

    /** вытягиваем url статьи из аргументов, если его там нет, то заменяем на пустую строку */
    private val articleUrl: String by lazy {
        arguments?.getString(ARG_ARTICLE_URL, DEFAULT_URL) ?: DEFAULT_URL
    }

    private val viewModel: ArticleViewModel by viewModel() { parametersOf(newsStore, articleUrl) }

    private var _binding: DetailsFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        /** инициализируем переменную хранителя экземпляра NewsStore */
        storeHolder = context as NewsStoreHolder
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DetailsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDetach() {
        super.onDetach()
        storeHolder = null
    }

    private fun initViewModel() {
        /**подписываемся на изменения состояний экрана */
        viewModel.viewState.onEach { renderState(it) }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    /**
     * метод обработки состояний экрана
     * */
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
            else -> {}
        }
    }

    private fun initContent(article: Article) = with(binding) {
        articleHeaderText.text = article.title
        articleSourceName.text = article.sourceName
        publicationDate.text = article.publishedDate.formatApiStringToDate()
        descriptionText.text = article.description
        detailsButton.setOnClickListener {
            showArticleFragment(ArticleWebVIewFragment.newInstance(article.contentUrl))
        }
        bookmarkIcon.setBookmarkIconColor(requireContext(), article.isChecked)
        bookmarkIcon.setOnClickListener {
            // оставляем только отправку команды во viewModel, весь код по смене ui не нужен
            // все перерисуется автоматом при обновлении состояния стора
            article.isChecked = !article.isChecked
            onBookmarkClickListener(article)
            bookmarkIcon.setBookmarkIconColor(requireContext(), article.isChecked)
            }

        Glide.with(articleImage)
            .load(article.imageUrl)
            .placeholder(R.drawable.ic_newspaper_24)
            .error(R.drawable.ic_newspaper_24)
            .into(articleImage)
    }

    /**
     * метод обработки нажатия на иконку с закладкой
     * добавляет или удаляет статью из БД с закладками
     */
    private fun onBookmarkClickListener(article: Article) {
        // меньше логики во фрагменте, этим будет заниматься viewModel
        if (article.isChecked) {
            viewModel.saveToDB(article)
        } else {
            viewModel.deleteBookmark(article)
        }
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
        private const val ARG_ARTICLE_URL = "arg_article_url"
        private const val DEFAULT_URL = ""

        @JvmStatic
        fun newInstance(articleUrl: String): ArticleDetailsFragment =
            ArticleDetailsFragment().apply {
                arguments = bundleOf(ARG_ARTICLE_URL to articleUrl)
            }
    }
}