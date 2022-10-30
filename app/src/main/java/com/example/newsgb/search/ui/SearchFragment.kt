package com.example.newsgb.search.ui

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
import androidx.recyclerview.widget.RecyclerView
import com.example.newsgb.R
import com.example.newsgb._core.ui.BaseFragment
import com.example.newsgb._core.ui.adapter.NewsListAdapter
import com.example.newsgb._core.ui.adapter.RecyclerItemListener
import com.example.newsgb._core.ui.model.Article
import com.example.newsgb._core.ui.model.ListViewState
import com.example.newsgb.article.ui.ArticleFragment
import com.example.newsgb.databinding.SearchFragmentBinding
import com.example.newsgb.utils.hideKeyboard
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : BaseFragment<SearchFragmentBinding>() {

    private val viewModel by viewModel<SearchViewModel>()

    private val phrase: String by lazy {
        requireArguments().getString(ARG_SEARCH_PHRASE, "")
    }

    private val recyclerItemListener = object : RecyclerItemListener {
        override fun onItemClick(itemArticle: Article) {
            showFragment(fragment = ArticleFragment.newInstance(article = itemArticle))
        }

        override fun onBookmarkCheck(itemArticle: Article) {
            viewModel.checkBookmark(article = itemArticle)
        }
    }

    private val searchListAdapter: NewsListAdapter = NewsListAdapter(listener = recyclerItemListener)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMenu()
        initViewModel()
        initView()
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    override fun getViewBinding() = SearchFragmentBinding.inflate(layoutInflater)

    private fun initMenu() {
        (requireActivity() as AppCompatActivity).apply {
            setSupportActionBar(binding.searchToolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {}

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    android.R.id.home -> {
                        requireActivity().onBackPressed()
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

    private fun initView() = with(binding) {
        searchRecycler.adapter = searchListAdapter
        searchRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
                    viewModel.getMoreDataToList()
                }
            }
        })

        searchInputLayout.setEndIconOnClickListener {
            hideKeyboard()
            viewModel.getData(phrase = searchEditText.text.toString(), newSearch = true)
        }
        searchEditText.text?.apply {
            if (phrase.isBlank()) clear() else replace(0, this.length, phrase)
        }
    }

    private fun initData() {
        viewModel.getData(phrase = phrase, newSearch = true)
    }

    private fun renderState(state: ListViewState) {
        when (state) {
            is ListViewState.Data -> {
                enableProgress(state = false)
                enableRecyclerProgress(state = false)
                enableEmptyState(state = false)
                enableError(state = false)
                enableContent(state = true)
                setDataToAdapter(data = state.data)
            }
            is ListViewState.Loading -> {
                enableProgress(state = true)
                enableRecyclerProgress(state = false)
                enableEmptyState(state = false)
                enableContent(state = false)
                enableError(state = false)
            }
            is ListViewState.MoreLoading -> {
                enableProgress(state = state.mainProgressState)
                enableRecyclerProgress(state = state.recyclerProgressState)
                enableEmptyState(state = state.mainProgressState)
                enableError(state = false)
                enableContent(state = state.recyclerProgressState)
            }
            is ListViewState.Empty -> {
                enableProgress(state = false)
                enableRecyclerProgress(state = false)
                enableEmptyState(state = true)
                enableContent(state = false)
                enableError(state = false)
            }
            is ListViewState.Error -> {
                enableEmptyState(state = false)
                enableProgress(state = false)
                enableRecyclerProgress(state = false)
                enableContent(state = false)
                enableError(state = true)
                showToastMessage(message = state.message ?: getString(R.string.unknown_error))
            }

            is ListViewState.Refreshing -> {
                enableProgress(state = false)
                enableRecyclerProgress(state = true)
                enableContent(state = true)
                enableEmptyState(state = false)
                enableError(state = false)
            }
        }
    }

    private fun setDataToAdapter(data: List<Article>) {
        searchListAdapter.submitList(data)
    }

    private fun enableContent(state: Boolean) {
        binding.searchRecycler.isVisible = state
    }

    private fun enableProgress(state: Boolean) {
        binding.loader.isVisible = state
    }

    private fun enableRecyclerProgress(state: Boolean) {
        binding.recyclerLoader.isVisible = state
    }

    private fun enableEmptyState(state: Boolean) {
        binding.emptySearchWarning.isVisible = state
    }

    private fun enableError(state: Boolean) {
        binding.error.isVisible = state
    }

    private fun showFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager
            .beginTransaction()
            .add(R.id.main_container, fragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .addToBackStack(ARTICLE_DETAILS_FRAGMENT_FROM_SEARCH)
            .commit()
    }

    private fun showToastMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
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