package dev.toastbits.composekit.utils.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize

@Composable
fun MeasureUnconstrainedView(
    view_to_measure: @Composable () -> Unit,
    view_constraints: Constraints = Constraints(),
    content: @Composable (IntSize) -> Unit
) {
    SubcomposeLayout { constraints ->
        val measurement: Placeable? = subcompose("viewToMeasure", view_to_measure).firstOrNull()?.measure(view_constraints)

        val content_placeable: Placeable? = subcompose("content") {
            content(
                measurement?.let {
                    IntSize(it.width, it.height)
                } ?: IntSize.Zero
            )
        }.firstOrNull()?.measure(constraints)

        layout(content_placeable?.width ?: 0, content_placeable?.height ?: 0) {
            content_placeable?.place(0, 0)
        }
    }
}
