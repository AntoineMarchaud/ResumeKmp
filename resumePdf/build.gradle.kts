plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

// Print version of the resume: a Kotlin program writes one HTML page per language, from the same JSON files
// as the app, then headless Chrome prints them to PDF. The PDFs are published with the website.
kotlin {
    jvmToolchain(21)
    jvm()

    sourceSets {
        jvmMain.dependencies {
            implementation(project(":shared"))
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.io.core)
        }
    }
}

val resumeResources = layout.projectDirectory.dir("../shared/src/commonMain/composeResources")
val pagesDir = layout.buildDirectory.dir("resume/pages")
val pdfDir = layout.buildDirectory.dir("resume/cv")

val generateResumePages = tasks.register<JavaExec>("generateResumePages") {
    description = "Writes the print version of the resume, one HTML page per language."
    group = "resume"
    val main = kotlin.jvm().compilations.getByName("main")
    classpath(main.output.allOutputs, main.runtimeDependencyFiles)
    mainClass.set("com.amarchaud.resumepdf.MainKt")
    inputs.dir(resumeResources).withPropertyName("resumeResources").withPathSensitivity(PathSensitivity.RELATIVE)
    outputs.dir(pagesDir).withPropertyName("pages")
    args(resumeResources.asFile.path, pagesDir.get().asFile.path)
}

// Chrome: the `chrome` Gradle property, the CHROME_PATH environment variable, or the usual install of the platform.
val chrome = providers.gradleProperty("chrome")
    .orElse(providers.environmentVariable("CHROME_PATH"))
    .orElse(
        providers.systemProperty("os.name").map { os ->
            if (os.startsWith("Mac")) "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome" else "google-chrome"
        },
    )

tasks.register<Exec>("resumePdf") {
    description = "Prints the resume of every language to PDF with headless Chrome."
    group = "resume"
    dependsOn(generateResumePages)
    inputs.dir(pagesDir).withPropertyName("pages").withPathSensitivity(PathSensitivity.RELATIVE)
    outputs.dir(pdfDir).withPropertyName("pdfs")
    commandLine(
        "sh", "-c",
        """
        set -e
        rm -rf "${'$'}2" && mkdir -p "${'$'}2"
        for page in "${'$'}1"/*.html; do
          "${'$'}0" --headless --disable-gpu --no-sandbox --no-pdf-header-footer --virtual-time-budget=10000 \
            --print-to-pdf="${'$'}2/${'$'}(basename "${'$'}page" .html).pdf" "file://${'$'}page" 2>/dev/null
        done
        """.trimIndent(),
        chrome.get(),
        pagesDir.get().asFile.path,
        pdfDir.get().asFile.path,
    )
}
