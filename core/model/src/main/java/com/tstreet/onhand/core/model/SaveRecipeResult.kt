package com.tstreet.onhand.core.model

class SaveRecipeResult(val recipeId: Int? = null, val status: Status, val message: String? = null) {
    companion object {
        fun success(recipeId: Int) = SaveRecipeResult(
            recipeId = recipeId,
            status = Status.SUCCESS
        )

        fun namingConflict(message: String?) = SaveRecipeResult(
            status = Status.NAME_CONFLICT,
            message = message
        )

        fun unknownError(message: String?) = SaveRecipeResult(
            status = Status.UNKNOWN_ERROR,
            message = message
        )
    }
}

enum class Status {
    SUCCESS,
    NAME_CONFLICT,
    UNKNOWN_ERROR
}
