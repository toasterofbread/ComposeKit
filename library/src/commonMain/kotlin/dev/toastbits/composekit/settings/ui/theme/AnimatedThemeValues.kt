package dev.toastbits.composekit.settings.ui

import kotlinx.coroutines.launch
import kotlinx.coroutines.coroutineScope
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.Animatable
import androidx.compose.ui.graphics.Color

internal class AnimatedThemeValues(initial_state: ThemeValues): ThemeValues {
    private val background_state: Animatable<Color, AnimationVector4D> = Animatable(initial_state.background)
    private val on_background_state: Animatable<Color, AnimationVector4D> = Animatable(initial_state.on_background)
    private val card_state: Animatable<Color, AnimationVector4D> = Animatable(initial_state.card)
    private val accent_state: Animatable<Color, AnimationVector4D> = Animatable(initial_state.accent)
    private val error_state: Animatable<Color, AnimationVector4D> = Animatable(initial_state.error)

    override val background: Color get() = background_state.value
    override val on_background: Color get() = on_background_state.value
    override val card: Color get() = card_state.value
    override val accent: Color get() = accent_state.value
    override val error: Color get() = error_state.value

    suspend fun updateColours(values: ThemeValues) = coroutineScope {
        launch {
            background_state.animateTo(values.background)
        }
        launch {
            on_background_state.animateTo(values.on_background)
        }
        launch {
            card_state.animateTo(values.card)
        }
        launch {
            accent_state.animateTo(values.accent)
        }
        launch {
            error_state.animateTo(values.error)
        }
    }
}
