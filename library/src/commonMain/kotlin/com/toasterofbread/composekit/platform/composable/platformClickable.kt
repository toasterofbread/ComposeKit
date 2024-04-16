package dev.toastbits.composekit.platform.composable

import androidx.compose.foundation.Indication
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset

expect fun Modifier.platformClickable(
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    onAltClick: (() -> Unit)? = null,
    onAlt2Click: (() -> Unit)? = null,
    indication: Indication? = null
): Modifier

expect fun Modifier.platformClickableWithOffset(
    enabled: Boolean = true,
    onClick: ((Offset) -> Unit)? = null,
    onAltClick: ((Offset) -> Unit)? = null,
    onAlt2Click: ((Offset) -> Unit)? = null,
    indication: Indication? = null
): Modifier
