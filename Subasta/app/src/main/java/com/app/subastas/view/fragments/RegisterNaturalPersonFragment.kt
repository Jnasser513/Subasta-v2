package com.app.subastas.view.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.app.subastas.AppApplication
import com.app.subastas.R
import com.app.subastas.data.entity.model.parameters.DepartmentsDetail
import com.app.subastas.data.entity.model.parameters.MunicipalitiesDetail
import com.app.subastas.data.network.UIState
import com.app.subastas.databinding.RegisterNaturalPersonFragmentBinding
import com.app.subastas.viewmodel.AuthViewModel
import com.app.subastas.viewmodel.DataViewModel
import com.app.subastas.viewmodel.ViewModelFactory

class RegisterNaturalPersonFragment : Fragment() {

    private var mBinding: RegisterNaturalPersonFragmentBinding? = null
    private val binding get() = mBinding!!

    private val authViewModel: AuthViewModel by activityViewModels()

    private val userApp by lazy {
        activity?.application as AppApplication
    }

    private val dataViewModel: DataViewModel by viewModels {
        ViewModelFactory(userApp.dataRepository)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val direction = RegisterNaturalPersonFragmentDirections
                        .actionRegisterNaturalPersonFragmentToStepFragment(1)
                    view?.findNavController()?.navigate(direction)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            callback
        )
    }

    private val spinnerId = 1
    private var municipality: String = "Ahuachapán"
    private var department: String = "Ahuachapán"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = RegisterNaturalPersonFragmentBinding.inflate(inflater, container, false)
            .apply {
                viewmodel = authViewModel
                lifecycleOwner = viewLifecycleOwner
            }

        binding.registerNaturalPersonNameEdit.doAfterTextChanged { message ->
            authViewModel.nameRegister.value = message.toString()
        }

        binding.registerNaturalPersonDirectionEdit.doAfterTextChanged { message ->
            authViewModel.directionRegister.value = message.toString()
        }

        binding.registerNaturalPersonDuiEdit.doAfterTextChanged { message ->
            authViewModel.duiRegister.value = message.toString()
        }

        binding.registerNaturalPersonNitEdit.doAfterTextChanged { message ->
            authViewModel.nitRegister.value = message.toString()
        }

        binding.registerNaturalPersonTelephoneEdit.doAfterTextChanged { message ->
            authViewModel.phoneRegister.value = message.toString()
        }

        binding.registerNaturalPersonEmailEdit.doAfterTextChanged { message ->
            authViewModel.emailRegister.value = message.toString()
        }

        binding.registerNaturalPersonPasswordEdit.doAfterTextChanged { message ->
            authViewModel.passwordRegister.value = message.toString()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.registerNaturalPersonActionReturn.setOnClickListener {
            activity?.onBackPressed()
        }

        dataViewModel.getDepartmentList()

        setUpDepartmentSpinner()

        onCreateRegisterClickListener()
        //showConfirmDialog()
        setUpObservers()
    }

    private fun setUpObservers() {
        observeState()
    }

    private fun observeState() {
        //val layout: View = layoutInflater.inflate(R.layout.toast_style, view?.findViewById(R.id.ll_wrapper))
        authViewModel.stateUI.observe(viewLifecycleOwner) { uiState ->
            handleUIState(uiState)
        }
        dataViewModel.status.observe(viewLifecycleOwner) { uiState ->
            handleDataUIState(uiState)
        }
    }

    private fun handleUIState(uiState: UIState<Int>?) {
        //val toastText = layout.findViewById<TextView>(R.id.toast_text)
        //layout.findViewById<ImageView>(R.id.toast_image).visibility = View.GONE
        when (uiState) {
            is UIState.Loading -> {
                showProgressBar()
            }
            is UIState.Success -> {
                endShowProgressBar()
                val direction = RegisterNaturalPersonFragmentDirections
                    .actionRegisterNaturalPersonFragmentToDialogConfirmRegister()
                findNavController().navigate(direction)
            }
            is UIState.Error -> {
                endShowProgressBar()
                Toast.makeText(requireContext(), uiState.message, Toast.LENGTH_SHORT).show()
                //activity?.applicationContext?.let { showToast.showToast(it, layout, toastText, uiState.message) }
            }
            is UIState.ErrorWithException -> {
                when(uiState.exception) {
                    is java.net.SocketTimeoutException -> {
                        endShowProgressBar()
                        Toast.makeText(requireContext(), "No internet", Toast.LENGTH_SHORT).show()
                    }
                    is java.net.UnknownHostException -> {
                        endShowProgressBar()
                        Toast.makeText(requireContext(), "No tiene coneccion a internet", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        endShowProgressBar()
                        Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else -> {
            }
        }
    }

    private fun handleDataUIState(uiState: UIState<Int>?) {
        when (uiState) {
            is UIState.Loading -> {
                showProgressBarData()
            }
            is UIState.Success -> {
                endShowProgressBarData()
            }
            is UIState.Error -> {
                endShowProgressBarData()
                Toast.makeText(requireContext(), uiState.message, Toast.LENGTH_SHORT).show()
            }
            else -> {
            }
        }
    }

    private fun endShowProgressBar() {
        binding.registerNaturalPersonActionStart.isClickable = true
        binding.registerNaturalPersonProgressbar.visibility = View.GONE
        binding.registerNaturalPersonActionStart.text = "Empezar"
    }

    private fun showProgressBar() {
        binding.registerNaturalPersonActionStart.isClickable = false
        binding.registerNaturalPersonProgressbar.visibility = View.VISIBLE
        binding.registerNaturalPersonActionStart.text = ""
    }

    private fun endShowProgressBarData() {
        binding.registerNaturalPersonDepartmentProgressbar.visibility = View.GONE
        binding.registerNaturalPersonMunicipalityProgressbar.visibility = View.GONE
    }

    private fun showProgressBarData() {
        binding.registerNaturalPersonDepartmentProgressbar.visibility = View.VISIBLE
        binding.registerNaturalPersonMunicipalityProgressbar.visibility = View.VISIBLE

    }

    private fun setUpDepartmentSpinner(): String {
        dataViewModel.departmentList.observe(viewLifecycleOwner) {
            val spinner = Spinner(requireContext())
            spinner.id = spinnerId

            val ll = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            binding.registerNaturalPersonDepartmentSpinnerLinearlayout.addView(spinner)

            val array: MutableList<DepartmentsDetail> = ArrayList()
            val init = DepartmentsDetail()
            init.idDepartamento = 0
            init.nombre = "Seleccionar departamento"
            array.add(init)
            array.addAll(it)

            val adapter =
                ArrayAdapter(requireContext(), R.layout.spinner_right_aligned, array)
            adapter.setDropDownViewResource(R.layout.spinner_focused)
            with(binding.registerNaturalPersonDepartmentSpinner) {
                this.adapter = adapter
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        adapterView: AdapterView<*>,
                        view: View?,
                        i: Int,
                        l: Long
                    ) {
                        val r: DepartmentsDetail = adapterView.selectedItem as DepartmentsDetail
                        department = r.nombre.toString()
                        setUpMunicipalitySpinner(r.nombre.toString())
                    }

                    override fun onNothingSelected(adapterView: AdapterView<*>?) {
                    }
                }
                layoutParams = ll
                prompt = "Seleccione"

                setPopupBackgroundResource(R.color.primary_color)
            }
        }
        return department
    }

    private fun setUpMunicipalitySpinner(department: String): String {
        dataViewModel.getMunicipalityList(department)
        dataViewModel.municipalityList.observe(viewLifecycleOwner) {
            val spinner = Spinner(requireContext())
            spinner.id = spinnerId

            val ll = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            binding.registerNaturalPersonMunicipalitySpinnerLinearlayout.addView(spinner)

            val array: MutableList<MunicipalitiesDetail> = ArrayList()
            val init = MunicipalitiesDetail()
            init.idMunicipio = 0
            init.nombre = "Seleccionar municipio"
            array.add(init)
            array.addAll(it)

            val adapter =
                ArrayAdapter(requireContext(), R.layout.spinner_right_aligned, array)
            adapter.setDropDownViewResource(R.layout.spinner_focused)
            with(binding.registerNaturalPersonMunicipalitySpinner) {
                this.adapter = adapter
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        adapterView: AdapterView<*>,
                        view: View?,
                        i: Int,
                        l: Long
                    ) {
                        val r: MunicipalitiesDetail =
                            adapterView.selectedItem as MunicipalitiesDetail
                        municipality = r.nombre.toString()
                    }

                    override fun onNothingSelected(adapterView: AdapterView<*>?) {
                    }
                }
                layoutParams = ll
                prompt = "Seleccione"

                setPopupBackgroundResource(R.color.primary_color)
            }
        }
        return municipality
    }

    private fun onCreateRegisterClickListener() {
        binding.registerNaturalPersonActionStart.setOnClickListener {
            if (binding.registerNaturalPersonNameEdit.text.isNullOrEmpty() || binding.registerNaturalPersonDirectionEdit.text.isNullOrEmpty() ||
                binding.registerNaturalPersonDuiEdit.text.isNullOrEmpty() || binding.registerNaturalPersonNitEdit.text.isNullOrEmpty() ||
                binding.registerNaturalPersonTelephoneEdit.text.isNullOrEmpty() || binding.registerNaturalPersonEmailEdit.text.isNullOrEmpty() ||
                binding.registerNaturalPersonPasswordEdit.text.isNullOrEmpty() || binding.registerNaturalPersonRepeatEdit.text.isNullOrEmpty()
            ) {
                Toast.makeText(requireContext(), "Debe llenar todos los campos", Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (municipality == "Seleccionar municipio" || department == "Seleccionar departamento") {
                    Toast.makeText(
                        requireContext(),
                        "Debe elegir el municipio y departamento",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    if (binding.registerNaturalPersonPasswordEdit.text.toString() != binding.registerNaturalPersonRepeatEdit.text.toString()) {
                        Toast.makeText(
                            requireContext(),
                            "La contraseña debe coincidir",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        authViewModel.register(municipality, department)
                    }
                }
            }
        }
    }

    /*private fun showConfirmDialog() {
        authViewModel.goCodeDialog.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    val direction = RegisterNaturalPersonFragmentDirections
                        .actionRegisterNaturalPersonFragmentToDialogConfirmRegister()
                    binding.root.findNavController().navigate(direction)
                }
                authViewModel.endShowCodeDialog()
            }
        }
    }*/

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

}