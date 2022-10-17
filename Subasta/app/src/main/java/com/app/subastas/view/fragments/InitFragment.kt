package com.app.subastas.view.fragments

import android.content.Context
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.app.subastas.databinding.InitFragmentBinding
import com.app.subastas.viewmodel.AuthViewModel

class InitFragment : Fragment() {

    private var mBinding: InitFragmentBinding? = null
    private val binding get() = mBinding!!

    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    activity?.finishAffinity()
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
        mBinding = InitFragmentBinding.inflate(inflater, container, false)
            .apply {
                viewmodel = authViewModel
                lifecycleOwner = viewLifecycleOwner
            }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authViewModel.goChangePassword.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    val direction = InitFragmentDirections
                        .actionInitFragmentToChangePasswordFragment(null, true)
                    findNavController().navigate(direction)
                }
                authViewModel.endGoChangePassword()
            }
        }

        initView()
        setUpListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

    private fun initView() {
        val noRegisterLined: SpannableString = SpannableString("¿Aún no está registrado?")
        noRegisterLined.setSpan(UnderlineSpan(), 0, noRegisterLined.length, 0)
        binding.initActionRegister.text = noRegisterLined

        val forgotLined: SpannableString = SpannableString("Olvidé mi contraseña")
        forgotLined.setSpan(UnderlineSpan(), 0, forgotLined.length, 0)
        binding.initActionForgot.text = forgotLined
    }

    private fun setUpListeners() {
        goDataValidationFragment()
        goRegisterFragment()
        goForgotPassword()
    }

    private fun goDataValidationFragment() {
        binding.initActionStart.setOnClickListener {
            val direction = InitFragmentDirections
                .actionInitFragmentToDataValidationFragment()
            it.findNavController().navigate(direction)
        }
    }

    private fun goRegisterFragment() {
        binding.initActionRegister.setOnClickListener {
            val direction = InitFragmentDirections
                .actionInitFragmentToNoRegisterFragment()
            it.findNavController().navigate(direction)
        }
    }

    private fun goForgotPassword() {
        binding.initActionForgot.setOnClickListener {
            val direction = InitFragmentDirections
                .actionInitFragmentToEnterEmailFragment()
            it.findNavController().navigate(direction)
        }
    }

}