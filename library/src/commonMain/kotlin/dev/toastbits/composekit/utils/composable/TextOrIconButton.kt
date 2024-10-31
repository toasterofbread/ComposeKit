package dev.toastbits.composekit.utils.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import dev.toastbits.composekit.utils.common.copy
import dev.toastbits.composekit.utils.common.thenIf
import dev.toastbits.composekit.utils.common.toFloat

@Composable
fun TextOrIconButton(
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    onAltClick: (() -> Unit)? = null,
    onAlt2Click: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    var show_text: Boolean by remember { mutableStateOf(false) }
    val icon_alpha: Float by animateFloatAsState((!show_text).toFloat())

    BoxWithConstraints(modifier, contentAlignment = Alignment.Center) {
        MeasureUnconstrainedView({
            Text(
                text,
                Modifier.width(maxWidth).padding(ButtonDefaults.ContentPadding),
                softWrap = false,
                onTextLayout = { result ->
                    show_text = !result.didOverflowWidth
                }
            )
        }) {}

        PlatformClickableButton(
            onClick = onClick,
            onAltClick = onAltClick,
            onAlt2Click = onAlt2Click,
            colors = colors,
            elevation = elevation,
            border = border,
            modifier = Modifier.requiredSizeIn(minWidth = 40.dp, minHeight = 40.dp),
            contentPadding = ButtonDefaults.ContentPadding.thenIf(!show_text) { copy(start = 0.dp, end = 0.dp) }
        ) {
            Box(contentAlignment = Alignment.Center) {
                this@PlatformClickableButton.AnimatedVisibility(show_text) {
                    Text(text, softWrap = false)
                }

                Icon(icon, null, Modifier.graphicsLayer { alpha = icon_alpha })
            }
        }
    }
}
