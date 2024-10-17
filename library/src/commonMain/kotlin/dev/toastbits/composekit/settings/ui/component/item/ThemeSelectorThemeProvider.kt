package dev.toastbits.composekit.settings.ui.component.item

import dev.toastbits.composekit.settings.ui.ThemeValues
import dev.toastbits.composekit.settings.ui.NamedTheme

interface ThemeSelectorThemeProvider {
    fun getThemeCount(): Int
    fun getTheme(index: Int): NamedTheme?
    fun isThemeEditable(index: Int): Boolean
    fun onThemeEdited(index: Int, theme: ThemeValues, theme_name: String)
    suspend fun createTheme(index: Int): Int
    suspend fun removeTheme(index: Int)
}
