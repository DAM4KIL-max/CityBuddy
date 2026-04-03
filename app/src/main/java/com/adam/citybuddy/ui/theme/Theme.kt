package com.adam.citybuddy.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// DO NOT define DeepPurple/LightPurple here again!
// They are automatically pulled from Color.kt because they are in the same package.

private val LightColorScheme = lightColorScheme(
    primary = ButtonPurple,
    secondary = DeepPurple,
    tertiary = LightPurple,
    background = SoftBg,
    surface = SoftBg,
    onPrimary = Color.White
)

@Composable
fun CityBuddyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}