package com.infinity8.truecallermirror.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.infinity8.truecallermirror.databinding.FragmentNoteBottomSheetBinding
import com.infinity8.truecallermirror.viewmodel.CallLogViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoteBottomSheetDialogFragment(val id: Long, val number: String) :
    BottomSheetDialogFragment() {
    private var _binding: FragmentNoteBottomSheetBinding? = null
    private val binding get() = _binding!!
    private val callViewModel: CallLogViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding?.addNoteBtn?.setOnClickListener {
            callViewModel.updateNote(id, number)
            dismiss()
        }

        // Setup additional actions if needed
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}