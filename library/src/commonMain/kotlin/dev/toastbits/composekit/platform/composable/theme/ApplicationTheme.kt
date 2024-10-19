package dev.toastbits.composekit.platform.composable.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import dev.toastbits.composekit.platform.LocalContext
import dev.toastbits.composekit.platform.Platform
import dev.toastbits.composekit.platform.PlatformContext
import dev.toastbits.composekit.settings.ui.ThemeValues
import dev.toastbits.composekit.settings.ui.ThemeValuesData
import dev.toastbits.composekit.settings.ui.on_accent
import dev.toastbits.composekit.settings.ui.vibrant_accent
import dev.toastbits.composekit.utils.common.amplifyPercent
import dev.toastbits.composekit.utils.common.blendWith
import dev.toastbits.composekit.utils.common.contrastAgainst
import dev.toastbits.composekit.utils.common.getContrasted

val LocalApplicationTheme: ProvidableCompositionLocal<ThemeValues> =
    staticCompositionLocalOf { ThemeValuesData.fromColourScheme(darkColorScheme()) }

@Composable
fun ThemeValues.ApplicationTheme(
    context: PlatformContext,
    fontFamily: FontFamily = FontFamily.Default,
    content: @Composable () -> Unit
) {
    val darkTheme: Boolean = isSystemInDarkTheme()

    val primaryContainer: Color = accent.blendWith(background, our_ratio = 0.2f).contrastAgainst(background, by = 0.1f)
    val secondaryContainer: Color = accent.blendWith(background, our_ratio = 0.6f).contrastAgainst(background, by = 0.1f)
    val tertiaryContainer: Color = accent.blendWith(background, our_ratio = 0.8f).contrastAgainst(background, by = 0.1f)

    val colourScheme: ColorScheme =
        (if (darkTheme) context.getDarkColorScheme() else context.getLightColorScheme())
            .copy(
                primary = accent,
                onPrimary = on_accent,
                inversePrimary = vibrant_accent,
                secondary = on_background,
                onSecondary = background,
                tertiary = vibrant_accent,
                onTertiary = vibrant_accent.getContrasted(),

                primaryContainer = primaryContainer,
                onPrimaryContainer = primaryContainer.getContrasted(),
                secondaryContainer = secondaryContainer,
                onSecondaryContainer = secondaryContainer.getContrasted(),
                tertiaryContainer = tertiaryContainer,
                onTertiaryContainer = tertiaryContainer.getContrasted(),

                background = background,
                onBackground = on_background,

                surface = card.amplifyPercent(0.1f),
                onSurface = card.getContrasted(),
                surfaceVariant = card.amplifyPercent(0.2f),
                onSurfaceVariant = card.getContrasted(),
                surfaceTint = accent.blendWith(background, 0.75f),
                inverseSurface = vibrant_accent,
                inverseOnSurface = vibrant_accent.getContrasted(),

                outline = on_background,
                outlineVariant = vibrant_accent,

//                error = Color.Unspecified,
//                onError = Color.Unspecified,
//                errorContainer = Color.Unspecified,
//                onErrorContainer = Color.Unspecified,
//                scrim = Color.Unspecified,
                surfaceBright = card.amplifyPercent(0.1f),
                surfaceDim = card,
                surfaceContainer = card.amplifyPercent(0.1f),
                surfaceContainerHigh = card.amplifyPercent(0.025f),
                surfaceContainerHighest = card,
                surfaceContainerLow = card.amplifyPercent(0.2f),
                surfaceContainerLowest = card.amplifyPercent(0.3f),
            )

    val defaultTypography: Typography = MaterialTheme.typography
    val typography: Typography = remember(defaultTypography, fontFamily) {
        with(defaultTypography) {
            copy(
                displayLarge = displayLarge.copy(fontFamily = fontFamily),
                displayMedium = displayMedium.copy(fontFamily = fontFamily),
                displaySmall = displaySmall.copy(fontFamily = fontFamily),
                headlineLarge = headlineLarge.copy(fontFamily = fontFamily),
                headlineMedium = headlineMedium.copy(fontFamily = fontFamily),
                headlineSmall = headlineSmall.copy(fontFamily = fontFamily),
                titleLarge = titleLarge.copy(fontFamily = fontFamily),
                titleMedium = titleMedium.copy(fontFamily = fontFamily),
                titleSmall = titleSmall.copy(fontFamily = fontFamily),
                bodyLarge = bodyLarge.copy(fontFamily = fontFamily),
                bodyMedium = bodyMedium.copy(fontFamily = fontFamily),
                bodySmall = bodySmall.copy(fontFamily = fontFamily),
                labelLarge = labelLarge.copy(fontFamily = fontFamily),
                labelMedium = labelMedium.copy(fontFamily = fontFamily),
                labelSmall = labelSmall.copy(fontFamily = fontFamily)
            )
        }
    }

    val default_shapes: Shapes = MaterialTheme.shapes
    val shapes: Shapes = remember(default_shapes) {
        when (Platform.current) {
            Platform.DESKTOP ->
                default_shapes.copy(
                    extraSmall = RoundedCornerShape(2.dp),
                    small = RoundedCornerShape(4.dp),
                    medium = RoundedCornerShape(6.dp),
                    large = RoundedCornerShape(8.dp),
                    extraLarge = RoundedCornerShape(10.dp),
                )
            else -> default_shapes
        }
    }

    MaterialTheme(
        colorScheme = colourScheme,
        typography = typography,
        shapes = shapes
    ) {
        Box(Modifier.background(background)) {
            CompositionLocalProvider(
                LocalApplicationTheme provides this@ApplicationTheme,
                LocalContentColor provides on_background,
                LocalContext provides context
            ) {
                PlatformTheme {
                    content()
                }
            }
        }
    }
}

@Composable
internal expect fun ThemeValues.PlatformTheme(content: @Composable () -> Unit)
