package com.toasterofbread.composekit.settings.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.toasterofbread.composekit.platform.PlatformPreferences

class SettingsInterface(
    val themeProvider: () -> Theme,
    private val root_page: Int,
    val prefs: PlatformPreferences,
    val default_provider: (String) -> Any,
    val triggerVibration: () -> Unit,
    private val getPage: (Int, Any?) -> SettingsPage,
    private val onPageChanged: ((page: Int?) -> Unit)? = null,
    private val onCloseRequested: (() -> Unit)? = null,
    val getFooterModifier: @Composable () -> Modifier = { Modifier }
) {
    val theme: Theme get() = themeProvider()
    var current_page: SettingsPage by mutableStateOf(getUserPage(root_page, null))
        private set
    private val page_stack = mutableListOf<SettingsPage>()

    private fun getUserPage(page_id: Int, param: Any?): SettingsPage {
        return getPage(page_id, param).also { page ->
            page.id = page_id
            page.settings_interface = this
        }
    }

    fun goBack(): Boolean {
        if (page_stack.size > 0) {
            val target_page: SettingsPage? = page_stack.removeLastOrNull()
            if (current_page != target_page) {
                current_page.onClosed()
                if (target_page != null) {
                    current_page = target_page
                    onPageChanged?.invoke(current_page.id)
                }
            }
            return true
        }
        return false
    }

    fun openPage(target_page: SettingsPage) {
        if (target_page != current_page) {
            target_page.settings_interface = this@SettingsInterface
            page_stack.add(current_page)
            current_page = target_page
            onPageChanged?.invoke(current_page.id)
        }
    }

    fun openPageById(page_id: Int, param: Any?) {
        openPage(getUserPage(page_id, param))
    }

    @Composable
    fun Interface(modifier: Modifier = Modifier, content_padding: PaddingValues = PaddingValues(0.dp), page_top_padding: Dp? = null, titleFooter: (@Composable () -> Unit)? = null) {
        Crossfade(current_page, modifier = modifier) { page ->
            var width by remember { mutableStateOf(0) }

            Column(
                Modifier
                    .fillMaxSize()
                    .onSizeChanged { width = it.width }
            ) {
                page.TitleBar(
                    page.id == root_page,
                    Modifier.zIndex(10f).padding(content_padding.copy(bottom = 0.dp)),
                    titleFooter
                )

                Box(
                    contentAlignment = Alignment.TopCenter
                ) {
                    page.Page(
                        if (page_top_padding != null) content_padding.copy(top = page_top_padding)
                        else content_padding,
                        { target_page_id, param ->
                            if (current_page.id != target_page_id) {
                                page_stack.add(current_page)
                                current_page = getUserPage(target_page_id, param)
                                onPageChanged?.invoke(current_page.id)
                            }
                        },
                        this@SettingsInterface::openPage,
                        this@SettingsInterface::goBack
                    )
                }
            }
        }
    }
}

@Composable
fun PaddingValues.copy(
    start: Dp? = null,
    top: Dp? = null,
    end: Dp? = null,
    bottom: Dp? = null,
): PaddingValues {
    return PaddingValues(
        start ?: calculateStartPadding(LocalLayoutDirection.current),
        top ?: calculateTopPadding(),
        end ?: calculateEndPadding(LocalLayoutDirection.current),
        bottom ?: calculateBottomPadding()
    )
}
