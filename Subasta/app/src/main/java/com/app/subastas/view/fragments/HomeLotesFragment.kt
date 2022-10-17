package com.app.subastas.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.app.subastas.R
import com.app.subastas.databinding.HomeLotesFragmentBinding
import com.app.subastas.viewmodel.AuthViewModel

class HomeLotesFragment: Fragment() {

    private var mBinding: HomeLotesFragmentBinding? = null
    private val binding get() = mBinding!!

    private val authViewModel: AuthViewModel by activityViewModels()

    private var type: String? = null
    private var idLote: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        type = arguments?.getString("key")
        idLote = arguments?.getInt("idLote")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = HomeLotesFragmentBinding.inflate(inflater, container, false)
            .apply {
                viewmodel = authViewModel
                lifecycleOwner = viewLifecycleOwner
            }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(type == "true") {
            val navHostFragment = childFragmentManager.findFragmentById(R.id.lots_navigation) as NavHostFragment
            val navController = navHostFragment.navController
            setUpTabsClickListener(navController)
            setUpObservers(navController)
        } else {
            val navHostFragment = childFragmentManager.findFragmentById(R.id.lots_navigation) as NavHostFragment
            val navController = navHostFragment.navController
            setUpTabsClickListener(navController)
            setUpObservers(navController)
        }

        binding.homeLotesMenu.setOnClickListener {
            findNavController().navigate(R.id.dialogCloseSession)
        }
    }

    private fun setUpObservers(navController: NavController) {
        authViewModel.loteState.observe(viewLifecycleOwner) {
            it?.let {
                if(it) {
                    colorLotes()
                }
                authViewModel.endShowLotesState()
            }
        }

        authViewModel.inscriptionState.observe(viewLifecycleOwner) {
            it?.let {
                if(it) {
                    colorInscriptions()
                }
                authViewModel.endShowInscriptionState()
            }
        }

        authViewModel.goInscriptions.observe(viewLifecycleOwner) {
            it?.let {
                if(it) {
                    colorInscriptions()
                    navController.navigate(R.id.inscriptionsFragment)
                }
                authViewModel.endGoInscriptionsFromAuction()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

    private fun setUpTabsClickListener(navController: NavController) {
        binding.homeLotesActionLotes.setOnClickListener {
            colorLotes()
            navController.navigate(R.id.availableLotsFragment)
        }

        binding.homeLotesActionInscriptions.setOnClickListener {
            colorInscriptions()
            navController.navigate(R.id.inscriptionsFragment)
        }
    }

    private fun colorLotes() {
        binding.homeLotesActionLotes.background = resources.getDrawable(R.drawable.button_style_selected)
        binding.homeLotesActionLotes.setTextColor(resources.getColor(R.color.primary_color))
        binding.homeLotesActionInscriptions.setBackgroundColor(resources.getColor(android.R.color.transparent))
        binding.homeLotesActionInscriptions.setTextColor(resources.getColor(R.color.primary_color))
    }

    private fun colorInscriptions() {
        binding.homeLotesActionInscriptions.background = resources.getDrawable(R.drawable.button_style_selected)
        binding.homeLotesActionInscriptions.setTextColor(resources.getColor(R.color.primary_color))
        binding.homeLotesActionLotes.setBackgroundColor(resources.getColor(android.R.color.transparent))
        binding.homeLotesActionLotes.setTextColor(resources.getColor(R.color.primary_color))
    }

}