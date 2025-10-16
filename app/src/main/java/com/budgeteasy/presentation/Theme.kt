package com.budgeteasy.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    secondary = SecondaryGrey,
    tertiary = PrimaryGreenLight,
    background = BackgroundWhite,
    surface = SurfaceWhite,
    error = ErrorRed
)

@Composable
fun BudgetEasyTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = BudgetEasyTypography,
        content = content
    )
}