package com.tstreet.onhand.core.ui.theming

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val OnHandSizes = AppSizes(
    normal = 8.dp,
    extraSmall = 2.dp,
    small = 4.dp,
    medium = 12.dp,
    large = 24.dp,
    extraLarge = 48.dp
)

class AppSizes(
    val normal: Dp,
    val extraSmall: Dp,
    val small: Dp,
    val medium: Dp,
    val large: Dp,
    val extraLarge: Dp
)