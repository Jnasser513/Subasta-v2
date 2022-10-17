package com.app.subastas.view.fragments.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.app.subastas.databinding.DialogPaymentConfirmBinding
import com.app.subastas.viewmodel.AuthViewModel

class DialogPaymentConfirm : DialogFragment() {

    private var mBinding: DialogPaymentConfirmBinding? = null
    private val binding get() = mBinding!!

    private val authViewModel: AuthViewModel by activityViewModels()

    private val args: DialogPaymentConfirmArgs by navArgs()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
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
        mBinding = DialogPaymentConfirmBinding.inflate(inflater, container, false)
            .apply {
                viewmodel = authViewModel
                lifecycleOwner = viewLifecycleOwner
            }

        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.setCanceledOnTouchOutside(false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navHost = NavHostFragment.findNavController(this)

        if (args.typePayment == 0) {
            binding.dialogPaymentConfirmText.text =
                "¡Listo! \nTe notificaremos por aquí y por correo el día de la subasta"
        } else {
            binding.dialogPaymentConfirmText.text =
                "¡Listo! \nEl pago esta en proceso de verificación."
        }

        setUpObservers(navHost)
        setUpListeners(navHost)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

    private fun setUpObservers(navHost: NavController) {

    }

    private fun setUpListeners(navHost: NavController) {
        onOkClickListener(navHost)
    }

    private fun onOkClickListener(navHost: NavController) {
        binding.dialogBookingLotsActionBooking.setOnClickListener {
            val direction = DialogPaymentConfirmDirections
                .actionDialogPaymentConfirm2ToReceiptFragment2(
                    args.type,
                    args.porcent,
                    args.checkDetail
                )
            navHost.navigate(direction)
        }
    }

}