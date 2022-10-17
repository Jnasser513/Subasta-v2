package com.app.subastas.view.fragments.change_password

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.app.subastas.R
import com.app.subastas.data.network.UIState
import com.app.subastas.databinding.RecoveryCodeFragmentBinding
import com.app.subastas.viewmodel.AuthViewModel

class VerifyRecoveryCodeFragment: Fragment() {

    private var mBinding: RecoveryCodeFragmentBinding? = null
    private val binding get() = mBinding!!

    private val authViewModel: AuthViewModel by activityViewModels()

    private val args: VerifyRecoveryCodeFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = RecoveryCodeFragmentBinding.inflate(inflater, container, false)
            .apply {
                viewmodel = authViewModel
                lifecycleOwner = viewLifecycleOwner
            }

        binding.recoveryCode1.doAfterTextChanged { message ->
            authViewModel.recoveryCode1.value = message.toString()
        }

        binding.recoveryCode2.doAfterTextChanged { message ->
            authViewModel.recoveryCode2.value = message.toString()
        }

        binding.recoveryCode3.doAfterTextChanged { message ->
            authViewModel.recoveryCode3.value = message.toString()
        }

        binding.recoveryCode4.doAfterTextChanged { message ->
            authViewModel.recoveryCode4.value = message.toString()
        }

        binding.recoveryCode5.doAfterTextChanged { message ->
            authViewModel.recoveryCode5.value = message.toString()
        }

        initView()
        return binding.root
    }

    private fun initView() {
        binding.recoveryCodeEmailTitle.text = args.email
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpChangeEdit()
        verifyClickListener()
        setUpObservers()

        binding.recoveryCodeActionReturn.setOnClickListener {
            val navController = findNavController()
            navController.navigate(R.id.enterEmailFragment)
        }
    }

    private fun setUpChangeEdit() {
        binding.recoveryCode1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count == 1) {
                    binding.recoveryCode2.requestFocus()
                }
            }
        })

        binding.recoveryCode2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count == 1) {
                    binding.recoveryCode3.requestFocus()
                }
            }
        })

        binding.recoveryCode3.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count == 1) {
                    binding.recoveryCode4.requestFocus()
                }
            }
        })

        binding.recoveryCode4.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count == 1) {
                    binding.recoveryCode5.requestFocus()
                }
            }
        })
    }

    private fun verifyClickListener() {
        binding.recoveryCodeActionSend.setOnClickListener {
            authViewModel.verifyRecoveryCode(args.email)
        }
    }

    private fun setUpObservers() {
        authViewModel.goFragment.observe(viewLifecycleOwner) {
            it?.let {
                if(it) {
                    val completeCode = binding.recoveryCode1.text.toString() + binding.recoveryCode2.text.toString() +
                            binding.recoveryCode3.text.toString() + binding.recoveryCode4.text.toString() +
                            binding.recoveryCode5.text.toString()
                    val direction = VerifyRecoveryCodeFragmentDirections
                        .actionVerifyRecoveryCodeFragmentToChangePasswordFragment(completeCode)
                    val navController = findNavController()
                    navController.navigate(direction)
                }
                authViewModel.endGoFragment()
            }
        }

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
        binding.recoveryCodeActionSend.isClickable = true
        binding.recoveryCodeProgressBar.visibility = View.GONE
        binding.recoveryCodeActionSend.text = "Enviar"
    }

    private fun showProgressBar() {
        binding.recoveryCodeActionSend.isClickable = false
        binding.recoveryCodeProgressBar.visibility = View.VISIBLE
        binding.recoveryCodeActionSend.text = ""
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }


}