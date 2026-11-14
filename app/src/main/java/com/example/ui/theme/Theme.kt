package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

private val SpaceDarkColorScheme = darkColorScheme(
    primary = ElectricBlue,
    onPrimary = SpaceBlack,
    primaryContainer = MissionBlue,
    onPrimaryContainer = PureWhite,
    secondary = CosmicPurple,
    onSecondary = PureWhite,
    secondaryContainer = ElevatedNavy,
    onSecondaryContainer = SoftIceWhite,
    tertiary = NebulaViolet,
    onTertiary = PureWhite,
    background = SpaceBlack,
    onBackground = PureWhite,
    surface = SpaceNavy,
    onSurface = PureWhite,
    surfaceVariant = CardNavy,
    onSurfaceVariant = SoftIceWhite,
    outline = BorderNavy,
    error = AlertCoral,
    onError = PureWhite
)

private val SpaceShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun JuniorAstronautTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = SpaceDarkColorScheme,
        typography = AppTypography,
        shapes = SpaceShapes,
        content = content
    )
}
