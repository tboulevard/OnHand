package com.tstreet.onhand.feature.home

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.tstreet.onhand.core.common.R.string.add_to_pantry_content_description
import com.tstreet.onhand.core.common.R.string.remove_from_pantry_content_description
import com.tstreet.onhand.core.model.data.IngredientCategory
import com.tstreet.onhand.core.model.ui.home.HomeUiState
import com.tstreet.onhand.core.model.ui.home.PantryRowItem
import com.tstreet.onhand.core.model.ui.home.SelectableIngredientCategory
import com.tstreet.onhand.core.model.ui.home.UiPantryIngredientV2
import com.tstreet.onhand.core.ui.IngredientSearchBarScaffold
import com.tstreet.onhand.core.ui.theming.AppTheme
import com.tstreet.onhand.core.ui.theming.OnHandTheme

@Composable
fun HomeScreenContainer(
    viewModel: HomeViewModel,
    onIngredientSearchBarClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsStateWithLifecycle(initialValue = HomeUiEvent.Idle)

    HomeScreen(
        uiState,
        event,
        // TODO: Make this part of the UI state object
        viewModel.filterCategories,
        onIngredientSearchBarClick,
        viewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    event: HomeUiEvent,
    filterCategories: List<SelectableIngredientCategory>,
    onIngredientSearchBarClick: () -> Unit,
    onEvent: (HomeUiEvent) -> Unit
) {

    LaunchedEffect(event) {
        when (event) {
            HomeUiEvent.Navigation.IngredientSearch -> {
                onIngredientSearchBarClick()
            }

            is HomeUiEvent.ShowSnackbar -> {
                // TODO: Implement
            }

            else -> {
                Log.d("[OnHand]", "Unhandled event: ${event.javaClass.simpleName}")
            }
        }
    }

    IngredientSearchBarScaffold(
        onClick = { onEvent(HomeUiEvent.Navigation.IngredientSearch) },
        enabled = false,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.Top
        ) {

            when (uiState) {
                is HomeUiState.Content -> {
                    IngredientCategoryFilters(
                        modifier = Modifier.padding(AppTheme.sizes.small),
                        filterCategories,
                        onEvent
                    )
                    PantryBody(
                        modifier = Modifier.fillMaxSize(),
                        rows = uiState.pantryRows,
                        onEvent
                    )
                }

                HomeUiState.Empty -> {

                }

                HomeUiState.Error -> {


                }

                HomeUiState.Loading -> {

                }
            }
        }
    }
}

@Composable
fun IngredientCategoryFilters(
    modifier: Modifier,
    filterState: List<SelectableIngredientCategory>,
    onCategoryClick: (HomeUiEvent.ButtonClick.CategoryFilter) -> Unit
) {
    LazyRow(modifier) {
        items(filterState) { item ->
            IngredientCategoryFilterItem(item, onCategoryClick)
        }
    }
}

@Composable
fun IngredientCategoryFilterItem(
    item: SelectableIngredientCategory,
    onClick: (HomeUiEvent.ButtonClick.CategoryFilter) -> Unit
) {
    Card(
        modifier = Modifier.padding(AppTheme.sizes.small),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isSelected.value) AppTheme.colorScheme.secondaryContainer else Color.Unspecified,
            contentColor = AppTheme.colorScheme.onSecondaryContainer
        ),
        border = BorderStroke(
            width = AppTheme.sizes.extraSmall,
            color = AppTheme.colorScheme.secondaryContainer
        )

    ) {
        Text(
            modifier =
                Modifier
                    .clickable { onClick(HomeUiEvent.ButtonClick.CategoryFilter(item)) }
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
    onEvent: (HomeUiEvent.ButtonClick) -> Unit
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
                        onEvent = onEvent
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
    onEvent: (HomeUiEvent.ButtonClick) -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            modifier = Modifier
                .size(48.dp)
                .padding(AppTheme.sizes.small),
            painter = painterResource(item.ingredient.category.placeholder),
            contentDescription = null
        )

        Column(
            modifier = Modifier
                .padding(AppTheme.sizes.small)
                .weight(1f),
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                item.ingredient.name,
                style = AppTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.size(AppTheme.sizes.small))
            Text(
                "Additional Info",
                style = AppTheme.typography.labelMedium
            )
        }

        IconButton(
            modifier = Modifier.padding(AppTheme.sizes.small),
            onClick = {
                if (item.inPantry.value) {
                    onEvent(HomeUiEvent.ButtonClick.RemoveFromPantry(item))
                } else {
                    onEvent(HomeUiEvent.ButtonClick.AddToPantry(item))
                }
            }
        ) {
            Icon(
                imageVector = if (item.inPantry.value) Icons.Outlined.Delete else Icons.Outlined.Add,
                contentDescription =
                    if (item.inPantry.value) {
                        stringResource(remove_from_pantry_content_description)
                    } else {
                        stringResource(add_to_pantry_content_description)
                    },
                tint = MaterialTheme.colorScheme.primary
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
    preview: HomeCombinedPreviewParameterUiState
) {
    OnHandTheme {
        HomeScreen(preview.uiStateV2, HomeUiEvent.Idle, preview.filterCategories, { }, { })
    }
}
