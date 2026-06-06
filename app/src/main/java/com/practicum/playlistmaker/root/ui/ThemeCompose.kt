package com.practicum.playlistmaker.root.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Black,
    secondary = White,
    tertiary = White
)

private val LightColorScheme = lightColorScheme(
    primary = White,
    secondary = Black,
    tertiary = Gray
)

private val LightCustomColors = CustomColors(
    buttonColors = ButtonColors(
        primaryColors = ButtonStateColors(
            backgroundColor = PrimaryButtonBackgroundLight,
            textColor = PrimaryButtonTextLight
        )
    )
)

private val DarkCustomColors = CustomColors(
    buttonColors = ButtonColors(
        primaryColors = ButtonStateColors(
            backgroundColor = PrimaryButtonBackgroundDark,
            textColor = PrimaryButtonTextDark
        )
    )
)

data class CustomColors(
    val buttonColors: ButtonColors
)

data class ButtonColors(
    val primaryColors: ButtonStateColors
)

data class ButtonStateColors(
    val backgroundColor: Color,
    val textColor: Color
)

val LocalCustomColors = staticCompositionLocalOf<CustomColors> {
    error("No CustomColors provided")
}

@Composable
fun ThemeComposeUsing(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val customColors = if (darkTheme) DarkCustomColors else LightCustomColors

    CompositionLocalProvider(
        LocalCustomColors provides customColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}