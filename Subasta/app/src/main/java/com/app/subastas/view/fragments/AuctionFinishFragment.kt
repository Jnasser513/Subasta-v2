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
import com.app.subastas.databinding.AuctionFinishFragmentBinding
import com.app.subastas.view.HomeActivity
import com.app.subastas.viewmodel.AuthViewModel

class AuctionFinishFragment : Fragment() {

    private var mBinding: AuctionFinishFragmentBinding? = null
    private val binding get() = mBinding!!

    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val intent = Intent(Intent(requireContext(), HomeActivity::class.java))
                    //intent.putExtra("key", true)
                    startActivity(intent)
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
        mBinding = AuctionFinishFragmentBinding.inflate(inflater, container, false)
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
        binding.auctionFinishActionMyLots.setOnClickListener {
            val intent = Intent(Intent(requireContext(), HomeActivity::class.java))
            startActivity(intent)
        }
    }

}