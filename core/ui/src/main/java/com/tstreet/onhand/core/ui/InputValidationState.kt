package com.tstreet.onhand.core.ui

class InputValidationState(
    val shown: Boolean,
    val message: String
) {
    companion object {
        fun hidden() = InputValidationState(false, "")
        fun shown(message: String) = InputValidationState(true, message)
    }
}
