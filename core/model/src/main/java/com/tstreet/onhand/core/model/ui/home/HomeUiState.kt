package com.tstreet.onhand.core.model.ui.home


sealed interface HomeUiState {

    data class Content(
        val pantryRows: List<PantryRowItem>
    ) : HomeUiState

    object Empty : HomeUiState

    object Error : HomeUiState

    object Loading : HomeUiState
}