rootProject.name = "ComposeKit"

include(":library")
include(":testing-library")
include(":compose-color-picker:color-picker")

pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        mavenCentral()
        google()
    }

    plugins {
        val kotlin_version: String = extra["kotlin.version"] as String
        kotlin("multiplatform").version(kotlin_version)
        kotlin("jvm").version(kotlin_version)
        kotlin("android").version(kotlin_version)
        kotlin("plugin.compose").version(kotlin_version)
        kotlin("plugin.serialization").version(kotlin_version)

        id("org.jetbrains.kotlinx.atomicfu").version("0.25.0")

        val agp_version: String = extra["agp.version"] as String
        id("com.android.library").version(agp_version)

        val compose_version: String = extra["compose.version"] as String
        id("org.jetbrains.compose").version(compose_version)

        id("com.vanniktech.maven.publish").version("0.28.0")
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")

        // https://github.com/d1snin/catppuccin-kotlin
        maven("https://maven.d1s.dev/snapshots")

        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")

        // Mokkery
        mavenLocal()
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
        }
    }
}
