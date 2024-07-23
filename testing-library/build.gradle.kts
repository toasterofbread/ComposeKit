@file:OptIn(ExperimentalWasmDsl::class)

import com.vanniktech.maven.publish.SonatypeHost
import com.vanniktech.maven.publish.KotlinMultiplatform
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    id("com.android.library")
    id("com.vanniktech.maven.publish")
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

allprojects {
    group = "dev.toastbits.composekit"
    version = "0.0.3-SNAPSHOT"
}

kotlin {
    androidTarget()

    jvm("desktop")

    wasmJs {
        browser()
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "dev.toastbits.composekit"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        minSdk = (findProperty("android.minSdk") as String).toInt()
        targetSdk = (findProperty("android.targetSdk") as String).toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

mavenPublishing {
    coordinates(artifactId = "composekit-testing")
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    // signAllPublications()

    configure(KotlinMultiplatform(
        sourcesJar = true,
        androidVariantsToPublish = listOf("debug", "release")
    ))

    pom {
        name.set("ComposeKitTesting")
        url.set("https://github.com/toasterofbread/composekit")
        inceptionYear.set("2024")

        licenses {
            license {
                name.set("GPL-3.0")
                url.set("https://www.gnu.org/licenses/gpl-3.0.en.html")
            }
        }
        developers {
            developer {
                id.set("toasterofbread")
                name.set("Talo Halton")
                email.set("talohalton@gmail.com")
                url.set("https://github.com/toasterofbread")
            }
        }
        scm {
            connection.set("https://github.com/toasterofbread/composekit.git")
            url.set("https://github.com/toasterofbread/composekit")
        }
        issueManagement {
            system.set("Github")
            url.set("https://github.com/toasterofbread/composekit/issues")
        }
    }
}
