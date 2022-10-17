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
import androidx.navigation.fragment.navArgs
import com.app.subastas.R
import com.app.subastas.databinding.DialogConfirmPayBinding
import com.app.subastas.viewmodel.AuthViewModel

class DialogConfirmPay: DialogFragment() {

    private var mBinding: DialogConfirmPayBinding? = null
    private val binding get() = mBinding!!
    
    private val args: DialogConfirmPayArgs by navArgs()

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
        mBinding = DialogConfirmPayBinding.inflate(inflater, container, false)

        dialog?.setCanceledOnTouchOutside(true)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {
        binding.dialogConfirmPayPriceText.text = getString(
            R.string.dialog_confirm_pay_text,
            args.price.toString()
            )

        val navController = NavHostFragment.findNavController(this)

        binding.dialogConfirmPayActionPay.setOnClickListener {
            val direction = DialogConfirmPayDirections
                .actionDialogConfirmPayToPaymentMethodFragment2(args.idLote.toString(), args.price.toString(), args.porc, args.typePayment, args.idSuscripcion)
            navController.navigate(direction)
            authViewModel.showLotesState()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

}