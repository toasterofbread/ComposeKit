package dev.toastbits.composekit.settings.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.toastbits.composekit.navigation.screen.Screen
import dev.toastbits.composekit.platform.composable.theme.LocalApplicationTheme
import dev.toastbits.composekit.settings.ui.component.item.GroupSettingsItem
import dev.toastbits.composekit.settings.ui.component.item.SettingsItem
import dev.toastbits.composekit.utils.composable.WidthShrinkText
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope

interface SettingsScreen: Screen {
    suspend fun resetKeys()
}

@Deprecated("Use SettingsScreen and Navigator")
abstract class SettingsPage {
    var id: Int? = null
        internal set
    lateinit var settings_interface: SettingsInterface

    open val scrolling: Boolean
        @Composable
        get() = true

    open val apply_padding: Boolean = true

    open val title: String?
        @Composable
        get() = null
    open val icon: ImageVector?
        @Composable
        get() = null

    @Composable
    fun Page(content_padding: PaddingValues, openPage: (Int, Any?) -> Unit, openCustomPage: (SettingsPage) -> Unit, goBack: () -> Unit) {
        CompositionLocalProvider(LocalContentColor provides LocalApplicationTheme.current.on_background) {
            PageView(content_padding, openPage, openCustomPage, goBack)
        }
    }

    @Composable
    open fun hasTitleBar(): Boolean = true

    @Composable
    fun TitleBar(is_root: Boolean, modifier: Modifier = Modifier) {
        TitleBar(is_root, modifier, null)
    }

    @Composable
    open fun TitleBar(is_root: Boolean, modifier: Modifier, titleFooter: (@Composable () -> Unit)?) {
        val theme: ThemeValues = LocalApplicationTheme.current

        Crossfade(title, modifier) { title ->
            Column(Modifier.fillMaxWidth()) {

                FlowRow(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val ic: ImageVector? = icon
                    if (ic != null) {
                        Icon(ic, null, Modifier.align(Alignment.CenterVertically))
                    }
                    else {
                        Spacer(Modifier.width(24.dp))
                    }

                    if (title != null) {
                        WidthShrinkText(
                            title,
                            Modifier
                                .padding(horizontal = 30.dp)
                                .weight(1f)
                                .align(Alignment.CenterVertically),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = theme.on_background,
                                fontWeight = FontWeight.Light,
                                textAlign = TextAlign.Center
                            )
                        )
                    }

                    TitleBarEndContent(Modifier.align(Alignment.CenterVertically))
                }

                titleFooter?.invoke()
            }
        }
    }

    @Composable
    open fun TitleBarEndContent(modifier: Modifier) {
        val coroutine_scope: CoroutineScope = rememberCoroutineScope()

        AnimatedVisibility(canResetKeys(), modifier) {
            IconButton({
                coroutine_scope.launch {
                    resetKeys()
                }
            }) {
                Icon(Icons.Default.Refresh, null)
            }
        }
    }

    @Composable
    protected abstract fun PageView(content_padding: PaddingValues, openPage: (Int, Any?) -> Unit, openCustomPage: (SettingsPage) -> Unit, goBack: () -> Unit)

    @Composable
    open fun canResetKeys(): Boolean = true

    abstract suspend fun resetKeys()
    open fun onClosed() {}
}

private const val SETTINGS_PAGE_WITH_ITEMS_SPACING = 20f

open class SettingsPageWithItems(
    val getTitle: @Composable () -> String?,
    val getItems: () -> List<SettingsItem>,
    val modifier: Modifier = Modifier,
    val getIcon: (@Composable () -> ImageVector?)? = null
): SettingsPage() {

    override val title: String?
        @Composable
        get() = getTitle()
    override val icon: ImageVector?
        @Composable
        get() = getIcon?.invoke()

    @Composable
    override fun PageView(
        content_padding: PaddingValues,
        openPage: (Int, Any?) -> Unit,
        openCustomPage: (SettingsPage) -> Unit,
        goBack: () -> Unit
    ) {
        Crossfade(getItems()) { items ->
            val showItems: List<SettingsItem> = items.filter { it.showItem() }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(SETTINGS_PAGE_WITH_ITEMS_SPACING.dp),
                contentPadding = content_padding
            ) {
                itemsIndexed(showItems) { index, item ->
                    if (index != 0 && item is GroupSettingsItem) {
                        Spacer(Modifier.height(30.dp))
                    }

                    item.Item(Modifier)
                }
            }
        }
    }

    override suspend fun resetKeys() {
        for (item in getItems()) {
            item.resetValues()
        }
    }
}
