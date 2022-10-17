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
import com.app.subastas.databinding.DialogLeaveAuctionBinding
import com.app.subastas.view.HomeActivity
import com.app.subastas.viewmodel.AuthViewModel

class DialogLeaveAuction: DialogFragment() {

    private var mBinding: DialogLeaveAuctionBinding? = null
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
        mBinding = DialogLeaveAuctionBinding.inflate(inflater, container, false)
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

        setUpListeners(navHost)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

    private fun setUpListeners(navHost: NavController) {
        onLeaveClickListener()
        onCancelClickListener(navHost)
    }

    private fun onLeaveClickListener() {
        binding.dialogLeaveAuctionActionLeave.setOnClickListener {
            val intent = Intent(Intent(requireContext(), HomeActivity::class.java))
            startActivity(intent)
        }
    }

    private fun onCancelClickListener(navHost: NavController) {
        binding.dialogLeaveAuctionActionCancel.setOnClickListener {
            dismiss()
        }
    }

}