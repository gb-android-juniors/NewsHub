package com.example.newsgb.main.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.newsgb.R
import com.example.newsgb.bookmarks.ui.BookmarksFragment
import com.example.newsgb.databinding.NavigationFragmentBinding
import com.example.newsgb.news.ui.NewsFragment
import com.example.newsgb.settings.ui.SettingsFragment

class NavigationFragment : Fragment() {

    private var _binding: NavigationFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NavigationFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        setFragmentInHostContainer(NewsFragment.newInstance())
        setBottomNavigationListener()
    }

    private fun setFragmentInHostContainer(fragment: Fragment?) {
        fragment?.let {
            childFragmentManager.beginTransaction()
                .replace(R.id.host_container, fragment)
                .commitNow()
        }
    }

    private fun setBottomNavigationListener() {
        binding.navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_news -> {
                    setFragmentInHostContainer(NewsFragment.newInstance())
                    true
                }
                R.id.navigation_bookmarks -> {
                    setFragmentInHostContainer(BookmarksFragment.newInstance())
                    true
                }
                R.id.navigation_settings -> {
                    setFragmentInHostContainer(SettingsFragment.newInstance())
                    true
                }
                else -> false
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = NavigationFragment()
    }
}