package com.tstreet.onhand.core.model.ui.home


sealed interface HomeViewUiStateV2 {

    data class Content(
        val pantryRows: List<PantryRowItem>
    ) : HomeViewUiStateV2

    object Empty : HomeViewUiStateV2

    object Error : HomeViewUiStateV2

    object Loading : HomeViewUiStateV2
}