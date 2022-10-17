package com.app.subastas.view.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.app.subastas.R
import com.app.subastas.databinding.NoRegisterFragmentBinding
import com.app.subastas.viewmodel.AuthViewModel

class NoRegisterFragment : Fragment() {

    private var mBinding: NoRegisterFragmentBinding? = null
    private val binding get() = mBinding!!

    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    view?.findNavController()?.navigate(R.id.initFragment)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            callback
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = NoRegisterFragmentBinding.inflate(inflater, container, false)
            .apply {
                viewmodel = authViewModel
                lifecycleOwner = viewLifecycleOwner
            }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

    private fun setUpListeners() {
        onNaturalPersonClickListener()
        onJuridicPErsonClickListener()
        onReturnClickListener()
    }

    private fun onNaturalPersonClickListener() {
        binding.noRegisterActionNaturalPerson.setOnClickListener {
            val direction = NoRegisterFragmentDirections
                .actionNoRegisterFragmentToStepFragment(1)
            it.findNavController().navigate(direction)
        }
    }

    private fun onJuridicPErsonClickListener() {
        binding.noRegisterActionJuridicPerson.setOnClickListener {
            val direction = NoRegisterFragmentDirections
                .actionNoRegisterFragmentToStepFragment(2)
            it.findNavController().navigate(direction)
        }
    }

    private fun onReturnClickListener() {
        binding.noRegisterActionReturn.setOnClickListener {
            activity?.onBackPressed()
        }
    }

}