package com.example.newsgb.main.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.newsgb.R
import com.example.newsgb._core.ui.BaseFragment
import com.example.newsgb.bookmarks.ui.BookmarksFragment
import com.example.newsgb.databinding.NavigationFragmentBinding
import com.example.newsgb.news.ui.NewsFragment
import com.example.newsgb.settings.ui.SettingsFragment
import com.example.newsgb.utils.network.OnlineLiveData
import com.example.newsgb.utils.network.isNetworkAvailable
import com.example.newsgb.utils.ui.AlertDialogFragment

class NavigationFragment : BaseFragment<NavigationFragmentBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        setBottomNavigationListener()
        checkInternetAvailability()
        subscribeToNetworkChange()
    }

    private fun setBottomNavigationListener() {
        binding.navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_news -> {
                    showFragment(NewsFragment.newInstance())
                    true
                }
                R.id.navigation_bookmarks -> {
                    showFragment(BookmarksFragment.newInstance())
                    true
                }
                R.id.navigation_settings -> {
                    showFragment(SettingsFragment.newInstance())
                    true
                }
                else -> false
            }
        }
    }
    private fun checkInternetAvailability() {
        if (!isNetworkAvailable(requireContext()) && isDialogNull()) {
            disableNetwork()
            showNoInternetConnectionDialog()
        } else if (isNetworkAvailable(requireContext())) {
            enableNetwork()
            showFragment(NewsFragment.newInstance())
        }
    }

    private fun showFragment(fragment: Fragment?) {
        fragment?.let {
            childFragmentManager.beginTransaction()
                .replace(R.id.host_container, fragment)
                .commitNow()
        }
    }

    private fun isDialogNull(): Boolean =
        requireActivity().supportFragmentManager.findFragmentByTag(ALERT_DIALOG_NETWORK_DISABLE_TAG) == null

    private fun enableNetwork() = with(binding) {
        networkLostImage.visibility = View.GONE
        hostContainer.visibility = View.VISIBLE
    }

    private fun disableNetwork() = with(binding) {
        networkLostImage.visibility = View.VISIBLE
        hostContainer.visibility = View.GONE
    }

    private fun showNoInternetConnectionDialog() {
        showAlertDialog(
            getString(R.string.dialog_title_device_is_offline),
            getString(R.string.dialog_message_device_is_offline)
        )
    }

    private fun showAlertDialog(title: String?, message: String?) =
        AlertDialogFragment.newInstance(title, message)
            .show(requireActivity().supportFragmentManager, ALERT_DIALOG_NETWORK_DISABLE_TAG)

    private fun subscribeToNetworkChange() {
        OnlineLiveData(requireActivity()).observe(requireActivity()) { checkInternetAvailability() }
    }

    companion object {
        @JvmStatic
        fun newInstance() = NavigationFragment()
        const val ALERT_DIALOG_NETWORK_DISABLE_TAG = "alert_dialog_network_disable_tag"
    }

    override fun getViewBinding() = NavigationFragmentBinding.inflate(layoutInflater)
}