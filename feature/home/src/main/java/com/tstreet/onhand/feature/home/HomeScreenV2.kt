package com.tstreet.onhand.feature.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.tstreet.onhand.core.model.data.IngredientCategory
import com.tstreet.onhand.core.model.ui.home.HomeViewUiStateV2
import com.tstreet.onhand.core.model.ui.home.PantryRowItem
import com.tstreet.onhand.core.model.ui.home.UiPantryIngredientV2
import com.tstreet.onhand.core.ui.IngredientSearchBar
import com.tstreet.onhand.core.ui.theming.OnHandTheme

@Composable
fun HomeScreenContainerV2(
    viewModel: HomeViewModelV2,
    onIngredientSearchBarClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreenV2(
        uiState,
        onIngredientSearchBarClick,
        viewModel::onIngredientClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenV2(
    uiState: HomeViewUiStateV2,
    onIngredientSearchBarClick: () -> Unit,
    onIngredientClick: () -> Unit
) {

    Scaffold(
        topBar = {
            IngredientSearchBar(
                onClick = onIngredientSearchBarClick,
                enabled = false
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.Top
        ) {

            when (uiState) {
                is HomeViewUiStateV2.Content -> {
                    PantryBody(
                        modifier = Modifier.fillMaxSize(),
                        rows = uiState.pantryRows,
                        onIngredientClick = onIngredientClick
                    )
                }

                HomeViewUiStateV2.Empty -> {

                }

                HomeViewUiStateV2.Error -> {


                }

                HomeViewUiStateV2.Loading -> {

                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PantryBody(
    modifier: Modifier,
    rows: List<PantryRowItem>,
    onIngredientClick: () -> Unit
) {
    LazyColumn(modifier = modifier) {
        items(rows) { item ->
            when (item) {
                is PantryRowItem.Header -> {
                    PantryIngredientCategoryHeader(
                        modifier = Modifier.fillMaxWidth(),
                        category = item.category,
                        this@LazyColumn
                    )
                }

                is PantryRowItem.Ingredient -> {
                    PantryIngredientListItem(
                        modifier = Modifier.fillMaxWidth(),
                        item = item.ingredient,
                        onClick = onIngredientClick
                    )

                }
            }
        }
    }
}

@Composable
fun PantryIngredientListItem(
    modifier: Modifier,
    item: UiPantryIngredientV2,
    onClick: () -> Unit
) {
    Row(modifier) {
        // Image

        // Column with (name, addtl info)

        Column(horizontalAlignment = Alignment.Start) {
            Text(item.ingredientName)
            Spacer(modifier = Modifier.size(16.dp))
            Text("Additional Info")
        }

        // State Info

    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PantryIngredientCategoryHeader(
    modifier: Modifier,
    category: IngredientCategory,
    scope: LazyListScope
) {
    Row(modifier) {
        Text(stringResource(category.displayName))
    }
}

@Preview
@Composable
private fun SearchScreenPreview(
    @PreviewParameter(HomeUiStatePreviewParameterProvider::class)
    uiState: HomeViewUiStateV2,
) {
    OnHandTheme {
        HomeScreenV2(uiState, { }, { })
    }
}