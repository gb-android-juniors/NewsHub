package com.example.newsgb.search.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.newsgb.databinding.SearchDialogFragmentBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SearchDialogFragment : BottomSheetDialogFragment() {

    private var _binding: SearchDialogFragmentBinding? = null
    private val binding get() = _binding!!
    private var searchClickListener: SearchClickListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SearchDialogFragmentBinding.inflate(inflater, null, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onDestroyView() {
        _binding = null
        searchClickListener = null
        super.onDestroyView()
    }

    private fun initView() = with(binding) {
        searchInputLayout.setEndIconOnClickListener {
            searchClickListener?.onClick(phrase = searchEditText.text.toString())
            dismiss()
        }
    }

    fun setOnSearchClickListener(listener: SearchClickListener) {
        searchClickListener = listener
    }

    companion object {
        fun newInstance() = SearchDialogFragment()
    }
}