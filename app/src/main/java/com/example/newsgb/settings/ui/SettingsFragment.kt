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
import com.example.newsgb.utils.ui.Languages
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
        selectAppLanguageText.setText(getSelectedLanguage())
        setCountryListListener()
        setThemeListListener()
        setLanguageListListener()
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
            ThemeModes.values().map { getString(it.nameResId) }.toList()
        )
        selectAppThemeText.setAdapter(adapter)
        selectAppThemeText.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val selectedTheme = ThemeModes.values()[position].name
                saveSelectedParameterToPreferences(prefName = Constants.APP_PREFERENCES_THEME_MODE, selectedParameter = selectedTheme)
            }
    }

    private fun setLanguageListListener() = with(binding) {
        selectAppLanguageText.setOnClickListener { hideKeyboard() }
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.settings_options_list_item,
            Languages.values().map { getString(it.nameResId) }.toList()
        )
        selectAppLanguageText.setAdapter(adapter)
        selectAppLanguageText.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val selectedLanguage = Languages.values()[position].languageCode
                saveSelectedParameterToPreferences(prefName = Constants.APP_PREFERENCES_LANGUAGE, selectedParameter = selectedLanguage)
            }
    }

    private fun saveSelectedParameterToPreferences(prefName: String, selectedParameter: String?) {
        PrivateSharedPreferences(
            context = requireContext(),
            prefName = prefName
        ).save(parameter = selectedParameter)
        requireActivity().recreate()
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
    ).readString()?.let { selectedTheme ->
        getString(ThemeModes.valueOf(selectedTheme).nameResId)
    } ?: getString(ThemeModes.SYSTEM_MODE.nameResId)

    private fun getSelectedCountryNameFromPreferences(): String = PrivateSharedPreferences(
        context = requireContext(),
        prefName = Constants.APP_PREFERENCES_COUNTRY_CODE
    ).readInt().let { index ->
        getString(Countries.values()[index].nameResId)
    }

    private fun getSelectedLanguage(): String = PrivateSharedPreferences(
        context = requireContext(),
        prefName = Constants.APP_PREFERENCES_LANGUAGE
    ).readString()?.let { selectedLocale ->
        getString(Languages.valueOf(selectedLocale.uppercase()).nameResId)
    } ?: getString(Languages.DEFAULT.nameResId)

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