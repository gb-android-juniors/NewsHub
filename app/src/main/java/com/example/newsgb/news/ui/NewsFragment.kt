package com.example.newsgb.news.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.newsgb._core.ui.BaseFragment
import com.example.newsgb.databinding.NewsFragmentBinding
import com.example.newsgb._core.ui.adapter.ViewPagerAdapter
import com.example.newsgb.databinding.NewsFragmentBinding
import com.example.newsgb.utils.ui.Category
import com.google.android.material.tabs.TabLayoutMediator

class NewsFragment : BaseFragment<NewsFragmentBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        setViewPagerAndTabsNavigation()
    }

    private fun setViewPagerAndTabsNavigation() = with(binding) {
        viewPager.adapter = ViewPagerAdapter(this@NewsFragment)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = getString(Category.values()[position].nameResId)
        }.attach()
        disableViewPagerCache()
    }

    /** метод отключает кэширование фрагментов вьюпейджера */
    private fun disableViewPagerCache() {
        (binding.viewPager.getChildAt(0) as RecyclerView).apply {
            layoutManager?.isItemPrefetchEnabled = false
            setItemViewCacheSize(0)
        }
    }

    companion object {
        fun newInstance() = NewsFragment()
    }

    override fun getViewBinding() = NewsFragmentBinding.inflate(layoutInflater)
}