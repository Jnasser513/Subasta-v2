package com.app.subastas.view.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.app.subastas.AppApplication
import com.app.subastas.R
import com.app.subastas.data.entity.User
import com.app.subastas.data.entity.model.lots.LotesDetail
import com.app.subastas.data.network.UIState
import com.app.subastas.databinding.AvailableLostFragmentBinding
import com.app.subastas.view.MainActivity
import com.app.subastas.view.adapters.LotesAdapter
import com.app.subastas.viewmodel.LotViewModel
import com.app.subastas.viewmodel.ViewModelFactory


class AvailableLotsFragment : Fragment() {

    private var mBinding: AvailableLostFragmentBinding? = null
    private val binding get() = mBinding!!

    private val userApp by lazy {
        activity?.application as AppApplication
    }

    private val lotViewModel: LotViewModel by viewModels {
        ViewModelFactory(userApp.lotRepository)
    }

    private lateinit var mGridLayout: GridLayoutManager

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    activity?.finish()
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
        mBinding = AvailableLostFragmentBinding.inflate(inflater, container, false)
            .apply {
                viewmodel = lotViewModel
                lifecycleOwner = viewLifecycleOwner
            }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lotViewModel.userData1.observe(viewLifecycleOwner) { user ->
            setUpLotesRecyclerview(user)

            binding.swipeRefresh.setOnRefreshListener {
                setUpLotesRecyclerview(user)
                binding.swipeRefresh.isRefreshing = false
            }
        }

        setUpObservers()
    }

    private fun setUpObservers() {
        observeState()
        goLogin()
    }

    private fun observeState() {
        val layout: View =
            layoutInflater.inflate(R.layout.toast_style, view?.findViewById(R.id.ll_wrapper))
        lotViewModel.stateUI.observe(viewLifecycleOwner) { uiState ->
            handleUIState(uiState, layout)
        }
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
        binding.availableLotsProgressBar.visibility = View.GONE
    }

    private fun showProgressBar() {
        binding.availableLotsProgressBar.visibility = View.VISIBLE
    }

    private fun setUpLotesRecyclerview(user: List<User>) {
        try {
            mGridLayout =
                GridLayoutManager(requireContext(), resources.getInteger(R.integer.lotes_columns))
            binding.availableLotsRecyclerview.apply {
                setHasFixedSize(true)
                layoutManager = mGridLayout
                adapter = LotesAdapter()
            }

            lotViewModel.getLotesList(user[0].accessToken!!)

            lotViewModel.lotesList.observe(viewLifecycleOwner) { list ->
                if (list.isEmpty()) {
                    binding.availableLotsEmptyListContainer.visibility = View.VISIBLE
                    binding.availableLotsRecyclerview.visibility = View.GONE
                } else {
                    binding.availableLotsEmptyListContainer.visibility = View.GONE
                    binding.availableLotsRecyclerview.visibility = View.VISIBLE
                    (binding.availableLotsRecyclerview.adapter as LotesAdapter).setData(list)
                }
            }
        } catch (e: Exception) {
        }
    }

    private fun goLogin() {
        lotViewModel.goFragment.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    startActivity(Intent(Intent(requireContext(), MainActivity::class.java)))
                    activity?.finish()
                }
                lotViewModel.endGoFragment()
            }
        }
    }

}
