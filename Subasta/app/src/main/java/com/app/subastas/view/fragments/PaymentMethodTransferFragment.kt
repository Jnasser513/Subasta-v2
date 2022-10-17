package com.app.subastas.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.app.subastas.AppApplication
import com.app.subastas.R
import com.app.subastas.data.entity.model.parameters.BankDetail
import com.app.subastas.data.entity.model.payment.PayCheckDetail
import com.app.subastas.data.network.UIState
import com.app.subastas.databinding.PaymentMethodTransferFragmentBinding
import com.app.subastas.view.MainActivity
import com.app.subastas.view.util.DatePickerFragment
import com.app.subastas.viewmodel.AuthViewModel
import com.app.subastas.viewmodel.LotViewModel
import com.app.subastas.viewmodel.SubscriptionViewModel
import com.app.subastas.viewmodel.ViewModelFactory


class PaymentMethodTransferFragment : Fragment() {

    private var mBinding: PaymentMethodTransferFragmentBinding? = null
    private val binding get() = mBinding!!

    private val userApp by lazy {
        activity?.application as AppApplication
    }

    private val lotViewModel: LotViewModel by viewModels {
        ViewModelFactory(userApp.lotRepository)
    }

    private val subscriptionViewModel: SubscriptionViewModel by viewModels {
        ViewModelFactory(userApp.subscriptionRepository)
    }

    private val authViewModel: AuthViewModel by activityViewModels()

    private val args: PaymentMethodTransferFragmentArgs by navArgs()

    private var banco: String = "Banco Agrícola"
    private val spinnerId = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = PaymentMethodTransferFragmentBinding.inflate(inflater, container, false)
            .apply {
                viewmodel = lotViewModel
                lifecycleOwner = viewLifecycleOwner
            }

        binding.dataValidationAmountEdit.doAfterTextChanged { message ->
            lotViewModel.reserva.value = message.toString()
        }

        binding.dataValidationDateEdit.doAfterTextChanged { message ->
            lotViewModel.fecha.value = message.toString()
        }

