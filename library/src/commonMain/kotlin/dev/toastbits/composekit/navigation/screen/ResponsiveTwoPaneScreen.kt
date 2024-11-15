package dev.toastbits.composekit.navigation.screen

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.toastbits.composekit.navigation.navigator.Navigator
import dev.toastbits.composekit.utils.composable.pane.ResizableTwoPaneRow
import dev.toastbits.composekit.utils.composable.pane.model.ResizablePaneContainerParams
import dev.toastbits.composekit.utils.composable.pane.model.ResizablePaneContainerParamsData
import dev.toastbits.composekit.utils.composable.pane.model.ResizablePaneContainerParamsProvider

abstract class ResponsiveTwoPaneScreen<T: Any>(
    private val initialStartPaneRatio: Float = 0.5f,
    private val paneParams: ResizablePaneContainerParamsProvider = ResizablePaneContainerParamsData()
): Screen {
    @Composable
    protected abstract fun getCurrentData(): T?

    protected open fun BoxWithConstraintsScope.shouldDisplayBothPanes(): Boolean {
        val availableGroupsWidth: Dp = maxWidth * (1f - initialStartPaneRatio)
        return availableGroupsWidth >= DEFAULT_PRIMARY_PANE_MIN_WIDTH
    }

    @Composable
    final override fun Content(navigator: Navigator, modifier: Modifier, contentPadding: PaddingValues) {
        val currentData: T? = getCurrentData()

        BoxWithConstraints(modifier) {
            Crossfade(shouldDisplayBothPanes(), Modifier.fillMaxSize()) { displayBothPanes ->
                if (displayBothPanes) {
                    ResizableTwoPaneRow(
                        startPaneContent = {
                            PrimaryPane(currentData, it, Modifier)
                        },
                        endPaneContent = {
                            SecondaryPane(currentData, it, Modifier)
                        },
                        showEndPane = currentData != null,
                        initialStartPaneRatio = initialStartPaneRatio,
                        contentPadding = contentPadding,
                        modifier = Modifier.fillMaxSize(),
                        params = paneParams()
                    )
                }
                else {
                    Crossfade(currentData, Modifier.fillMaxSize()) { data ->
                        if (data == null) {
                            PrimaryPane(null, contentPadding, Modifier.fillMaxSize())
                        }
                        else {
                            SecondaryPane(data, contentPadding, Modifier.fillMaxSize())
                        }
                    }
                }
            }
        }
    }

    @Composable
    protected abstract fun PrimaryPane(data: T?, contentPadding: PaddingValues, modifier: Modifier)

    @Composable
    protected abstract fun SecondaryPane(data: T?, contentPadding: PaddingValues, modifier: Modifier)

    companion object {
        val DEFAULT_PRIMARY_PANE_MIN_WIDTH: Dp = 300.dp
    }
}
