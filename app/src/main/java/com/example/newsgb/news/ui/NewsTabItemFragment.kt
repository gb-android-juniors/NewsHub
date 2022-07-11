package com.example.newsgb.news.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.newsgb.R
import com.example.newsgb._core.data.db.entity.ArticleEntity
import com.example.newsgb._core.state.ViewState
import com.example.newsgb.databinding.NewsFragmentTabItemBinding
import org.koin.androidx.viewmodel.ext.android.viewModel


class NewsTabItemFragment : Fragment() {

    private var _binding: NewsFragmentTabItemBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<NewsViewModel>()
    private lateinit var newsAdapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NewsFragmentTabItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        checkViewState()
        newsAdapter.setOnItemClickListener {
            // TODO: display detailsFragment
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun checkViewState() {
        lifecycleScope.launchWhenStarted {
            viewModel.viewState.collect {
                when (it) {
                    is ViewState.SuccessState -> {
                        createFirstNews(it.newsResponse.articles[0])
                        it.newsResponse.articles.removeAt(0)
                        newsAdapter.newsListDiffer.submitList(it.newsResponse.articles)
                        hideProgressBar()
                    }
                    is ViewState.LoadingState -> {
                        showProgressBar()
                    }
                    is ViewState.ErrorState -> {
                        showToastMessage(it.message.toString())
                        hideProgressBar()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun createFirstNews(article: ArticleEntity) {
        binding.firstNewsHeader.text = article.title
        binding.firstNewsSource.text = article.source.name
        Glide.with(binding.firstNewsImage)
            .load(article.urlToImage)
            .placeholder(R.drawable.ic_newspaper_24)
            .error(R.drawable.ic_newspaper_24)
            .into(binding.firstNewsImage)
    }

    private fun setupAdapter() {
        newsAdapter = NewsAdapter()
        binding.mainRecycler.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun showProgressBar() {
        binding.loader.isVisible = true
    }

    private fun hideProgressBar() {
        binding.loader.isVisible = false
    }

    private fun showToastMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        @JvmStatic
        fun newInstance() = NewsTabItemFragment()
    }
}