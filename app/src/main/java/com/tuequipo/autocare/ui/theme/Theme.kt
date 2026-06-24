package com.tuequipo.autocare.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = AutoCarePrimary,
    onPrimary = TextPrimary,
    primaryContainer = Color(0xFF2E1767),
    onPrimaryContainer = Color(0xFFE9DDFF),
    secondary = AutoCareSecondary,
    onSecondary = Color(0xFF062B34),
    secondaryContainer = Color(0xFF103B49),
    onSecondaryContainer = Color(0xFFC8F7FF),
    tertiary = AccentPink,
    onTertiary = TextPrimary,
    background = AutoCareBackground,
    onBackground = TextPrimary,
    surface = AutoCareSurface,
    onSurface = TextPrimary,
    surfaceVariant = AutoCareSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = AutoCareBorder,
    error = StatusVencido,
    onError = Color(0xFF500014),
    errorContainer = Color(0xFF5B172B),
    onErrorContainer = Color(0xFFFFD9E0)
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    primaryContainer = Color(0xFFD8EAF9),
    secondaryContainer = Color(0xFFDCE5ED),
    surfaceVariant = Color(0xFFE8EEF3),
    background = Color(0xFFF8FAFC)
)

private val AutoCareShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun AutoCareTheme(
    darkTheme: Boolean = true,
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = AutoCareShapes,
        content = content
    )
}
