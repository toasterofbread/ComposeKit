package dev.toastbits.composekit.settings.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import dev.toastbits.composekit.platform.composable.theme.LocalApplicationTheme
import dev.toastbits.composekit.platform.preferences.PreferencesGroup
import dev.toastbits.composekit.settings.ui.ThemeValues
import dev.toastbits.composekit.utils.common.blendWith
import dev.toastbits.composekit.utils.composable.wave.WaveLineArea

@Composable
fun PreferencesGroupPreview(
    group: PreferencesGroup,
    modifier: Modifier = Modifier,
    highlight: Boolean = false
) {
    val theme: ThemeValues = LocalApplicationTheme.current

    val shape: Shape = MaterialTheme.shapes.medium
    val colours: CardColors =
        CardDefaults.elevatedCardColors(
            containerColor = theme.accent.blendWith(theme.background, 0.03f),
            contentColor = theme.on_background
        )

    ElevatedCard(
        Modifier.clip(shape).then(modifier),
        shape = shape,
        colors = colours
    ) {
        WaveLineArea(enabled = highlight) {
            Row(
                Modifier.padding(15.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Icon(group.getIcon(), null)

                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(group.getTitle(), style = MaterialTheme.typography.titleLarge)
                    Text(group.getDescription(), style = MaterialTheme.typography.bodySmall, modifier = Modifier.alpha(0.7f))
                }
            }
        }
    }
}
