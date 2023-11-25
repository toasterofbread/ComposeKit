package com.toasterofbread.composekit.platform.composable

import androidx.compose.foundation.Indication
import androidx.compose.ui.Modifier

expect fun Modifier.platformClickable(
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    onAltClick: (() -> Unit)? = null,
    onAlt2Click: (() -> Unit)? = null,
    indication: Indication? = null
): Modifier
