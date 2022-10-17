package com.app.subastas.view.fragments

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.app.subastas.R
import com.app.subastas.data.network.UIState
import com.app.subastas.databinding.StepFragmentBinding
import com.app.subastas.view.util.ConvertBase64
import com.app.subastas.viewmodel.AuthViewModel


class StepFragment : Fragment() {

    private var mBinding: StepFragmentBinding? = null
    private val binding get() = mBinding!!

    private val args: StepFragmentArgs by navArgs()

    private val authViewModel: AuthViewModel by activityViewModels()

    private val converter = ConvertBase64()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    view?.findNavController()?.navigate(R.id.noRegisterFragment)
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
        mBinding = StepFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpClickListeners()

        observeState()
    }

    private fun observeState() {
        val layout: View =
            layoutInflater.inflate(R.layout.toast_style, view?.findViewById(R.id.ll_wrapper))
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
                Toast.makeText(requireContext(), "Archivo enviado!", Toast.LENGTH_SHORT).show()
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
        binding.stepActionUpload.isClickable = true
        binding.stepProgressBar.visibility = View.GONE
        binding.stepActionUpload.text = "Subir PDF"
        binding.stepUploadImage.visibility = View.VISIBLE
    }

    private fun showProgressBar() {
        binding.stepActionUpload.isClickable = false
        binding.stepProgressBar.visibility = View.VISIBLE
        binding.stepActionUpload.text = ""
        binding.stepUploadImage.visibility = View.GONE
    }

    private fun setUpClickListeners() {
        onFormClickListener()

        binding.stepActionReturn.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.stepActionDownload.setOnClickListener {
            downLoad()
        }

        binding.stepActionUpload.setOnClickListener {
            val array = arrayOf(
                "application/pdf"
            )
            getAuctionFile.launch(array)
        }
    }

    private fun downLoad() {
        val manager =
            requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager?
        val uri =
            Uri.parse("https://sitio.aduana.gob.sv/download/solicitud-de-inscripcion/?wpdmdl=5572&refresh=627c1eb26c8f11652301490")
        val request = DownloadManager.Request(uri)
            .setTitle("File")
            .setDescription("Downloading....")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverMetered(true)

        manager?.enqueue(request)
    }

    private val getAuctionFile =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) {
                val encode = converter.uriToBase64(uri, requireActivity().contentResolver)
                authViewModel.uploadFile(encode)
            }
        }

    private fun onFormClickListener() {
        binding.stepActionForm.setOnClickListener {
            if (args.type == 1) {
                val direction = StepFragmentDirections
                    .actionStepFragmentToRegisterNaturalPersonFragment()
                binding.root.findNavController().navigate(direction)
            } else {
                val direction = StepFragmentDirections
                    .actionStepFragmentToRegisterJuridicPersonFragment()
                binding.root.findNavController().navigate(direction)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

}