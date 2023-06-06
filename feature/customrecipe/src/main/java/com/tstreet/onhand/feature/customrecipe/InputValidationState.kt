package com.tstreet.onhand.feature.customrecipe

class InputValidationState(
    val shown: Boolean,
    val message: String
) {
    companion object {
        fun hidden() = InputValidationState(false, "")
        fun shown(message: String) = InputValidationState(true, message)
    }
}
