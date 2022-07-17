package com.example.newsgb.news.ui

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.newsgb.utils.Category

class ViewPagerAdapter(fragment: NewsFragment) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return Category.values().size
    }

    //TODO нужно либо создать для каждой категории новостей свою реализацию фрагмента,
    // либо передавать свои параметры запроса к апи в каждом фрагменте этой коллекции
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> NewsTabItemFragment.newInstance() //Business
            2 -> NewsTabItemFragment.newInstance() //Sport
            3 -> NewsTabItemFragment.newInstance() //Entertainment
            4 -> NewsTabItemFragment.newInstance() //Technology
            5 -> NewsTabItemFragment.newInstance() //Science
            6 -> NewsTabItemFragment.newInstance() //Health
            else -> NewsTabItemFragment.newInstance() //General
        }
    }
}