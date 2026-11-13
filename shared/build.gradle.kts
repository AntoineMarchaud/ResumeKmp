import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.plugin.mpp.TestExecutable

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    jvmToolchain(21)

    android {
        namespace = "com.amarchaud.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        androidResources {
            enable = true
        }
        // Runs commonTest on the JVM against the Android variant as well.
        withHostTest {}
    }

    listOf(iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        // Test executables only run from the command line: built like release binaries (optimized,
        // without debug information), no dSYM is generated, whose dsymutil step cannot find the
        // Kotlin/Native cached objects and floods the build with "could not open" warnings.
        iosTarget.binaries.withType<TestExecutable>().configureEach {
            debuggable = false
            optimized = true
        }
    }

    jvm()

    // Executables let the browser tests bundle the Skiko runtime needed by Compose.
    js {
        browser()
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.ui)
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.material3)
            implementation(libs.compose.material3.adaptive)
            implementation(libs.lifecycle.viewmodel.compose)

            implementation(libs.koin.core)
            implementation(libs.koin.compose.viewmodel)

            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.json)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
        }

        jvmTest.dependencies {
            implementation(libs.compose.ui.test)
            implementation(libs.kotlinx.io.core)
            implementation(compose.desktop.currentOs)
        }
    }
}

compose.resources {
    packageOfResClass = "com.amarchaud.shared.resources"
}

dependencies {
    // Renders the common @Preview functions in Android Studio.
    androidRuntimeClasspath(libs.compose.ui.tooling)
}
