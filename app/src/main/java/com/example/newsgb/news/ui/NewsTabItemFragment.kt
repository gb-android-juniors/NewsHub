package com.example.newsgb.news.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.newsgb.R
import com.example.newsgb._core.ui.model.Article
import com.example.newsgb._core.ui.model.ViewState
import com.example.newsgb.databinding.NewsFragmentTabItemBinding
import com.example.newsgb.news.ui.adapter.NewsAdapter
import com.example.newsgb.news.ui.adapter.RecyclerItemListener
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel


class NewsTabItemFragment : Fragment() {

    private val viewModel by viewModel<NewsViewModel>()

    private var _binding: NewsFragmentTabItemBinding? = null
    private val binding get() = _binding!!

    private val recyclerItemListener = object : RecyclerItemListener {
        override fun onItemClick() {
            //TODO("Not yet implemented")
        }
        override fun onBookmarkCheck() {
            //TODO("Not yet implemented")
        }
    }
    private val newsAdapter: NewsAdapter = NewsAdapter(listener = recyclerItemListener)

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initView() = with(binding) {
        mainRecycler.adapter = newsAdapter
    }

    private fun initViewModel() {
        viewModel.viewState.onEach { renderState(it) }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun renderState(state: ViewState) {
        when (state) {
            is ViewState.Loading -> {
                enableProgress(state = true)
                enableError(state = false)
                enableContent(state = false)
            }
            is ViewState.Error -> {
                enableProgress(state = false)
                enableError(state = true)
                enableContent(state = false)
                showToastMessage(state.message.toString())
            }
            is ViewState.Success -> {
                enableProgress(state = false)
                enableError(state = false)
                enableContent(state = true)
                initContent(state.data)
            }
            else -> {}
        }
    }

    private fun initContent(data: List<Article>) {
        createFirstNews(data.first())
        newsAdapter.submitList(data.subList(1, data.size-1))
    }


    private fun createFirstNews(article: Article) {
        binding.firstNewsHeader.text = article.title
        binding.firstNewsSource.text = article.sourceName
        Glide.with(binding.firstNewsImage)
            .load(article.imageUrl)
            .placeholder(R.drawable.ic_newspaper_24)
            .error(R.drawable.ic_newspaper_24)
            .into(binding.firstNewsImage)
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

    companion object {
        @JvmStatic
        fun newInstance() = NewsTabItemFragment()
    }
}