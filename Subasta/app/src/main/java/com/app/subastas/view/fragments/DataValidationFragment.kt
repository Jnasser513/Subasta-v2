package com.app.subastas.view.fragments

import android.content.Context
import android.os.Bundle
import android.view.Gravity
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
import androidx.navigation.fragment.findNavController
import com.app.subastas.R
import com.app.subastas.data.network.UIState
import com.app.subastas.databinding.DataValidationFragmentBinding
import com.app.subastas.view.util.ShowToast
import com.app.subastas.viewmodel.AuthViewModel
import java.util.*

class DataValidationFragment : Fragment() {

    private var mBinding: DataValidationFragmentBinding? = null
    private val binding get() = mBinding!!

    private val authViewModel: AuthViewModel by activityViewModels()

    private val showToast = ShowToast()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    view?.findNavController()?.navigate(R.id.initFragment)
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
        mBinding = DataValidationFragmentBinding.inflate(inflater, container, false)
            .apply {
                viewmodel = authViewModel
                lifecycleOwner = viewLifecycleOwner
            }

        binding.dataValidationEmailEdit.doAfterTextChanged { message ->
            authViewModel.emailLogin.value = message.toString()
        }

        binding.dataValidationPasswordEdit.doAfterTextChanged { message ->
            authViewModel.passwordLogin.value = message.toString()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layout: View =
            layoutInflater.inflate(R.layout.toast_style, view.findViewById(R.id.ll_wrapper))
        setUpObservers(layout)

        binding.dataValidationActionReturn.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.dataValidationActionStart.setOnClickListener {
            if (binding.dataValidationEmailEdit.text.isNullOrEmpty() || binding.dataValidationPasswordEdit.text.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Debe llenar todos los campos", Toast.LENGTH_SHORT)
                    .show()
            } else {
                authViewModel.login()
            }
        }
    }

    private fun setUpObservers(layout: View) {
        goDataValidationFragment()
        observeState(layout)
    }

    private fun observeState(layout: View) {
        authViewModel.stateUI.observe(viewLifecycleOwner) { uiState ->
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
                activity?.applicationContext?.let {
                    val toast = Toast.makeText(requireContext(), uiState.message, Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.TOP or Gravity.START, 0, 0)
                    toast.show()
                }
            }
            else -> {

            }
        }
    }

    private fun endShowProgressBar() {
        binding.dialogEnterCodeLoading.visibility = View.GONE
        binding.dataValidationActionStart.text = "Ingresar"
    }

    private fun showProgressBar() {
        binding.dialogEnterCodeLoading.visibility = View.VISIBLE
        binding.dataValidationActionStart.text = ""
    }

    private fun goDataValidationFragment() {
        authViewModel.goCodeDialog.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    val direction =
                        DataValidationFragmentDirections.actionDataValidationFragmentToDialogEnterCode(
                            authViewModel.emailLogin.value.toString().trim()
                                .lowercase(Locale.getDefault()),
                            authViewModel.passwordLogin.value.toString().trim()
                        )
                    findNavController().navigate(direction)
                }
                authViewModel.endShowCodeDialog()
            }
        }
    }
}