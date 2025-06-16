package com.example.whiteboard.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class CustomColors(
    val buttonGray: Color,
    val canvasColor: Color,
    val bottomBarColor: Color,
    val colorPickerBorder: Color,
    val bottomBarExpansionBar: Color,
    val scrollButtonIcon: Color,
    val defaultStrokeColor: Color,
    val text: Color,
    val isDark: Boolean
)

private val DarkCustomColors = CustomColors(
    buttonGray = buttonGray,
    canvasColor = canvasColor,
    bottomBarColor = bottomBarColor,
    colorPickerBorder = colorPickerBorder,
    bottomBarExpansionBar = Color.White,
    scrollButtonIcon = scrollButtonIcon,
    defaultStrokeColor = defaultStrokeColor,
    text = Color.White,

    isDark = true
)

private val LightCustomColors = CustomColors(
    buttonGray = buttonGrayLight,
    canvasColor = canvasColorLight,
    bottomBarColor = bottomBarColorLight,
    colorPickerBorder = colorPickerBorderLight,
    bottomBarExpansionBar = Color.Gray,
    scrollButtonIcon = scrollButtonIconLight,
    defaultStrokeColor = defaultStrokeColorLight,
    text = Color.Gray,
    isDark = false
)

val LocalCustomColors = staticCompositionLocalOf { LightCustomColors }

@Composable
fun WhiteboardTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    val customColors = if (darkTheme) DarkCustomColors else LightCustomColors

    val materialColors = if (darkTheme) {
        darkColorScheme()
    } else {
        lightColorScheme()
    }

    CompositionLocalProvider(LocalCustomColors provides customColors) {
        MaterialTheme(
            colorScheme = materialColors,
            typography = Typography,
            content = content
        )
    }
}
