package com.amarchaud.resumekmp

import com.amarchaud.shared.data.formattedPeriod
import com.amarchaud.shared.data.parseResume
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readString
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * The web page embeds the resume as plain HTML for search engines, as the app draws its text on a canvas.
 * It must follow the bundled resume.
 */
class WebPageTest {

    private fun read(path: String) = SystemFileSystem.source(Path(path)).buffered().use { it.readString() }

    private val page = read("src/webMain/resources/index.html")
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&quot;", "\"")
        .replace("&amp;", "&")

    private val resume = parseResume(read("../shared/src/commonMain/composeResources/files/resume_template.json"))

    @Test
    fun `web page contains the whole resume`() {
        val expected = buildList {
            add(resume.info.name)
            add(resume.info.job)
            addAll(resume.about?.summary.orEmpty())
            addAll(resume.about?.strengths.orEmpty())
            resume.experiences.forEach { experience ->
                add("${experience.company} · ${experience.role}")
                experience.formattedPeriod(presentLabel = "Present")?.let(::add)
                experience.description?.let(::add)
                addAll(experience.highlights)
            }
            addAll(resume.skills)
            resume.education.forEach { add(it.school) }
        }
        expected.forEach { text -> assertTrue(text in page, "index.html is missing \"$text\"") }
    }

    /** The resume PDFs are generated with the site (`:resumePdf`), in its `cv` directory. */
    @Test
    fun `resume pdfs are the ones generated with the site`() {
        listOf("resume_template.json", "resume_template_fr.json").forEach { file ->
            val url = parseResume(read("../shared/src/commonMain/composeResources/files/$file")).contact.resumePdfUrl
            if (url != null) {
                assertTrue(url.startsWith("${SITE_URL}cv/") && url.endsWith(".pdf"), "$file: unexpected resume PDF $url")
            }
        }
    }

    @Test
    fun `web page keeps the email and phone number away from spam bots`() {
        assertFalse(resume.contact.email in page)
        resume.contact.phone?.let { assertFalse(it in page) }
    }

    private companion object {
        const val SITE_URL = "https://antoinemarchaud.github.io/ResumeKmp/"
    }
}
