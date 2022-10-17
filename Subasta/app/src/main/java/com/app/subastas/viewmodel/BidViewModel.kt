package com.app.subastas.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.subastas.R
import com.app.subastas.data.entity.model.bid.NewBidBody
import com.app.subastas.data.entity.model.bid.PujaDetail
import com.app.subastas.data.entity.model.lots.LotesDetail
import com.app.subastas.data.network.AppAPI
import com.app.subastas.data.network.UIState
import com.app.subastas.repository.BidRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class BidViewModel(private val repository: BidRepository) : ViewModel() {

    val userData1 = repository.findAllUsers()

    //Variables para mostrar el cambio de estado de las peticiones
    private val _stateUI = MutableLiveData<UIState<Int>?>()
    val stateUI: LiveData<UIState<Int>?> = _stateUI

    //Variables utilizadas para guardar y mostrar datos
    val bidPrice = MutableLiveData("")
    val lastBid = MutableLiveData("")
    val lastBidData = MutableLiveData<List<PujaDetail>?>()
    val lotDetail = MutableLiveData<LotesDetail>()

    //Variables para mostrar toast
    private val _showMessage = MutableLiveData<Boolean?>()
    val showMessage: LiveData<Boolean?> = _showMessage
    var toastMessage = MutableLiveData<String>()

    //Funciones para mostrar toast
    private fun startShowMessage(message: String) {
        toastMessage.value = message
        _showMessage.value = true
    }

    fun endShowMessage() {
        _showMessage.value = null
    }

    fun getLastBid(token: String, idLote: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val call = AppAPI.bidService.getLastBid(
                    "Bearer $token",
                    idLote
                )
                val response = call.body()
                val errorCode = response?.code ?: 0
                val data = response?.data
                val message = response?.message ?: "No se pudo obtener la ultima puja"

                withContext(Dispatchers.Main) {
                    if (call.isSuccessful) {
                        if (errorCode == 1) {
                            if (data.isNullOrEmpty()) {
                                lastBid.value == "$0000"
                            } else {
                                lastBid.value = data[0].monto.toString()
                                lastBidData.value = data
                            }
                        } else if (errorCode == 2) {
                            lastBid.value = "0000"
                        } else {
                            startShowMessage(message)
                        }
                    } else {
                        startShowMessage(message)
                    }
                }
            } catch (e: IOException) {
                when (e) {
                    is java.net.SocketTimeoutException -> {
                        withContext(Dispatchers.Main) {
                            _stateUI.value = UIState.Error("Error de internet")
                            _stateUI.value = null
                        }
                    }
                    is java.net.UnknownHostException -> {
                        withContext(Dispatchers.Main) {
                            _stateUI.value = UIState.Error("No tiene coneccion a internet")
                            _stateUI.value = null
                        }
                    }
                    is com.google.gson.stream.MalformedJsonException -> {
                        withContext(Dispatchers.Main) {
                            _stateUI.value = UIState.Error("Error de internet")
                            _stateUI.value = null
                        }
                    }
                }
            }
        }
    }

    fun getLotDetail(token: String, idLote: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val call = AppAPI.bidService.getLotDetail(
                    "Bearer $token",
                    idLote
                )
                val response = call.body()
                val errorCode = response?.code ?: 0
                val message = response?.message ?: "Error obteniendo datos"
                val data = response?.data
                withContext(Dispatchers.Main) {
                    if (call.isSuccessful) {
                        if (errorCode == 1) {
                            lotDetail.postValue(data?.get(0))
                        } else {
                        }
                    } else {
                    }
                }
            } catch (e: IOException) {
                when (e) {
                    is java.net.SocketTimeoutException -> {
                        withContext(Dispatchers.Main) {
                            _stateUI.value = UIState.Error("Error de internet")
                            _stateUI.value = null
                        }
                    }
                    is java.net.UnknownHostException -> {
                        withContext(Dispatchers.Main) {
                            _stateUI.value = UIState.Error("No tiene coneccion a internet")
                            _stateUI.value = null
                        }
                    }
                    is com.google.gson.stream.MalformedJsonException -> {
                        withContext(Dispatchers.Main) {
                            _stateUI.value = UIState.Error("Error de internet")
                            _stateUI.value = null
                        }
                    }
                }
            }
        }
    }

    fun bidUp(token: String, idUser: Long, idLote: Long, monto: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _stateUI.postValue(UIState.Loading(R.id.auction_in_progres_progressbar))

                val body = NewBidBody(
                    idUser,
                    idLote,
                    monto
                )

                val call = AppAPI.bidService.bidUp(
                    "Bearer $token",
                    body
                )
                val response = call.body()
                val error = response?.code ?: 0
                val message = response?.message ?: "No se pudo realizar la puja"
                val data = response?.data

                withContext(Dispatchers.Main) {
                    if (call.isSuccessful) {
                        if (error == 1) {
                            _stateUI.value = UIState.Success
                            _stateUI.value = null
                        } else {
                            _stateUI.value = UIState.Error(message)
                            _stateUI.value = null
                        }
                    } else {
                        startShowMessage(message)
                    }
                }

            } catch (e: IOException) {
                when (e) {
                    is java.net.SocketTimeoutException -> {
                        withContext(Dispatchers.Main) {
                            _stateUI.value = UIState.Error("Error de internet")
                            _stateUI.value = null
                        }
                    }
                    is java.net.UnknownHostException -> {
                        withContext(Dispatchers.Main) {
                            _stateUI.value = UIState.Error("No tiene coneccion a internet")
                            _stateUI.value = null
                        }
                    }
                    is com.google.gson.stream.MalformedJsonException -> {
                        withContext(Dispatchers.Main) {
                            _stateUI.value = UIState.Error("Error de internet")
                            _stateUI.value = null
                        }
                    }
                }
            }
        }
    }

}