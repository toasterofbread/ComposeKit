package dev.toastbits.composekit.settings.ui.component.item

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.platform.ViewConfiguration
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.github.krottv.compose.sliders.DefaultThumb
import com.github.krottv.compose.sliders.DefaultTrack
import com.github.krottv.compose.sliders.ListenOnPressed
import com.github.krottv.compose.sliders.SliderValueHorizontal
import dev.toastbits.composekit.platform.Platform
import dev.toastbits.composekit.platform.composable.theme.LocalApplicationTheme
import dev.toastbits.composekit.platform.preferences.PreferencesProperty
import dev.toastbits.composekit.settings.ui.ThemeValues
import dev.toastbits.composekit.settings.ui.vibrant_accent
import dev.toastbits.composekit.utils.common.CustomStringResource
import dev.toastbits.composekit.utils.common.getContrasted
import dev.toastbits.composekit.utils.common.roundTo
import dev.toastbits.composekit.utils.composable.MeasureUnconstrainedView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class SliderSettingsItem(
    val state: PreferencesProperty<out Number>,
    val getErrMsgValueOutOfRange: suspend (range: ClosedFloatingPointRange<Float>) -> String,
    val errmsg_value_not_int: CustomStringResource,
    val errmsg_value_not_float: CustomStringResource,
    val min_label: CustomStringResource? = null,
    val max_label: CustomStringResource? = null,
    val steps: Int = 0,
    val range: ClosedFloatingPointRange<Float> = 0f .. 1f,
    val getValueText: ((value: Number) -> String?)? = {
        if (it is Float) it.roundTo(2).toString()
        else it.toString()
    },
    val getFieldModifier: @Composable () -> Modifier = { Modifier }
): SettingsItem() {
    private suspend fun isInt(): Boolean =
        when (val default: Number = state.getDefaultValue()) {
            is Float -> false
            is Int -> true
            else -> throw NotImplementedError(default::class.toString())
        }
    private var value_state: Float? by mutableStateOf(null)

    @Suppress("UNCHECKED_CAST")
    fun setValue(value: Float) {
        value_state = value
    }

    suspend fun saveValue() {
        if (isInt()) {
            (state as PreferencesProperty<Int>).set(getValue().roundToInt())
        }
        else {
            (state as PreferencesProperty<Float>).set(getValue())
        }
    }

    fun getValue(): Float =
        value_state ?: range.start

    private fun getTypedValue(is_int: Boolean): Number =
        if (is_int) getValue().roundToInt()
        else getValue()

    override suspend fun resetValues() {
        state.reset()
        value_state = state.get().toFloat()
    }

    override fun getProperties(): List<PreferencesProperty<*>> = listOf(state)

    @Composable
    override fun Item(
        modifier: Modifier
    ) {
        val coroutine_scope: CoroutineScope = rememberCoroutineScope()
        val theme: ThemeValues = LocalApplicationTheme.current
        var show_edit_dialog: Boolean by remember { mutableStateOf(false) }
        var is_int: Boolean by remember { mutableStateOf(false) }

        LaunchedEffect(this) {
            launch {
                value_state = state.get().toFloat()
            }
            is_int = isInt()
        }

        if (show_edit_dialog) {
            var text: String by remember { mutableStateOf("") }
            var error: String? by remember { mutableStateOf(null) }

            LaunchedEffect(this) {
                text = (if (isInt()) getValue().roundToInt() else getValue()).toString()
            }

            AlertDialog(
                {
                    show_edit_dialog = false
                },
                confirmButton = {
                    FilledTonalButton(
                        {
                            coroutine_scope.launch {
                                try {
                                    setValue(if (is_int) text.toInt().toFloat() else text.toFloat())
                                    saveValue()
                                    show_edit_dialog = false
                                }
                                catch (_: NumberFormatException) {}
                            }
                        },
                        enabled = error == null
                    ) {
                        Text("Done")
                    }
                },
                dismissButton = { TextButton( { show_edit_dialog = false } ) { Text("Cancel") } },
                title = { ItemTitleText(state.getName() ?: "Edit field", theme) },
                text = {
                    OutlinedTextField(
                        value = text,
                        isError = error != null,
                        label = {
                            Crossfade(error) { error_text ->
                                if (error_text != null) {
                                    Text(error_text)
                                }
                            }
                        },
                        onValueChange = {
                            text = it

                            try {
                                val value: Float = if (is_int) text.toInt().toFloat() else text.toFloat()
                                if (!range.contains(value)) {
                                    coroutine_scope.launch {
                                        error = getErrMsgValueOutOfRange(range)
                                    }
                                    return@OutlinedTextField
                                }

                                error = null
                            }
                            catch (_: NumberFormatException) {
                                coroutine_scope.launch {
                                    error = (if (is_int) errmsg_value_not_int else errmsg_value_not_float).getString()
                                }
                            }
                        },
                        singleLine = true,
                        modifier = getFieldModifier()
                    )
                }
            )
        }

        Column(modifier.fillMaxWidth().padding(bottom = 10.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                ItemTitleText(state.getName(), theme, Modifier.fillMaxWidth().weight(1f))

                IconButton({ show_edit_dialog = true }, Modifier.size(25.dp)) {
                    Icon(Icons.Filled.Edit, null)
                }
            }

            ItemText(state.getDescription(), theme)

            Spacer(Modifier.requiredHeight(10.dp))

            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                if (min_label != null) {
                    ItemText(min_label.getComposable(), theme)
                }

                val view_configuration: ViewConfiguration = LocalViewConfiguration.current
                CompositionLocalProvider(
                    LocalViewConfiguration provides remember {
                        object : ViewConfiguration {
                            override val doubleTapMinTimeMillis get() = view_configuration.doubleTapMinTimeMillis
                            override val doubleTapTimeoutMillis get() = view_configuration.doubleTapTimeoutMillis
                            override val longPressTimeoutMillis get() = view_configuration.longPressTimeoutMillis

                            override val touchSlop: Float
                                get() {
                                    return view_configuration.touchSlop * 2f
                                }
                        }
                    }
                ) {
                    SliderValueHorizontal(
                        value = getValue(),
                        onValueChange = { setValue(it) },
                        onValueChangeFinished = {
                            coroutine_scope.launch {
                                saveValue()
                            }
                        },
                        thumbSizeInDp = DpSize(12.dp, 12.dp),
                        track = { a, b, c, d, e ->
                            DefaultTrack(a, b, c, d, e,
                                theme.vibrant_accent.copy(alpha = 0.5f),
                                theme.vibrant_accent,
                                colorTickProgress = theme.vibrant_accent.getContrasted().copy(alpha = 0.5f)
                            )
                        },
                        track_press_enabled = !Platform.ANDROID.isCurrent(),
                        thumb = { modifier, offset, interaction_source, enabled, thumb_size ->
                            val colour: Color = theme.vibrant_accent
                            val scale_on_press: Float = 1.15f
                            val animation_spec: SpringSpec<Float> = SpringSpec(0.65f)
                            var value_text: String? by remember { mutableStateOf(null) }

                            val typed_value: Number = getTypedValue(is_int)
                            LaunchedEffect(typed_value, getValueText) {
                                value_text = getValueText?.invoke(typed_value)
                            }

                            if (value_text != null) {
                                MeasureUnconstrainedView({ ItemText(value_text, theme) }) { size ->
                                    var is_pressed by remember { mutableStateOf(false) }
                                    interaction_source.ListenOnPressed { is_pressed = it }
                                    val scale: Float by animateFloatAsState(
                                        if (is_pressed) scale_on_press else 1f,
                                        animationSpec = animation_spec
                                    )

                                    Column(
                                        Modifier
                                            .offset(
                                                x = with(LocalDensity.current) { offset - (size.width.toDp() / 2) + 12.dp },
                                                y = (-5).dp
                                            )
                                            .requiredHeight(55.dp)
                                            .graphicsLayer(scale, scale),
                                        verticalArrangement = Arrangement.Bottom,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Spacer(
                                            Modifier
                                                .size(12.dp)
                                                .background(
                                                    if (enabled) colour else
                                                        colour.copy(alpha = 0.6f), CircleShape
                                                )
                                        )
                                        ItemText(value_text, theme, linkify = false)
                                    }
                                }
                            }
                            else {
                                DefaultThumb(
                                    modifier,
                                    offset,
                                    interaction_source,
                                    true,
                                    thumb_size,
                                    colour,
                                    scale_on_press,
                                    animation_spec
                                )
                            }
                        },
                        steps = steps,
                        modifier = Modifier.weight(1f),
                        valueRange = range
                    )
                }

                if (max_label != null) {
                    ItemText(max_label.getComposable(), theme)
                }
            }
        }
    }
}
