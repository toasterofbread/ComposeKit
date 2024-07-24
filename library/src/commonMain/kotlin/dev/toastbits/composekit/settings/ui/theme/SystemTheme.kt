package dev.toastbits.composekit.settings.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import dev.toastbits.composekit.platform.PlatformContext
import dev.toastbits.composekit.platform.getEnv
import org.jetbrains.compose.resources.getString

@Composable
fun rememberSystemTheme(name: String, context: PlatformContext): NamedTheme {
    val dark_mode: Boolean = isSystemInDarkTheme()
    val environment_theme: NamedTheme? = remember { getCurrentEnvironmentTheme() }

    return remember(environment_theme ?: dark_mode) {
        if (environment_theme != null) {
            return@remember environment_theme.copy(name = "$name - ${environment_theme.name}")
        }

        return@remember NamedTheme(
            name,
            ThemeValuesData.fromColourScheme(
                if (dark_mode) context.getDarkColorScheme()
                else context.getLightColorScheme()
            )
        )
    }
}

fun getCurrentEnvironmentTheme(getEnvironment: (String) -> String? = ::getEnv): NamedTheme? {
    val gtk_theme: String? = getEnvironment("GTK_THEME")?.lowercase()

    if (gtk_theme?.startsWith("catppuccin-") == true) {
        val split: List<String> = gtk_theme.substring(11).split("-", limit = 4)
        if (split.size >= 3) {
            val flavour: String = split[0]
            val accent: String = split[2]

            return getCatppuccinTheme(flavour, accent)
        }
    }

    return null
}
