package com.tstreet.onhand.core.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.tstreet.onhand.core.common.R.string.search_ingredients
import com.tstreet.onhand.core.ui.theming.AppTheme
import com.tstreet.onhand.core.ui.theming.Blue500

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientSearchBarScaffold(
    searchText: String = "",
    onTextChanged: (String) -> Unit = { },
    onBackClicked: (() -> Unit)? = null,
    onClick: () -> Unit = { },
    enabled: Boolean = true,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = Modifier.padding(AppTheme.sizes.medium),
        topBar = {
            IngredientSearchBar(
                searchText,
                onTextChanged,
                onBackClicked,
                onClick,
                enabled
            )
        },
        containerColor = AppTheme.colorScheme.background
    ) { paddingValues ->
        content(paddingValues)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientSearchBar(
    searchText: String = "",
    onTextChanged: (String) -> Unit,
    onBackClicked: (() -> Unit)?,
    onClick: () -> Unit = { },
    enabled: Boolean = true
) {
    val isFocused = remember { mutableStateOf<Boolean>(false) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        if (enabled) {
            focusRequester.requestFocus()
        }
    }

    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = AppTheme.sizes.medium)
            .border(
                width = AppTheme.sizes.extraSmall,
                color = AppTheme.colorScheme.primary,
                shape = AppTheme.shapes.fullyRoundedCornerShape
            )
            .clickable {
                onClick.invoke()
            }
            .focusRequester(focusRequester)
            .onFocusChanged { isFocused.value = it.isFocused },
        value = searchText,
        onValueChange = onTextChanged,
        enabled = enabled,
        singleLine = true,
        placeholder = { Text(stringResource(search_ingredients)) },
        leadingIcon = {
            if (onBackClicked != null) {
                IconButton(onClick = onBackClicked) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Go back"
                    )
                }
            }
        },
        trailingIcon = {
            if (searchText.isNotEmpty()) {
                IconButton(onClick = { onTextChanged("") }) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Clear search"
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search"
                )
            }
        },
        shape = AppTheme.shapes.fullyRoundedCornerShape,
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}