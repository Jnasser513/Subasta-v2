package com.app.subastas.view.fragments.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.app.subastas.R
import com.app.subastas.data.entity.model.lots.LotesDetail
import com.app.subastas.databinding.DialogLotDetailFragmentBinding
import com.app.subastas.viewmodel.AuthViewModel
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel

class DialogLotDetailFragment: DialogFragment() {

    private var mBinding: DialogLotDetailFragmentBinding? = null
    private val binding get() = mBinding!!

    private val authViewModel: AuthViewModel by activityViewModels()

    private val args: DialogLotDetailFragmentArgs by navArgs()

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
        mBinding = DialogLotDetailFragmentBinding.inflate(inflater, container, false)
            .apply {
                viewmodel = authViewModel
                lifecycleOwner = viewLifecycleOwner
            }

        dialog?.setCanceledOnTouchOutside(true)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        onBookingClickListener()
    }

    private fun initView() {
        val lote = args.lot
        binding.dialogDetailTitle.text = lote.tipoLote?.nombre.toString()
        binding.dialogLotDetailPrice.text = getString(R.string.dialog_lot_detail_price,
                lote.precio
            )
        binding.dialogLotDetailName.text = getString(R.string.dialog_lot_detail_name,
                lote.nombre
            )
        binding.dialogLotDetailWeight.text = getString(R.string.dialog_lot_detail_weight,
                lote.peso
            )
        binding.dialogLotDetailSize.text = getString(R.string.dialog_lot_detail_size,
                lote.medidas
            )
        val fecha = lote.subasta?.fechaInicio?.split("T")
        binding.dialogLotDetailDate.text = getString(R.string.dialog_lot_detail_date,
                fecha?.get(0)
            )
        val hora = fecha?.get(1)?.split(":")
        if((hora?.get(0) ?: "0") > "12") {
            binding.dialogLotDetailHour.text = getString(R.string.dialog_lot_detail_hour,
                "${hora?.get(0)}:${hora?.get(1)} pm"
            )
        } else {
            binding.dialogLotDetailHour.text = getString(R.string.dialog_lot_detail_hour,
                "${hora?.get(0)}:${hora?.get(1)} am"
            )
        }

        changeImage(lote)
    }

    private fun changeImage(lote: LotesDetail) {
        val slideList: ArrayList<SlideModel> = ArrayList()
        for(i in 1..lote.imagenes?.size!!) {
            slideList.add(SlideModel("https://desadga2.mh.gob.sv/subastaol/files/${lote.imagenes[i-1].src}", ScaleTypes.CENTER_CROP))
        }
        binding.dialogLotDetailImage.setImageList(slideList, ScaleTypes.CENTER_CROP)
    }

    private fun onBookingClickListener() {
        binding.dialoLotDetailActionBooking.setOnClickListener {
            val navHost = NavHostFragment.findNavController(this)
            val direction = DialogLotDetailFragmentDirections
                .actionDialogLotDetailFragmentToDialogBookingLots2(args.lot.precio.toString(), args.lot.idLote.toString(), args.lot.porcentajeReserva!!)
            navHost.navigate(direction)
        }

        binding.dialogLotDetailActionCancel.setOnClickListener {
            dismiss()
        }
    }

}