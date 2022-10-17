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
import com.app.subastas.AppApplication
import com.app.subastas.R
import com.app.subastas.data.entity.model.parameters.DepartmentsDetail
import com.app.subastas.data.entity.model.parameters.MunicipalitiesDetail
import com.app.subastas.data.network.UIState
import com.app.subastas.databinding.RegisterJuridicPersonFragmentBinding
import com.app.subastas.view.util.ShowToast
import com.app.subastas.viewmodel.AuthViewModel
import com.app.subastas.viewmodel.DataViewModel
import com.app.subastas.viewmodel.ViewModelFactory

class RegisterJuridicPersonFragment : Fragment() {

    private var mBinding: RegisterJuridicPersonFragmentBinding? = null
    private val binding get() = mBinding!!

    private val authViewModel: AuthViewModel by activityViewModels()

    private val userApp by lazy {
        activity?.application as AppApplication
    }

    private val dataViewModel: DataViewModel by viewModels {
        ViewModelFactory(userApp.dataRepository)
    }

    private val showToast = ShowToast()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val direction = RegisterJuridicPersonFragmentDirections
                        .actionRegisterJuridicPersonFragmentToStepFragment(2)
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
        mBinding = RegisterJuridicPersonFragmentBinding.inflate(inflater, container, false)
            .apply {
                viewmodel = authViewModel
                lifecycleOwner = viewLifecycleOwner
            }

        binding.registerJuridicPersonNameEdit.doAfterTextChanged { message ->
            authViewModel.nameBusinessRegister.value = message.toString()
        }

        binding.registerJuridicPersonNitEdit.doAfterTextChanged { message ->
            authViewModel.nitBusinessRegister.value = message.toString()
        }

        binding.registerJuridicPersonNameHostEdit.doAfterTextChanged { message ->
            authViewModel.nameRegister.value = message.toString()
        }

        binding.registerJuridicPersonDirectionEdit.doAfterTextChanged { message ->
            authViewModel.directionRegister.value = message.toString()
        }

        binding.registerJuridicPersonDuiHostEdit.doAfterTextChanged { message ->
            authViewModel.duiRegister.value = message.toString()
        }

        binding.registerJuridicPersonNitHostEdit.doAfterTextChanged { message ->
            authViewModel.nitRegister.value = message.toString()
        }

        binding.registerJuridicPersonPhoneEdit.doAfterTextChanged { message ->
            authViewModel.phoneRegister.value = message.toString()
        }

        binding.registerJuridicPersonEmailEdit.doAfterTextChanged { message ->
            authViewModel.emailRegister.value = message.toString()
        }

        binding.registerJuridicPersonPasswordEdit.doAfterTextChanged { message ->
            authViewModel.passwordRegister.value = message.toString()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.registerJuridicPersonActionReturn.setOnClickListener {
            activity?.onBackPressed()
        }

        dataViewModel.getDepartmentList()

        setUpDepartmentSpinner()

        onCreateRegisterClickListener()
        showConfirmDialog()

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
    }

    private fun handleUIState(uiState: UIState<Int>?) {
        //val toastText = layout.findViewById<TextView>(R.id.toast_text)
        //layout.findViewById<ImageView>(R.id.toast_image).visibility = View.GONE
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
        binding.registerJuridicPersonActionStart.isClickable = true
        binding.registerJuridicPersonProgressbar.visibility = View.GONE
        binding.registerJuridicPersonActionStart.text = "Empezar"
    }

    private fun showProgressBar() {
        binding.registerJuridicPersonActionStart.isClickable = false
        binding.registerJuridicPersonProgressbar.visibility = View.VISIBLE
        binding.registerJuridicPersonActionStart.text = ""
    }

    private fun setUpDepartmentSpinner(): String {
        dataViewModel.departmentList.observe(viewLifecycleOwner) {
            val spinner = Spinner(requireContext())
            spinner.id = spinnerId

            val ll = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            binding.registerJuridicPersonDepartmentSpinnerLinearlayout.addView(spinner)

            val array: MutableList<DepartmentsDetail> = ArrayList()
            val init = DepartmentsDetail()
            init.idDepartamento = 0
            init.nombre = "Seleccionar departamento"
            array.add(init)
            array.addAll(it)

            val adapter =
                ArrayAdapter(requireContext(), R.layout.spinner_right_aligned, array)
            adapter.setDropDownViewResource(R.layout.spinner_focused)
            with(binding.registerJuridicPersonDepartmentSpinner) {
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

            binding.registerJuridicPersonMunicipalitySpinnerLinearlayout.addView(spinner)

            val array: MutableList<MunicipalitiesDetail> = ArrayList()
            val init = MunicipalitiesDetail()
            init.idMunicipio = 0
            init.nombre = "Seleccionar municipio"
            array.add(init)
            array.addAll(it)

            val adapter =
                ArrayAdapter(requireContext(), R.layout.spinner_right_aligned, array)
            adapter.setDropDownViewResource(R.layout.spinner_focused)
            with(binding.registerJuridicPersonMunicipalitySpinner) {
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
        binding.registerJuridicPersonActionStart.setOnClickListener {
            if (binding.registerJuridicPersonNameEdit.text.isNullOrEmpty() || binding.registerJuridicPersonNitEdit.text.isNullOrEmpty() ||
                binding.registerJuridicPersonNameHostEdit.text.isNullOrEmpty() || binding.registerJuridicPersonDirectionEdit.text.isNullOrEmpty() ||
                binding.registerJuridicPersonDuiHostEdit.text.isNullOrEmpty() || binding.registerJuridicPersonNitHostEdit.text.isNullOrEmpty() ||
                binding.registerJuridicPersonPhoneEdit.text.isNullOrEmpty() || binding.registerJuridicPersonEmailEdit.text.isNullOrEmpty() ||
                binding.registerJuridicPersonPasswordEdit.text.isNullOrEmpty() || binding.registerJuridicPersonRepeatEdit.text.isNullOrEmpty()
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
                    if (binding.registerJuridicPersonPasswordEdit.text.toString() != binding.registerJuridicPersonRepeatEdit.text.toString()) {
                        Toast.makeText(
                            requireContext(),
                            "La contraseña debe coincidir",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        authViewModel.registerJuridic(municipality, department)
                    }
                }
            }
        }
    }

    private fun showConfirmDialog() {
        authViewModel.goCodeDialog.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    val direction = RegisterJuridicPersonFragmentDirections
                        .actionRegisterJuridicPersonFragmentToDialogConfirmRegister()
                    binding.root.findNavController().navigate(direction)
                }
                authViewModel.endShowCodeDialog()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

}