package com.app.subastas.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.app.subastas.R
import com.app.subastas.data.entity.model.lots.WonLotsDetail
import com.app.subastas.view.fragments.InscriptionsFragmentDirections
import com.app.subastas.viewmodel.LotViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class WonLotsAdapter: RecyclerView.Adapter<WonLotsAdapter.WonLotsViewHolder>() {

    private var wonList: List<WonLotsDetail>? = null
    private lateinit var authViewModel: LotViewModel
    private lateinit var viewLifeCycleOwner: LifecycleOwner

    inner class WonLotsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WonLotsViewHolder {
        val card = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_won_lots, parent, false)

        return WonLotsViewHolder(card)
    }

    override fun onBindViewHolder(holder: WonLotsViewHolder, position: Int) {
        val lote = wonList?.get(position)
        val image = holder.itemView.findViewById<ImageView>(R.id.item_won_lots_image)
        val uri = "https://desadga2.mh.gob.sv/subastaol/files/"+ lote?.image
        val payButton = holder.itemView.findViewById<Button>(R.id.item_won_lots_action_pay)
        val stateText = holder.itemView.findViewById<TextView>(R.id.item_won_lots_state_text)
        val price = holder.itemView.findViewById<TextView>(R.id.item_won_lots_price)
        val name = holder.itemView.findViewById<TextView>(R.id.item_won_lots_name)
        val date = holder.itemView.findViewById<TextView>(R.id.item_won_lots_date)
        val check = holder.itemView.findViewById<ImageView>(R.id.item_won_lots_check)
        val pay2 = holder.itemView.findViewById<Button>(R.id.item_won_lots_action_pay_2)

        Glide.with(holder.itemView.context)
            .load(uri)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .into(image)

        price.text = holder.itemView.resources.getString(R.string.item_registered_lots_price,
            lote?.precio.toString()
        )
        name.text = lote?.lote

        val fecha = lote?.fecha_inicio?.split(" ")
        date.text = fecha?.get(0)

        when(lote?.id_estado_sub) {
            13 -> {
                payButton.visibility = View.VISIBLE
                stateText.visibility = View.GONE
                pay2.visibility = View.GONE
                check.visibility = View.GONE
                image.alpha = 1F
                price.setTextColor(holder.itemView.resources.getColor(R.color.secondary_color))
                name.setTextColor(holder.itemView.resources.getColor(R.color.secondary_color))
                date.setTextColor(holder.itemView.resources.getColor(R.color.secondary_color))

                authViewModel.userData1.observe(viewLifeCycleOwner) { user ->
                    try {
                        authViewModel.getLotDetail(user[0].accessToken!!, lote.id_lote.toString())
                        authViewModel.lotDetail.observe(viewLifeCycleOwner) { detail ->
                            payButton.setOnClickListener {
                                val direction = InscriptionsFragmentDirections
                                    .actionInscriptionsFragmentToDialogConfirmPay(lote.precio.toFloat(),
                                        lote.id_lote.toInt(),
                                        1,
                                        detail.porcentajeReserva!!,
                                        lote.id_usuario_suscripcion.toInt())
                                it.findNavController().navigate(direction)
                            }
                        }
                    }catch (e: Exception) {

                    }
                }
            }
            8 -> {
                payButton.visibility = View.GONE
                stateText.visibility = View.VISIBLE
                pay2.visibility = View.GONE
                check.visibility = View.GONE
                image.alpha = 1F
                price.setTextColor(holder.itemView.resources.getColor(R.color.secondary_color))
                name.setTextColor(holder.itemView.resources.getColor(R.color.secondary_color))
                date.setTextColor(holder.itemView.resources.getColor(R.color.secondary_color))
                stateText.text = "Pendiente de aprobacion"
            }
            9 -> {
                payButton.visibility = View.GONE
                stateText.visibility = View.VISIBLE
                pay2.visibility = View.GONE
                image.alpha = 0.4F
                price.setTextColor(holder.itemView.resources.getColor(R.color.secondary_color_opacity))
                name.setTextColor(holder.itemView.resources.getColor(R.color.secondary_color_opacity))
                date.setTextColor(holder.itemView.resources.getColor(R.color.secondary_color_opacity))
                check.visibility = View.VISIBLE
                stateText.text = "Pago\n aceptado"
            }
            10 -> {
                payButton.visibility = View.GONE
                pay2.visibility = View.VISIBLE
                stateText.visibility = View.VISIBLE
                check.visibility = View.GONE
                image.alpha = 0.4F
                price.setTextColor(holder.itemView.resources.getColor(R.color.secondary_color_opacity))
                name.setTextColor(holder.itemView.resources.getColor(R.color.secondary_color_opacity))
                date.setTextColor(holder.itemView.resources.getColor(R.color.secondary_color_opacity))
                stateText.text = "Pago\n rechazado"

                pay2.setOnClickListener {
                    authViewModel.userData1.observe(viewLifeCycleOwner) { user ->
                        authViewModel.getLotDetail(user[0].accessToken!!, lote.id_lote.toString())
                        authViewModel.lotDetail.observe(viewLifeCycleOwner) { detail ->
                            val direction = InscriptionsFragmentDirections
                                .actionInscriptionsFragmentToPaymentMethodFragment2(lote.id_lote.toString(), lote.precio.toString(), detail.porcentajeReserva!!, 1,
                                    lote.id_usuario_suscripcion.toInt(), again = true, againTotal = true
                                )
                            holder.itemView.findNavController().navigate(direction)
                            authViewModel.showLotesState()
                        }
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = wonList?.size ?: 0

    fun setData(list: List<WonLotsDetail>) {
        wonList = list
        notifyDataSetChanged()
    }

    fun getViewModel(viewmodel: LotViewModel) {
        authViewModel = viewmodel
    }

    fun getLifeCycleOwner(lifecycleOwner: LifecycleOwner) {
        viewLifeCycleOwner = lifecycleOwner
    }
}