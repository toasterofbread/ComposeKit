package dev.toastbits.composekit.settings.ui

import kotlinx.serialization.Serializable

@Serializable
data class NamedTheme(val name: String, val theme: ThemeValuesData)
