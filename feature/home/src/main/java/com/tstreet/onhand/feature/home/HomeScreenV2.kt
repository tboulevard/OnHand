package com.tstreet.onhand.feature.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.tstreet.onhand.core.model.data.IngredientCategory
import com.tstreet.onhand.core.model.ui.home.HomeViewUiStateV2
import com.tstreet.onhand.core.model.ui.home.PantryRowItem
import com.tstreet.onhand.core.model.ui.home.SelectableIngredientCategory
import com.tstreet.onhand.core.model.ui.home.SelectedIngredientCategoryState
import com.tstreet.onhand.core.model.ui.home.UiPantryIngredientV2
import com.tstreet.onhand.core.ui.IngredientSearchBarScaffold
import com.tstreet.onhand.core.ui.theming.AppTheme
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
        viewModel::onIngredientClick,
        viewModel::onCategoryClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenV2(
    uiState: HomeViewUiStateV2,
    onIngredientSearchBarClick: () -> Unit,
    onIngredientClick: () -> Unit,
    onCategoryClick: (SelectableIngredientCategory) -> Unit,
) {
    IngredientSearchBarScaffold(
        onClick = onIngredientSearchBarClick,
        enabled = false,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.Top
        ) {

            when (uiState) {
                is HomeViewUiStateV2.Content -> {
                    IngredientCategoryFilters(
                        modifier = Modifier.padding(AppTheme.sizes.small),
                        uiState.filterState,
                        onCategoryClick
                    )
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

@Composable
fun IngredientCategoryFilters(
    modifier: Modifier,
    filterState: SelectedIngredientCategoryState,
    onCategoryClick: (SelectableIngredientCategory) -> Unit
) {
    LazyRow(modifier) {
        items(filterState.categories) { item ->
            IngredientCategoryFilterItem(item, onCategoryClick)
        }
    }
}

@Composable
fun IngredientCategoryFilterItem(
    item: SelectableIngredientCategory,
    onClick: (SelectableIngredientCategory) -> Unit
) {
    Card(
        modifier = Modifier.padding(AppTheme.sizes.small),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isSelected.value) AppTheme.colorScheme.secondaryContainer else Color.Unspecified,
            contentColor = if (item.isSelected.value) AppTheme.colorScheme.onSecondaryContainer else Color.Unspecified
        ),
        border = BorderStroke(
            width = AppTheme.sizes.extraSmall,
            color = AppTheme.colorScheme.secondaryContainer
        )

    ) {
        Text(
            modifier =
                Modifier
                    .clickable { onClick(item) }
                    .padding(AppTheme.sizes.normal),
            text = stringResource(item.category.displayName),
            style = AppTheme.typography.bodySmall,
        )
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = AppTheme.sizes.small,
                                top = AppTheme.sizes.medium,
                                bottom = AppTheme.sizes.normal,
                                end = AppTheme.sizes.small
                            ),
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
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .size(48.dp)
                .padding(AppTheme.sizes.small),
            painter = painterResource(item.category.placeholder),
            contentDescription = null
        )

        Column(
            modifier = Modifier.padding(AppTheme.sizes.small),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                item.ingredientName,
                style = AppTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.size(AppTheme.sizes.small))
            Text(
                "Additional Info",
                style = AppTheme.typography.labelMedium
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PantryIngredientCategoryHeader(
    modifier: Modifier,
    category: IngredientCategory,
    scope: LazyListScope
) {
    Card(
        modifier,
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.colorScheme.secondaryContainer,
            contentColor = AppTheme.colorScheme.onSecondaryContainer
        )
    ) {
        Row(modifier.padding(AppTheme.sizes.small)) {
            Text(
                stringResource(category.displayName),
                style = AppTheme.typography.headlineSmall
            )
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview(
    @PreviewParameter(HomeUiStatePreviewParameterProvider::class)
    uiState: HomeViewUiStateV2,
) {
    OnHandTheme {
        HomeScreenV2(uiState, { }, { }, { })
    }
}
