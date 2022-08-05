package com.example.newsgb.settings.ui

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
        selectCountryText.setText(getSelectedCountryName())
        setCountryListListener()
    }


    private fun setCountryListListener() = with(binding) {
        val adapter =
            ArrayAdapter(requireContext(), R.layout.country_list_item, getCountryNamesList())
        selectCountryText.setAdapter(adapter)
        selectCountryText.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                PrivateSharedPreferences(requireContext()).save(position)
                App.countryCode = Countries.values()[position].countryCode
            }
    }

    private fun getSelectedCountryName(): String {
        val position = PrivateSharedPreferences(requireContext()).read()
        return getString(Countries.values()[position].nameResId)
    }

    private fun getCountryNamesList(): List<String> = Countries.values().map { country ->
        getString(country.nameResId)
    }

    companion object {
        @JvmStatic
        fun newInstance() = SettingsFragment()
    }

    override fun getViewBinding() = SettingsFragmentBinding.inflate(layoutInflater)
}