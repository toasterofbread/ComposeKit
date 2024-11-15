package dev.toastbits.composekit.settings.ui.component.item

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.toastbits.composekit.navigation.screen.Screen
import dev.toastbits.composekit.navigation.compositionlocal.LocalNavigator
import dev.toastbits.composekit.navigation.navigator.Navigator
import dev.toastbits.composekit.settings.ui.vibrant_accent
import dev.toastbits.composekit.settings.ui.on_accent
import dev.toastbits.composekit.platform.preferences.PreferencesProperty
import dev.toastbits.composekit.platform.composable.theme.LocalApplicationTheme
import dev.toastbits.composekit.settings.ui.ThemeValues

class SubpageSettingsItem(
    val title: String,
    val subtitle: String?,
    val target_page: Screen
): SettingsItem() {
    override fun getProperties(): List<PreferencesProperty<*>> = emptyList()
    override suspend fun resetValues() {}

    @Composable
    override fun Item(
        modifier: Modifier
    ) {
        val theme: ThemeValues = LocalApplicationTheme.current
        val navigator: Navigator = LocalNavigator.current

        Button(
            { navigator.pushScreen(target_page) },
            modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = theme.vibrant_accent,
                contentColor = theme.on_accent
            )
        ) {
            Column(Modifier.weight(1f)) {
                Text(title, color = theme.on_accent)
                ItemText(subtitle, theme)
            }
        }
    }
}
