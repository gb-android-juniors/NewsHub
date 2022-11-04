package com.robivan.newsgb.news.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.robivan.newsgb.R
import com.robivan.newsgb._core.ui.BaseFragment
import com.robivan.newsgb._core.ui.adapter.ViewPagerAdapter
import com.robivan.newsgb.databinding.NewsFragmentBinding
import com.robivan.newsgb.search.ui.SearchClickListener
import com.robivan.newsgb.search.ui.SearchDialogFragment
import com.robivan.newsgb.search.ui.SearchFragment
import com.robivan.newsgb.utils.ui.Category
import com.google.android.material.tabs.TabLayoutMediator

class NewsFragment : BaseFragment<NewsFragmentBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun getViewBinding() = NewsFragmentBinding.inflate(layoutInflater)

    private fun initView() {
        setViewPagerAndTabsNavigation()
        binding.searchFab.setOnClickListener { showSearchDialog() }
    }

    private fun setViewPagerAndTabsNavigation() = with(binding) {
        viewPager.adapter = ViewPagerAdapter(this@NewsFragment)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = getString(Category.values()[position].nameResId)
        }.attach()
        disableViewPagerCache()
    }

    private fun disableViewPagerCache() {
        (binding.viewPager.getChildAt(0) as RecyclerView).apply {
            layoutManager?.isItemPrefetchEnabled = false
            setItemViewCacheSize(0)
        }
    }

    private fun showSearchDialog() {
        SearchDialogFragment.newInstance().apply {
            setOnSearchClickListener(object : SearchClickListener {
                override fun onClick(phrase: String) {
                    showFragment(fragment = SearchFragment.newInstance(phrase = phrase))
                }
            })
        }.show(parentFragmentManager, "")
    }

    private fun showFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager
            .beginTransaction()
            .add(R.id.main_container, fragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .addToBackStack(ARTICLE_SEARCH_FRAGMENT_FROM_DIALOG)
            .commit()
    }

    companion object {
        private const val ARTICLE_SEARCH_FRAGMENT_FROM_DIALOG = "ArticleSearchFragmentFromDialog"

        fun newInstance() = NewsFragment()
    }
}