package com.app.subastas.view.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.app.subastas.R
import com.app.subastas.databinding.InscriptionsFragmentBinding
import com.app.subastas.view.adapters.InscriptionsViewPagerAdapter
import com.app.subastas.viewmodel.AuthViewModel

class InscriptionsFragment : Fragment() {

    private var mBinding: InscriptionsFragmentBinding? = null
    val binding get() = mBinding!!

    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    view?.findNavController()?.navigate(R.id.availableLotsFragment)
                    authViewModel.showLotesState()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            callback
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mBinding = InscriptionsFragmentBinding.inflate(inflater, container, false)
            .apply {
                viewmodel = authViewModel
                lifecycleOwner = viewLifecycleOwner
            }

        addFragment()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

    private fun addFragment() {
        val adapter = InscriptionsViewPagerAdapter(childFragmentManager)
        adapter.addFragment(RegisteredLotsFragment(), "Lotes inscritos")
        adapter.addFragment(WonLotsFragment(), "Lotes ganados")
        binding.viewPagerInscriptions.adapter = adapter
        binding.directoryTabLayout.setupWithViewPager(binding.viewPagerInscriptions)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*binding.directoryTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if(tab?.position == 0) {
                    Log.d("POSITION", "0")
                } else if(tab?.position == 1) {
                    Log.d("POSITION", "1")
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })*/
    }

}