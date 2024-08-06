package com.infinity8.truecallermirror.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewbinding.ViewBinding


abstract class BaseFragment<B : ViewBinding>(
    val bindingFactory: (LayoutInflater) -> B,
) :
    Fragment() {
    lateinit var binding: B
    private var contextNullSafe: Context? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = bindingFactory(layoutInflater)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        contextNullSafe = context
    }

    /**CALL THIS IF YOU NEED CONTEXT */
    fun getContextNullSafety(): Context? {
        if (context != null) return context
        if (activity != null) return activity
        if (contextNullSafe != null) return contextNullSafe
        if (view != null && requireView().context != null) return requireView().context
        if (requireContext() != null) return requireContext()
        if (requireActivity() != null) return requireActivity()
        return if (requireView() != null && requireView().context != null) requireView().context else null
    }

    /**CALL THIS IF YOU NEED ACTIVITY */
    fun getActivityNullSafety(): FragmentActivity? =
        if (getContextNullSafety() != null && getContextNullSafety() is FragmentActivity) {
            /*It is observed that if context it not null then it will be
                  * the related host/container activity always*/
            getContextNullSafety() as FragmentActivity?
        } else null

}