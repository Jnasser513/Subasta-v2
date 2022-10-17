package com.app.subastas.view.fragments.dialog

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import com.app.subastas.databinding.DialogCloseSessionBinding
import com.app.subastas.view.MainActivity
import com.app.subastas.viewmodel.AuthViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DialogCloseSession: BottomSheetDialogFragment() {

    private var mBinding: DialogCloseSessionBinding? = null
    private val binding get() = mBinding!!

    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true)
            {
                override fun handleOnBackPressed() {

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
        mBinding = DialogCloseSessionBinding.inflate(inflater, container, false)
            .apply {
                viewmodel = authViewModel
                lifecycleOwner = viewLifecycleOwner
            }

        dialog?.setCanceledOnTouchOutside(true)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpListeners()
        setUpObservers()
    }

    private fun setUpListeners() {

        binding.dialogCloseSessionSignout.setOnClickListener {
            authViewModel.signOut()
        }

        binding.dialogCloseSessionChangePassword.setOnClickListener {
            val direction = DialogCloseSessionDirections
                .actionDialogCloseSessionToChangePasswordFragment2(null, true)
            NavHostFragment.findNavController(this).navigate(direction)
            /*val intent = Intent(Intent(requireContext(), MainActivity::class.java))
            intent.putExtra("change", true)
            startActivity(intent)*/
        }
    }

    private fun setUpObservers() {
        goFragmentObserver()
    }

    private fun goFragmentObserver() {
        authViewModel.goFragment.observe(viewLifecycleOwner) {
            it?.let {
                if(it) {
                    startActivity(Intent(Intent(requireContext(), MainActivity::class.java)))
                    activity?.finish()
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