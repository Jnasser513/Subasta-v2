package com.app.subastas.view.fragments.dialog

import android.content.Context
import android.content.Intent
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
import com.app.subastas.R
import com.app.subastas.databinding.DialogBookingLotsBinding
import com.app.subastas.view.HomeActivity
import com.app.subastas.viewmodel.AuthViewModel

class DialogBookingLots: DialogFragment() {

    private var mBinding: DialogBookingLotsBinding? = null
    private val binding get() = mBinding!!

    private val authViewModel: AuthViewModel by activityViewModels()

    private val args: DialogBookingLotsArgs by navArgs()

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
        mBinding = DialogBookingLotsBinding.inflate(inflater, container, false)
            .apply {
                viewmodel = authViewModel
                lifecycleOwner = viewLifecycleOwner
            }

        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.setCanceledOnTouchOutside(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navHost = NavHostFragment.findNavController(this)

        initView()
        setUpObservers(navHost)
        setUpListeners(navHost)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

    private fun initView() {
        val porcentaje = args.porc.toString().split(".")

        binding.dialogBookingLotsText.text = resources.getString(
            R.string.dialog_booking_lots_text,
            porcentaje[0].toInt()
        )
    }

    private fun setUpObservers(navHost: NavController) {

    }

    private fun setUpListeners(navHost: NavController) {
        onBookingClickListener(navHost)

        binding.dialogBookingLotsActionCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun onBookingClickListener(navHost: NavController) {
        binding.dialogBookingLotsActionBooking.setOnClickListener {
            val direction = DialogBookingLotsDirections
                .actionDialogBookingLots2ToPaymentMethodFragment2(args.idLote, args.precio, args.porc, 0)
            navHost.navigate(direction)
        }
    }

}