package com.app.subastas.view.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.app.subastas.AppApplication
import com.app.subastas.R
import com.app.subastas.data.entity.model.parameters.BankDetail
import com.app.subastas.data.entity.model.payment.PayCheckDetail
import com.app.subastas.data.network.UIState
import com.app.subastas.databinding.PaymentMethodCheckFragmentBinding
import com.app.subastas.view.MainActivity
import com.app.subastas.view.util.ConvertBase64
import com.app.subastas.view.util.DatePickerFragment
import com.app.subastas.viewmodel.LotViewModel
import com.app.subastas.viewmodel.SubscriptionViewModel
import com.app.subastas.viewmodel.ViewModelFactory
import java.io.ByteArrayOutputStream

class PaymentMethodCheckFragment : Fragment() {

    private var mBinding: PaymentMethodCheckFragmentBinding? = null
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

    private val args: PaymentMethodCheckFragmentArgs by navArgs()

    private var banco: String = "Banco Agrícola"
    private val spinnerId = 1

    private var image = ""

    private val converter = ConvertBase64()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = PaymentMethodCheckFragmentBinding.inflate(inflater, container, false)
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

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpObservers()
        setUpListeners()

        initView()
        setUpBankSpinner()
    }

    private fun initView() {
        binding.dataValidationDateEdit.setText("")
        binding.dataValidationNumberEdit.setText("")
        binding.dataValidationAmountEdit.setText("")

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

        lotViewModel.userData1.observe(viewLifecycleOwner) {
            try {
                lotViewModel.getBankList(it[0].accessToken!!)
            } catch (e: Exception) {

            }
        }

        val reserva = args.precio.toDouble() * 0.2
        if (args.typePayment == 1) {
            binding.dataValidationAmountEdit.setText(args.precio)
            binding.paymentMethodCheckDescription.text = "Paga el ${args.porc}% restante"
        } else {
            binding.dataValidationAmountEdit.setText(reserva.toString())
            binding.paymentMethodCheckDescription.text = "Reservar el lote con un ${args.porc}%"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

    private fun setUpBankSpinner(): String {
        lotViewModel.bankList.observe(viewLifecycleOwner) {
            val spinner = Spinner(requireContext())
            spinner.id = spinnerId

            val ll = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            binding.paymentMethodCheckBankSpinnerLinearlayout.addView(spinner)

            val array: MutableList<BankDetail> = ArrayList()
            val init = BankDetail()
            init.id_banco = 0
            init.nombre = "Seleccionar banco"
            array.add(init)
            array.addAll(it)

            val adapter =
                ArrayAdapter(requireContext(), R.layout.spinner_right_aligned, array)
            adapter.setDropDownViewResource(R.layout.spinner_focused)
            with(binding.paymentMethodCheckBankSpinner) {
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

    @RequiresApi(Build.VERSION_CODES.R)
    private fun setUpListeners() {
        onPayClickListener()
        showDatePickerDialog()
        onPhotoClickListener()

        binding.paymentMethodCheckImageContainerActionCancel.setOnClickListener {
            lotViewModel.voucher.value = ""
            image = ""
            binding.paymentMethodCheckImageContainer.visibility = View.GONE
            binding.paymentMethodCheckImageEmptyContainer.visibility = View.VISIBLE
        }
    }

    private fun onPayClickListener() {
        binding.paymentMethodCheckActionPay.setOnClickListener {
            lotViewModel.userData1.observe(viewLifecycleOwner) { user ->
                if (args.again) {
                    if (binding.dataValidationAmountEdit.text.isNullOrEmpty() ||
                        binding.dataValidationDateEdit.text.isNullOrEmpty() ||
                        binding.dataValidationNumberEdit.text.isNullOrEmpty()
                    ) {
                        Toast.makeText(
                            requireContext(),
                            "Debe llenar todos los datos",
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
                            if(args.againTotal) {
                                try {
                                    lotViewModel.payTotalCheckAgain(
                                        user[0].accessToken!!,
                                        args.idSuscripcion,
                                        banco
                                    )
                                } catch (e: Exception) {}
                            } else {
                                try {
                                    lotViewModel.payCheckAgain(
                                        user[0].accessToken!!,
                                        user[0].id!!,
                                        args.idLote.toInt(),
                                        banco,
                                        args.idSuscripcion
                                    )
                                } catch (e: Exception) {}
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
                            "Debe llenar todos los datos",
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
                            lotViewModel.voucher.observe(viewLifecycleOwner) { voucher ->
                                if (voucher.toString() == "") {
                                    Toast.makeText(
                                        requireContext(),
                                        "Debe tomar la foto del comprobante",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    if (args.typePayment == 0) {
                                        try {
                                            lotViewModel.payCheck(
                                                user[0].accessToken!!,
                                                user[0].id!!,
                                                args.idLote.toInt(),
                                                banco
                                            )
                                        } catch (e: Exception) {}
                                    } else if (args.typePayment == 1) {
                                        try {
                                            lotViewModel.payTotalCheck(
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
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun onPhotoClickListener() {
        binding.paymentMethodCheckImageEmptyContainer.setOnClickListener {
            verifyStoragePermissions()
        }
    }

    private val requestLocationLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if(uri != null) {
                val encode = converter.uriToBase64(uri, requireActivity().contentResolver)
                val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, Uri.parse(
                    uri.toString()
                ))
                binding.paymentMethodCheckImageEmptyContainer.visibility = View.GONE
                binding.paymentMethodCheckImageContainer.visibility = View.VISIBLE
                //val bmp = BitmapFactory.decodeFile(image)
                binding.paymentMethodCheckImageContainerImage.setImageBitmap(bitmap)

                binding.paymentMethodCheckImageContainer.setOnClickListener {
                    val direction = PaymentMethodCheckFragmentDirections
                        .actionPaymentMethodCheckFragment2ToDialogVoucher(encode)
                    it.findNavController().navigate(direction)
                }
                binding.paymentMethodCheckVoucherImage
                lotViewModel.getVoucherImage(encode)
            }
        }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun verifyStoragePermissions() {
        if (Environment.isExternalStorageManager()) {
            requestLocationLauncher.launch("image/*")
        } else {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {

                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    1
                )

            } else {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                val uri = Uri.fromParts("package", activity?.packageName, null)
                intent.data = uri
                startActivity(intent)
            }
        }
    }

    private fun setUpObservers() {
        goConfirmDialog()
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
            }
            else -> {

            }
        }
    }

    private fun endShowProgressBar() {
        binding.paymentMethodCheckActionPay.isClickable = true
        binding.paymentMethodCheckProgressBar.visibility = View.GONE
        binding.paymentMethodCheckActionPay.text = "Realizar pago"
    }

    private fun showProgressBar() {
        binding.paymentMethodCheckActionPay.isClickable = false
        binding.paymentMethodCheckProgressBar.visibility = View.VISIBLE
        binding.paymentMethodCheckActionPay.text = ""
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
                            val direction = PaymentMethodCheckFragmentDirections
                                .actionPaymentMethodCheckFragment2ToDialogPaymentConfirm2(
                                    "Cheque",
                                    0,
                                    args.porc.toInt(),
                                    body
                                )
                            view?.findNavController()?.navigate(direction)
                        } else {
                            val direction = PaymentMethodCheckFragmentDirections
                                .actionPaymentMethodCheckFragment2ToDialogPaymentConfirm2(
                                    "Cheque",
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