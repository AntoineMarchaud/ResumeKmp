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

/** Technologies sorted by the number of positions they appear in, most used first. */
fun ResumeModel.technologyUsage(): List<TechnologyUsage> =
    experiences
        .flatMap { it.technologies.distinct() }
        .groupingBy { it }
        .eachCount()
        .map { (name, count) -> TechnologyUsage(name, count) }
        .sortedWith(compareByDescending<TechnologyUsage> { it.positions }.thenBy { it.name })

/** Removes the scheme, `www.`, query and trailing slash of a URL: `https://www.github.com/me/` → `github.com/me`. */
fun displayUrl(url: String): String =
    url.substringAfter("://")
        .removePrefix("www.")
        .substringBefore('?')
        .trimEnd('/')
