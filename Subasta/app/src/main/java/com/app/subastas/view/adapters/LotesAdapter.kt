package com.app.subastas.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.app.subastas.R
import com.app.subastas.data.entity.model.lots.LotesDetail
import com.app.subastas.view.fragments.AvailableLotsFragmentDirections
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class LotesAdapter: RecyclerView.Adapter<LotesAdapter.LotesViewHolder>() {

    private var lotesList: List<LotesDetail>? = null

    inner class LotesViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LotesViewHolder {
        val card =LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lotes, parent, false)

        return LotesViewHolder(card)
    }

    override fun onBindViewHolder(holder: LotesViewHolder, position: Int) {
        try {
            val lote = lotesList?.get(position)
            val uri = "https://desadga2.mh.gob.sv/subastaol/files/"+ lote?.imagenes?.get(0)?.src
            Glide.with(holder.itemView.context)
                .load(uri)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(holder.itemView.findViewById(R.id.item_lotes_image))

            holder.itemView.findViewById<TextView>(R.id.item_lotes_money).text = holder.itemView.resources.getString(
                R.string.item_lotes_price_text,
                lote?.precio.toString()
            )
            holder.itemView.findViewById<TextView>(R.id.item_lotes_name).text = lote?.nombre.toString()
            val fecha = lote?.fechaInicio!!.split("T")
            holder.itemView.findViewById<TextView>(R.id.item_lotes_date).text = fecha[0]

            holder.itemView.setOnClickListener {
                val direction = lote.let { it1 ->
                    AvailableLotsFragmentDirections
                        .actionAvailableLotsFragmentToDialogLotDetailFragment(it1)
                }
                it.findNavController().navigate(direction)
            }
        } catch (e: Exception) {

        }
    }

    override fun getItemCount() = lotesList?.size ?: 0

    fun setData(list: List<LotesDetail>) {
        lotesList = list
        notifyDataSetChanged()
    }
}