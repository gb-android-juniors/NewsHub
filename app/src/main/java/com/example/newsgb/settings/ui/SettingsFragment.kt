package com.example.newsgb.settings.ui

import com.example.newsgb._core.ui.BaseFragment
import com.example.newsgb.databinding.SettingsFragmentBinding

class SettingsFragment : BaseFragment<SettingsFragmentBinding>() {

    companion object {
        @JvmStatic
        fun newInstance() = SettingsFragment()
    }

    override fun getViewBinding() = SettingsFragmentBinding.inflate(layoutInflater)
}