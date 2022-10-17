package com.app.subastas.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.subastas.R
import com.app.subastas.data.entity.model.ApiResponse
import com.app.subastas.data.entity.model.parameters.DepartmentsDetail
import com.app.subastas.data.entity.model.parameters.MunicipalitiesDetail
import com.app.subastas.data.network.AppAPI
import com.app.subastas.data.network.UIState
import com.app.subastas.repository.DataRepository
import com.google.android.gms.common.api.Api
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.UnknownHostException

class DataViewModel(private val repository: DataRepository): ViewModel() {

    private val _status = MutableLiveData<UIState<Int>?>()
    val status: LiveData<UIState<Int>?> get() = _status

    //Variables utilizadas para almacenar y mostrar datos
    val departmentList = MutableLiveData<List<DepartmentsDetail>>()
    val municipalityList = MutableLiveData<MutableList<MunicipalitiesDetail>>()

    //Variables utilizadas para mostrar los toast
    private val _showMessage = MutableLiveData<Boolean?>()
    val showMessage: LiveData<Boolean?> = _showMessage
    var toastMessage = MutableLiveData<String>()

    //Funciones utilizadas para mostrar los toast
    private fun startShowMessage(message: String) {
        toastMessage.value = message
        _showMessage.value = true
    }

    fun endShowMessage() {
        _showMessage.value = null
    }

    fun getDepartmentList() {
        _status.value = UIState.Loading(R.id.register_natural_person_department_progressbar)
        viewModelScope.launch(Dispatchers.IO) {
            _status.postValue(when(val response = repository.getDepartments()) {
                is ApiResponse.Error -> UIState.Error(response.message)
                is ApiResponse.ErrorWithException -> UIState.ErrorWithException(response.exception)
                is ApiResponse.Success -> {
                    departmentList.postValue(response.data!!)
                    UIState.Success
                }
            })
        }
    }

    fun getMunicipalityList(department: String) {
        _status.value = UIState.Loading(R.id.register_natural_person_municipality_progressbar)
        viewModelScope.launch(Dispatchers.IO) {
            _status.postValue(when(val response = repository.getMunicipalities(department)) {
                is ApiResponse.Error -> UIState.Error(response.message)
                is ApiResponse.ErrorWithException -> UIState.ErrorWithException(response.exception)
                is ApiResponse.Success -> {
                    municipalityList.postValue(response.data!!)
                    UIState.Success
                }
            })
        }
    }

}