import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
}

kotlin {
    jvmToolchain(21)

    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    jvm()

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
            implementation(project(":shared"))
            implementation(libs.compose.runtime)
            implementation(libs.compose.ui)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }
        jvmTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.io.core)
        }
    }
}

// WebPageTest reads the web page and the resume directly, so they must invalidate the test results.
tasks.withType<Test>().configureEach {
    inputs.files("src/webMain/resources/index.html", "../shared/src/commonMain/composeResources/files/resume_template.json")
        .withPropertyName("webPageTestFiles")
        .withPathSensitivity(PathSensitivity.RELATIVE)
}

// The website: the WebAssembly app, plus the JavaScript app for Apple's WebKit (Safari, and every browser
// on iOS and iPadOS), where the WebAssembly app runs but its canvas stays blank. index.html picks the bundle.
tasks.register<Sync>("webDistribution") {
    description = "Assembles the website, with the WebAssembly app and its JavaScript fallback."
    group = "distribution"
    from(tasks.named("wasmJsBrowserDistribution"))
    from(tasks.named("jsBrowserDistribution")) {
        include("composeApp.js", "composeApp.js.map")
        rename("composeApp\\.js", "composeApp-js.js")
        filter { line -> line.replace("sourceMappingURL=composeApp.js.map", "sourceMappingURL=composeApp-js.js.map") }
    }
    into(layout.buildDirectory.dir("dist/web"))
}

compose.desktop {
    application {
        mainClass = "com.amarchaud.resumekmp.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "ResumeKmp"
            packageVersion = "2.0.0"
        }
    }
}
