package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.model.AppThemeMode

private val AmoledColorScheme = darkColorScheme(
    primary = SonoraGold,
    onPrimary = Color.Black,
    primaryContainer = SonoraGoldDark,
    onPrimaryContainer = Color.White,
    secondary = SonoraCyan,
    onSecondary = Color.Black,
    background = AmoledBackground,
    onBackground = Color(0xFFF1F5F9),
    surface = AmoledSurface,
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = AmoledSurfaceVariant,
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = AmoledBorder
)

private val MidnightColorScheme = darkColorScheme(
    primary = SonoraGold,
    onPrimary = Color.Black,
    primaryContainer = SonoraGoldDark,
    onPrimaryContainer = Color.White,
    secondary = SonoraCyan,
    onSecondary = Color.Black,
    background = MidnightBackground,
    onBackground = Color(0xFFF8FAFC),
    surface = MidnightSurface,
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = MidnightSurfaceVariant,
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = MidnightBorder
)

private val StudioLightColorScheme = lightColorScheme(
    primary = SonoraGoldDark,
    onPrimary = Color.White,
    primaryContainer = SonoraGoldLight,
    onPrimaryContainer = Color(0xFF78350F),
    secondary = SonoraCyanDark,
    onSecondary = Color.White,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder
)

@Composable
fun SonoraTheme(
    themeMode: AppThemeMode = AppThemeMode.MIDNIGHT_DARK,
    content: @Composable () -> Unit
) {
    val isSystemDark = isSystemInDarkTheme()
    val colorScheme = when (themeMode) {
        AppThemeMode.AMOLED_DARK -> AmoledColorScheme
        AppThemeMode.MIDNIGHT_DARK -> MidnightColorScheme
        AppThemeMode.STUDIO_LIGHT -> StudioLightColorScheme
        AppThemeMode.SYSTEM -> if (isSystemDark) MidnightColorScheme else StudioLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
