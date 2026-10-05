package com.pemmob.schedulyy.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Immutable
data class ExtendedColors(
    val ink: Color,
    val textSecondary: Color,
    val tintCobalt: Color,
    val btnPeriwinkle: Color,
    val danger: Color,
    val pastelPink: Color,
    val pastelButter: Color,
    val pastelMint: Color,
    val pastelSky: Color,
    val pastelLilac: Color,
    val pastelLime: Color,
    val ipodBevel: Color,
    val ipodWheel: Color
)

val LocalExtendedColors = staticCompositionLocalOf {
    ExtendedColors(
        ink = InkLight,
        textSecondary = TextSecondaryLight,
        tintCobalt = TintCobaltLight,
        btnPeriwinkle = BtnPeriwinkleLight,
        danger = DangerLight,
        pastelPink = PastelPinkLight,
        pastelButter = PastelButterLight,
        pastelMint = PastelMintLight,
        pastelSky = PastelSkyLight,
        pastelLilac = PastelLilacLight,
        pastelLime = PastelLimeLight,
        ipodBevel = IpodBevelLight,
        ipodWheel = IpodWheelLight
    )
}

private val LightColorScheme = lightColorScheme(
    primary = TintCobaltLight,
    onPrimary = Color.White,
    primaryContainer = BtnPeriwinkleLight,
    onPrimaryContainer = InkLight,
    secondary = InkLight,
    onSecondary = Color.White,
    background = BgGroupedLight,
    onBackground = InkLight,
    surface = SurfaceLight,
    onSurface = InkLight,
    surfaceVariant = SurfaceRaisedLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = InkLight,
    outlineVariant = Color(0xFFD1D1D6),
    error = DangerLight,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = TintCobaltDark,
    onPrimary = InkDark,
    primaryContainer = BtnPeriwinkleDark,
    onPrimaryContainer = InkDark,
    secondary = InkDark,
    onSecondary = Color.Black,
    background = BgGroupedDark,
    onBackground = InkDark,
    surface = SurfaceDark,
    onSurface = InkDark,
    surfaceVariant = SurfaceRaisedDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = InkDark,
    outlineVariant = Color(0xFF38383A),
    error = DangerDark,
    onError = Color.Black
)

@Composable
fun SchedulyyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val extendedColors = if (darkTheme) {
        ExtendedColors(
            ink = InkDark,
            textSecondary = TextSecondaryDark,
            tintCobalt = TintCobaltDark,
            btnPeriwinkle = BtnPeriwinkleDark,
            danger = DangerDark,
            pastelPink = PastelPinkDark,
            pastelButter = PastelButterDark,
            pastelMint = PastelMintDark,
            pastelSky = PastelSkyDark,
            pastelLilac = PastelLilacDark,
            pastelLime = PastelLimeDark,
            ipodBevel = IpodBevelDark,
            ipodWheel = IpodWheelDark
        )
    } else {
        ExtendedColors(
            ink = InkLight,
            textSecondary = TextSecondaryLight,
            tintCobalt = TintCobaltLight,
            btnPeriwinkle = BtnPeriwinkleLight,
            danger = DangerLight,
            pastelPink = PastelPinkLight,
            pastelButter = PastelButterLight,
            pastelMint = PastelMintLight,
            pastelSky = PastelSkyLight,
            pastelLilac = PastelLilacLight,
            pastelLime = PastelLimeLight,
            ipodBevel = IpodBevelLight,
            ipodWheel = IpodWheelLight
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = !darkTheme
                    isAppearanceLightNavigationBars = !darkTheme
                }
            }
        }
    }

    CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}

object SchedulyyTheme {
    val extendedColors: ExtendedColors
        @Composable
        get() = LocalExtendedColors.current
}