        binding.dataValidationNumberEdit.doAfterTextChanged { message ->
            lotViewModel.comprobante.value = message.toString()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpObservers()
        setUpListeners()

        lotViewModel.userData1.observe(viewLifecycleOwner) {
            try {
                lotViewModel.getBankList(it[0].accessToken!!)
            } catch (e: Exception) {
            }
        }

        initView()
        showDatePickerDialog()
        setUpBankSpinner()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

    private fun initView() {
        binding.dataValidationDateEdit.setText("")
        binding.dataValidationNumberEdit.setText("")
        binding.dataValidationAmountEdit.setText("")
        binding.paymentMethodDescription.text = getString(R.string.payment_method_transfer_porc,
                args.porc.toString()
            )

        if (args.again) {
            lotViewModel.userData1.observe(viewLifecycleOwner) { user ->
                try {
                    subscriptionViewModel.getSubscriptionDetail(
                        user[0].accessToken!!,
                        args.idSuscripcion
                    )
                } catch (e: Exception) {
                }
            }

            subscriptionViewModel.subscriptionDetail.observe(viewLifecycleOwner) { detail ->
                binding.dataValidationAmountEdit.setText(detail.reserva.toString())
                val fecha = detail.fechaAdjudicacion.split("T")
                binding.dataValidationDateEdit.setText(fecha[0].replace("-", "/"))
                binding.dataValidationNumberEdit.setText(detail.transaccion)
            }
        }

        val porcentaje = args.porc
        val reserva = args.precio.toDouble() * porcentaje / 100
        if (args.typePayment == 1) {
            binding.dataValidationAmountEdit.setText(args.precio)
            binding.paymentMethodDescription.text = "Paga el ${args.porc}% restante"
        } else {
            binding.dataValidationAmountEdit.setText(reserva.toString())
            binding.paymentMethodDescription.text = getString(R.string.payment_method_transfer_porc,
                    args.porc.toString()
                )
        }
    }

    private fun setUpListeners() {
        binding.paymentMethodTransferActionPay.setOnClickListener {
            lotViewModel.userData1.observe(viewLifecycleOwner) { user ->
                if (args.again) {
                    if (binding.dataValidationAmountEdit.text.isNullOrEmpty() ||
                        binding.dataValidationDateEdit.text.isNullOrEmpty() ||
                        binding.dataValidationNumberEdit.text.isNullOrEmpty()
                    ) {
                        Toast.makeText(
                            requireContext(),
                            "Debe llenar todos los campos",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        if (banco == "Seleccionar banco") {
                            Toast.makeText(
                                requireContext(),
                                "Debe seleccionar un banco",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            if (args.againTotal) {
                                try {
                                    lotViewModel.payTotalTransferAgain(
                                        user[0].accessToken!!,
                                        args.idSuscripcion,
                                        banco
                                    )
                                } catch (e: Exception) {
                                }
                            } else {
                                try {
                                    lotViewModel.payTransferAgain(
                                        user[0].accessToken!!,
                                        user[0].id!!,
                                        args.idLote.toInt(),
                                        banco,
                                        args.idSuscripcion
                                    )
                                } catch (e: Exception) {
                                }
                            }
                        }
                    }
                } else {
                    if (binding.dataValidationAmountEdit.text.isNullOrEmpty() ||
                        binding.dataValidationDateEdit.text.isNullOrEmpty() ||
                        binding.dataValidationNumberEdit.text.isNullOrEmpty()
                    ) {
                        Toast.makeText(
                            requireContext(),
                            "Debe llenar todos los campos",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        if (banco == "Seleccionar banco") {
                            Toast.makeText(
                                requireContext(),
                                "Debe seleccionar un banco",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            if (args.typePayment == 0) {
                                try {
                                    lotViewModel.payTransfer(
                                        user[0].accessToken!!,
                                        user[0].id!!,
                                        args.idLote.toInt(),
                                        banco
                                    )
                                } catch (e: Exception) {}
                            } else if (args.typePayment == 1) {
                                try {
                                    lotViewModel.payTotalTransfer(
                                        user[0].accessToken!!,
                                        args.idSuscripcion,
                                        banco
                                    )
                                } catch (e: Exception) {}
                            }
                        }
                    }
                }
            }

            /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                val uri = Uri.fromParts("package", activity?.packageName, null)
                intent.data = uri
                storagePermissionResultLauncher.launch(intent)
            } else {
                ActivityCompat.requestPermissions(requireActivity(),
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    100);
            }*/
        }
    }

    private fun setUpObservers() {
        goConfirmDialog()
        showMessageObserver()
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
        binding.paymentMethodTransferActionPay.isClickable = true
        binding.paymentMethodTransferProgressBar.visibility = View.GONE
        binding.paymentMethodTransferActionPay.text = "Realizar pago"
    }

    private fun showProgressBar() {
        binding.paymentMethodTransferActionPay.isClickable = false
        binding.paymentMethodTransferProgressBar.visibility = View.VISIBLE
        binding.paymentMethodTransferActionPay.text = ""
    }

    private fun goConfirmDialog() {
        lotViewModel.goCodeDialog.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    lotViewModel.checkDetail.observe(viewLifecycleOwner) { checkDetail ->
                        val body = PayCheckDetail(
                            args.idSuscripcion.toString(),
                            checkDetail.nombreLote,
                            checkDetail.montoPagar,
                            checkDetail.porcentajeReserva,
                            checkDetail.fechaComprobante,
                            checkDetail.precioLote
                        )
                        if (args.typePayment == 0) {
                            val direction = PaymentMethodTransferFragmentDirections
                                .actionPaymentMethodTransferFragment2ToDialogPaymentConfirm2(
                                    "Transferencia",
                                    0,
                                    args.porc.toInt(),
                                    body
                                )
                            view?.findNavController()?.navigate(direction)
                        } else {
                            val direction = PaymentMethodTransferFragmentDirections
                                .actionPaymentMethodTransferFragment2ToDialogPaymentConfirm2(
                                    "Transferencia",
                                    1,
                                    args.porc.toInt(),
                                    body
                                )
                            view?.findNavController()?.navigate(direction)
                        }
                    }
                }
                lotViewModel.endShowCodeDialog()
            }
        }
    }

    private fun showMessageObserver() {
        lotViewModel.showMessage.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    lotViewModel.toastMessage.observe(viewLifecycleOwner) { message ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
                }
                lotViewModel.endShowMessage()
            }
        }
    }

    private fun showDatePickerDialog() {
        binding.dataValidationDateEdit.setOnClickListener {
            val datePicker =
                DatePickerFragment { day, month, year -> onDateSelected(day, month, year) }
            datePicker.show(parentFragmentManager, "datePicker")
        }
    }

    private fun onDateSelected(day: Int, month: Int, year: Int) {
        binding.dataValidationDateEdit.setText("${year}/${month + 1}/${day}")
    }

    private fun setUpBankSpinner(): String {
        lotViewModel.bankList.observe(viewLifecycleOwner) {
            val spinner = Spinner(requireContext())
            spinner.id = spinnerId

            val ll = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            binding.paymentMethodTransferBankSpinnerLinearlayout.addView(spinner)

            val array: MutableList<BankDetail> = ArrayList()
            val init = BankDetail()
            init.id_banco = 0
            init.nombre = "Seleccionar banco"
            array.add(init)
            array.addAll(it)

            val adapter =
                ArrayAdapter(requireContext(), R.layout.spinner_right_aligned, array)
            adapter.setDropDownViewResource(R.layout.spinner_focused)
            with(binding.verifySupplierBusinessSpinner) {
                this.adapter = adapter
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        adapterView: AdapterView<*>,
                        view: View,
                        i: Int,
                        l: Long
                    ) {
                        val r: BankDetail = adapterView.selectedItem as BankDetail
                        banco = r.nombre.toString()
                    }

                    override fun onNothingSelected(adapterView: AdapterView<*>?) {
                    }
                }
                layoutParams = ll
                prompt = "Seleccione"

                setPopupBackgroundResource(R.color.primary_color)
            }
        }
        return banco
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