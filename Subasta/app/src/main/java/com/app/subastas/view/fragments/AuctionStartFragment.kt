package com.app.subastas.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.app.subastas.R
import com.app.subastas.databinding.AuctionStartFragmentBinding
import com.app.subastas.viewmodel.AuthViewModel

class AuctionStartFragment : Fragment() {

    private var mBinding: AuctionStartFragmentBinding? = null
    private val binding get() = mBinding!!

    private val authViewModel: AuthViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = AuctionStartFragmentBinding.inflate(inflater, container, false)
            .apply {
                viewmodel = authViewModel
                lifecycleOwner = viewLifecycleOwner
            }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAuctionCircle()
    }

    private fun initAuctionCircle() {
        binding.circularProgressBar.apply {
            progressMax = 100f
            setProgressWithAnimation(100f)
            progressBarWidth = 20f
            backgroundProgressBarWidth = 20f
            progressBarColor = resources.getColor(R.color.grey)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

}