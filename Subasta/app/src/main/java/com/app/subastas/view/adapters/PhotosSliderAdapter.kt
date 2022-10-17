package com.app.subastas.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.app.subastas.R
import com.app.subastas.data.entity.model.lots.ImagenesDetail
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class PhotosSliderAdapter : RecyclerView.Adapter<PhotosSliderAdapter.ActivitiesSliderViewHolder>(){

    private var sliderItems: List<ImagenesDetail>? = null

    class ActivitiesSliderViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.advertisements_image)

        fun image(sliderItem: ImagenesDetail) {
            val uri = "https://desadga2.mh.gob.sv/subastaol/files/" + sliderItem.src
            Glide.with(itemView.context)
                .load(uri)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(imageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivitiesSliderViewHolder {
        val card = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_photo, parent, false)

        return ActivitiesSliderViewHolder(card)
    }

    override fun onBindViewHolder(holder: ActivitiesSliderViewHolder, position: Int) {
        val advertisement = sliderItems?.get(position)
        holder.image(advertisement!!)
    }

    override fun getItemCount() = sliderItems?.size ?: 0

    fun setData(list: List<ImagenesDetail>) {
        sliderItems = list
        notifyDataSetChanged()
    }
}