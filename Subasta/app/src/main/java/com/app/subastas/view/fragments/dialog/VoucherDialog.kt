package com.app.subastas.view.fragments.dialog

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.app.subastas.databinding.VoucherDialogBinding

class VoucherDialog : DialogFragment() {

    private var mBinding: VoucherDialogBinding? = null
    private val binding get() = mBinding!!

    private val args: VoucherDialogArgs by navArgs()

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
        mBinding = VoucherDialogBinding.inflate(inflater, container, false)

        dialog?.setCanceledOnTouchOutside(true)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bmp = stringToBitMap(args.image)
        binding.voucherDialogImage.setImageBitmap(bmp)

        binding.paymentMethodCheckImageContainerActionCancel.setOnClickListener {
            dismiss()
        }
    }

    fun stringToBitMap(encodedString: String?): Bitmap? {
        return try {
            val encodeByte: ByteArray = Base64.decode(encodedString, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
        } catch (e: Exception) {
            e.message
            null
        }
    }

}