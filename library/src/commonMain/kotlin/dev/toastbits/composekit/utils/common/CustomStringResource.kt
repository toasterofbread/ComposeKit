package dev.toastbits.composekit.utils.common

import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.runtime.Composable

interface CustomStringResource {
    suspend fun getString(): String
    @Composable
    fun getComposable(): String
}

data class ComposeCustomStringResource(val resource: StringResource): CustomStringResource {
    override suspend fun getString(): String =
        org.jetbrains.compose.resources.getString(resource)

    @Composable
    override fun getComposable(): String =
        stringResource(resource)
}

data class RawCustomStringResource(val string: String): CustomStringResource {
    override suspend fun getString(): String = string

    @Composable
    override fun getComposable(): String = string
}

fun StringResource.toCustomResource(): ComposeCustomStringResource =
    ComposeCustomStringResource(this)

fun String.toCustomResource(): RawCustomStringResource =
    RawCustomStringResource(this)
