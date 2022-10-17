package com.app.subastas.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.app.subastas.databinding.ThirdAddFragmentBinding
import com.app.subastas.view.MainActivity
import com.app.subastas.viewmodel.AuthViewModel

class ThirdAddFragment: Fragment() {

    private var mBinding: ThirdAddFragmentBinding? = null
    private val binding get() = mBinding!!

    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = ThirdAddFragmentBinding.inflate(inflater, container, false)
            .apply {
                viewmodel = authViewModel
                lifecycleOwner = viewLifecycleOwner
            }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpObservers()
    }

    private fun setUpObservers() {
        onChangeStateObserver()
    }

    private fun onChangeStateObserver() {
        authViewModel.goFragment.observe(viewLifecycleOwner) {
            it?.let {
                if(it) {
                    startActivity(Intent(Intent(requireContext(), MainActivity::class.java)))
                }
                authViewModel.endGoFragment()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }
}