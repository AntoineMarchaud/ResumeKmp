package com.amarchaud.shared.data

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ResumeParserTest {

    /** JSON files written for the first version of the template keep working. */
    @Test
    fun `legacy resume format is still supported`() {
        val legacy = """
            {
              "info": { "name": "Jane Doe", "job": "Android Developer" },
              "contact": {
                "email": "jane@example.com",
                "phone": "+33 6 00 00 00 00",
                "githubUrl": "https://github.com/jane",
                "linkedInUrl": "https://www.linkedin.com/in/jane"
              },
              "skills": ["Kotlin"],
              "experiences": [
                {
                  "company": "Acme",
                  "period": "Since 2020",
                  "role": "Android Developer",
                  "highlights": ["Did things"],
                  "technologies": ["Compose"],
                  "unknownField": true
                }
              ]
            }
        """.trimIndent()

        val resume = parseResume(legacy)

        assertEquals("Jane Doe", resume.info.name)
        assertNull(resume.about)
        assertTrue(resume.education.isEmpty())
        assertTrue(resume.experiences.single().isCurrent)
        assertEquals("+33 6 00 00 00 00", resume.contact.phone)
    }
}
