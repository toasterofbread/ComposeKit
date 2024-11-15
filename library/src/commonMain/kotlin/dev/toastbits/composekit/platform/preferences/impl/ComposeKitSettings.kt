package dev.toastbits.composekit.platform.preferences.impl

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import dev.toastbits.composekit.platform.preferences.impl.group.ComposeKitInterfacePreferencesGroup

val LocalComposeKitSettings: ProvidableCompositionLocal<ComposeKitSettings?> =
    staticCompositionLocalOf { null }

interface ComposeKitSettings {
    val Interface: ComposeKitInterfacePreferencesGroup
}
