package com.toasterofbread.composekit.platform

interface Cookies: Iterable<Cookie> {
    operator fun get(key: String): String?
    operator fun set(key: String, value: String)
}