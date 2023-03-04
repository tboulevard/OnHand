package com.tstreet.onhand.core.common

// TODO: doc cleanup
// Represents an invokable use case in the domain layer of app
abstract class UseCase {
    init {
        println("[OnHand] Creating ${this.javaClass.simpleName}")
    }
}
