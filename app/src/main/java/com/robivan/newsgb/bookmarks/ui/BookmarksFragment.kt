package com.robivan.newsgb.bookmarks.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.robivan.newsgb.R
import com.robivan.newsgb._core.ui.BaseFragment
import com.robivan.newsgb._core.ui.adapter.NewsListAdapter
import com.robivan.newsgb._core.ui.adapter.RecyclerItemListener
import com.robivan.newsgb._core.ui.model.Article
import com.robivan.newsgb._core.ui.model.ListViewState
import com.robivan.newsgb.article.ui.ArticleFragment
import com.robivan.newsgb.databinding.BookmarksFragmentBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class BookmarksFragment : BaseFragment<BookmarksFragmentBinding>() {

    private val viewModel by viewModel<BookmarksViewModel>()

    private val recyclerItemListener = object : RecyclerItemListener {
        override fun onItemClick(itemArticle: Article) {
            showFragment(fragment = ArticleFragment.newInstance(article = itemArticle))
        }

        override fun onBookmarkCheck(itemArticle: Article) {
            viewModel.checkBookmark(article = itemArticle)
        }
    }

    private val bookmarksListAdapter: NewsListAdapter =
        NewsListAdapter(listener = recyclerItemListener)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        initView()
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    private fun initData() {
        viewModel.getData()
    }

    private fun initView() = with(binding) {
        bookmarksRecycler.adapter = bookmarksListAdapter
        clearAllBookmarks.setOnClickListener { showWarningDialog() }
        swipeRefreshLayoutBookmarks.setOnRefreshListener {
            initData()
            swipeRefreshLayoutBookmarks.isRefreshing = false
        }
    }

    private fun initViewModel() {
        viewModel.viewState.onEach { renderState(it) }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun renderState(state: ListViewState) {
        when (state) {
            is ListViewState.Data -> {
                enableProgress(state = false)
                enableContent(state = true)
                enableEmptyState(state = false)
                setDataToAdapter(data = state.data)
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
            is ListViewState.Error -> {
                enableEmptyState(state = false)
                enableProgress(state = false)
                enableContent(state = false)
                showToastMessage(message = state.message ?: getString(R.string.unknown_error))
            }

            is ListViewState.Refreshing -> {
                enableProgress(state = true)
                enableContent(state = true)
                enableEmptyState(state = false)
            }
            else -> {}
        }
    }

    private fun setDataToAdapter(data: List<Article>) {
        bookmarksListAdapter.submitList(data)
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

    private fun showFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager
            .beginTransaction()
            .add(R.id.main_container, fragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .addToBackStack(ARTICLE_DETAILS_FRAGMENT_FROM_BOOKMARKS)
            .commit()
    }

    private fun showToastMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showWarningDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.closing_warning)
            .setPositiveButton(R.string.yes) { _, _ -> viewModel.clearBookmarks() }
            .setNegativeButton(R.string.no) { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    companion object {
        private const val ARTICLE_DETAILS_FRAGMENT_FROM_BOOKMARKS =
            "ArticleDetailsFragmentFromBookmarks"

        fun newInstance() = BookmarksFragment()
    }

    override fun getViewBinding() = BookmarksFragmentBinding.inflate(layoutInflater)
}