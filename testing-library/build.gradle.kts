import dev.toastbits.composekit.plugin.applyProjectHierarchyTemplate
import com.vanniktech.maven.publish.KotlinMultiplatform
import dev.mokkery.gradle.ApplicationRule

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    kotlin("plugin.compose")
    id("com.android.library")
    id("org.jetbrains.compose")
    id("com.vanniktech.maven.publish")
    id("dev.mokkery")
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

    applyProjectHierarchyTemplate()

    sourceSets {
        commonMain {
            dependencies {
                implementation(kotlin("test"))
                implementation(project(":library"))

                implementation(compose.runtime)
            }
        }
    }
}

mokkery {
    // Apply Mokkery to all sourceSets
    rule.set { true }
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "dev.toastbits.composekit.testing"

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
//    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
//     signAllPublications()

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
