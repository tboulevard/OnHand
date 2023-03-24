package com.tstreet.onhand.feature.recipedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.domain.GetRecipeDetailUseCase
import com.tstreet.onhand.core.model.RecipeDetail
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

class RecipeDetailViewModel @Inject constructor(
    getRecipeDetail: Provider<GetRecipeDetailUseCase>,
    // TODO: leaving around as an example...
    @Named(IO) private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    init {
        println("[OnHand] Creating ${this.javaClass.simpleName}")
    }

    fun onPageLoaded(recipeId : Int) {
        println("[OnHand] RecipeDetailViewModel, onPageLoaded($recipeId)")
        _recipeId.update { recipeId }
    }

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading

    private val _recipeId = MutableStateFlow(0)
    // TODO: value unused, but needs to be observed to trigger
    val recipeId = _recipeId
        .onStart { _isLoading.update { true } }
        .onEach { id ->
            _recipe.update { getRecipeDetail.get().invoke(id).first() }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            // TODO: make this an empty state instead
            initialValue = 0
        )

    private val _recipe = MutableStateFlow(RecipeDetail(0, ""))
    val recipe = _recipe
        .onEach {
            _isLoading.update { false }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            // TODO: make this an empty state instead
            initialValue = RecipeDetail(0, "")
        )
}
