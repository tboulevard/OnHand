package com.tstreet.onhand.feature.customrecipe

import androidx.lifecycle.ViewModel

abstract class ReceivableViewModel<T> : ViewModel() {
    abstract fun onReceiveData(data: T)
}