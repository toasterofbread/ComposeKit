plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    google()
}

dependencies {
    val kotlin_version: String = "2.0.0"
    implementation("org.jetbrains.kotlin.multiplatform:org.jetbrains.kotlin.multiplatform.gradle.plugin:$kotlin_version")

    val agp_version: String = "8.1.0"
    implementation("com.android.library:com.android.library.gradle.plugin:$agp_version")
}
