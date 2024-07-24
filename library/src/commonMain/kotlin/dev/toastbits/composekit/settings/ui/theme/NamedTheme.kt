package dev.toastbits.composekit.settings.ui.theme

import kotlinx.serialization.Serializable

@Serializable
data class NamedTheme(val name: String, val theme: ThemeValuesData)
