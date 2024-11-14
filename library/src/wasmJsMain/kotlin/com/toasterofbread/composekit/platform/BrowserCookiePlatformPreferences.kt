package com.toasterofbread.composekit.platform

import dev.toastbits.composekit.platform.InMemoryPlatformPreferences
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

class CookiesPlatformPreferences(
    private val cookies: Cookies,
    json: Json = Json
): InMemoryPlatformPreferences(json) {
    override val data: MutableMap<String, JsonElement> =
        mutableMapOf<String, JsonElement>().also { map ->
            for ((key, value) in cookies) {
                val decoded: JsonElement =
                    try {
                        json.decodeFromString(value)
                    }
                    catch (e: Throwable) {
                        RuntimeException("Decoding cookie ($key=$value) failed, ignoring", e).printStackTrace()
                        continue
                    }

                map[key] = decoded
            }
        }

    override fun onKeyChanged(key: String) {
        super.onKeyChanged(key)
        cookies[key] = json.encodeToString(data[key])
    }
}
