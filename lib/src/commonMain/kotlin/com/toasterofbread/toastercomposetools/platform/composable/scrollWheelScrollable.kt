package com.toasterofbread.toastercomposetools.platform.composable

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.ui.Modifier

expect fun Modifier.scrollWheelScrollable(
    state: ScrollableState,
    reverse_direction: Boolean = false
): Modifier
