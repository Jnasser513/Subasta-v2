package com.app.subastas.view.fragments.dialog

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.app.subastas.R
import com.app.subastas.data.network.UIState
import com.app.subastas.databinding.DialogEnterCodeBinding
import com.app.subastas.view.HomeActivity
import com.app.subastas.viewmodel.AuthViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

class DialogEnterCode : DialogFragment() {

    private var mBinding: DialogEnterCodeBinding? = null
    private val binding get() = mBinding!!

    private val authViewModel: AuthViewModel by activityViewModels()

    private val args: DialogEnterCodeArgs by navArgs()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true)
            {
                override fun handleOnBackPressed() {

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
        mBinding = DialogEnterCodeBinding.inflate(inflater, container, false)
            .apply {
                viewmodel = authViewModel
                lifecycleOwner = viewLifecycleOwner
            }

        binding.dialogEnterCodeFirst.doAfterTextChanged { message ->
            authViewModel.firstCode.value = message.toString()
        }

        binding.dialogEnterCodeSecond.doAfterTextChanged { message ->
            authViewModel.secondCode.value = message.toString()
        }

        binding.dialogEnterCodeThird.doAfterTextChanged { message ->
            authViewModel.thirdCode.value = message.toString()
        }

        binding.dialogEnterCodeFour.doAfterTextChanged { message ->
            authViewModel.fourCode.value = message.toString()
        }

        binding.dialogEnterCodeFive.doAfterTextChanged { message ->
            authViewModel.fiveCode.value = message.toString()
        }

        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.setCanceledOnTouchOutside(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navHost = NavHostFragment.findNavController(this)

        setUpChangeEdit()
        setUpObservers()

        binding.dialogEnterCodeActionStart.setOnClickListener {
            getFirebaseToken()
        }
    }

    private fun getFirebaseToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            } else {
                val email = args.email
                val password = args.password
                if (task.result != null) {
                    authViewModel.verifyCode(email, password, task.result!!)
                }
            }
        })
    }

    private fun setUpChangeEdit() {
        binding.dialogEnterCodeFirst.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count == 1) {
                    binding.dialogEnterCodeSecond.requestFocus()
                }
            }
        })

        binding.dialogEnterCodeSecond.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count == 1) {
                    binding.dialogEnterCodeThird.requestFocus()
                }
            }
        })

        binding.dialogEnterCodeThird.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count == 1) {
                    binding.dialogEnterCodeFour.requestFocus()
                }
            }
        })

        binding.dialogEnterCodeFour.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count == 1) {
                    binding.dialogEnterCodeFive.requestFocus()
                }
            }
        })
    }

    private fun setUpObservers() {
        goHomeFragment()
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
        binding.dialogEnterCodeActionStart.isClickable = true
        binding.dialogEnterCodeProgressBar.visibility = View.GONE
        binding.dialogEnterCodeActionStart.text = "Empezar"
    }

    private fun showProgressBar() {
        binding.dialogEnterCodeActionStart.isClickable = false
        binding.dialogEnterCodeProgressBar.visibility = View.VISIBLE
        binding.dialogEnterCodeActionStart.text = ""
    }

    private fun goHomeFragment() {
        authViewModel.goFragment.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    val intent = Intent(Intent(requireContext(), HomeActivity::class.java))
                    startActivity(intent)
                    activity?.finish()
                }
                authViewModel.endGoFragment()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

}