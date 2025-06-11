package com.tstreet.onhand.core.ui.theming

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color


@Composable
fun OnHandTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {

    val colorScheme = if (darkTheme) {
        OnHandDarkColorScheme
    } else {
        OnHandLightColorScheme
    }

    CompositionLocalProvider(
        LocalColorScheme provides colorScheme,
        LocalTypography provides OnHandTypography,
        LocalShape provides OnHandShapes,
        LocalSize provides OnHandSizes,
        content = content
    )
}

object AppTheme {
    val colorScheme
        @Composable get() = LocalColorScheme.current

    val typography
        @Composable get() = LocalTypography.current

    val shapes
        @Composable get() = LocalShape.current

    val sizes
        @Composable get() = LocalSize.current
}


private val OnHandLightColorScheme = lightColorScheme(
    primary = SkyBlue600,
    onPrimary = White,
    primaryContainer = SkyBlue100,
    onPrimaryContainer = SkyBlue900,
    secondary = SkyBlue500,
    onSecondary = White,
    secondaryContainer = SkyBlue200,
    onSecondaryContainer = SkyBlue800,
    tertiary = Green600,
    onTertiary = White,
    tertiaryContainer = Green100,
    onTertiaryContainer = Green900,
    error = Red600,
    errorContainer = Red100,
    onError = White,
    onErrorContainer = Red900,
    background = SkyBlue50,
    onBackground = Slate900,
    surface = White,
    onSurface = Gray900,
    surfaceVariant = SkyBlue100,
    onSurfaceVariant = Gray700,
    outline = SkyBlue300,
)

private val OnHandDarkColorScheme = darkColorScheme(
    primary = SkyBlue300,
    onPrimary = SkyBlue900,
    primaryContainer = SkyBlue700,
    onPrimaryContainer = SkyBlue100,
    secondary = SkyBlue400,
    onSecondary = SkyBlue800,
    secondaryContainer = SkyBlue600,
    onSecondaryContainer = SkyBlue200,
    tertiary = Green400,
    onTertiary = Green900,
    tertiaryContainer = Green600,
    onTertiaryContainer = Green100,
    error = Red400,
    errorContainer = Red600,
    onError = Red900,
    onErrorContainer = Red100,
    background = Slate900,
    onBackground = Slate100,
    surface = Slate800,
    onSurface = Slate200,
    surfaceVariant = Slate700,
    onSurfaceVariant = Slate300,
    outline = Slate600,
)

val LocalColorScheme = staticCompositionLocalOf(defaultFactory = {
    OnHandDarkColorScheme
})

val LocalTypography = staticCompositionLocalOf(defaultFactory = {
    OnHandTypography
})

val LocalShape = staticCompositionLocalOf(defaultFactory = {
    OnHandShapes
})

val LocalSize = staticCompositionLocalOf(defaultFactory = {
    OnHandSizes
})

// TODO: Revisit - unsure how this should be provided now
object PantryColorScheme {
    val primaryGradient: List<Color> = listOf(
        SkyBlue600,
        SkyBlue500,
        SkyBlue400
    )

    val freshGradient: List<Color> = listOf(
        Green600,
        Green500,
        Green400
    )

    val backgroundGradient: List<Color> = listOf(
        SkyBlue50,
        SkyBlue100,
        White
    )

    val inPantry: Color = Green600
    val inCart: Color = SkyBlue600
    val none: Color = Gray500

    val fresh: Color = Green500
    val expiringSoon: Color = Orange500
    val expired: Color = Red500

    val dairy: Color = Amber50
    val meat: Color = Rose100
    val produce: Color = Emerald100
    val pantryStaples: Color = Violet50
    val frozen: Color = LightCyan
    val snacks: Color = Violet100
    val bakery: Color = Yellow200
    val beverages: Color = SkyBlue100
    val condiments: Color = Rose50
    val spices: Color = Amber500

    val warning: Color = Amber500
    val success: Color = Emerald500
    val info: Color = Blue500
}