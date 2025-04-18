package com.tstreet.onhand.core.domain.usecase

import android.util.Log

// TODO: doc cleanup
// Represents an invokable use case in the domain layer of app
abstract class UseCase {
    init {
        Log.d("[OnHand]", "${this.javaClass.simpleName} created")
    }
}