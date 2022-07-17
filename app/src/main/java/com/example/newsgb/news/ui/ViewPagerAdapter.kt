package com.example.newsgb.news.ui

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.newsgb.utils.ui.Category

class ViewPagerAdapter(fragment: NewsFragment) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return Category.values().size
    }

    /**
     * Выбираем из enum-класса элемент по позиции нажатого таба,
     * и передаем этот элемент в качестве аргумента в создаваемый фрагмент ленты новостей.
     * Конструкция try-catch на случай если элемент по такому индексу не будет найден
     **/
    override fun createFragment(position: Int): Fragment {
        return try {
            val category = Category.values().elementAt(index = position)
            NewsTabItemFragment.newInstance(category = category)
        } catch (ex: Throwable) {
            NewsTabItemFragment.newInstance(category = null)
        }
    }
}