package com.example.newsgb.search.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.newsgb.R
import com.example.newsgb._core.ui.BaseFragment
import com.example.newsgb._core.ui.adapter.NewsListAdapter
import com.example.newsgb._core.ui.adapter.RecyclerItemListener
import com.example.newsgb._core.ui.model.Article
import com.example.newsgb._core.ui.store.NewsStore
import com.example.newsgb._core.ui.store.NewsStoreHolder
import com.example.newsgb.article.ui.ArticleFragment
import com.example.newsgb.databinding.SearchFragmentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SearchFragment : BaseFragment<SearchFragmentBinding>() {

    private val viewModel by viewModel<SearchViewModel> { parametersOf(newsStore) }
    /** переменная хранителя экземпляра NewsStore */
    private var storeHolder: NewsStoreHolder? = null

    /** экземпляр NewsStore, который получаем из MainActivity как хранителя этого экземпляра */
    private val newsStore: NewsStore by lazy {
        storeHolder?.newsStore ?: throw IllegalArgumentException()
    }

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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        /** инициализируем переменную хранителя экземпляра NewsStore */
        storeHolder = context as NewsStoreHolder
    }

    /** инициализируем адаптер для RecyclerView и передаем туда слушатель нажатий на элементы списка */
    private val searchListAdapter: NewsListAdapter = NewsListAdapter(listener = recyclerItemListener)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        initContentView()
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    override fun onDetach() {
        super.onDetach()
        storeHolder = null
    }

    override fun getViewBinding() = SearchFragmentBinding.inflate(layoutInflater)

    private fun initViewModel() {
//        TODO("Not yet implemented")
    }

    private fun initContentView() {
//        TODO("Not yet implemented")
    }

    private fun initData() {
        val phrase = requireArguments().getString(ARG_SEARCH_PHRASE, "")
        viewModel.getData(phrase = phrase)
    }

    private fun showFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager
            .beginTransaction()
            .add(R.id.main_container, fragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .addToBackStack(ARTICLE_DETAILS_FRAGMENT_FROM_SEARCH)
            .commit()
    }


    companion object {
        private const val ARTICLE_DETAILS_FRAGMENT_FROM_SEARCH = "ArticleDetailsFragmentFromSearch"
        private const val ARG_SEARCH_PHRASE = "arg_search_phase"

        fun newInstance(phrase: String): SearchFragment =
            SearchFragment().apply {
                arguments = bundleOf(ARG_SEARCH_PHRASE to phrase)
            }
    }
}