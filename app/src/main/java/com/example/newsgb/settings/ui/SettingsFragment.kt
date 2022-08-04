package com.example.newsgb.settings.ui

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.example.newsgb.App
import com.example.newsgb.R
import com.example.newsgb._core.ui.BaseFragment
import com.example.newsgb.databinding.SettingsFragmentBinding
import com.example.newsgb.utils.PrivateSharedPreferences
import com.example.newsgb.utils.ui.Countries

class SettingsFragment : BaseFragment<SettingsFragmentBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() = with(binding) {
        val adapter =
            ArrayAdapter(requireContext(), R.layout.country_list_item, getCountriesNames())
        (selectCountryLayout.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        selectCountryLayout.hint = getCountryName()
        selectCountryText.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                PrivateSharedPreferences(requireContext()).save(position)
                App.countryCode = Countries.values()[position].countryCode
            }
    }

    private fun getCountryName(): String {
        val position = PrivateSharedPreferences(requireContext()).read()
        return getString(Countries.values()[position].nameResId)
    }

    private fun getCountriesNames(): List<String> = Countries.values().map { country ->
        getString(country.nameResId)
    }

    companion object {
        @JvmStatic
        fun newInstance() = SettingsFragment()
    }

    override fun getViewBinding() = SettingsFragmentBinding.inflate(layoutInflater)
}