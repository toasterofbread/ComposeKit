package com.toasterofbread.composekit.platform.composable

import androidx.compose.foundation.Indication
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.isPrimary
import androidx.compose.ui.input.pointer.isPrimaryPressed
import androidx.compose.ui.input.pointer.isSecondary
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.isTertiary
import androidx.compose.ui.input.pointer.isTertiaryPressed
import com.toasterofbread.composekit.utils.common.thenWith

@OptIn(ExperimentalComposeUiApi::class)
actual fun Modifier.platformClickable(
    enabled: Boolean,
    onClick: ((Offset) -> Unit)?,
    onAltClick: ((Offset) -> Unit)?,
    onAlt2Click: ((Offset) -> Unit)?,
    indication: Indication?
): Modifier =
    if (!enabled) this
    else this
        .thenWith(onClick) {
            detectReleaseEvents(it) { event ->
                if (event.button.isPrimary) {
                    it(event.changes.first().position)
                    event.consume()
                }
            }
        }
        .thenWith(onAltClick) {
            detectReleaseEvents(it) { event ->
                if (event.button.isSecondary) {
                    it(event.changes.first().position)
                    event.consume()
                }
            }
        }
        .thenWith(onAlt2Click) {
            detectReleaseEvents(it) { event ->
                if (event.button.isTertiary) {
                    it(event.changes.first().position)
                    event.consume()
                }
            }
        }
