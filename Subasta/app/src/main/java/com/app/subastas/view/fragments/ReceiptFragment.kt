package com.app.subastas.view.fragments

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.app.subastas.AppApplication
import com.app.subastas.R
import com.app.subastas.databinding.ReceiptFragmentBinding
import com.app.subastas.viewmodel.AuthViewModel
import com.app.subastas.viewmodel.LotViewModel
import com.app.subastas.viewmodel.ViewModelFactory
import com.project.itexpdf.ScreenShot
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.net.URI
import java.net.URL


class ReceiptFragment : Fragment() {

    private var mBinding: ReceiptFragmentBinding? = null
    private val binding get() = mBinding!!

    private val userApp by lazy {
        activity?.application as AppApplication
    }

    private val lotViewModel: LotViewModel by viewModels {
        ViewModelFactory(userApp.lotRepository)
    }

    private val authViewModel: AuthViewModel by activityViewModels()

    private val args: ReceiptFragmentArgs by navArgs()

    private lateinit var screenShot: ScreenShot
    private lateinit var file: File

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
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
        mBinding = ReceiptFragmentBinding.inflate(inflater, container, false)
            .apply {
                viewmodel = lotViewModel
                lifecycleOwner = viewLifecycleOwner
            }

        screenShot = ScreenShot(requireContext())

        initView()

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpListeners()
    }

    private fun initView() {
        binding.receiptTypeText.text = args.type

        val nombre = args.checkDetail?.nombreLote
        val pecio = args.checkDetail?.precioLote
        val monto = args.checkDetail?.montoPagar

        binding.receiptLotTitle.text = nombre
        binding.receiptLotPrice.text = getString(R.string.receipt_price, pecio)
        binding.receiptLotDiscount.text = getString(R.string.receipt_porcentaje, monto)
        val restante = pecio!!.toDouble() - monto!!.toDouble()
        binding.receiptRemaining.text = getString(R.string.receipt_por_pagar, restante.toString())
        binding.receiptTotalPrice.text = getString(R.string.receipt_cancel, monto)

        binding.payLastStepLotDiscountTitle.text = getString(
            R.string.receipt_percentage,
            args.porcent
        )
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun setUpListeners() {
        binding.payLastStepActionLots.setOnClickListener {
            val direction = ReceiptFragmentDirections
                .actionReceiptFragment2ToInscriptionsFragment()
            it.findNavController().navigate(direction)
            authViewModel.showInscriptionState()
        }

        binding.payLastStepActionDownload.setOnClickListener {
            verifyStoragePermissions()
        }

        setUpEnlace()
    }

    private fun setUpEnlace() {
        val url = "https://sitio.aduana.gob.sv/subastas/"
        binding.payLastStepDetailEnlace.setOnClickListener {
            val uri = Uri.parse(url)
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
        binding.receiptLinkImage.setOnClickListener {
            val uri = Uri.parse(url)
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
        val noRegisterLined: SpannableString = SpannableString("Lineamientos y generalidades")
        noRegisterLined.setSpan(UnderlineSpan(), 0, noRegisterLined.length, 0)
        binding.payLastStepDetailEnlace.text = noRegisterLined
    }

    val requestStoragePermissionQLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {

            } else {

            }
        }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun verifyStoragePermissions() {
        if (Environment.isExternalStorageManager()) {
            takeScreenShot()
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

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun takeScreenShot() {
        val bitmap: Bitmap? = screenShot.getViewScreenshot(
            binding.constraintScreen,
            binding.constraintScreen.height,
            binding.constraintScreen.width
        )
        //binding.receiptImage.setImageBitmap(bitmap)
        bitmapToFile(bitmap!!, "Recibo - ${args.checkDetail?.idSuscripcion}")
        saveToGallery()
        //bitmap?.let { screenShot.saveScreenshot(it) }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveToGallery() {
        //Crear un contenedor
        val content = createContent()
        //Guardar imagen
        val uri = save(content)
        //Limpiar el contenedor
        clearContent(content, uri)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun createContent(): ContentValues {
        val fileName = file.name
        val typeFile = "image/jpeg"
        return ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName) //Nombre del archivo
            put(MediaStore.Files.FileColumns.MIME_TYPE, typeFile) //MimeType de la imagen
            put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES
            ) // Directorio a guardar la imagen
            put(
                MediaStore.MediaColumns.IS_PENDING,
                1
            ) // Estado del contenedor aun no se ha guardado
        }
    }

    private fun save(content: ContentValues): Uri {
        var outputStream: OutputStream?
        var uri: Uri?
        activity?.application?.contentResolver.also { resolver ->
            uri = resolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, content)
            outputStream = resolver?.openOutputStream(uri!!)
        }
        outputStream.use { output ->
            getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, output)
        }

        Toast.makeText(requireContext(), "Recibo almacenado en la galeria!", Toast.LENGTH_SHORT).show()
        return uri!!
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun clearContent(content: ContentValues, uri: Uri) {
        content.clear()
        content.put(MediaStore.MediaColumns.IS_PENDING, 0)
        requireContext().contentResolver.update(uri, content, null, null)
    }

    private fun getBitmap(): Bitmap {
        return BitmapFactory.decodeFile(file.toString())
    }

    fun bitmapToFile(bitmap: Bitmap, fileNameToSave: String): File? { // File name like "image.png"
        //create a file to write bitmap data
        return try {
            file = File(
                Environment.getExternalStorageDirectory()
                    .toString() + File.separator + fileNameToSave
            )
            file.createNewFile()

            //Convert bitmap to byte array
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos) // YOU can also save it in JPEG
            val bitmapdata = bos.toByteArray()

            //write the bytes in file
            val fos = FileOutputStream(file)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            file // it will return null
        }
    }

}