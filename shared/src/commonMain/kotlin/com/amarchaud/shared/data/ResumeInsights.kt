package com.amarchaud.shared.data

import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.YearMonth
import kotlinx.datetime.monthsUntil
import kotlinx.datetime.parseOrNull
import kotlinx.datetime.todayIn
import kotlinx.datetime.yearMonth

/*
 * Pure Kotlin helpers deriving display data from the resume model.
 * They have no Android dependency so they are covered by local unit tests.
 */

private val EnglishMonthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

private fun YearMonth.label(monthNames: List<String>) = "${monthNames[month.ordinal]} $year"

private val currentPeriodMarkers = listOf("present", "since", "today", "now")

/** The current month in the device time zone. */
fun currentYearMonth(clock: Clock = Clock.System): YearMonth =
    clock.todayIn(TimeZone.currentSystemDefault()).yearMonth

val ExperienceModel.startMonth: YearMonth? get() = start?.let { YearMonth.parseOrNull(it) }
val ExperienceModel.endMonth: YearMonth? get() = end?.let { YearMonth.parseOrNull(it) }

/** `true` when the position is still ongoing. */
val ExperienceModel.isCurrent: Boolean
    get() = when {
        startMonth != null -> endMonth == null
        else -> period?.let { text -> currentPeriodMarkers.any { text.contains(it, ignoreCase = true) } } == true
    }

/**
 * Human readable period, e.g. `Sep 2025 – Aug 2026`, with the given abbreviated month names
 * (January first). Falls back to the free-text [ExperienceModel.period].
 */
fun ExperienceModel.formattedPeriod(presentLabel: String, monthNames: List<String> = EnglishMonthNames): String? {
    val from = startMonth ?: return period
    val to = endMonth?.label(monthNames) ?: presentLabel
    return "${from.label(monthNames)} – $to"
}

/**
 * Duration of the position in months, both bounds included (like LinkedIn does):
 * `Sep 2025 → Aug 2026` is 12 months.
 */
fun ExperienceModel.durationInMonths(now: YearMonth): Int? {
    val from = startMonth ?: return null
    val to = endMonth ?: now
    return (from.monthsUntil(to) + 1).takeIf { it > 0 }
}

/** Splits a number of months into whole years and remaining months. */
fun splitDuration(months: Int): Pair<Int, Int> = months / 12 to months % 12

/** Up to [max] initials of a name, ignoring anything between parentheses: `V-Labs` → `VL`. */
fun initialsOf(name: String, max: Int = 2): String =
    name.replace(Regex("\\(.*?\\)"), " ")
        .split(' ', '-', '_', '.', '/')
        .filter { it.firstOrNull()?.isLetterOrDigit() == true }
        .take(max)
        .joinToString(separator = "") { it.first().uppercase() }

/** A technology and the number of positions it was used in. */
data class TechnologyUsage(val name: String, val positions: Int)

/** Technologies of these positions, sorted by the number of positions they appear in, most used first. */
fun List<ExperienceModel>.technologyUsage(): List<TechnologyUsage> =
    flatMap { it.technologies.distinct() }
        .groupingBy { it }
        .eachCount()
        .map { (name, count) -> TechnologyUsage(name, count) }
        .sortedWith(compareByDescending<TechnologyUsage> { it.positions }.thenBy { it.name })

/**
 * Positions of the [ResumeModel.focus] career, and the other ones, summarized apart. Without focus, or
 * when no position uses it, [focus] is null and every position belongs to the main career.
 */
data class CareerSplit(
    val focus: String?,
    val focusPositions: List<ExperienceModel>,
    val otherPositions: List<ExperienceModel>,
) {
    /** Year of the first position of the focus career. */
    val focusSinceYear: Int? get() = focusPositions.mapNotNull { it.startMonth?.year }.minOrNull()

    /** Years covered by the other positions, e.g. `2007..2014`. */
    val otherYears: IntRange?
        get() {
            val years = otherPositions.flatMap { listOfNotNull(it.startMonth?.year, it.endMonth?.year) }
            return if (years.isEmpty()) null else years.min()..years.max()
        }

    /** Whether every other position ended before the focus career started. */
    val othersAreEarlier: Boolean
        get() {
            val focusStart = focusPositions.mapNotNull { it.startMonth }.minOrNull() ?: return false
            return otherPositions.all { other -> other.endMonth?.let { it <= focusStart } == true }
        }
}

/** Splits the positions between the [ResumeModel.focus] career and the other ones. */
fun ResumeModel.careerSplit(): CareerSplit {
    val focus = focus
    val (focusPositions, otherPositions) = experiences.partition { focus != null && focus in it.technologies }
    return if (focus == null || focusPositions.isEmpty()) {
        CareerSplit(focus = null, focusPositions = experiences, otherPositions = emptyList())
    } else {
        CareerSplit(focus = focus, focusPositions = focusPositions, otherPositions = otherPositions)
    }
}

/** Removes the scheme, `www.`, query and trailing slash of a URL: `https://www.github.com/me/` → `github.com/me`. */
fun displayUrl(url: String): String =
    url.substringAfter("://")
        .removePrefix("www.")
        .substringBefore('?')
        .trimEnd('/')
