@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

package dev.toastbits.composekit.plugin

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

fun KotlinMultiplatformExtension.applyProjectHierarchyTemplate() {
    applyDefaultHierarchyTemplate {
        common {
            withAndroidTarget()
            withJvm()
            withWasmJs()

            group("jvm") {
                withAndroidTarget()
                withJvm()
            }
            group("cmpJbr") {
                withJvm()
                withWasmJs()
            }
        }
    }
}
