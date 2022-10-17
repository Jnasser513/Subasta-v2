package com.app.subastas.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.app.subastas.AppApplication
import com.app.subastas.R
import com.app.subastas.data.entity.User
import com.app.subastas.data.network.UIState
import com.app.subastas.databinding.RegisteredLotsFragmentBinding
import com.app.subastas.view.MainActivity
import com.app.subastas.view.adapters.RegisteredLotsAdapter
import com.app.subastas.view.util.Connectivity
import com.app.subastas.viewmodel.AuthViewModel
import com.app.subastas.viewmodel.SubscriptionViewModel
import com.app.subastas.viewmodel.ViewModelFactory

class RegisteredLotsFragment : Fragment() {

    private var mBinding: RegisteredLotsFragmentBinding? = null
    private val binding get() = mBinding!!

    private val userApp by lazy {
        activity?.application as AppApplication
    }

    private val subscriptionViewModel: SubscriptionViewModel by viewModels {
        ViewModelFactory(userApp.subscriptionRepository)
    }

    private val authViewModel: AuthViewModel by activityViewModels()

    private lateinit var mGridLayout: GridLayoutManager

    private val connectivity = Connectivity()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = RegisteredLotsFragmentBinding.inflate(inflater, container, false)
            .apply {
                viewmodel = subscriptionViewModel
                lifecycleOwner = viewLifecycleOwner
            }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscriptionViewModel.userData1.observe(viewLifecycleOwner) { user ->
            setUpRegisteredLotsRecyclerview(user)

            binding.swipeRefresh.setOnRefreshListener {
                setUpRegisteredLotsRecyclerview(user)
                binding.swipeRefresh.isRefreshing = false
            }
        }

        observeState()
    }

    private fun setUpRegisteredLotsRecyclerview(user: List<User>) {
        try {
            mGridLayout =
                GridLayoutManager(requireContext(), resources.getInteger(R.integer.lotes_columns))
            binding.registeredLotsRecyclerview.apply {
                setHasFixedSize(true)
                layoutManager = mGridLayout
                adapter = RegisteredLotsAdapter()
            }

            user[0].accessToken?.let { subscriptionViewModel.getRegisteredLots(it) }
            (binding.registeredLotsRecyclerview.adapter as RegisteredLotsAdapter).getUserData(user[0])

            val navHostFragment =
                activity?.supportFragmentManager?.findFragmentById(R.id.home_nav) as NavHostFragment
            val navController = navHostFragment.navController
            (binding.registeredLotsRecyclerview.adapter as RegisteredLotsAdapter).getNavController(
                navController
            )
            (binding.registeredLotsRecyclerview.adapter as RegisteredLotsAdapter).getAppViewModel(
                authViewModel
            )
            (binding.registeredLotsRecyclerview.adapter as RegisteredLotsAdapter).getLifeCycleOwner(
                viewLifecycleOwner
            )

            subscriptionViewModel.registeredList.observe(viewLifecycleOwner) { list ->
                if (list.isEmpty()) {
                    binding.registeredLotsEmptyListContainer.visibility = View.VISIBLE
                    binding.registeredLotsRecyclerview.visibility = View.GONE
                } else {
                    binding.registeredLotsEmptyListContainer.visibility = View.GONE
                    binding.registeredLotsRecyclerview.visibility = View.VISIBLE
                    (binding.registeredLotsRecyclerview.adapter as RegisteredLotsAdapter).setData(
                        list
                    )
                }
            }
        } catch (e: Exception) {
        }
    }

    private fun observeState() {
        val layout: View =
            layoutInflater.inflate(R.layout.toast_style, view?.findViewById(R.id.ll_wrapper))
        subscriptionViewModel.stateUI.observe(viewLifecycleOwner) { uiState ->
            handleUIState(uiState, layout)
        }
        goLogin()
    }

    private fun handleUIState(uiState: UIState<Int>?, layout: View) {
        val toastText = layout.findViewById<TextView>(R.id.toast_text)
        layout.findViewById<ImageView>(R.id.toast_image).visibility = View.GONE
        when (uiState) {
            is UIState.Loading -> {
                showProgressBar()
            }
            is UIState.Success -> {
                endShowProgressBar()
            }
            is UIState.Error -> {
                endShowProgressBar()
                Toast.makeText(requireContext(), uiState.message, Toast.LENGTH_SHORT).show()
                //activity?.applicationContext?.let { showToast.showToast(it, layout, toastText, uiState.message) }
            }
            else -> {

            }
        }
    }

    private fun endShowProgressBar() {
        binding.registeredLotsProgressBar.visibility = View.GONE
    }

    private fun showProgressBar() {
        binding.registeredLotsProgressBar.visibility = View.VISIBLE
    }

    private fun goLogin() {
        subscriptionViewModel.goFragment.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    startActivity(Intent(Intent(requireContext(), MainActivity::class.java)))
                    activity?.finish()
                }
                subscriptionViewModel.endGoFragment()
            }
        }
    }

}