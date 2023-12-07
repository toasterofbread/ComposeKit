package com.toasterofbread.composekit.utils.composable

import androidx.compose.foundation.Indication
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.material.ContentAlpha
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.minimumInteractiveComponentSize
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.toasterofbread.composekit.platform.composable.platformClickable
import com.toasterofbread.composekit.utils.common.thenIf

@Composable
fun PlatformClickableIconButton(
    onClick: ((Offset) -> Unit)? = null,
    onAltClick: ((Offset) -> Unit)? = null,
    onAlt2Click: ((Offset) -> Unit)? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    indication: Indication? = rememberRipple(bounded = false, radius = 24.dp),
    apply_minimum_size: Boolean = true,
    content: @Composable () -> Unit
) {
    val interaction_source: MutableInteractionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .hoverable(interaction_source)
            .indication(interaction_source, indication)
            .platformClickable(
                onClick = onClick,
                onAltClick = onAltClick,
                onAlt2Click = onAlt2Click,
                enabled = enabled,
                indication = indication
            )
            .thenIf(apply_minimum_size) {
                minimumInteractiveComponentSize()
            },
        contentAlignment = Alignment.Center
    ) {
        val contentAlpha = if (enabled) LocalContentAlpha.current else ContentAlpha.disabled
        CompositionLocalProvider(LocalContentAlpha provides contentAlpha, content = content)
    }
}
