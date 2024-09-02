package com.toasterofbread.composekit.platform

import kotlinx.browser.document

private external fun encodeURIComponent(uriComponent: String): String
private external fun decodeURIComponent(encodedURI: String): String

object BrowserCookies: Cookies {
    override fun iterator(): Iterator<Cookie> =
        document.cookie.splitToSequence(';').mapNotNull {
            if (it.isBlank()) {
                return@mapNotNull null
            }

            val cookie: String = it.trim()

            val split: Int = cookie.indexOf('=')
            if (split == -1) {
                RuntimeException("Ignoring malformed cookie '$cookie'").printStackTrace()
                return@mapNotNull null
            }

            return@mapNotNull Cookie(
                key = cookie.substring(0, split).decode(),
                value = cookie.substring(split + 1).decode()
            )
        }.iterator()

    override operator fun get(key: String): String? =
        firstOrNull { it.key == key }?.value

    override operator fun set(key: String, value: String) {
        document.cookie = "${key.encode()}=${value.encode()}; SameSite=Strict; path=/"
    }

    private fun String.encode(): String = encodeURIComponent(this)
    private fun String.decode(): String = decodeURIComponent(this)
}
