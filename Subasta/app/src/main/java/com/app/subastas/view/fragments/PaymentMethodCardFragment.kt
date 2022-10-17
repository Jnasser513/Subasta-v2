package com.app.subastas.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.app.subastas.databinding.PaymentMethodCardFragmentBinding
import com.app.subastas.viewmodel.AuthViewModel

class PaymentMethodCardFragment : Fragment() {

    private var mBinding: PaymentMethodCardFragmentBinding? = null
    private val binding get() = mBinding!!

    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = PaymentMethodCardFragmentBinding.inflate(inflater, container, false)
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
        binding.paymentMethodCardActionPay.setOnClickListener {
            val direction = PaymentMethodCardFragmentDirections
                .actionPaymentMethodCardFragment2ToMyCardsFragment2()
            it.findNavController().navigate(direction)
        }
    }

}