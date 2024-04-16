package dev.toastbits.composekit.settings.ui.item

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.toastbits.composekit.platform.PlatformPreferences
import dev.toastbits.composekit.settings.ui.SettingsInterface
import dev.toastbits.composekit.settings.ui.SettingsPage
import dev.toastbits.composekit.settings.ui.StaticThemeData
import dev.toastbits.composekit.settings.ui.Theme
import dev.toastbits.composekit.settings.ui.ThemeData
import dev.toastbits.composekit.utils.common.contrastAgainst
import dev.toastbits.composekit.utils.common.generatePalette
import dev.toastbits.composekit.utils.common.getContrasted
import dev.toastbits.composekit.utils.common.random
import dev.toastbits.composekit.utils.common.sorted
import dev.toastbits.composekit.utils.composable.AlignableCrossfade
import dev.toastbits.composekit.utils.composable.ColourPicker
import dev.toastbits.composekit.utils.composable.OnChangedEffect
import dev.toastbits.composekit.utils.composable.ShapedIconButton
import dev.toastbits.composekit.utils.composable.WidthShrinkText
import org.jetbrains.compose.resources.painterResource
import dev.toastbits.composekit.library.generated.resources.*

class ThemeSelectorSettingsItem(
    val state: SettingsValueState<Int>,
    val title: String?,
    val subtitle: String?,

    val str_editor_title: String?,
    val str_field_name: String,
    val str_field_background: String,
    val str_field_on_background: String,
    val str_field_card: String,
    val str_field_accent: String,
    val str_button_preview: String,

    val getThemeCount: () -> Int,
    val getTheme: (index: Int) -> ThemeData?,
    val onThemeEdited: (index: Int, edited_theme: ThemeData) -> Unit,
    val createTheme: (Int) -> Unit,
    val removeTheme: (index: Int) -> Unit,

    val getFieldModifier: @Composable () -> Modifier = { Modifier }
): SettingsItem() {
    override fun initialiseValueStates(
        prefs: PlatformPreferences,
        default_provider: (String) -> Any,
    ) {
        state.init(prefs, default_provider)
    }

    override fun releaseValueStates(prefs: PlatformPreferences) {
        state.release(prefs)
    }

    override fun setEnableAutosave(value: Boolean) {
        state.setEnableAutosave(value)
    }

    override fun PlatformPreferences.Editor.saveItem() {
        with (state) {
            save()
        }
    }

    override fun resetValues() {
        state.reset()
        for (i in getThemeCount() - 1 downTo 0) {
            removeTheme(i)
        }
    }

    override fun getKeys(): List<String> = state.getKeys()

    @Composable
    override fun Item(
        settings_interface: SettingsInterface,
        openPage: (Int, Any?) -> Unit,
        openCustomPage: (SettingsPage) -> Unit,
        modifier: Modifier
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val icon_button_colours: IconButtonColors = IconButtonDefaults.iconButtonColors(
                    containerColor = settings_interface.theme.accent,
                    contentColor = settings_interface.theme.accent.getContrasted(),
                    disabledContainerColor = settings_interface.theme.accent.copy(alpha = 0.1f)
                )

                ItemTitleText(
                    title,
                    settings_interface.theme,
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )

                Text("${state.get() + 1} / ${getThemeCount()}")

                ShapedIconButton(
                    { state.set((state.get() - 1).coerceAtLeast(0)) },
                    icon_button_colours,
                    onLongClick = {
                        settings_interface.triggerVibration()
                        state.set(0)
                    },
                    enabled = state.get() > 0
                ) {
                    Icon(Icons.AutoMirrored.Default.KeyboardArrowLeft, null)
                }

                ShapedIconButton(
                    { state.set((state.get() + 1).coerceAtMost(getThemeCount() - 1)) },
                    icon_button_colours,
                    onLongClick = {
                        settings_interface.triggerVibration()
                        state.set(getThemeCount() - 1)
                    },
                    enabled = state.get() + 1 < getThemeCount()
                ) {
                    Icon(Icons.AutoMirrored.Default.KeyboardArrowRight, null)
                }

                IconButton({
                    val index: Int = state.get() + 1
                    createTheme(index)
                    state.set(index)
                }) {
                    Icon(Icons.Filled.Add, null)
                }
            }

            Crossfade(state.get().let { Pair(getTheme(it), it) }) {
                val (theme_data, theme_index) = it
                if (theme_data == null) {
                    return@Crossfade
                }

                val height: Dp = 40.dp

                Row(
                    Modifier.height(height),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    WidthShrinkText(
                        theme_data.name,
                        Modifier
                            .border(2.dp, theme_data.accent, CircleShape)
                            .fillMaxHeight()
                            .weight(1f)
                            .padding(start = 15.dp)
                    )

                    val icon_button_colours = IconButtonDefaults.iconButtonColors(
                        containerColor = theme_data.accent,
                        contentColor = theme_data.accent.getContrasted(),
                        disabledContainerColor = theme_data.accent.copy(alpha = 0.1f)
                    )

                    ShapedIconButton(
                        {
                            openCustomPage(
                                getEditPage(
                                    str_editor_title,
                                    str_field_name,
                                    str_field_background,
                                    str_field_on_background,
                                    str_field_card,
                                    str_field_accent,
                                    str_button_preview,
                                    theme_data,
                                    onEditCompleted = {
                                        onThemeEdited(theme_index, it)
                                    },
                                    getFieldModifier = getFieldModifier
                                )
                            )
                        },
                        icon_button_colours,
                        Modifier.size(height),
                        enabled = theme_data.isEditable()
                    ) {
                        Icon(Icons.Filled.Edit, null, tint = theme_data.accent.getContrasted())
                    }

                    ShapedIconButton(
                        {
                            removeTheme(theme_index)
                            if (getThemeCount() <= theme_index) {
                                state.set(theme_index - 1)
                            }
                        },
                        icon_button_colours,
                        Modifier.size(height),
                        enabled = theme_data.isEditable()
                    ) {
                        Icon(Icons.Filled.Close, null, tint = theme_data.accent.getContrasted())
                    }
                }
            }
        }
    }
}

