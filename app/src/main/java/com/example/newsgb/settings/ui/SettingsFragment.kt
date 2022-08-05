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
        selectCountryText.setText(getSelectedCountryNameFromPreferences())
        setCountryListListener()
    }

    private fun setCountryListListener() = with(binding) {
        val adapter =
            ArrayAdapter(requireContext(), R.layout.country_list_item, getMapOfCountryNamesWithIndexes().keys.toTypedArray().sorted())
        selectCountryText.setAdapter(adapter)
        selectCountryText.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                val selectedCountryName = parent.getItemAtPosition(position).toString()
                getMapOfCountryNamesWithIndexes()[selectedCountryName]?.let { index ->
                    saveSelectedRegionToPreferences(index)
                }
            }
    }

    private fun saveSelectedRegionToPreferences(index: Int) {
        PrivateSharedPreferences(requireContext()).save(index)
        App.countryCode = Countries.values()[index].countryCode
    }

    private fun getSelectedCountryNameFromPreferences(): String {
        val index = PrivateSharedPreferences(requireContext()).read()
        return getString(Countries.values()[index].nameResId)
    }

    private fun getMapOfCountryNamesWithIndexes(): Map<String, Int> = mapOf< String, Int>().plus(Countries.values().map { country ->
        getString(country.nameResId) to country.ordinal
    })

    companion object {
        @JvmStatic
        fun newInstance() = SettingsFragment()
    }

    override fun getViewBinding() = SettingsFragmentBinding.inflate(layoutInflater)
}