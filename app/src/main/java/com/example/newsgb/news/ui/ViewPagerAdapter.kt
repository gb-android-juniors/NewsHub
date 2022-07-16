package com.example.newsgb.news.ui

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.newsgb.utils.Category
import com.example.newsgb.utils.Category.*

class ViewPagerAdapter(fragmentManager: FragmentManager, val context: Context) :
    FragmentStatePagerAdapter(fragmentManager) {

    override fun getCount(): Int {
        return Category.values().size
    }

    //TODO нужно либо создать для каждой категории новостей свою реализацию фрагмента,
    // либо передавать свои параметры запроса к апи в каждом фрагменте этой коллекции
    override fun getItem(position: Int): Fragment {
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

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {

            1 -> context.getString(BUSINESS.categoryNameId)
            2 -> context.getString(SPORT.categoryNameId)
            3 -> context.getString(ENTERTAINMENT.categoryNameId)
            4 -> context.getString(TECHNOLOGY.categoryNameId)
            5 -> context.getString(SCIENCE.categoryNameId)
            6 -> context.getString(HEALTH.categoryNameId)
            else -> context.getString(GENERAL.categoryNameId)
        }
    }
}