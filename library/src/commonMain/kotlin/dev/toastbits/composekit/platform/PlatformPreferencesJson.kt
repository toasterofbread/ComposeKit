package dev.toastbits.composekit.platform

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import okio.buffer
import okio.use

open class PlatformPreferencesJson(private val file: PlatformFile): InMemoryPlatformPreferences() {
    override val data: MutableMap<String, JsonElement> by lazy { loadData() }
    private val listeners: MutableList<PlatformPreferencesListener> = mutableListOf()

    private val json: Json =
        Json {
            ignoreUnknownKeys = true
            explicitNulls = false
        }

    protected open fun loadData(): MutableMap<String, JsonElement> {
        if (!file.exists) {
            return mutableMapOf()
        }

        return file.inputStream().use { stream ->
            json.decodeFromString(stream.buffer().readUtf8())
        }
    }
    private fun saveData() {
        file.createFile()

        file.outputStream().buffer().use { writer ->
            writer.writeUtf8(Json.encodeToString(data))
            writer.flush()
        }
    }

    override fun edit(action: PlatformPreferences.Editor.() -> Unit) {
        super.edit(action)
        saveData()
    }
}
