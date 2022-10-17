package com.app.subastas.view.fragments

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.app.subastas.AppApplication
import com.app.subastas.R
import com.app.subastas.data.entity.User
import com.app.subastas.data.entity.model.lots.ImagenesDetail
import com.app.subastas.data.network.UIState
import com.app.subastas.databinding.AuctionInProgressFragmentBinding
import com.app.subastas.view.HomeActivity
import com.app.subastas.view.adapters.PhotosSliderAdapter
import com.app.subastas.view.util.PusherConnection
import com.app.subastas.view.util.ShowToast
import com.app.subastas.viewmodel.BidViewModel
import com.app.subastas.viewmodel.ViewModelFactory
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionState
import com.pusher.client.connection.ConnectionStateChange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.time.Duration.Companion.seconds

class AuctionInProgressFragment : Fragment() {

    private var mBinding: AuctionInProgressFragmentBinding? = null
    private val binding get() = mBinding!!

    private val userApp by lazy {
        activity?.application as AppApplication
    }

    private val bidViewModel: BidViewModel by viewModels {
        ViewModelFactory(userApp.bidRepository)
    }

    private lateinit var countDownTimer: CountDownTimer

    private val showToast = ShowToast()

    private lateinit var idLote: String
    private var startHour: Long = 0
    private var finishHour: Long = 0
    private var entryHour: Long = 0

    private var isStarted = false
    private var win = false

    private val pusher = PusherConnection().setConnection()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val intent = Intent(Intent(requireContext(), HomeActivity::class.java))
                    intent.putExtra("key", true)
                    startActivity(intent)
                    onDestroyView()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            callback
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        idLote = arguments?.getString("idLote").toString()
        startHour = arguments?.getLong("fechaInicio")!!.toLong()
        finishHour = arguments?.getLong("fechaFin")!!.toLong()
        entryHour = arguments?.getLong("serverTime")!!.toLong()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = AuctionInProgressFragmentBinding.inflate(inflater, container, false)
            .apply {
                viewmodel = bidViewModel
                lifecycleOwner = viewLifecycleOwner
            }

        binding.auctionStartBetEdit.doAfterTextChanged { message ->
            bidViewModel.bidPrice.value = message.toString()
        }

        if (entryHour >= finishHour) {
            if (!win) {
                val direction = AuctionInProgressFragmentDirections
                    .actionAuctionInProgressFragmentToAuctionFinishFragment2()
                view?.findNavController()?.navigate(direction)
            } else {
                val direction = AuctionInProgressFragmentDirections
                    .actionAuctionInProgressFragmentToAuctionFinishWinningFragment()
                view?.findNavController()?.navigate(direction)
            }
        }

        connectPusher()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeToRefreshListener()

        val layout: View = layoutInflater.inflate(
            R.layout.toast_style,
            view.findViewById(R.id.ll_wrapper)
        )

        count()
        initAuctionCircle()
        bidViewModel.userData1.observe(viewLifecycleOwner) { user ->
            try {
                bidViewModel.getLastBid(user[0].accessToken!!, idLote)
                initView(user)
                setUpListeners(user)
                bidViewModel.lastBidData.observe(viewLifecycleOwner) { puja ->
                    if (puja?.get(0)?.id_usuario == user[0].id) {
                        binding.auctionInProgressWinStatusText.setBackgroundDrawable(
                            resources.getDrawable(
                                R.drawable.winning_status_style
                            )
                        )
                        binding.auctionInProgressWinStatusText.text = "¡Vas ganando!"
                    } else {
                        binding.auctionInProgressWinStatusText.setBackgroundDrawable(
                            resources.getDrawable(
                                R.drawable.loosing_status_style
                            )
                        )
                        binding.auctionInProgressWinStatusText.text = "¡Vas perdiendo!"
                    }
                }
            } catch (e: Exception) {

            }
        }

