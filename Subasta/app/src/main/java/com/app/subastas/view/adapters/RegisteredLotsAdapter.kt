package com.app.subastas.view.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.app.subastas.R
import com.app.subastas.data.entity.User
import com.app.subastas.data.entity.model.lots.RegisteredLotsDetail
import com.app.subastas.view.fragments.InscriptionsFragmentDirections
import com.app.subastas.viewmodel.AuthViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import java.text.SimpleDateFormat
import java.util.*

class RegisteredLotsAdapter :
    RecyclerView.Adapter<RegisteredLotsAdapter.RegisteredLotsViewHolder>() {

    private var registeredList: List<RegisteredLotsDetail>? = null

    private lateinit var userData: User
    private lateinit var navController2: NavController
    private lateinit var authViewModel: AuthViewModel
    private lateinit var viewLifecycleOwner: LifecycleOwner

    inner class RegisteredLotsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegisteredLotsViewHolder {
        val card = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_registered_lots, parent, false)

        return RegisteredLotsViewHolder(card)
    }

    override fun onBindViewHolder(holder: RegisteredLotsViewHolder, position: Int) {
        val lote = registeredList?.get(position)
        val image = holder.itemView.findViewById<ImageView>(R.id.item_registered_lots_image)
        val status = holder.itemView.findViewById<TextView>(R.id.item_registered_win_status)

        val id_user = userData.id
        val id_winner = lote?.id_winner

        if (id_user == id_winner) {
            status.text = "¡Ganando!"
            status.setTextColor(holder.itemView.resources.getColor(R.color.primary_color))
            status.setBackgroundDrawable(holder.itemView.resources.getDrawable(R.drawable.win_status_style))
        } else {
            status.text = "¡Perdiendo!"
            status.setTextColor(holder.itemView.resources.getColor(R.color.secondary_color))
            status.setBackgroundDrawable(holder.itemView.resources.getDrawable(R.drawable.loss_status_style))
        }

        val state = holder.itemView.findViewById<TextView>(R.id.item_registered_lots_status_title)

        val payButton = holder.itemView.findViewById<Button>(R.id.item_registered_lots_action_pay_denied)

        when (lote?.id_estado_sub) {
            3 -> {
                state.text = "En proceso"
                payButton.visibility = View.GONE
                holder.itemView.findViewById<LinearLayout>(R.id.item_registered_lots_details_container).alpha = 1F
                holder.itemView.findViewById<LinearLayout>(R.id.item_registered_lots_image_container).alpha = 1F
                holder.itemView.isClickable = false
            }
            6 -> {
                state.text = "Aprobado"
                payButton.visibility = View.GONE
                holder.itemView.findViewById<LinearLayout>(R.id.item_registered_lots_details_container).alpha = 1F
                holder.itemView.findViewById<LinearLayout>(R.id.item_registered_lots_image_container).alpha = 1F
                holder.itemView.isClickable = true
            }
            7 -> {
                state.text = "Rechazado"
                payButton.visibility = View.VISIBLE
                payButton.setOnClickListener {
                    authViewModel.getLotDetail(userData.accessToken.toString(), lote.id_lote.toString())
                    authViewModel.lotDetail.observe(viewLifecycleOwner) { detail ->
                        val direction = InscriptionsFragmentDirections
                            .actionInscriptionsFragmentToPaymentMethodFragment2(lote.id_lote.toString(), lote.precio.toString(), detail.porcentajeReserva!!.toLong(), 0,
                                lote.id_usuario_suscripcion.toInt(), true
                            )
                        it.findNavController().navigate(direction)
                        authViewModel.showLotesState()
                    }
                }
                holder.itemView.findViewById<LinearLayout>(R.id.item_registered_lots_details_container).alpha = 0.4F
                holder.itemView.findViewById<LinearLayout>(R.id.item_registered_lots_image_container).alpha = 0.4F
                holder.itemView.isClickable = false
            }
            8 -> {
                state.text = "Finalizada"
                payButton.visibility = View.GONE
                holder.itemView.findViewById<LinearLayout>(R.id.item_registered_lots_details_container).alpha = 1F
                holder.itemView.findViewById<LinearLayout>(R.id.item_registered_lots_image_container).alpha = 1F
                holder.itemView.isClickable = true
            }
            9 -> {
                state.text = "Finalizada"
                payButton.visibility = View.GONE
                holder.itemView.findViewById<LinearLayout>(R.id.item_registered_lots_details_container).alpha = 1F
                holder.itemView.findViewById<LinearLayout>(R.id.item_registered_lots_image_container).alpha = 1F
                holder.itemView.isClickable = true
            }
            10 -> {
                state.text = "Finalizada"
                payButton.visibility = View.GONE
                holder.itemView.findViewById<LinearLayout>(R.id.item_registered_lots_details_container).alpha = 1F
                holder.itemView.findViewById<LinearLayout>(R.id.item_registered_lots_image_container).alpha = 1F
                holder.itemView.isClickable = true
            }
            11 -> {
                state.text = "En proceso de devolucion"
                payButton.visibility = View.GONE
                holder.itemView.findViewById<LinearLayout>(R.id.item_registered_lots_details_container).alpha = 1F
                holder.itemView.findViewById<LinearLayout>(R.id.item_registered_lots_image_container).alpha = 1F
                holder.itemView.isClickable = true
            }
            12 -> {
                state.text = "Devolucion efectuada"
                payButton.visibility = View.GONE
                holder.itemView.findViewById<LinearLayout>(R.id.item_registered_lots_details_container).alpha = 1F
                holder.itemView.findViewById<LinearLayout>(R.id.item_registered_lots_image_container).alpha = 1F
                holder.itemView.isClickable = true
            }
            13 -> {
                state.text = "Finalizada"
                payButton.visibility = View.GONE
                holder.itemView.findViewById<LinearLayout>(R.id.item_registered_lots_details_container).alpha = 1F
                holder.itemView.findViewById<LinearLayout>(R.id.item_registered_lots_image_container).alpha = 1F
                holder.itemView.isClickable = true
            }
            else -> {
                state.text = "En proceso de devolucion"
                payButton.visibility = View.GONE
                holder.itemView.findViewById<LinearLayout>(R.id.item_registered_lots_details_container).alpha = 1F
                holder.itemView.findViewById<LinearLayout>(R.id.item_registered_lots_image_container).alpha = 1F
                holder.itemView.isClickable = false
            }
        }

        if (lote?.isStarted == true) {
            if(lote.id_estado_sub == 3 || lote.id_estado_sub == 7) {
                status.visibility = View.GONE
            } else {
                status.visibility = View.VISIBLE
            }
        } else {
            status.visibility = View.GONE
        }

        val uri = "https://desadga2.mh.gob.sv/subastaol/files/" + lote?.image
        Glide.with(holder.itemView.context)
            .load(uri)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .into(image)
        holder.itemView.findViewById<TextView>(R.id.item_registered_lots_price).text =
            holder.itemView.resources.getString(
                R.string.item_registered_lots_price,
                lote?.precio.toString()
            )
        holder.itemView.findViewById<TextView>(R.id.item_registered_lots_name).text = lote?.lote

        val date = lote?.fecha_inicio?.toLong()?.let { Date(it) }
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val myTime = formatter.format(date)

        holder.itemView.findViewById<TextView>(R.id.item_registered_lots_date).text = myTime

        holder.itemView.setOnClickListener {
            when (lote?.id_estado_sub) {
                3 -> {
                    Toast.makeText(
                        holder.itemView.context,
                        "La reserva sigue en proceso",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                6 -> {
                    when(lote.id_estado_lote) {
                        1 -> {
                            val direction = InscriptionsFragmentDirections
                                .actionInscriptionsFragmentToAuctionStartFragment(
                                    lote.id_lote.toString(),
                                    lote.fecha_inicio.toString(),
                                    lote.fecha_fin.toString(),
                                    lote.server_time.toString()
                                )
                            it.findNavController().navigate(direction)
                        }
                        2 -> {
                            val bundle = Bundle()
                            bundle.putString("idLote", lote.id_lote.toString())
                            bundle.putLong("fechaInicio", lote.fecha_inicio.toLong())
                            bundle.putLong("fechaFin", lote.fecha_fin.toLong())
                            bundle.putLong("serverTime", lote.server_time.toLong())
                            navController2.navigate(R.id.auctionInProgressFragment, bundle)
                        }
                        3 -> {
                            if(lote.id_winner == userData.id) {
                                navController2.navigate(R.id.auctionFinishWinningFragment)
                            } else {
                                navController2.navigate(R.id.auctionFinishFragment2)
                            }
                        }
                    }
                }
                8 -> {
                    navController2.navigate(R.id.auctionFinishWinningFragment)
                }
                9 -> {
                    navController2.navigate(R.id.auctionFinishWinningFragment)
                }
                10 -> {
                    navController2.navigate(R.id.auctionFinishWinningFragment)
                }
                11 -> {
                    navController2.navigate(R.id.auctionFinishFragment2)
                }
                12 -> {
                    navController2.navigate(R.id.auctionFinishFragment2)
                }
                13 -> {
                    navController2.navigate(R.id.auctionFinishWinningFragment)
                }
            }
        }
    }

    override fun getItemCount() = registeredList?.size ?: 0

    fun getUserData(user: User) {
        userData = user
    }

    fun getNavController(navController: NavController) {
        navController2 = navController
    }

    fun getAppViewModel(viewModel: AuthViewModel) {
        authViewModel = viewModel
    }

    fun getLifeCycleOwner(lifecycle: LifecycleOwner) {
        viewLifecycleOwner = lifecycle
    }

    fun setData(list: List<RegisteredLotsDetail>) {
        registeredList = list
        notifyDataSetChanged()
    }
}