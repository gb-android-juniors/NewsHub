package com.example.newsgb.settings.ui

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.newsgb.App
import com.example.newsgb.R
import com.example.newsgb._core.ui.BaseFragment
import com.example.newsgb.databinding.SettingsFragmentBinding
import com.example.newsgb.utils.Constants
import com.example.newsgb.utils.PrivateSharedPreferences
import com.example.newsgb.utils.hideKeyboard
import com.example.newsgb.utils.ui.Countries
import com.example.newsgb.utils.ui.ThemeModes
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : BaseFragment<SettingsFragmentBinding>() {

    private val viewModel by viewModel<SettingsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() = with(binding) {
        root.setOnClickListener { hideKeyboard() }
        selectCountryText.setText(getSelectedCountryNameFromPreferences())
        selectAppThemeText.setText(getSelectedAppThemeName())
        selectAppThemeLayout.helperText = getString(R.string.settings_theme_helper_text)
        setCountryListListener()
        setThemeListListener()
    }

    private fun setCountryListListener() = with(binding) {
        val adapter =
            ArrayAdapter(
                requireContext(),
                R.layout.settings_options_list_item,
                getMapOfCountryNamesWithIndexes().keys.toTypedArray().sorted()
            )
        selectCountryText.setAdapter(adapter)
        selectCountryText.onItemClickListener =
            AdapterView.OnItemClickListener { adapterView, _, position, _ ->
                hideKeyboard()
                saveSelectedCountry(adapterView, position)
                viewModel.refreshData()
            }
    }

    private fun setThemeListListener() = with(binding) {
        selectAppThemeText.setOnClickListener { hideKeyboard() }
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.settings_options_list_item,
            ThemeModes.values().map { getString(it.resIdName) }.toList()
        )
        selectAppThemeText.setAdapter(adapter)
        selectAppThemeText.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                PrivateSharedPreferences(
                    context = requireContext(),
                    prefName = Constants.APP_PREFERENCES_THEME_MODE
                ).save(index = position)
                requireActivity().recreate()
            }
    }

    private fun saveSelectedCountry(adapterView: AdapterView<*>, position: Int) {
        val selectedCountryName = adapterView.getItemAtPosition(position).toString()
        getMapOfCountryNamesWithIndexes()[selectedCountryName]?.let { index ->
            PrivateSharedPreferences(
                context = requireContext(),
                prefName = Constants.APP_PREFERENCES_COUNTRY_CODE
            ).save(index = index)
            App.countryCode = Countries.values()[index].countryCode
        }
    }

    private fun getSelectedAppThemeName(): String = PrivateSharedPreferences(
        context = requireContext(),
        prefName = Constants.APP_PREFERENCES_THEME_MODE
    ).read().let { index ->
        getString(ThemeModes.values()[index].resIdName)
    }

    private fun getSelectedCountryNameFromPreferences(): String = PrivateSharedPreferences(
        context = requireContext(),
        prefName = Constants.APP_PREFERENCES_COUNTRY_CODE
    ).read().let { index ->
        getString(Countries.values()[index].nameResId)
    }

    private fun getMapOfCountryNamesWithIndexes(): Map<String, Int> =
        mapOf<String, Int>().plus(Countries.values().map { country ->
            getString(country.nameResId) to country.ordinal
        })

    companion object {
        @JvmStatic
        fun newInstance() = SettingsFragment()
    }

    override fun getViewBinding() = SettingsFragmentBinding.inflate(layoutInflater)
}