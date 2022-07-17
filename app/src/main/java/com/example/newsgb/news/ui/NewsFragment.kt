package com.example.newsgb.news.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.newsgb.databinding.NewsFragmentBinding
import com.example.newsgb.utils.ui.Category
import com.google.android.material.tabs.TabLayoutMediator

class NewsFragment : Fragment() {

    private var _binding: NewsFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NewsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewPagerAndTabsNavigation()
    }

    private fun setViewPagerAndTabsNavigation() = with(binding) {
        viewPager.adapter = ViewPagerAdapter(this@NewsFragment)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = getString(Category.values()[position].nameResId)
        }.attach()
    }

    companion object {
        fun newInstance() = NewsFragment()
    }
}