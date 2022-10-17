package com.app.subastas.data.network

import androidx.annotation.IdRes

sealed class UIState<out Int> {

    object Resume: UIState<Nothing>()

    object Success : UIState<Nothing>()

    class Loading(@IdRes val loadingMessageId: Int) : UIState<Int>()

    class Error(val message: String) : UIState<Int>()

    class ErrorWithException(val exception: Exception): UIState<Int>()

}