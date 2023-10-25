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
//
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
//
                implementation("com.google.code.gson:gson:2.10.1")
                implementation("com.github.catppuccin:java:v1.0.0")
                implementation("com.godaddy.android.colorpicker:compose-color-picker:0.7.0")
            }
        }

        val androidMain by getting {
            dependencies {
                api("androidx.activity:activity-compose:1.8.0")
//               api("androidx.core:core-ktx:1.12.0")
//                api("androidx.appcompat:appcompat:1.6.1")
//
                implementation("com.anggrayudi:storage:1.5.5")
                implementation("com.google.accompanist:accompanist-swiperefresh:0.21.2-beta")
                implementation("io.coil-kt:coil-compose:2.3.0")
//
//                implementation("com.google.accompanist:accompanist-pager:0.21.2-beta")
//                implementation("com.google.accompanist:accompanist-pager-indicators:0.21.2-beta")
//                implementation("com.google.accompanist:accompanist-systemuicontroller:0.21.2-beta")
//                implementation("com.google.accompanist:accompanist-swiperefresh:0.21.2-beta")
//                implementation("androidx.localbroadcastmanager:localbroadcastmanager:1.1.0")
//                implementation("androidx.palette:palette:1.0.0")
//                //noinspection GradleDependency
//                implementation("com.github.andob:android-awt:1.0.0")
//                implementation("com.github.toasterofbread:KizzyRPC:84e79614b4")
//                implementation("app.cash.sqldelight:android-driver:2.0.0-rc02")
//                implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.6.0")
//                implementation("io.github.jan-tennert.supabase:functions-kt:1.3.2")
//                implementation("io.ktor:ktor-client-cio:2.3.4")
            }
        }

        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.common)
                implementation("com.github.ltttttttttttt:load-the-image:1.0.5")
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
