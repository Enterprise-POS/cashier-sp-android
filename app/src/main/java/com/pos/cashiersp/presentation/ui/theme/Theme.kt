package com.pos.cashiersp.presentation.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFB77D),
    onPrimary = Color(0xFF552000),
    primaryContainer = Color(0xFF783500),
    onPrimaryContainer = Color(0xFFFFDBC2),
    secondary = Color(0xFFA9C7E4),
    onSecondary = Color(0xFF102C45),
    secondaryContainer = Color(0xFF24435D),
    onSecondaryContainer = Color(0xFFD4E3F3),
    tertiary = Color(0xFF85D6C7),
    onTertiary = Color(0xFF00382F),
    tertiaryContainer = Color(0xFF005047),
    onTertiaryContainer = Color(0xFFA5F2E2),
    background = Color(0xFF111418),
    onBackground = Color(0xFFE1E2E6),
    surface = Color(0xFF191C20),
    onSurface = Color(0xFFE1E2E6),
    surfaceVariant = Color(0xFF42474D),
    onSurfaceVariant = Color(0xFFC1C7CE),
    outline = Color(0xFF8B929A),
    outlineVariant = Color(0xFF42474D),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFA64B00),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFDBC2),
    onPrimaryContainer = Color(0xFF351000),
    secondary = Color(0xFF092C4C),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD4E3F3),
    onSecondaryContainer = Color(0xFF0A2238),
    tertiary = Color(0xFF006B5F),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFA5F2E2),
    onTertiaryContainer = Color(0xFF00201A),
    background = Color(0xFFF8F9FC),
    onBackground = Color(0xFF191C20),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF191C20),
    surfaceVariant = Color(0xFFE7E9ED),
    onSurfaceVariant = Color(0xFF41474D),
    outline = Color(0xFF6D747C),
    outlineVariant = Color(0xFFC1C7CE),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
)

private val AppShapes = Shapes()

@Composable
fun CashierSPTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val semanticColors = if (darkTheme) DarkPosSemanticColors else LightPosSemanticColors

    CompositionLocalProvider(LocalPosSemanticColors provides semanticColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = AppShapes,
            content = content,
        )
    }
}
