package com.tstreet.onhand.core.ui.theming

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val OnHandShapes = AppShapes(
    default = Shapes(),
    fullyRoundedCornerShape = RoundedCornerShape(100.dp)
)

class AppShapes(
    val default: Shapes,
    val fullyRoundedCornerShape: RoundedCornerShape
)
