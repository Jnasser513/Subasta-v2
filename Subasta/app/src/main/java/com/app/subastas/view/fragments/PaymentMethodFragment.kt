package com.app.subastas.view.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.app.subastas.databinding.PaymentMethodFragmentBinding
import com.app.subastas.view.HomeActivity
import com.app.subastas.viewmodel.AuthViewModel

class PaymentMethodFragment: Fragment() {

    private var mBinding: PaymentMethodFragmentBinding? = null
    private val binding get() = mBinding!!

    private val authViewModel: AuthViewModel by activityViewModels()

    private val args: PaymentMethodFragmentArgs by navArgs()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true)
            {
                override fun handleOnBackPressed() {
                    startActivity(Intent(Intent(requireContext(), HomeActivity::class.java)))
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
        mBinding = PaymentMethodFragmentBinding.inflate(inflater, container, false)
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
        onCardClickListener()
        onTransferClickListener()
        onCheckClickListener()
    }

    private fun onCardClickListener() {
        binding.paymentMethodCard.setOnClickListener {
            val direction = PaymentMethodFragmentDirections
                .actionPaymentMethodFragment2ToPaymentMethodCardFragment2()
            it.findNavController().navigate(direction)
        }
    }

    private fun onTransferClickListener() {
        binding.paymentMethodTransfer.setOnClickListener {
            val direction = PaymentMethodFragmentDirections
                .actionPaymentMethodFragment2ToPaymentMethodTransferFragment2(args.idLote, args.precio, args.porc, args.typePayment, args.idSuscripcion, args.again, args.againTotal)
            it.findNavController().navigate(direction)
        }
    }

    private fun onCheckClickListener() {
        binding.paymentMethodCheck.setOnClickListener {
            val direction = args.idLote.let { it1 ->
                PaymentMethodFragmentDirections
                    .actionPaymentMethodFragment2ToPaymentMethodCheckFragment2(it1, args.precio, args.porc, args.typePayment, args.idSuscripcion, args.again, args.againTotal)
            }
            it.findNavController().navigate(direction)
        }
    }

}