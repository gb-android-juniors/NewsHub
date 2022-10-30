package com.example.newsgb._core.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.newsgb.news.ui.NewsFragment
import com.example.newsgb.news.ui.NewsTabItemFragment
import com.example.newsgb.utils.ui.Category

class ViewPagerAdapter(fragment: NewsFragment) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return Category.values().size - 2
    }

    override fun createFragment(position: Int): Fragment {
        return try {
            val category = Category.values().elementAt(index = position)
            NewsTabItemFragment.newInstance(category = category)
        } catch (ex: Throwable) {
            NewsTabItemFragment.newInstance(category = null)
        }
    }
}