package com.amarchaud.shared.data

import com.amarchaud.shared.ui.i18n.FrenchStrings
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
    fun `period can use french month names`() {
        assertEquals(
            "sept. 2025 – août 2026",
            experience(start = "2025-09", end = "2026-08").formattedPeriod("Aujourd’hui", FrenchStrings.monthNames),
        )
        assertEquals("sept. 2026 – Aujourd’hui", experience(start = "2026-09").formattedPeriod("Aujourd’hui", FrenchStrings.monthNames))
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

    private fun resume(experiences: List<ExperienceModel>, focus: String? = null) = ResumeModel(
        info = InfoModel(name = "Name", job = "Job"),
        experiences = experiences,
        skills = emptyList(),
        contact = ContactModel(email = "me@example.com"),
        focus = focus,
    )

    @Test
    fun `technologies are ranked by number of positions`() {
        val positions = listOf(
            experience(technologies = listOf("Kotlin", "Compose")),
            experience(technologies = listOf("Kotlin", "Kotlin", "Room")),
            experience(technologies = listOf("Compose", "Kotlin")),
        )
        assertEquals(
            listOf(TechnologyUsage("Kotlin", 3), TechnologyUsage("Compose", 2), TechnologyUsage("Room", 1)),
            positions.technologyUsage(),
        )
    }

    @Test
    fun `without focus every position belongs to the main career`() {
        val positions = listOf(experience(technologies = listOf("Kotlin")), experience(technologies = listOf("C++")))
        val split = resume(positions).careerSplit()
        assertNull(split.focus)
        assertEquals(positions, split.focusPositions)
        assertTrue(split.otherPositions.isEmpty())
    }

    @Test
    fun `focus separates the earlier career`() {
        val android = experience(start = "2014-09", end = "2018-07", technologies = listOf("Android", "Kotlin"))
        val lastCpp = experience(start = "2014-01", end = "2014-09", technologies = listOf("C++"))
        val firstCpp = experience(start = "2007-07", end = "2010-04", technologies = listOf("C++", "Video streaming"))
        val split = resume(listOf(android, lastCpp, firstCpp), focus = "Android").careerSplit()
        assertEquals("Android", split.focus)
        assertEquals(listOf(android), split.focusPositions)
        assertEquals(listOf(lastCpp, firstCpp), split.otherPositions)
        assertEquals(2014, split.focusSinceYear)
        assertEquals(2007..2014, split.otherYears)
        assertTrue(split.othersAreEarlier)
    }

    @Test
    fun `focus used by no position is ignored`() {
        val positions = listOf(experience(technologies = listOf("C++")))
        val split = resume(positions, focus = "Android").careerSplit()
        assertNull(split.focus)
        assertEquals(positions, split.focusPositions)
        assertTrue(split.otherPositions.isEmpty())
    }

    @Test
    fun `positions after the start of the focus career are not earlier`() {
        val positions = listOf(
            experience(start = "2015-01", end = "2016-01", technologies = listOf("Android")),
            experience(start = "2017-01", technologies = listOf("iOS")),
        )
        assertFalse(resume(positions, focus = "Android").careerSplit().othersAreEarlier)
    }

    @Test
    fun `urls are displayed without scheme www query and trailing slash`() {
        assertEquals("github.com/me", displayUrl("https://www.github.com/me/"))
        assertEquals("linkedin.com/in/me", displayUrl("https://www.linkedin.com/in/me?trk=profile"))
    }
}
