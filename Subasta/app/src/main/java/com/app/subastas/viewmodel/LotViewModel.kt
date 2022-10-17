package com.app.subastas.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.subastas.R
import com.app.subastas.data.entity.model.lots.LotesDetail
import com.app.subastas.data.entity.model.lots.WonLotsDetail
import com.app.subastas.data.entity.model.parameters.BankDetail
import com.app.subastas.data.entity.model.payment.*
import com.app.subastas.data.network.AppAPI
import com.app.subastas.data.network.UIState
import com.app.subastas.repository.LotRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class LotViewModel(private val repository: LotRepository) : ViewModel() {

    val userData1 = repository.findAllUsers()

    //Variables para mostrar el cambio de estado de las peticiones
    private val _stateUI = MutableLiveData<UIState<Int>?>()
    val stateUI: LiveData<UIState<Int>?> = _stateUI

    //Variable para guardar y mostrar datos
    val lotesList = MutableLiveData<List<LotesDetail>>()
    val bankList = MutableLiveData<List<BankDetail>>()
    val checkDetail = MutableLiveData<PayCheckDetail>()
    val wonList = MutableLiveData<List<WonLotsDetail>>()
    val lotDetail = MutableLiveData<LotesDetail>()

    //Variables para navegar entre fragmentos
    private val _goFragment = MutableLiveData<Boolean?>()
    val goFragment: LiveData<Boolean?> = _goFragment

    private val _goCodeDialog = MutableLiveData<Boolean?>()
    val goCodeDialog: LiveData<Boolean?> = _goCodeDialog

    //Funciones para navegar entre fragmentos
    fun goFragment() {
        _goFragment.value = true
    }

    fun endGoFragment() {
        _goFragment.value = null
    }

    private fun startShowCodeDialog() {
        _goCodeDialog.value = true
    }

    fun endShowCodeDialog() {
        _goCodeDialog.value = null
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

    //Variables para mostrar cambio de tabs
    private val _inscriptionState = MutableLiveData<Boolean?>()
    val inscriptionState: LiveData<Boolean?> = _inscriptionState

    private val _loteState = MutableLiveData<Boolean?>()
    val loteState: LiveData<Boolean?> = _loteState

    //Funciones para mostrar cambio de tabs
    fun showInscriptionState() {
        _inscriptionState.value = true
    }

    fun endShowInscriptionState() {
        _inscriptionState.value = null
    }

    fun showLotesState() {
        _loteState.value = true
    }

    fun endShowLotesState() {
        _loteState.value = null
    }

    //Variables utilizadas para el pago de la reserva del lote
    val reserva = MutableLiveData("")
    val comprobante = MutableLiveData("")
    val fecha = MutableLiveData("")
    val voucher = MutableLiveData("")

    fun getLotesList(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _stateUI.postValue(UIState.Loading(R.id.available_lots_progressBar))
                val call = AppAPI.lotService.getLotes(
                    "Bearer $token"
                )

                withContext(Dispatchers.Main) {
                    if (call.isSuccessful) {
                        val response = call.body()
                        var errorCode = response?.code ?: 0
                        var msg = response?.message ?: "Error intente nuevamente"
                        val data = response?.data

                        when (errorCode) {
                            1 -> {
                                _stateUI.value = UIState.Success
                                _stateUI.value = null
                                viewModelScope.launch(Dispatchers.IO) {
                                    lotesList.postValue(data?.content)
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
                        when (call.code()) {
                            500 -> {
                                _stateUI.value = UIState.Error("Error de servidor")
                            }
                            else -> {
                                _stateUI.value = UIState.Error("Error")
                            }
                        }
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

    fun getWonLots(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val call = AppAPI.lotService.getWonLots(
                    "Bearer $token"
                )
                val response = call.body()
                val errorCode = response?.code ?: 0
                val data = response?.data
                val message = response?.message ?: "No se pudo obtener la lista de lotes ganados"

                withContext(Dispatchers.Main) {
                    if (call.isSuccessful) {
                        when (errorCode) {
                            1 -> {
                                wonList.value = data!!
                            }
                            4 -> {
                                signOut()
                            }
                            else -> {
                                startShowMessage(message)
                            }
                        }
                    } else {
                        when (call.code()) {
                            500 -> {
                                _stateUI.value = UIState.Error("Error de servidor")
                            }
                            else -> {
                                _stateUI.value = UIState.Error("Error")
                            }
                        }
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

    fun getBankList(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {

                val call = AppAPI.lotService.getBankList(
                    "Bearer $token"
                )
                val response = call.body()
                val errorCode = response?.code ?: 0
                val data = response?.data
                val message = response?.message ?: "No se pudo obtener la lista de bancos"

                withContext(Dispatchers.Main) {
                    if (call.isSuccessful) {
                        when (errorCode) {
                            1 -> {
                                bankList.value = data!!
                            }
                            4 -> {
                                signOut()
                            }
                            else -> {
                                startShowMessage(message)
                            }
                        }
                    } else {
                        when (call.code()) {
                            500 -> {
                                _stateUI.value = UIState.Error("Error de servidor")
                            }
                            else -> {
                                _stateUI.value = UIState.Error("Error")
                            }
                        }
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

    fun payCheck(token: String, idUser: Long, idLote: Int, banco: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _stateUI.postValue(UIState.Loading(R.id.payment_method_check_progressBar))

                val image = voucher.value.toString().replace("\n", "")

                val body = PayReserveBody(
                    1,
                    "data:image/jpeg;base64,$image",
                    reserva.value.toString(),
                    idLote,
                    idUser,
                    comprobante.value.toString(),
                    banco,
                    fecha.value.toString()
                )

                val call = AppAPI.lotService.payCheck(
                    "Bearer $token",
                    body
                )
                val response = call.body()
                val errorCode = response?.code ?: 0
                val data = response?.data
                val msg = response?.message ?: "Error intente nuevamente"

                withContext(Dispatchers.Main) {
                    if (call.isSuccessful) {
                        when (errorCode) {
                            1 -> {
                                _stateUI.value = UIState.Success
                                _stateUI.value = null
                                startShowCodeDialog()
                                checkDetail.value = data?.get(0)
                                fecha.value = ""
                                comprobante.value = ""
                                reserva.value = ""
                                voucher.value = ""
                                showInscriptionState()
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
                        when (call.code()) {
                            500 -> {
                                _stateUI.value = UIState.Error("Error de servidor")
                            }
                            else -> {
                                _stateUI.value = UIState.Error("Error")
                            }
                        }
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

    fun payCheckAgain(token: String, idUser: Long, idLote: Int, banco: String, idSuscripcion: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _stateUI.postValue(UIState.Loading(R.id.payment_method_check_progressBar))

                val image = voucher.value.toString().replace("\n", "")

                val body = ReserveAgainBody(
                    1,
                    idSuscripcion,
                    "data:image/jpeg;base64,$image",
                    reserva.value.toString(),
                    idLote,
                    idUser,
                    comprobante.value.toString(),
                    banco,
                    fecha.value.toString()
                )

                val call = AppAPI.lotService.reserveAgain(
                    "Bearer $token",
                    body
                )
                val response = call.body()

                withContext(Dispatchers.Main) {
                    if (call.isSuccessful) {
                        val errorCode = response?.code ?: 0
                        val msg = response?.message ?: "Error intente nuevamente"
                        when (errorCode) {
                            1 -> {
                                _stateUI.value = UIState.Success
                                _stateUI.value = null
                                startShowCodeDialog()
                                checkDetail.value = response?.data?.get(0)
                                fecha.value = ""
                                comprobante.value = ""
                                reserva.value = ""
                                voucher.value = ""
                                showInscriptionState()
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
                        when (call.code()) {
                            500 -> {
                                _stateUI.value = UIState.Error("Error de servidor")
                            }
                            else -> {
                                _stateUI.value = UIState.Error("Error")
                            }
                        }
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

    fun getVoucherImage(image: String): String {
        return voucher.postValue(image).toString()
    }

    fun payTotalCheck(token: String, idSuscripcion: Int, bank: String) {
        try {
            viewModelScope.launch(Dispatchers.IO) {

                _stateUI.postValue(UIState.Loading(R.id.payment_method_check_progressBar))

                val image = voucher.value.toString().replace("\n", "")

                val body = PayTotalBody(
                    idSuscripcion,
                    1,
                    "data:image/jpeg;base64,$image",
                    reserva.value!!.toDouble(),
                    fecha.value.toString(),
                    comprobante.value.toString(),
                    bank
                )

                val call = AppAPI.lotService.payTotal(
                    "Bearer $token",
                    body
                )

                val response = call.body()
                val errorCode = response?.code ?: 0
                val data = response?.data
                val message = response?.message ?: "Error"

                withContext(Dispatchers.Main) {
                    if (call.isSuccessful) {
                        when (errorCode) {
                            1 -> {
                                _stateUI.value = UIState.Success
                                _stateUI.value = null
                                startShowCodeDialog()
                                checkDetail.value = data?.get(0)
                                fecha.value = ""
                                comprobante.value = ""
                                reserva.value = ""
                                voucher.value = ""
                                showInscriptionState()
                            }
                            4 -> {
                                _stateUI.value = UIState.Success
                                _stateUI.value = null
                                signOut()
                            }
                            else -> {
                                _stateUI.value = UIState.Error(message)
                                _stateUI.value = null
                            }
                        }
                    } else {
                        when (call.code()) {
                            500 -> {
                                _stateUI.value = UIState.Error("Error de servidor")
                            }
                            else -> {
                                _stateUI.value = UIState.Error("Error")
                            }
                        }
                    }
                }

            }
        } catch (e: Exception) {
        }
    }

    fun payTotalCheckAgain(token: String, idSuscripcion: Int, bank: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {

                _stateUI.postValue(UIState.Loading(R.id.payment_method_check_progressBar))

                val image = voucher.value.toString().replace("\n", "")

                val body = UpdateTotalBody(
                    idSuscripcion,
                    2,
                    "data:image/jpeg;base64,$image",
                    reserva.value!!.toDouble(),
                    fecha.value.toString(),
                    comprobante.value.toString(),
                    bank
                )

                val call = AppAPI.lotService.updateTotalPayment(
                    "Bearer $token",
                    body
                )
                val response = call.body()
                val errorCode = response?.code ?: 0
                val message = response?.message ?: "Error"

                withContext(Dispatchers.Main) {
                    if (call.isSuccessful) {
                        when (errorCode) {
                            1 -> {
                                _stateUI.value = UIState.Success
                                _stateUI.value = null
                                startShowCodeDialog()
                                checkDetail.value = response?.data?.get(0)
                                fecha.value = ""
                                comprobante.value = ""
                                reserva.value = ""
                                voucher.value = ""
                                showInscriptionState()
                            }
                            4 -> {
                                _stateUI.value = UIState.Success
                                _stateUI.value = null
                                signOut()
                            }
                            else -> {
                                _stateUI.value = UIState.Error(message)
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

    //Pago mediante transferencia

    fun payTransfer(token: String, idUser: Long, idLote: Int, banco: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {

                _stateUI.postValue(UIState.Loading(R.id.payment_method_transfer_progressBar))

                val body = PayReserveBody(
                    2,
                    "",
                    reserva.value.toString(),
                    idLote,
                    idUser,
                    comprobante.value.toString(),
                    banco,
                    fecha.value.toString()
                )

                val call = AppAPI.lotService.payCheck(
                    "Bearer $token",
                    body
                )
                val response = call.body()
                val errorCode = response?.code ?: 0
                val data = response?.data
                val msg = response?.message ?: "Error intente nuevamente"

                withContext(Dispatchers.Main) {
                    if (call.isSuccessful) {
                        when (errorCode) {
                            1 -> {
                                _stateUI.value = UIState.Success
                                _stateUI.value = null
                                startShowCodeDialog()
                                checkDetail.value = data?.get(0)
                                fecha.value = ""
                                comprobante.value = ""
                                reserva.value = ""
                                showInscriptionState()
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

    fun payTransferAgain(
        token: String,
        idUser: Long,
        idLote: Int,
        banco: String,
        idSuscripcion: Int
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {

                _stateUI.postValue(UIState.Loading(R.id.payment_method_transfer_progressBar))

                val body = ReserveAgainBody(
                    2,
                    idSuscripcion,
                    reserva = reserva.value.toString(),
                    idLote = idLote,
                    idUsuario = idUser,
                    transaccion = comprobante.value.toString(),
                    banco = banco,
                    fechaComprobante = fecha.value.toString()
                )

                val call = AppAPI.lotService.reserveAgain(
                    "Bearer $token",
                    body
                )
                val response = call.body()
                val errorCode = response?.code ?: 0
                val msg = response?.message ?: "Error intente nuevamente"

                withContext(Dispatchers.Main) {
                    if (call.isSuccessful) {
                        when (errorCode) {
                            1 -> {
                                _stateUI.value = UIState.Success
                                _stateUI.value = null
                                startShowCodeDialog()
                                checkDetail.value = response?.data?.get(0)
                                fecha.value = ""
                                comprobante.value = ""
                                reserva.value = ""
                                showInscriptionState()
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

    fun payTotalTransfer(token: String, idSuscripcion: Int, bank: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {

                _stateUI.postValue(UIState.Loading(R.id.payment_method_transfer_progressBar))

                val body = PayTotalBody(
                    idSuscripcion,
                    2,
                    "",
                    reserva.value!!.toDouble(),
                    fecha.value.toString(),
                    comprobante.value.toString(),
                    bank
                )

                val call = AppAPI.lotService.payTotal(
                    "Bearer $token",
                    body
                )
                val response = call.body()
                val errorCode = response?.code ?: 0
                val message = response?.message ?: "Error"

                withContext(Dispatchers.Main) {
                    if (call.isSuccessful) {
                        when (errorCode) {
                            1 -> {
                                _stateUI.value = UIState.Success
                                _stateUI.value = null
                                startShowCodeDialog()
                                checkDetail.value = response?.data?.get(0)
                                fecha.value = ""
                                comprobante.value = ""
                                reserva.value = ""
                                showInscriptionState()
                            }
                            4 -> {
                                _stateUI.value = UIState.Success
                                _stateUI.value = null
                                signOut()
                            }
                            else -> {
                                _stateUI.value = UIState.Error(message)
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

    fun payTotalTransferAgain(token: String, idSuscripcion: Int, bank: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {

                _stateUI.postValue(UIState.Loading(R.id.payment_method_transfer_progressBar))

                val body = UpdateTotalBody(
                    idSuscripcion,
                    2,
                    "",
                    reserva.value!!.toDouble(),
                    fecha.value.toString(),
                    comprobante.value.toString(),
                    bank
                )

                val call = AppAPI.lotService.updateTotalPayment(
                    "Bearer $token",
                    body
                )
                val response = call.body()
                val errorCode = response?.code ?: 0
                val message = response?.message ?: "Error"

                withContext(Dispatchers.Main) {
                    if (call.isSuccessful) {
                        when (errorCode) {
                            1 -> {
                                _stateUI.value = UIState.Success
                                _stateUI.value = null
                                startShowCodeDialog()
                                checkDetail.value = response?.data?.get(0)
                                fecha.value = ""
                                comprobante.value = ""
                                reserva.value = ""
                                showInscriptionState()
                            }
                            4 -> {
                                _stateUI.value = UIState.Success
                                _stateUI.value = null
                                signOut()
                            }
                            else -> {
                                _stateUI.value = UIState.Error(message)
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

    private fun signOut() {
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
