package com.toasterofbread.composekit.platform.composable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Indication
import androidx.compose.foundation.PointerMatcher
import androidx.compose.foundation.onClick
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerButton
import com.toasterofbread.composekit.utils.common.thenWith

@OptIn(ExperimentalFoundationApi::class)
actual fun Modifier.platformClickable(enabled: Boolean, onClick: (() -> Unit)?, onAltClick: (() -> Unit)?, indication: Indication?): Modifier =
    this
        .thenWith(onClick) {
            onClick(onClick = it)
        }
        .thenWith(onAltClick) {
            onClick(
                matcher = PointerMatcher.mouse(PointerButton.Secondary),
                onClick = it
            )
        }
