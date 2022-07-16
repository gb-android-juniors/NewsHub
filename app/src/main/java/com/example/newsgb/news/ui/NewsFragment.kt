package com.example.newsgb.news.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import com.example.newsgb.R
import com.example.newsgb.databinding.NewsFragmentBinding
import com.example.newsgb.utils.Category

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
        with(binding) {
            viewPager.adapter = ViewPagerAdapter(childFragmentManager, requireContext())
            tabLayout.setupWithViewPager(viewPager)
        }
        setCustomTabs()
    }

    private fun setCustomTabs() {
        val layoutInflater = LayoutInflater.from(requireContext())

        for (category in Category.values()) {
            binding.tabLayout.getTabAt(category.ordinal)?.customView =
                layoutInflater.inflate(R.layout.activity_news_api_custom_tab, null).apply {
                    val textTab = this.findViewById<AppCompatTextView>(R.id.tab_text)
                    textTab.text = getString(category.categoryNameId)
                    //Если будем использовать иконки для табов с категориями - настроить здесь ->
                }

        }
    }

    companion object {
        fun newInstance() = NewsFragment()
    }
}