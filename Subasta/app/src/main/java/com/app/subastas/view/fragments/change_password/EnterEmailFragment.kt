package com.app.subastas.view.fragments.change_password

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.app.subastas.R
import com.app.subastas.data.network.UIState
import com.app.subastas.databinding.EnterEmailFragmentBinding
import com.app.subastas.viewmodel.AuthViewModel
import java.util.*

class EnterEmailFragment: Fragment() {

    private var mBinding: EnterEmailFragmentBinding? = null
    private val binding get() = mBinding!!

    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val direction = EnterEmailFragmentDirections
                        .actionEnterEmailFragmentToInitFragment()
                    view?.findNavController()?.navigate(direction)
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
        mBinding = EnterEmailFragmentBinding.inflate(inflater, container, false)
            .apply {
                viewmodel = authViewModel
                lifecycleOwner = viewLifecycleOwner
            }

        binding.changePasswordEmailEdit.doAfterTextChanged { message ->
            authViewModel.recoveryCode.value = message.toString()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpListeners()
        setUpObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

    private fun setUpListeners() {
        goInitFragment()
        sendRecoveryCode()
    }

    private fun setUpObservers() {
        goCodeDialogObserver()
        observeState()
    }

    private fun observeState() {
        val layout: View = layoutInflater.inflate(R.layout.toast_style, view?.findViewById(R.id.ll_wrapper))
        authViewModel.stateUI.observe(viewLifecycleOwner) { uiState ->
            handleUIState(uiState, layout)
        }
    }

    private fun handleUIState(uiState: UIState<Int>?, layout: View) {
        val toastText = layout.findViewById<TextView>(R.id.toast_text)
        layout.findViewById<ImageView>(R.id.toast_image).visibility = View.GONE
        when(uiState) {
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
        binding.changePasswordActionChange.isClickable = true
        binding.enterEmailProgressBar.visibility = View.GONE
        binding.changePasswordActionChange.text = "Enviar"
    }

    private fun showProgressBar() {
        binding.changePasswordActionChange.isClickable = false
        binding.enterEmailProgressBar.visibility = View.VISIBLE
        binding.changePasswordActionChange.text = ""
    }

    private fun goInitFragment() {
        binding.changePasswordActionReturn.setOnClickListener {
            val direction = EnterEmailFragmentDirections
                .actionEnterEmailFragmentToInitFragment()
            it.findNavController().navigate(direction)
        }
    }

    private fun sendRecoveryCode() {
        binding.changePasswordActionChange.setOnClickListener {
            if(binding.changePasswordEmailEdit.text.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Debe llenar todos los campos", Toast.LENGTH_SHORT)
                    .show()
            } else {
                authViewModel.sendRecoveryCode()
            }
        }
    }

    private fun goCodeDialogObserver() {
        authViewModel.goCodeDialog.observe(viewLifecycleOwner) {
            it?.let {
                if(it) {
                    val direction = EnterEmailFragmentDirections
                        .actionEnterEmailFragmentToVerifyRecoveryCodeFragment(binding.changePasswordEmailEdit.text.toString().trim()
                            .lowercase(Locale.getDefault()))
                    view?.findNavController()?.navigate(direction)
                }
                authViewModel.endShowCodeDialog()
            }
        }
    }

}