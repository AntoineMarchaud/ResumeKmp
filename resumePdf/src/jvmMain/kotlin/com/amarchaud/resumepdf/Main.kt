package com.amarchaud.resumepdf

import com.amarchaud.shared.data.Language
import com.amarchaud.shared.data.parseResume
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readString
import kotlinx.io.writeString

/**
 * Writes the print version of the resume: one HTML page per language whose JSON has a `resumePdfUrl`, named after
 * that PDF, next to the assets it uses. Headless Chrome then prints the pages to PDF (see `build.gradle.kts`).
 *
 * Arguments: the `composeResources` directory of the shared module, and the output directory.
 */
fun main(args: Array<String>) {
    val resources = Path(args[0])
    val output = Path(args[1])
    SystemFileSystem.createDirectories(Path(output, "assets", "logos"))
    copy(Path(resources, "font", "plus_jakarta_sans.ttf"), Path(output, "assets", "plus_jakarta_sans.ttf"))

    Language.entries.forEach { language ->
        val resume = parseResume(read(Path(resources, language.resumeFile)))
        val pdfUrl = resume.contact.resumePdfUrl ?: return@forEach
        resume.info.photo?.let { copy(Path(resources, "files", it), Path(output, "assets", it)) }
        resume.experiences.mapNotNull { it.logo }.distinct().forEach { copy(Path(resources, "files", it), Path(output, "assets", it)) }

        val page = ResumePage(resume = resume, language = language, siteUrl = pdfUrl.substringBefore("cv/"))
        write(Path(output, pdfUrl.substringAfterLast('/').removeSuffix(".pdf") + ".html"), page.html())
    }
}

private fun read(path: Path): String = SystemFileSystem.source(path).buffered().use { it.readString() }

private fun write(path: Path, text: String) = SystemFileSystem.sink(path).buffered().use { it.writeString(text) }

private fun copy(from: Path, to: Path) {
    SystemFileSystem.source(from).buffered().use { source ->
        SystemFileSystem.sink(to).buffered().use { sink -> source.transferTo(sink) }
    }
}
