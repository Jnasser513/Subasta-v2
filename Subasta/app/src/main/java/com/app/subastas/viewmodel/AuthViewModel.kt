package com.app.subastas.viewmodel

import android.util.Log
import android.util.MalformedJsonException
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.subastas.R
import com.app.subastas.data.entity.SliderState
import com.app.subastas.data.entity.User
import com.app.subastas.data.entity.model.change_password.ChangePasswordBody
import com.app.subastas.data.entity.model.change_password.ForgotPasswordBody
import com.app.subastas.data.entity.model.change_password.RecoveryCodeBody
import com.app.subastas.data.entity.model.change_password.VerifyRecoveryBody
import com.app.subastas.data.entity.model.login.LoginCodeRequest
import com.app.subastas.data.entity.model.login.LoginRequest
import com.app.subastas.data.entity.model.lots.LotesDetail
import com.app.subastas.data.entity.model.register.RegisterJuridicPersonBody
import com.app.subastas.data.entity.model.register.RegisterNaturalBody
import com.app.subastas.data.entity.model.register.UploadFileBody
import com.app.subastas.data.network.AppAPI
import com.app.subastas.data.network.UIState
import com.app.subastas.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.net.URLEncoder
import java.util.*

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _stateUI = MutableLiveData<UIState<Int>?>()
    val stateUI: LiveData<UIState<Int>?> = _stateUI

    val userData1 = repository.findAllUsers()
    val sliderState = repository.findAllStates()

    private val _goChangePassword = MutableLiveData<Boolean?>()
    val goChangePassword: LiveData<Boolean?> = _goChangePassword

    fun goChangePassword() {
        _goChangePassword.value = true
    }

    fun endGoChangePassword() {
        _goChangePassword.value = null
    }

    private val _goInscriptions = MutableLiveData<Boolean?>()
    val goInscriptions: LiveData<Boolean?> = _goInscriptions

    fun goInscriptionsFromAuction() {
        _goInscriptions.value = true
    }

    fun endGoInscriptionsFromAuction() {
        _goInscriptions.value = null
    }

    private val _loteState = MutableLiveData<Boolean?>()
    val loteState: LiveData<Boolean?> = _loteState

    fun showLotesState() {
        _loteState.value = true
    }

    fun endShowLotesState() {
        _loteState.value = null
    }

    private val _inscriptionState = MutableLiveData<Boolean?>()
    val inscriptionState: LiveData<Boolean?> = _inscriptionState

    fun showInscriptionState() {
        _inscriptionState.value = true
    }

    fun endShowInscriptionState() {
        _inscriptionState.value = null
    }

    //Variables utilizadas para el registro
    val nameRegister = MutableLiveData("")
    val directionRegister = MutableLiveData("")
    val duiRegister = MutableLiveData("")
    val nitRegister = MutableLiveData("")
    val phoneRegister = MutableLiveData("")
    val emailRegister = MutableLiveData("")
    val nameBusinessRegister = MutableLiveData("")
    val nitBusinessRegister = MutableLiveData("")
    val passwordRegister = MutableLiveData("")

    //Variables utilizadas para el envio del correo
    val emailLogin = MutableLiveData("")
    val passwordLogin = MutableLiveData("")

    //Variables utilizadas para la verificacion del codigo
    var completeCode: String = ""
    val firstCode = MutableLiveData("")
    val secondCode = MutableLiveData("")
    val thirdCode = MutableLiveData("")
    val fourCode = MutableLiveData("")
    val fiveCode = MutableLiveData("")

    //Variables observables para cambio de fragmentos
    private val _goCodeDialog = MutableLiveData<Boolean?>()
    val goCodeDialog: LiveData<Boolean?> = _goCodeDialog

    private val _goFragment = MutableLiveData<Boolean?>()
    val goFragment: LiveData<Boolean?> = _goFragment

    //Funciones para navegar entre fragmentos
    private fun startShowCodeDialog() {
        _goCodeDialog.value = true
    }

    fun endShowCodeDialog() {
        _goCodeDialog.value = null
    }

    fun goFragment() {
        _goFragment.value = true
    }

    fun endGoFragment() {
        _goFragment.value = null
    }

    fun register(municipality: String, department: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _stateUI.postValue(UIState.Loading(R.id.register_natural_person_progressbar))

                val body = RegisterNaturalBody(
                    1,
                    nameRegister.value.toString(),
                    directionRegister.value.toString(),
                    municipality.trim(),
                    department.trim(),
                    duiRegister.value.toString().replace("-", "").trim(),
                    nitRegister.value.toString().replace("-", "").trim(),
                    phoneRegister.value.toString().replace("-", "").trim(),
                    emailRegister.value.toString().trim().lowercase(Locale.getDefault()),
                    passwordRegister.value.toString()
                )

                val call = AppAPI.authService.register(body)
                val response = call.body()
                val errorCode = response?.errorCode ?: 0
                val msg = response?.message ?: "Error intente nuevamente"

                withContext(Dispatchers.Main) {
                    if (call.isSuccessful) {
                        if (errorCode == 1) {
                            _stateUI.value = UIState.Success
                            _stateUI.value = null
                            //startShowCodeDialog()
                            nameRegister.value = ""
                            directionRegister.value = ""
                            duiRegister.value = ""
                            nitRegister.value = ""
                            phoneRegister.value = ""
                            emailRegister.value = ""
                            passwordRegister.value = ""
                        } else {
                            _stateUI.value = UIState.Error(msg)
                            _stateUI.value = null
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

    fun registerJuridic(municipality: String, department: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _stateUI.postValue(UIState.Loading(R.id.register_juridic_person_progressbar))

                val body = RegisterJuridicPersonBody(
                    2,
                    nameRegister.value.toString(),
                    duiRegister.value.toString().replace("-", "").trim(),
                    nitRegister.value.toString().replace("-", "").trim(),
                    phoneRegister.value.toString().replace("-", "").trim(),
                    emailRegister.value.toString().trim().lowercase(Locale.getDefault()),
                    nameBusinessRegister.value.toString(),
                    nitBusinessRegister.value.toString().trim(),
                    directionRegister.value.toString(),
                    municipality.trim(),
                    department.trim(),
                    passwordRegister.value.toString()
                )

                val call = AppAPI.authService.registerJuridic(body)
                val response = call.body()
                val errorCode = response?.errorCode ?: 0
                val msg = response?.message ?: "Error intente nuevamente"

                withContext(Dispatchers.Main) {
                    if (call.isSuccessful) {
                        if (errorCode == 1) {
                            _stateUI.value = UIState.Success
                            _stateUI.value = null
                            startShowCodeDialog()
                            nameRegister.value = ""
                            duiRegister.value = ""
                            nitRegister.value = ""
                            phoneRegister.value = ""
                            emailRegister.value = ""
                            nameBusinessRegister.value = ""
                            nitBusinessRegister.value = ""
                            directionRegister.value = ""
                            passwordRegister.value = ""
                        } else {
                            _stateUI.value = UIState.Error(msg)
                            _stateUI.value = null
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

    fun login() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _stateUI.postValue(UIState.Loading(R.id.dialog_enter_code_loading))

                val body = LoginRequest(
                    emailLogin.value.toString().trim().lowercase(Locale.getDefault()),
                    passwordLogin.value.toString()
                )

                val call = AppAPI.authService.loginUser(body)
                val response = call.body()
                val errorCode = response?.code ?: 0
                val msg = response?.message ?: "Error intente nuevamente"

                withContext(Dispatchers.Main) {
                    if (call.isSuccessful) {
                        if (errorCode == 1) {
                            _stateUI.value = UIState.Success
                            _stateUI.value = null
                            startShowCodeDialog()
                        } else {
                            _stateUI.value = UIState.Error(msg)
                            _stateUI.value = null
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

    fun verifyCode(email: String, password: String, token_push: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _stateUI.postValue(UIState.Loading(R.id.dialog_enter_code_progressBar))
                completeCode =
                    "${firstCode.value.toString()}${secondCode.value.toString()}" +
                            "${thirdCode.value.toString()}${fourCode.value.toString()}" +
                            "${fiveCode.value.toString()}"

                val body = LoginCodeRequest(
                    email,
                    password,
                    completeCode,
                    token_push
                )

                val call = AppAPI.authService.loginCode(body)
                val response = call.body()
                val errorCode = response?.code ?: 0
                val msg = response?.message ?: "Error intente nuevamente"
                val data = response?.data

                withContext(Dispatchers.Main) {
                    if (call.isSuccessful) {
                        if (errorCode == 1) {
                            viewModelScope.launch(Dispatchers.IO) {
                                data?.get(0).let {
                                    if (it != null) {
                                        if (userData1.value!!.isEmpty()) {
                                            val newUser = User(
                                                it.id,
                                                it.email,
                                                it.roles[0],
                                                it.accessToken,
                                                it.tokenType
                                            )
                                            repository.insertUser(newUser)
                                        } else {
                                            repository.updateUser(
                                                it.id,
                                                it.roles,
                                                it.accessToken,
                                                it.tokenType,
                                                it.email
                                            )
                                        }
                                    }
                                }
                            }
                            _stateUI.value = UIState.Success
                            _stateUI.value = null
                            goFragment()
                        } else {
                            _stateUI.value = UIState.Error(msg)
                            _stateUI.value = null
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

    fun changeState() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val newState = SliderState(true)
                repository.insertState(newState)

                withContext(Dispatchers.Main) {
                    goFragment()
                }
            } catch (e: Exception) {
            }
        }
    }

    fun uploadFile(file: String) {

        viewModelScope.launch(Dispatchers.IO) {
            try {

                _stateUI.postValue(UIState.Loading(R.id.step_progressBar))

                val body =
                    UploadFileBody(URLEncoder.encode("data:application/pdf;base64,$file", "UTF-8"))
                val call = AppAPI.authService.uploadFile(body)
                val response = call.body()
                val errorCode = response?.code ?: 0
                val message = response?.message ?: "Error"

                withContext(Dispatchers.Main) {
                    if (call.isSuccessful) {
                        if (errorCode == 1) {
                            _stateUI.value = UIState.Success
                            _stateUI.value = null
                        } else {
                            _stateUI.value = UIState.Error(message)
                            _stateUI.value = null
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

    val recoveryCode = MutableLiveData("")

    fun sendRecoveryCode() {
        viewModelScope.launch(Dispatchers.IO) {
            try {

                _stateUI.postValue(UIState.Loading(R.id.enter_email_progressBar))

                val body = RecoveryCodeBody(
                    recoveryCode.value.toString().trim()
                        .lowercase(Locale.getDefault())
                )

                val call = AppAPI.authService.sendRecoveryCode(body)
                val response = call.body()
                val errorCode = response?.code ?: 0
                val message = response?.message ?: "Error"

                withContext(Dispatchers.Main) {
                    if (call.isSuccessful) {
                        if (errorCode == 1) {
                            _stateUI.value = UIState.Success
                            _stateUI.value = null
                            recoveryCode.value = ""
                            startShowCodeDialog()
                        } else {
                            _stateUI.value = UIState.Error(message)
                            _stateUI.value = null
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

    val recoveryCode1 = MutableLiveData("")
    val recoveryCode2 = MutableLiveData("")
    val recoveryCode3 = MutableLiveData("")
    val recoveryCode4 = MutableLiveData("")
    val recoveryCode5 = MutableLiveData("")

    fun verifyRecoveryCode(email: String) {

        viewModelScope.launch(Dispatchers.IO) {
            try {
                _stateUI.postValue(UIState.Loading(R.id.recovery_code_progressBar))

                val completeCode =
                    recoveryCode1.value.toString() +
                            recoveryCode2.value.toString() +
                            recoveryCode3.value.toString() +
                            recoveryCode4.value.toString() +
                            recoveryCode5.value.toString()

                val body = VerifyRecoveryBody(
                    email,
                    completeCode
                )

                val call = AppAPI.authService.verifyRecoveryCode(body)
                val response = call.body()
                val errorCode = response?.code ?: 0
                val message = response?.message ?: "Error"

                withContext(Dispatchers.Main) {
                    if (call.isSuccessful) {
                        if (errorCode == 1) {
                            _stateUI.value = UIState.Success
                            _stateUI.value = null
                            goFragment()
                            recoveryCode1.value = ""
                            recoveryCode2.value = ""
                            recoveryCode3.value = ""
                            recoveryCode4.value = ""
                            recoveryCode5.value = ""
                        } else {
                            _stateUI.value = UIState.Error(message)
                            _stateUI.value = null
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

    fun changePassword(body: ChangePasswordBody) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _stateUI.postValue(UIState.Loading(R.id.change_password_progressBar))

                val call = AppAPI.authService.changePassword(body)
                val response = call.body()
                val errorCode = response?.code ?: 0
                val message = response?.message ?: "Error"

                withContext(Dispatchers.Main) {
                    if (call.isSuccessful) {
                        if (errorCode == 1) {
                            _stateUI.value = UIState.Success
                            _stateUI.value = null
                            goFragment()
                        } else {
                            _stateUI.value = UIState.Error(message)
                            _stateUI.value = null
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

    val passwordChange = MutableLiveData("")

    fun updatePassword(token: String, email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {

                _stateUI.postValue(UIState.Loading(R.id.change_password_progressBar))

                val body = ForgotPasswordBody(
                    email,
                    passwordChange.value.toString()
                )

                val call = AppAPI.authService.updatePassword(
                    "Bearer $token",
                    body
                )

                val response = call.body()
                val errorCode = response?.code ?: 0
                val message = response?.message ?: "Error"

                withContext(Dispatchers.Main) {
                    if (call.isSuccessful) {
                        if (errorCode == 1) {
                            _stateUI.value = UIState.Success
                            _stateUI.value = null
                            goChangePassword()
                        } else {
                            _stateUI.value = UIState.Error(message)
                            _stateUI.value = null
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

    val lotDetail = MutableLiveData<LotesDetail>()

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