        setUpObservers(layout)

    }

    private fun swipeToRefreshListener() {
        binding.auctionInProgressSwipeRefresh.setOnRefreshListener {
            try {
                bidViewModel.userData1.observe(viewLifecycleOwner) { user ->
                    bidViewModel.getLotDetail(user[0].accessToken!!, idLote)
                    bidViewModel.getLastBid(user[0].accessToken!!, idLote)
                }
                binding.auctionInProgressSwipeRefresh.isRefreshing = false
            } catch (e: Exception) {

            }
        }
    }

    private fun connectPusher() {
        pusher.connect(object : ConnectionEventListener {
            override fun onConnectionStateChange(change: ConnectionStateChange) {

            }

            override fun onError(
                message: String,
                code: String,
                e: Exception
            ) {
                Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
            }
        }, ConnectionState.ALL)

        val channel = pusher.subscribe("subasta")
        channel.bind("puja") {
            lifecycleScope.launch(Dispatchers.Main) {
                bidViewModel.userData1.observe(viewLifecycleOwner) { user ->
                    try {
                        bidViewModel.getLastBid(user[0].accessToken!!, idLote)
                        bidViewModel.getLotDetail(user[0].accessToken!!, idLote)
                    } catch (e: Exception) {

                    }
                }
            }
        }
    }

    private fun initAuctionCircle() {
        bidViewModel.lotDetail.observe(viewLifecycleOwner) { detail ->
            val remainingTime = detail.finish_subasta.toString().toLong() - detail.server_time.toString().toLong()
            val elapsedTime = detail.server_time.toString().toLong() - detail.start_subasta.toString().toLong()
            val time = detail.finish_subasta.toString().toLong() - detail.start_subasta.toString().toLong()
            val progressTime = (elapsedTime * 100) / time
            binding.circularProgressBar.apply {
                progressMax = 100f
                progress = progressTime.toFloat()
                if (detail.server_time.toString().toLong() >= detail.finish_subasta.toString().toLong()) {
                    setProgressWithAnimation(100f, 0)
                } else {
                    setProgressWithAnimation(100f, remainingTime)
                }
                progressBarWidth = 20f
                backgroundProgressBarWidth = 20f
                progressBarColor = resources.getColor(R.color.yellow)
            }
        }
    }

    private fun count() {
        bidViewModel.lotDetail.observe(viewLifecycleOwner) { detail ->
            val remainingTime = detail.finish_subasta.toString().toLong() - detail.server_time.toString().toLong()
            val time = detail.finish_subasta.toString().toLong() - detail.start_subasta.toString().toLong()
            val finalTime = time / 10f
            countDownTimer = object : CountDownTimer(remainingTime, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    try {
                        binding.auctionStartTimes.text = getString(
                            R.string.formatted_time,
                            TimeUnit.MILLISECONDS.toDays(millisUntilFinished) % 31,
                            TimeUnit.MILLISECONDS.toHours(millisUntilFinished) % 24,
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60,
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60
                        )
                        if (millisUntilFinished <= finalTime) {
                            binding.circularProgressBar.progressBarColor = Color.RED
                        }
                    } catch (e: Exception) {

                    }
                }

                override fun onFinish() {
                    bidViewModel.userData1.observe(viewLifecycleOwner) { user ->
                        try {
                            bidViewModel.getLastBid(user[0].accessToken!!, idLote)

                            bidViewModel.lastBid.observe(viewLifecycleOwner) {
                                if (it == "finish") {
                                    val direction = AuctionInProgressFragmentDirections
                                        .actionAuctionInProgressFragmentToAuctionFinishFragment2()
                                    view?.findNavController()?.navigate(direction)
                                }
                            }

                            bidViewModel.lastBidData.observe(viewLifecycleOwner) { puja ->
                                win = puja?.get(0)?.id_usuario == user[0].id
                                if (!win) {
                                    val direction = AuctionInProgressFragmentDirections
                                        .actionAuctionInProgressFragmentToAuctionFinishFragment2()
                                    view?.findNavController()?.navigate(direction)
                                } else {
                                    val direction = AuctionInProgressFragmentDirections
                                        .actionAuctionInProgressFragmentToAuctionFinishWinningFragment()
                                    view?.findNavController()?.navigate(direction)
                                }
                            }
                        } catch (e: Exception) {

                        }
                    }
                }
            }
            returnTimer(countDownTimer)
            if (!isStarted) {
                startTimer()
            } else {
                stopTimer()
            }
        }
    }

    private fun returnTimer(countDownTimer2: CountDownTimer): CountDownTimer {
        countDownTimer = countDownTimer2
        return countDownTimer
    }

    private fun startTimer() {
        countDownTimer.start()
        isStarted = true
    }

    private fun stopTimer() {
        countDownTimer.cancel()
        isStarted = false
        binding.auctionStartTimes.text = "00:00"
    }

    override fun onStop() {
        super.onStop()
        pusher.disconnect()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
        countDownTimer.cancel()
    }

    override fun onResume() {
        super.onResume()
        pusher.connect()
        lifecycleScope.launch(Dispatchers.Main) {
            bidViewModel.userData1.observe(viewLifecycleOwner) { user ->
                try {
                    bidViewModel.getLotDetail(user[0].accessToken!!, idLote)
                    bidViewModel.getLastBid(user[0].accessToken!!, idLote)
                } catch (e: Exception) {

                }
            }
        }

    }

    private fun setUpAdvertisementsRecyclerview(images: List<ImagenesDetail>) {
        binding.auctionInProgressViewpager2.apply {
            adapter = PhotosSliderAdapter()
            clipToPadding = false
            clipChildren = false
            offscreenPageLimit = 3
            getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        }

        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(30))
        compositePageTransformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.70f + r * 0.25f
        }

        binding.auctionInProgressViewpager2.apply {
            setPageTransformer(compositePageTransformer)
        }

        (binding.auctionInProgressViewpager2.adapter as PhotosSliderAdapter).setData(images)
    }

    private fun setUpListeners(users: List<User>) {
        binding.auctionInProgressActionReturn.setOnClickListener {
            val intent = Intent(Intent(requireContext(), HomeActivity::class.java))
            intent.putExtra("key", true)
            startActivity(intent)
            onDestroyView()
        }

        onBidClickListener(users)
    }

    private fun initView(user: List<User>) {
        try {
            user[0].accessToken?.let { bidViewModel.getLotDetail(it, idLote) }

            bidViewModel.lotDetail.observe(viewLifecycleOwner) { lotDetail ->
                val precioReserva = (lotDetail.porcentajeReserva!!.toDouble() * lotDetail.precio!!.toDouble()) / 100

                if (lotDetail.tipo_puja == 1) {
                    binding.auctionStartBetLayout.visibility = View.VISIBLE
                    binding.auctionStartActionBet.visibility = View.VISIBLE
                    binding.auctionStartActionBetAmount.visibility = View.GONE
                } else if (lotDetail.tipo_puja == 2) {
                    binding.auctionStartBetLayout.visibility = View.GONE
                    binding.auctionStartActionBet.visibility = View.GONE
                    binding.auctionStartActionBetAmount.visibility = View.VISIBLE
                    binding.auctionStartActionBetAmount.text = getString(
                        R.string.auction_in_progress_button_text,
                        lotDetail.monto_puja
                    )
                }
                bidViewModel.lastBid.observe(viewLifecycleOwner) {
                    when (it) {
                        "0000" -> {
                            binding.auctionInProgressWinStatusText.visibility = View.GONE
                            binding.auctionStartAmount.text = getString(
                                R.string.last_bet_text,
                                lotDetail.precio.toString()
                            )
                        }
                        precioReserva.toString() -> {
                            binding.auctionInProgressWinStatusText.visibility = View.VISIBLE
                            binding.auctionStartAmount.text = getString(
                                R.string.last_bet_text,
                                lotDetail.precio.toString()
                            )
                        }
                        else -> {
                            binding.auctionInProgressWinStatusText.visibility = View.VISIBLE
                            binding.auctionStartAmount.text = getString(
                                R.string.last_bet_text,
                                it
                            )
                        }
                    }
                }

                binding.auctionInProgressLotTitle.text = lotDetail.nombre
                binding.auctionInProgressPrice.text = lotDetail.precio.toString()
                binding.auctionInProgressWeight.text = lotDetail.peso
                binding.auctionInProgressHeight.text = lotDetail.medidas
                val fecha = lotDetail.fechaFin!!.replace("T"," ").replace("-", "/").split(".")
                binding.auctionInProgressDate.text = fecha[0]
                binding.auctionInProgressType.text = lotDetail.tipoLote?.nombre

                val images = lotDetail.imagenes
                if (images != null) {
                    setUpAdvertisementsRecyclerview(images)
                }
            }
        } catch (e: Exception) {

        }
    }

    private fun onBidClickListener(users: List<User>) {
        binding.auctionStartActionBet.setOnClickListener {
            try {
                val amount = binding.auctionStartAmount.text.toString().split("$")
                if (amount[1].isEmpty()) {
                    bidViewModel.bidUp(
                        users[0].accessToken!!,
                        users[0].id!!,
                        idLote.toLong(),
                        binding.auctionStartBetEdit.text.toString()
                    )
                    binding.auctionStartBetEdit.setText("")
                } else {
                    if (binding.auctionStartBetEdit.text.toString()
                            .toDouble() <= amount[1].toDouble()
                    ) {
                        Toast.makeText(
                            requireContext(),
                            "Ingrese una cantidad mayor",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    } else {
                        bidViewModel.bidUp(
                            users[0].accessToken!!,
                            users[0].id!!,
                            idLote.toLong(),
                            binding.auctionStartBetEdit.text.toString()
                        )
                        binding.auctionStartBetEdit.setText("")
                    }
                }
            } catch (e: Exception) {
            }
        }

        bidViewModel.lastBid.observe(viewLifecycleOwner) { bid ->
            bidViewModel.lotDetail.observe(viewLifecycleOwner) { detail ->
                binding.auctionStartActionBetAmount.setOnClickListener {
                    try {
                        val newBid = bid.toDouble() + detail.monto_puja?.toDouble()!!
                        bidViewModel.bidUp(
                            users[0].accessToken!!,
                            users[0].id!!,
                            idLote.toLong(),
                            newBid.toString()
                        )
                    } catch (e: Exception) {
                    }
                }
            }
        }
    }

    private fun setUpObservers(layout: View) {
        observeState(layout)
    }

    private fun observeState(layout: View) {
        bidViewModel.stateUI.observe(viewLifecycleOwner) { uiState ->
            handleUIState(uiState, layout)
        }
    }

    private fun handleUIState(uiState: UIState<Int>?, layout: View) {
        val toastText = layout.findViewById<TextView>(R.id.toast_text)
        when (uiState) {
            is UIState.Loading -> {
                showProgressBar()
            }
            is UIState.Success -> {
                endShowProgressBar()
                showToast.showToast(requireContext(), layout, toastText, "Oferta enviada")
            }
            is UIState.Error -> {
                endShowProgressBar()
                showToast.showToast(requireContext(), layout, toastText, uiState.message)
            }
            else -> {

            }
        }
    }

    private fun endShowProgressBar() {
        binding.auctionStartActionBetAmount.isClickable = true
        binding.auctionInProgresProgressbar.visibility = View.GONE
        binding.auctionStartActionBetAmount.setTextColor(resources.getColor(R.color.primary_color))
    }

    private fun showProgressBar() {
        binding.auctionStartActionBetAmount.isClickable = false
        binding.auctionInProgresProgressbar.visibility = View.VISIBLE
        binding.auctionStartActionBetAmount.setTextColor(resources.getColor(R.color.white))
    }

}