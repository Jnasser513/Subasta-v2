package com.app.subastas.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.app.subastas.AppApplication
import com.app.subastas.R
import com.app.subastas.databinding.WonLotsFragmentBinding
import com.app.subastas.view.MainActivity
import com.app.subastas.view.adapters.WonLotsAdapter
import com.app.subastas.view.util.Connectivity
import com.app.subastas.viewmodel.LotViewModel
import com.app.subastas.viewmodel.ViewModelFactory

class WonLotsFragment : Fragment() {

    private var mBinding: WonLotsFragmentBinding? = null
    private val binding get() = mBinding!!

    private val userApp by lazy {
        activity?.application as AppApplication
    }

    private val lotViewModel: LotViewModel by viewModels {
        ViewModelFactory(userApp.lotRepository)
    }

    private lateinit var mGridLayout: GridLayoutManager

    private val connectivity = Connectivity()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mBinding = WonLotsFragmentBinding.inflate(inflater, container, false)
            .apply {
                viewmodel = lotViewModel
                lifecycleOwner = viewLifecycleOwner
            }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpListeners()
        setUpObservers()
    }

    private fun setUpObservers() {
        goLogin()
    }

    private fun setUpListeners() {

        setUpRecyclerView()

        binding.swipeRefresh.setOnRefreshListener {
            setUpRecyclerView()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun setUpRecyclerView() {
        lotViewModel.userData1.observe(viewLifecycleOwner) { user ->
            try {
                mGridLayout =
                    GridLayoutManager(
                        requireContext(),
                        resources.getInteger(R.integer.lotes_columns)
                    )
                binding.wonLotsRecyclerview.apply {
                    setHasFixedSize(true)
                    layoutManager = mGridLayout
                    adapter = WonLotsAdapter()
                }

                user[0].accessToken?.let { lotViewModel.getWonLots(it) }

                lotViewModel.wonList.observe(viewLifecycleOwner) { list ->
                    if (list.isEmpty()) {
                        binding.wonLotsEmptyListContainer.visibility = View.VISIBLE
                        binding.wonLotsRecyclerview.visibility = View.GONE
                    } else {
                        binding.wonLotsEmptyListContainer.visibility = View.GONE
                        binding.wonLotsRecyclerview.visibility = View.VISIBLE
                        (binding.wonLotsRecyclerview.adapter as WonLotsAdapter).setData(list)
                        (binding.wonLotsRecyclerview.adapter as WonLotsAdapter).getViewModel(
                            lotViewModel
                        )
                        (binding.wonLotsRecyclerview.adapter as WonLotsAdapter).getLifeCycleOwner(viewLifecycleOwner)
                    }
                }
            } catch (e: Exception) {
            }
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

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }
}