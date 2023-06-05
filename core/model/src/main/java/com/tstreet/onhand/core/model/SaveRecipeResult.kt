package com.tstreet.onhand.core.model

class SaveRecipeResult(val status: Status, val message: String? = null) {
    companion object {
        fun success() = SaveRecipeResult(Status.SUCCESS)

        fun namingConflict(message: String?) = SaveRecipeResult(Status.NAME_CONFLICT, message)
    }
}

enum class Status {
    SUCCESS,
    NAME_CONFLICT
}