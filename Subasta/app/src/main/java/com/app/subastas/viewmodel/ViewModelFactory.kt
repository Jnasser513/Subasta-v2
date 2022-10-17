package com.app.subastas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.subastas.repository.*
import java.lang.Exception

class ViewModelFactory<R>(private val repository: R): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(repository as AuthRepository) as T
        }
        if(modelClass.isAssignableFrom(LotViewModel::class.java)) {
            return LotViewModel(repository as LotRepository) as T
        }
        if(modelClass.isAssignableFrom(DataViewModel::class.java)) {
            return DataViewModel(repository as DataRepository) as T
        }
        if(modelClass.isAssignableFrom(BidViewModel::class.java)) {
            return BidViewModel(repository as BidRepository) as T
        }
        if(modelClass.isAssignableFrom(SubscriptionViewModel::class.java)) {
            return SubscriptionViewModel(repository as SubscriptionRepository) as T
        }
        throw Exception("Unknown viewmodel class")
    }
}