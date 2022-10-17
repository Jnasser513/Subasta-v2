package com.app.subastas.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.subastas.R
import com.app.subastas.data.entity.model.lots.RegisteredLotsDetail
import com.app.subastas.data.entity.model.lots.SuscriptionDetail
import com.app.subastas.data.network.AppAPI
import com.app.subastas.data.network.UIState
import com.app.subastas.repository.SubscriptionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class SubscriptionViewModel(private val repository: SubscriptionRepository) : ViewModel() {

    val userData1 = repository.findAllUsers()

    //Variables para mostrar el cambio de estado de los llamados de la api
    private val _stateUI = MutableLiveData<UIState<Int>?>()
    val stateUI: LiveData<UIState<Int>?> = _stateUI

    //Variables para almacenar y mostrar datos
    val registeredList = MutableLiveData<List<RegisteredLotsDetail>>()
    val subscriptionDetail = MutableLiveData<SuscriptionDetail>()

    //Variables para mostrar cambio en tabs
    private val _loteState = MutableLiveData<Boolean?>()
    val loteState: LiveData<Boolean?> = _loteState

    //Funciones para mostrar cambio en tabs
    fun showLotesState() {
        _loteState.value = true
    }

    fun endShowLotesState() {
        _loteState.value = null
    }

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

    fun getRegisteredLots(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {

                _stateUI.postValue(UIState.Loading(R.id.registered_lots_progressBar))

                val response = AppAPI.subscriptionService.getRegisteredLots(
                    "Bearer $token"
                )
                val errorCode = response.body()?.code ?: 0
                val msg = response.body()?.message ?: "Error intente nuevamente"

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {

                        when (errorCode) {
                            1 -> {
                                _stateUI.value = UIState.Success
                                _stateUI.value = null
                                viewModelScope.launch(Dispatchers.IO) {
                                    response.body()?.data?.let { list ->
                                        registeredList.postValue(list)
                                    }
                                }
                            }
                            4 -> {
                                _stateUI.value = UIState.Success
                                _stateUI.value = null
                                signOut()
                            }
                            else -> {
                                _stateUI.value = UIState.Error(msg)
                                _stateUI.value = null
                            }
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

    fun getSubscriptionDetail(token: String, idSuscripcion: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val call = AppAPI.subscriptionService.getSuscriptionDetail(
                    "Bearer $token",
                    idSuscripcion
                )

                val response = call.body()
                val errorCode = response?.code ?: 0
                val message = response?.message ?: "Error al obtener el detalle de la suscripion"

                if (call.isSuccessful) {
                    when (errorCode) {
                        1 -> {
                            subscriptionDetail.postValue(response?.data!!)
                        }
                        else -> {
                            startShowMessage(message)
                        }
                    }
                } else {
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

    private val _goFragment = MutableLiveData<Boolean?>()
    val goFragment: LiveData<Boolean?> = _goFragment

    fun goFragment() {
        _goFragment.value = true
    }

    fun endGoFragment() {
        _goFragment.value = null
    }

    fun signOut() {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                val token = userData1.value?.get(0)?.accessToken
                val id = userData1.value?.get(0)?.id
                id?.let {
                    AppAPI.authService.deleteTokenPush(
                        "Bearer $token",
                        it
                    )
                }
                repository.deleteUser()
                withContext(Dispatchers.Main) {
                    goFragment()
                }
            }
        } catch (e: Exception) {
        }
    }

}