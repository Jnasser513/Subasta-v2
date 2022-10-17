package com.app.subastas.view.fragments.change_password

import android.content.Intent
import android.os.Bundle
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
import com.app.subastas.data.entity.model.change_password.ChangePasswordBody
import com.app.subastas.data.network.UIState
import com.app.subastas.databinding.ChangePasswordFragmentBinding
import com.app.subastas.view.HomeActivity
import com.app.subastas.viewmodel.AuthViewModel

class ChangePasswordFragment : Fragment() {

    private var mBinding: ChangePasswordFragmentBinding? = null
    private val binding get() = mBinding!!

    private val authViewModel: AuthViewModel by activityViewModels()

    private val args: ChangePasswordFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = ChangePasswordFragmentBinding.inflate(inflater, container, false)
            .apply {
                viewmodel = authViewModel
                lifecycleOwner = viewLifecycleOwner
            }

        binding.changePasswordPasswordEdit.doAfterTextChanged { message ->
            authViewModel.passwordChange.value = message.toString()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpListeners()
        setUpObservers()
    }

    private fun setUpListeners() {
        onChangeClickListener()

        binding.changePasswordActionReturn.setOnClickListener {
            if(args.inside) {
                activity?.onBackPressed()
            } else {
                val navController = findNavController()
                navController.navigate(R.id.verifyRecoveryCodeFragment)
            }
        }
    }

    private fun onChangeClickListener() {
        binding.changePasswordActionChange.setOnClickListener {
            if (binding.changePasswordPasswordEdit.text.isNullOrEmpty() || binding.changePasswordRepeatEdit.text.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Debe llenar todos los campos", Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (binding.changePasswordRepeatEdit.text.toString() != binding.changePasswordPasswordEdit.text.toString()) {
                    Toast.makeText(
                        requireContext(),
                        "Las contraseñas deben coincidir",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    if(args.inside) {
                        authViewModel.userData1.observe(viewLifecycleOwner) { user ->
                            authViewModel.updatePassword(
                                user[0].accessToken!!,
                                user[0].email
                            )
                        }
                    } else {
                        val body = ChangePasswordBody(args.code!!, binding.changePasswordPasswordEdit.text.toString())
                        authViewModel.changePassword(body)
                    }
                }
            }
        }
    }

    private fun setUpObservers() {
        goFragmentObserver()
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
                binding.changePasswordPasswordEdit.setText("")
                binding.changePasswordRepeatEdit.setText("")
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
        binding.changePasswordProgressBar.visibility = View.GONE
        binding.changePasswordActionChange.text = "Cambiar"
    }

    private fun showProgressBar() {
        binding.changePasswordActionChange.isClickable = false
        binding.changePasswordProgressBar.visibility = View.VISIBLE
        binding.changePasswordActionChange.text = ""
    }

    private fun goFragmentObserver() {
        authViewModel.goFragment.observe(viewLifecycleOwner) {
            it?.let {
                if(it) {
                    val direction = ChangePasswordFragmentDirections
                        .actionChangePasswordFragmentToInitFragment()
                    val navController = findNavController()
                    navController.navigate(direction)
                }
                authViewModel.endGoFragment()
            }
        }

        authViewModel.goChangePassword.observe(viewLifecycleOwner) {
            it?.let {
                if(it) {
                    startActivity(Intent(Intent(requireContext(), HomeActivity::class.java)))
                }
                authViewModel.endGoChangePassword()
            }
        }
    }

}