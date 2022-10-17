package com.app.subastas.view.fragments.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import com.app.subastas.databinding.DialogConfirmRegisterBinding
import com.app.subastas.viewmodel.AuthViewModel

class DialogConfirmRegister: DialogFragment() {

    private var mBinding: DialogConfirmRegisterBinding? = null
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
        mBinding = DialogConfirmRegisterBinding.inflate(inflater, container, false)
            .apply {
                viewmodel = authViewModel
                lifecycleOwner = viewLifecycleOwner
            }

        dialog?.setCanceledOnTouchOutside(false)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navHost = NavHostFragment.findNavController(this)

        binding.dialogBookingLotsActionBooking.setOnClickListener {
            val direction = DialogConfirmRegisterDirections
                .actionDialogConfirmRegisterToDataValidationFragment()
            navHost.navigate(direction)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

}