private fun getEditPage(
    editor_title: String?,
    str_field_name: String,
    str_field_background: String,
    str_field_on_background: String,
    str_field_card: String,
    str_field_accent: String,
    str_button_preview: String,
    theme: ThemeData,
    onEditCompleted: (theme_data: ThemeData) -> Unit,
    getFieldModifier: @Composable () -> Modifier
): SettingsPage {
    return object : SettingsPage() {
        override val title: String?
            @Composable
            get() = editor_title

        private var reset by mutableStateOf(false)

        private var name: String by mutableStateOf(theme.name)
        private var background: Color by mutableStateOf(theme.background)
        private var on_background: Color by mutableStateOf(theme.on_background)
        private var card: Color by mutableStateOf(theme.card)
        private var accent: Color by mutableStateOf(theme.accent)

        @Composable
        override fun PageView(
            content_padding: PaddingValues,
            openPage: (Int, Any?) -> Unit,
            openCustomPage: (SettingsPage) -> Unit,
            goBack: () -> Unit
        ) {
            val focus_manager: FocusManager = LocalFocusManager.current
            val density: Density = LocalDensity.current

            val ui_theme: Theme = settings_interface.theme
            var previewing: Boolean by remember { mutableStateOf(ui_theme.preview_active) }
            var randomise: Boolean by remember { mutableStateOf(false) }

            val icon_button_colours = IconButtonDefaults.iconButtonColors(
                containerColor = ui_theme.vibrant_accent,
                contentColor = ui_theme.vibrant_accent.getContrasted()
            )
            OnChangedEffect(previewing) {
                if (previewing) {
                    ui_theme.setPreviewThemeData(StaticThemeData(name, background, on_background, card, accent))
                }
                else {
                    ui_theme.setPreviewThemeData(null)
                }
            }

            Box(
                Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures {
                            focus_manager.clearFocus()
                        }
                    }
            ) {
                var footer_height: Dp by remember { mutableStateOf(0.dp) }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    contentPadding = content_padding
                ) {
                    item {
                        OutlinedTextField(
                            name,
                            { name = it },
                            getFieldModifier().fillMaxWidth(),
                            label = { Text(str_field_name) },
                            isError = name.isEmpty(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                cursorColor = ui_theme.vibrant_accent,
                                focusedBorderColor = ui_theme.vibrant_accent,
                                focusedLabelColor = ui_theme.vibrant_accent,
                            ),
                            keyboardActions = KeyboardActions(onDone = {
                                focus_manager.clearFocus()
                            })
                        )
                    }

                    fun Field(name: String, default_colour: Color, onChanged: suspend (Color) -> Unit) {
                        item {
                            ColourField(
                                name,
                                ui_theme,
                                default_colour,
                                icon_button_colours,
                                randomise
                            ) { colour ->
                                onChanged(colour)

                                if (ui_theme.preview_active) {
                                    ui_theme.setPreviewThemeData(
                                        StaticThemeData(
                                            "",
                                            background,
                                            on_background,
                                            card,
                                            accent
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Field(
                        str_field_background,
                        theme.background,
                    ) { colour ->
                        background = colour
                    }
                    Field(
                        str_field_on_background,
                        theme.on_background,
                    ) { colour ->
                        on_background = colour
                    }
                    Field(
                        str_field_card,
                        theme.card,
                    ) { colour ->
                        card = colour
                    }
                    Field(
                        str_field_accent,
                        theme.accent,
                    ) { colour ->
                        accent = colour
                    }

                    item {
                        Spacer(Modifier.height(footer_height))
                    }
                }

                Row(
                    settings_interface.getFooterModifier()
                        .fillMaxWidth()
                        .height(IntrinsicSize.Max)
                        .align(Alignment.BottomCenter)
                        .padding(20.dp)
                        .onSizeChanged {
                            footer_height = with (density) {
                                it.height.toDp()
                            }
                        },
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val button_colours: ButtonColors = ButtonDefaults.buttonColors(
                        containerColor = ui_theme.accent,
                        contentColor = ui_theme.on_accent
                    )

                    Button(
                        { previewing = !previewing },
                        Modifier.fillMaxHeight(),
                        colors = button_colours,
                        contentPadding = PaddingValues(start = 10.dp, end = 15.dp)
                    ) {
                        Switch(
                            previewing,
                            { previewing = it },
                            colors = SwitchDefaults.colors(
                                checkedTrackColor = ui_theme.vibrant_accent.getContrasted().copy(alpha = 0.5f),
                                checkedThumbColor = ui_theme.vibrant_accent,
                                uncheckedTrackColor = ui_theme.vibrant_accent.getContrasted(),
                                uncheckedThumbColor = ui_theme.vibrant_accent.copy(alpha = 0.5f)
                            )
                        )
                        Text(str_button_preview, Modifier.padding(start = 5.dp))
                    }

                    ShapedIconButton(
                        { randomise = !randomise },
                        icon_button_colours,
                        Modifier.fillMaxHeight().aspectRatio(1f)
                    ) {
                        Icon(
                            painterResource(Res.drawable.ic_die),
                            null,
                            Modifier.size(25.dp)
                        )
                    }

                    Spacer(Modifier.fillMaxWidth().weight(1f))

                    ShapedIconButton(
                        {
                            onEditCompleted(StaticThemeData(name, background, on_background, card, accent))
                            goBack()
                        },
                        icon_button_colours,
                        Modifier.fillMaxHeight().aspectRatio(1f)
                    ) {
                        Icon(
                            Icons.Filled.Done,
                            null,
                            Modifier.size(25.dp)
                        )
                    }
                }
            }
        }

        override suspend fun resetKeys() {
            name = theme.name
            background = theme.background
            on_background = theme.on_background
            card = theme.card
            accent = theme.accent
            reset = !reset
        }

        override fun onClosed() {
            super.onClosed()
            settings_interface.theme.setPreviewThemeData(null)
        }
    }
}

@Composable
private fun ColourField(
    name: String,
    ui_theme: Theme,
    default_colour: Color,
    button_colours: IconButtonColors,
    randomise: Any,
    onChanged: suspend (Color) -> Unit
) {
    var show_picker by remember { mutableStateOf(false) }
    var current by remember { mutableStateOf(default_colour) }
    var instance by remember { mutableStateOf(false) }
    val presets = remember(current) { current.generatePalette(10, 1f).sorted(true) }

    @Composable
    fun Color.presetItem() {
        Spacer(Modifier
            .size(40.dp)
            .background(this, CircleShape)
            .border(Dp.Hairline, contrastAgainst(current), CircleShape)
            .clickable {
                current = this
                instance = !instance
            }
        )
    }

    LaunchedEffect(current) {
        onChanged(current)
    }

    OnChangedEffect(randomise) {
        current = Color.random()
        instance = !instance
    }

    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(name, style = MaterialTheme.typography.titleMedium, color = ui_theme.background.getContrasted())

            Spacer(Modifier
                .fillMaxWidth()
                .weight(1f))

            FilledIconButton({
                show_picker = !show_picker
            }, colors = button_colours) {
                Crossfade(show_picker) { picker ->
                    Icon(if (picker) Icons.Filled.Done else Icons.Filled.Edit, null, Modifier.size(22.dp))
                }
            }
            FilledIconButton({
                current = Color.random()
                instance = !instance
            }, colors = button_colours) {
                Icon(painterResource(Res.drawable.ic_die), null, Modifier.size(22.dp))
            }
            FilledIconButton({
                current = default_colour
                instance = !instance
            }, colors = button_colours) {
                Icon(Icons.Filled.Refresh, null, Modifier.size(22.dp))
            }
        }

        val shape: Shape = RoundedCornerShape(13.dp)
        val arrangement = Arrangement.spacedBy(10.dp)

        AlignableCrossfade(
            show_picker,
            Modifier
                .align(Alignment.End)
                .fillMaxWidth()
                .animateContentSize()
                .background(current, shape)
                .border(Dp.Hairline, current.contrastAgainst(ui_theme.background), shape)
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) { picker ->
            if (picker) {
                ColourPicker(
                    current,
                    Modifier
                        .widthIn(max = 400.dp)
                        .fillMaxWidth(),
                    arrangement,
                    presets
                ) {
                    current = it
                }
            }
            else {
                LazyRow(Modifier.fillMaxWidth(), horizontalArrangement = arrangement) {
                    items(presets) { colour ->
                        colour.presetItem()
                    }
                }
            }
        }
    }
}
