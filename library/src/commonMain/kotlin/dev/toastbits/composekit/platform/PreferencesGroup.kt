package dev.toastbits.composekit.platform

interface PreferencesGroup {
    val group_key: String?
    fun getAllProperties(): List<PreferencesProperty<*>>
}