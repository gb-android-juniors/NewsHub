package com.robivan.newsgb.settings.ui

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.robivan.newsgb.App
import com.robivan.newsgb.R
import com.robivan.newsgb._core.ui.BaseFragment
import com.robivan.newsgb._core.ui.model.SettingsViewState
import com.robivan.newsgb.databinding.SettingsFragmentBinding
import com.robivan.newsgb.utils.Constants
import com.robivan.newsgb.utils.PreferencesHelper
import com.robivan.newsgb.utils.getEmailSendingIntent
import com.robivan.newsgb.utils.hideKeyboard
import com.robivan.newsgb.utils.ui.Countries
import com.robivan.newsgb.utils.ui.Languages
import com.robivan.newsgb.utils.ui.ThemeModes
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : BaseFragment<SettingsFragmentBinding>() {

    private val viewModel by viewModel<SettingsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initViewModel()
    }

    private fun initViewModel() {
        viewModel.viewState.onEach { renderState(it) }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun renderState(state: SettingsViewState) = with(binding) {
        when (state) {
            is SettingsViewState.CountryLoading -> countryLoader.isVisible = true
            else -> countryLoader.isVisible = false
        }
    }

    private fun initView() = with(binding) {
        root.setOnClickListener { hideKeyboard() }
        connectUs.setOnClickListener { showDevelopersDialogFragment() }
        selectCountryText.setText(getSelectedCountryNameFromPreferences())
        selectAppThemeText.setText(getSelectedAppThemeName())
        selectAppLanguageText.setText(getSelectedLanguage())
        setCountryListListener()
        setThemeListListener()
        setLanguageListListener()
        with(settingsSaveButton) {
            isEnabled = false
            setOnClickListener { requireActivity().recreate() }
        }
    }

    private fun showDevelopersDialogFragment() {
        AlertDialog.Builder(requireContext()).setView(R.layout.developers_dialog_fragment)
            .setCancelable(true).setPositiveButton(R.string.connect_us) { _, _ ->
                composeEmail(
                    arrayOf(getString(R.string.developers_email)),
                    getString(R.string.mail_header_feedback)
                )
            }.create().show()
    }

    private fun composeEmail(emails: Array<String>, subject: String) {
        try {
            startActivity(getEmailSendingIntent(emails, subject))
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(
                requireContext(), getString(R.string.email_clients_not_found), Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun setCountryListListener() = with(binding) {
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.settings_options_list_item,
            getMapOfCountryNamesWithIndexes().keys.toTypedArray().sorted()
        )
        selectCountryText.setAdapter(adapter)
        selectCountryText.onItemClickListener =
            AdapterView.OnItemClickListener { adapterView, _, position, _ ->
                hideKeyboard()
                val selectedCountryName = adapterView.getItemAtPosition(position).toString()
                if (selectedCountryName != getSelectedCountryNameFromPreferences()) {
                    saveSelectedCountry(selectedCountryName)
                    viewModel.refreshData()
                }
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
                if (getString(ThemeModes.values()[position].nameResId) != getSelectedAppThemeName()) {
                    val selectedTheme = ThemeModes.values()[position].name
                    PreferencesHelper(
                        context = requireContext(), prefName = Constants.APP_PREFERENCES_THEME_MODE
                    ).save(parameter = selectedTheme)
                    selectAppThemeLayout.helperText = getString(R.string.settings_theme_helper_text)
                    setSaveButtonActive()
                }
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
                if (getString(Languages.values()[position].nameResId) != getSelectedLanguage()) {
                    val selectedLanguage = Languages.values()[position].languageCode
                    PreferencesHelper(
                        context = requireContext(), prefName = Constants.APP_PREFERENCES_LANGUAGE
                    ).save(parameter = selectedLanguage)
                    selectAppLanguageLayout.helperText =
                        getString(R.string.settings_theme_helper_text)
                    setSaveButtonActive()
                }
            }
    }

    private fun setSaveButtonActive() = with(binding) {
        settingsSaveButton.apply {
            isEnabled = true
            setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(), R.color.settings_save_button_selected_color
                )
            )
        }

    }

    private fun saveSelectedCountry(selectedCountryName: String) {
        getMapOfCountryNamesWithIndexes()[selectedCountryName]?.let { index ->
            PreferencesHelper(
                context = requireContext(), prefName = Constants.APP_PREFERENCES_COUNTRY_CODE
            ).save(index = index)
            App.countryCode = Countries.values()[index].countryCode
        }
    }

    private fun getSelectedAppThemeName(): String = PreferencesHelper(
        context = requireContext(), prefName = Constants.APP_PREFERENCES_THEME_MODE
    ).readString()?.let { selectedTheme ->
        getString(ThemeModes.valueOf(selectedTheme).nameResId)
    } ?: getString(ThemeModes.SYSTEM_MODE.nameResId)

    private fun getSelectedCountryNameFromPreferences(): String = PreferencesHelper(
        context = requireContext(), prefName = Constants.APP_PREFERENCES_COUNTRY_CODE
    ).readInt().let { index ->
        getString(Countries.values()[index].nameResId)
    }

    private fun getSelectedLanguage(): String = PreferencesHelper(
        context = requireContext(), prefName = Constants.APP_PREFERENCES_LANGUAGE
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