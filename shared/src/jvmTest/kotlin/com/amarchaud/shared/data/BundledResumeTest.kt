package com.amarchaud.shared.data

import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class BundledResumeTest {

    private fun bundled(language: Language) =
        parseResume(SystemFileSystem.source(Path("src/commonMain/composeResources/${language.resumeFile}")).buffered().use { it.readString() })

    /** Translations must describe the same positions, in the same order, with the same dates. */
    @Test
    fun `translations match the english resume`() {
        val english = bundled(Language.English)
        Language.entries.filter { it != Language.English }.forEach { language ->
            val translation = bundled(language)
            assertEquals(english.experiences.map { Triple(it.company, it.start, it.end) }, translation.experiences.map { Triple(it.company, it.start, it.end) })
            assertEquals(english.experiences.map { it.logo }, translation.experiences.map { it.logo })
            assertEquals(english.skills.size, translation.skills.size)
        }
    }


    /** The resume bundled with the app must always be valid. */
    @Test
    fun `bundled resume is valid`() {
        val json = SystemFileSystem.source(Path("src/commonMain/composeResources/files/resume_template.json")).buffered().use { it.readString() }
        val resume = parseResume(json)

        assertTrue(resume.info.name.isNotBlank())
        assertTrue(resume.experiences.isNotEmpty())
        assertTrue(resume.skills.isNotEmpty())
        resume.experiences.forEach { experience ->
            assertTrue(
                experience.startMonth != null || experience.period != null,
                "${experience.company} needs a valid start month or a period",
            )
            if (experience.end != null) assertNotNull(experience.endMonth, "${experience.company} has an invalid end")
        }
        assertEquals(1, resume.experiences.count { it.isCurrent }, "Exactly one position should be ongoing")
        resume.experiences.mapNotNull { it.logo }.forEach { logo ->
            assertTrue(SystemFileSystem.exists(Path("src/commonMain/composeResources/files/$logo")), "Missing logo: $logo")
        }
    }
}
