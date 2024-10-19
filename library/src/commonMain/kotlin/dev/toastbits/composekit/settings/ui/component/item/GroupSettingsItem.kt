package dev.toastbits.composekit.settings.ui.component.item

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dev.toastbits.composekit.settings.ui.vibrant_accent
import dev.toastbits.composekit.platform.PreferencesProperty
import dev.toastbits.composekit.platform.composable.theme.LocalApplicationTheme
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

class GroupSettingsItem(var title: StringResource?): SettingsItem() {
    override fun getProperties(): List<PreferencesProperty<*>> = emptyList()
    override suspend fun resetValues() {}

    @Composable
    override fun Item(
        modifier: Modifier
    ) {
        title?.also {
            Text(
                stringResource(it),
                modifier,
                color = LocalApplicationTheme.current.vibrant_accent,
                fontSize = 20.sp,
                fontWeight = FontWeight.Light
            )
        }
    }
}
