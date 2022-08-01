package com.example.newsgb.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.newsgb.R
import com.example.newsgb.databinding.SettingsFragmentBinding
import com.example.newsgb.utils.PrivateSharedPreferences


class SettingsFragment : Fragment() {

    private var _binding: SettingsFragmentBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SettingsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() = with(binding) {
        val items = resources.getStringArray((R.array.Countries))
        val adapter = ArrayAdapter(requireContext(), R.layout.country_list_item, items)
        (selectCountryLayout.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        selectCountryLayout.hint = PrivateSharedPreferences(context!!).read()
        selectCountryText.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                val selectedItem = parent.getItemAtPosition(position).toString()
                PrivateSharedPreferences(context!!).save(selectedItem)
                Toast.makeText(requireContext(), "Selected: $selectedItem", Toast.LENGTH_SHORT)
                    .show()
            }
    }


    override fun onResume() {
        super.onResume()
        val country = PrivateSharedPreferences(context!!).read()
        Toast.makeText(requireContext(), "Selected: $country", Toast.LENGTH_SHORT).show()
    }

    companion object {
        @JvmStatic
        fun newInstance() = SettingsFragment()
    }

}
