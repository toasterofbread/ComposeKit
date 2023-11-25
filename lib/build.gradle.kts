plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")
}

kotlin {
    android()

    jvm("desktop")

    sourceSets {
        commonMain {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.material)
                implementation(compose.material3)
                implementation(compose.foundation)
                implementation(compose.materialIconsExtended)
                implementation(compose.ui)

                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)

                implementation("com.google.code.gson:gson:2.10.1")
                implementation("com.github.catppuccin:java:v1.0.0")
                implementation("com.godaddy.android.colorpicker:compose-color-picker:0.7.0")
            }
        }

        val androidMain by getting {
            dependencies {
                api("androidx.activity:activity-compose:1.8.1")

                implementation("com.anggrayudi:storage:1.5.5")
                implementation("com.google.accompanist:accompanist-swiperefresh:0.21.2-beta")
                implementation("io.coil-kt:coil-compose:2.4.0")
                implementation("com.github.andob:android-awt:1.0.0")
            }
        }

        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.common)
                implementation("com.github.ltttttttttttt:load-the-image:1.0.5")
                implementation("com.sshtools:two-slices:0.9.1")

                val file_chooser_version: String = "325fa2a"
                implementation("com.github.toasterofbread:gdx-nativefilechooser:$file_chooser_version")
            }
        }
    }
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "com.toasterofbread.spmp.shared"

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
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.2"
    }
    kotlin {
        jvmToolchain {
            version = "17"
        }
    }
    buildFeatures {
        compose = true
    }
}
