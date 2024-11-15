package dev.toastbits.composekit.platform.preferences

import dev.toastbits.composekit.platform.InMemoryPlatformPreferences
import dev.toastbits.composekit.platform.PlatformFile
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okio.buffer
import okio.use

open class PlatformPreferencesJson(
    private val file: PlatformFile,
    json: Json = Json
): InMemoryPlatformPreferences(json) {
    override val data: MutableMap<String, JsonElement> by lazy { loadData() }

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
            writer.writeUtf8(json.encodeToString(data))
            writer.flush()
        }
    }

    override fun edit(action: PlatformPreferences.Editor.() -> Unit) {
        super.edit(action)
        saveData()
    }
}
