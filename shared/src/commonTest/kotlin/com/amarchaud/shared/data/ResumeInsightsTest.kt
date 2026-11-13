package com.amarchaud.shared.data

import kotlinx.datetime.YearMonth
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ResumeInsightsTest {

    private fun experience(
        start: String? = null,
        end: String? = null,
        period: String? = null,
        technologies: List<String> = emptyList(),
    ) = ExperienceModel(
        company = "Company",
        role = "Role",
        start = start,
        end = end,
        period = period,
        technologies = technologies,
    )

    @Test
    fun `duration includes both the first and the last month`() {
        assertEquals(12, experience(start = "2025-09", end = "2026-08").durationInMonths(YearMonth(2030, 1)))
        assertEquals(26, experience(start = "2022-11", end = "2024-12").durationInMonths(YearMonth(2030, 1)))
    }

    @Test
    fun `ongoing position lasts until now`() {
        val ongoing = experience(start = "2026-09")
        assertEquals(3, ongoing.durationInMonths(YearMonth(2026, 11)))
        assertTrue(ongoing.isCurrent)
    }

    @Test
    fun `free text period is used when no start month is given`() {
        val legacy = experience(period = "Since september 2025")
        assertEquals("Since september 2025", legacy.formattedPeriod(presentLabel = "Present"))
        assertNull(legacy.durationInMonths(YearMonth(2026, 1)))
        assertTrue(legacy.isCurrent)
        assertFalse(experience(period = "2014 - 2018").isCurrent)
    }

    @Test
    fun `period is formatted with abbreviated english month names`() {
        assertEquals("Sep 2025 – Aug 2026", experience(start = "2025-09", end = "2026-08").formattedPeriod("Present"))
        assertEquals("Sep 2026 – Present", experience(start = "2026-09").formattedPeriod("Present"))
    }

    @Test
    fun `invalid months are ignored`() {
        val invalid = experience(start = "September 2025", period = "Fallback")
        assertNull(invalid.startMonth)
        assertEquals("Fallback", invalid.formattedPeriod("Present"))
    }

    @Test
    fun `duration is split into years and months`() {
        assertEquals(2 to 2, splitDuration(26))
        assertEquals(0 to 9, splitDuration(9))
        assertEquals(1 to 0, splitDuration(12))
    }

    @Test
    fun `initials skip parenthesized text and split on separators`() {
        assertEquals("AM", initialsOf("Antoine Marchaud"))
        assertEquals("VL", initialsOf("V-Labs"))
        assertEquals("S", initialsOf("Scalian (ex-Eurogiciel)"))
        assertEquals("FT", initialsOf("France Télévisions"))
    }

    @Test
    fun `technologies are ranked by number of positions`() {
        val resume = ResumeModel(
            info = InfoModel(name = "Name", job = "Job"),
            experiences = listOf(
                experience(technologies = listOf("Kotlin", "Compose")),
                experience(technologies = listOf("Kotlin", "Kotlin", "Room")),
                experience(technologies = listOf("Compose", "Kotlin")),
            ),
            skills = emptyList(),
            contact = ContactModel(email = "me@example.com"),
        )
        assertEquals(
            listOf(TechnologyUsage("Kotlin", 3), TechnologyUsage("Compose", 2), TechnologyUsage("Room", 1)),
            resume.technologyUsage(),
        )
    }

    @Test
    fun `urls are displayed without scheme www query and trailing slash`() {
        assertEquals("github.com/me", displayUrl("https://www.github.com/me/"))
        assertEquals("linkedin.com/in/me", displayUrl("https://www.linkedin.com/in/me?trk=profile"))
    }
}
