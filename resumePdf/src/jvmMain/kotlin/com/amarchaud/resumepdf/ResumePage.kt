package com.amarchaud.resumepdf

import com.amarchaud.shared.data.ExperienceModel
import com.amarchaud.shared.data.Language
import com.amarchaud.shared.data.ResumeModel
import com.amarchaud.shared.data.careerSplit
import com.amarchaud.shared.data.currentYearMonth
import com.amarchaud.shared.data.displayUrl
import com.amarchaud.shared.data.durationInMonths
import com.amarchaud.shared.data.formattedPeriod
import com.amarchaud.shared.data.initialsOf
import com.amarchaud.shared.data.isCurrent
import com.amarchaud.shared.data.splitDuration
import com.amarchaud.shared.data.technologyUsage
import com.amarchaud.shared.ui.i18n.strings

/**
 * Print version of the resume, as an A4 HTML page: a hero banner with the key figures, the client logos, the profile
 * next to the skills, the detailed career of the resume focus (e.g. Android) and a summary of the earlier positions.
 *
 * It uses the same data and the same translations as the app, so that both always say the same thing.
 */
internal class ResumePage(
    private val resume: ResumeModel,
    private val language: Language,
    private val siteUrl: String,
) {
    private val strings = language.strings
    private val labels = PrintLabels.of(language)
    private val now = currentYearMonth()

    fun html(): String = buildString {
        val info = resume.info
        append("<!DOCTYPE html>\n<html lang=\"${labels.code}\">\n<head>\n<meta charset=\"utf-8\">\n")
        append("<title>${info.name.escaped()} · ${labels.documentTitle}</title>\n")
        append("<style>\n$STYLE</style>\n</head>\n<body>\n")
        hero()
        clients()
        intro()
        experience()
        footer()
        append("</body>\n</html>\n")
    }

    private fun StringBuilder.hero() {
        val info = resume.info
        val contact = resume.contact
        append("<header class=\"hero\">\n<div class=\"hero-top\">\n<div class=\"identity\">\n")
        info.photo?.let { append("<img class=\"photo\" src=\"assets/${it.escaped()}\" alt=\"\">\n") }
        append("<div>\n<h1>${info.name.escaped()}</h1>\n<p class=\"headline\">${info.job.escaped()}</p>\n")
        info.location?.let { append("<p class=\"location\">${icon(Icons.LOCATION)}${it.escaped()}</p>\n") }
        append("</div>\n</div>\n<ul class=\"contact\">\n")
        contactLine(Icons.MAIL, "mailto:${contact.email}", contact.email)
        contact.phone?.let { contactLine(Icons.PHONE, "tel:${it.filter { c -> !c.isWhitespace() }}", it) }
        contact.linkedInUrl?.let { contactLine(Icons.LINKEDIN, it, displayUrl(it), brand = true) }
        contact.githubUrl?.let { contactLine(Icons.GITHUB, it, displayUrl(it), brand = true) }
        contactLine(Icons.WEB, siteUrl, displayUrl(siteUrl))
        append("</ul>\n</div>\n")
        if (info.stats.isNotEmpty()) {
            append("<div class=\"stats\">\n")
            info.stats.take(3).forEach { append("<div class=\"stat\"><b>${it.value.escaped()}</b><span>${it.label.escaped()}</span></div>\n") }
            append("</div>\n")
        }
        append("</header>\n")
    }

    private fun StringBuilder.contactLine(iconPath: String, href: String, text: String, brand: Boolean = false) {
        append("<li><a href=\"${href.escaped()}\">${icon(iconPath, brand)}<span>${text.escaped()}</span></a></li>\n")
    }

    private fun StringBuilder.clients() {
        val clients = resume.experiences.filter { it.logo != null }.distinctBy { it.logo }
        if (clients.isEmpty()) return
        append("<section class=\"clients\">\n<span class=\"label\">${strings.aboutTrustedBy.escaped()}</span>\n<div class=\"logos\">\n")
        clients.forEach { append("<img src=\"assets/${it.logo!!.escaped()}\" alt=\"${it.company.escaped()}\">\n") }
        append("</div>\n</section>\n")
    }

    private fun StringBuilder.intro() {
        val about = resume.about
        append("<section class=\"intro\">\n<div>\n")
        if (about != null && about.summary.isNotEmpty()) {
            title(labels.profile)
            about.summary.forEachIndexed { index, paragraph ->
                append(if (index == 0) "<p class=\"lead\">" else "<p>").append(paragraph.escaped()).append("</p>\n")
            }
        }
        if (about != null && about.strengths.isNotEmpty()) {
            append("<div class=\"block\">\n")
            title(strings.aboutStrengths)
            append("<ul class=\"checks\">\n")
            about.strengths.forEach { append("<li>${it.escaped()}</li>\n") }
            append("</ul>\n</div>\n")
        }
        append("</div>\n<aside>\n")
        if (resume.skills.isNotEmpty()) {
            append("<div class=\"block\">\n")
            title(strings.skillsTitle)
            append("<div class=\"chips\">")
            resume.skills.forEach { append("<span class=\"chip\">${it.escaped()}</span>") }
            append("</div>\n</div>\n")
        }
        if (resume.languages.isNotEmpty()) {
            append("<div class=\"block\">\n")
            title(strings.aboutLanguages)
            append("<ul class=\"facts\">\n")
            resume.languages.forEach { append("<li><b>${it.name.escaped()}</b><span>${it.level.escaped()}</span></li>\n") }
            append("</ul>\n</div>\n")
        }
        if (resume.education.isNotEmpty()) {
            append("<div class=\"block\">\n")
            title(strings.aboutEducation)
            append("<ul class=\"facts\">\n")
            resume.education.forEach { education ->
                append("<li><b>${education.school.escaped()}</b><span>${education.degree.escaped()}</span>")
                education.period?.let { append("<span>${it.escaped()}</span>") }
                append("</li>\n")
            }
            append("</ul>\n</div>\n")
        }
        append("</aside>\n</section>\n")
    }

    private fun StringBuilder.experience() {
        val split = resume.careerSplit()
        append("<section class=\"experience\">\n")
        title(labels.experience)
        split.focusPositions.forEachIndexed { index, experience -> position(experience, index) }
        if (split.otherPositions.isNotEmpty()) {
            val focus = split.focus
            val years = split.otherYears?.let { if (it.first == it.last) "${it.first}" else "${it.first} – ${it.last}" }
            val title = if (focus != null && split.othersAreEarlier) strings.skillsEarlierTitle(focus) else strings.skillsOtherTitle
            append("<div class=\"earlier\">\n")
            title(listOfNotNull(title, years).joinToString(" · "))
            val usage = split.otherPositions.technologyUsage()
            usage.firstOrNull()?.let { main ->
                val summary = strings.skillsOtherSummary(split.otherPositions.size, main.name, main.positions == split.otherPositions.size)
                append("<p class=\"summary\">${summary.escaped()}")
                usage.drop(1).forEach { append(" <span class=\"chip small\">${it.name.escaped()}</span>") }
                append("</p>\n")
            }
            append("<ul>\n")
            split.otherPositions.forEach { experience ->
                append("<li><span><b>${experience.company.escaped()}</b> · ${experience.role.escaped()}</span>")
                experience.periodLabel(withDuration = false)?.let { append("<span class=\"when\">${it.escaped()}</span>") }
                append("</li>\n")
            }
            append("</ul>\n</div>\n")
        }
        append("</section>\n")
    }

    private fun StringBuilder.position(experience: ExperienceModel, index: Int) {
        append("<article class=\"job\">\n")
        val logo = experience.logo
        if (logo != null) {
            append("<div class=\"logo\"><img src=\"assets/${logo.escaped()}\" alt=\"\"></div>\n")
        } else {
            append("<div class=\"logo mono tone${index % 3}\">${initialsOf(experience.company).escaped()}</div>\n")
        }
        append("<div>\n<div class=\"job-head\">\n<div>\n<h3>${experience.company.escaped()}")
        if (experience.isCurrent) append("<span class=\"badge\">${strings.experienceCurrent.escaped()}</span>")
        append("</h3>\n<p class=\"role\">${experience.role.escaped()}</p>\n</div>\n<div class=\"when\">")
        experience.periodLabel(withDuration = true)?.let { append(it.escaped()) }
        experience.location?.let { append("<br>${it.escaped()}") }
        append("</div>\n</div>\n")
        experience.description?.let { append("<p class=\"desc\">${it.escaped()}</p>\n") }
        if (experience.highlights.isNotEmpty()) {
            append("<ul>\n")
            experience.highlights.forEach { append("<li>${it.escaped()}</li>\n") }
            append("</ul>\n")
        }
        if (experience.technologies.isNotEmpty()) {
            append("<div class=\"tech\">")
            experience.technologies.forEach { append("<span class=\"chip small\">${it.escaped()}</span>") }
            append("</div>\n")
        }
        append("</div>\n</article>\n")
    }

    private fun StringBuilder.footer() {
        val updated = "${strings.monthNames[now.month.ordinal]} ${now.year}"
        append("<footer>\n<a href=\"${siteUrl.escaped()}\">${labels.interactiveVersion.escaped()} ${displayUrl(siteUrl).escaped()}</a>\n")
        append("<span>${labels.updated.escaped()} ${updated.escaped()}</span>\n</footer>\n")
    }

    private fun StringBuilder.title(text: String) {
        append("<h2>${text.escaped()}</h2>\n")
    }

    /** `sept. 2025 – août 2026 · 1 an`, as in the app. */
    private fun ExperienceModel.periodLabel(withDuration: Boolean): String? {
        val period = formattedPeriod(strings.experiencePresent, strings.monthNames) ?: return null
        val months = durationInMonths(now)?.takeIf { withDuration } ?: return period
        val (years, remainingMonths) = splitDuration(months)
        val duration = listOfNotNull(
            years.takeIf { it > 0 }?.let(strings.durationYears),
            remainingMonths.takeIf { it > 0 }?.let(strings.durationMonths),
        ).joinToString(" ")
        return if (duration.isEmpty()) period else "$period · $duration"
    }
}

/** Labels only used by the print version; the others come from the app translations. */
internal class PrintLabels(
    val code: String,
    val documentTitle: String,
    val profile: String,
    val experience: String,
    val interactiveVersion: String,
    val updated: String,
) {
    companion object {
        fun of(language: Language): PrintLabels = when (language) {
            Language.English -> PrintLabels(
                code = "en",
                documentTitle = "Resume",
                profile = "Profile",
                experience = "Professional experience",
                interactiveVersion = "Interactive version:",
                updated = "Updated",
            )
            Language.French -> PrintLabels(
                code = "fr",
                documentTitle = "CV",
                profile = "Profil",
                experience = "Expérience professionnelle",
                interactiveVersion = "Version interactive :",
                updated = "Mis à jour en",
            )
        }
    }
}

private fun String.escaped(): String =
    replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")

/** An inline SVG icon: Material Symbols (`0 -960 960 960` viewBox) or a brand icon from Simple Icons (24 x 24). */
private fun icon(path: String, brand: Boolean = false): String {
    val viewBox = if (brand) "0 0 24 24" else "0 -960 960 960"
    return "<svg viewBox=\"$viewBox\" aria-hidden=\"true\"><path d=\"$path\"/></svg>"
}

/** Same icons as the app (Material Symbols Rounded, Apache 2.0; Simple Icons, CC0). */
private object Icons {
    const val MAIL = "M160-160q-33 0-56.5-23.5T80-240v-480q0-33 23.5-56.5T160-800h640q33 0 56.5 23.5T880-720v480q0 33-23.5 56.5T800-160H160Zm640-480L501-453q-5 3-10.5 4.5T480-447q-5 0-10.5-1.5T459-453L160-640v400h640v-400ZM480-520l320-200H160l320 200ZM160-640v10-59 1-32 32-.5 58.5-10 400-400Z"
    const val PHONE = "M798-120q-125 0-247-54.5T329-329Q229-429 174.5-551T120-798q0-18 12-30t30-12h162q14 0 25 9.5t13 22.5l26 140q2 16-1 27t-11 19l-97 98q20 37 47.5 71.5T387-386q31 31 65 57.5t72 48.5l94-94q9-9 23.5-13.5T670-390l138 28q14 4 23 14.5t9 23.5v162q0 18-12 30t-30 12Z"
    const val LOCATION = "M480-186q122-112 181-203.5T720-552q0-109-69.5-178.5T480-800q-101 0-170.5 69.5T240-552q0 71 59 162.5T480-186Zm0 79q-14 0-28-5t-25-15q-65-60-115-117t-83.5-110.5q-33.5-53.5-51-103T160-552q0-150 96.5-239T480-880q127 0 223.5 89T800-552q0 45-17.5 94.5t-51 103Q698-301 648-244T533-127q-11 10-25 15t-28 5Zm0-373q33 0 56.5-23.5T560-560q0-33-23.5-56.5T480-640q-33 0-56.5 23.5T400-560q0 33 23.5 56.5T480-480Z"
    const val WEB = "M480-80q-82 0-155-31.5t-127.5-86Q143-252 111.5-325T80-480q0-83 31.5-155.5t86-127Q252-817 325-848.5T480-880q83 0 155.5 31.5t127 86q54.5 54.5 86 127T880-480q0 82-31.5 155t-86 127.5q-54.5 54.5-127 86T480-80Zm0-82q26-36 45-75t31-83H404q12 44 31 83t45 75Zm-104-16q-18-33-31.5-68.5T322-320H204q29 50 72.5 87t99.5 55Zm208 0q56-18 99.5-55t72.5-87H638q-9 38-22.5 73.5T584-178ZM170-400h136q-3-20-4.5-39.5T300-480q0-21 1.5-40.5T306-560H170q-5 20-7.5 39.5T160-480q0 21 2.5 40.5T170-400Zm216 0h188q3-20 4.5-39.5T580-480q0-21-1.5-40.5T574-560H386q-3 20-4.5 39.5T380-480q0 21 1.5 40.5T386-400Zm268 0h136q5-20 7.5-39.5T800-480q0-21-2.5-40.5T790-560H654q3 20 4.5 39.5T660-480q0 21-1.5 40.5T654-400Zm-16-240h118q-29-50-72.5-87T584-782q18 33 31.5 68.5T638-640Zm-234 0h152q-12-44-31-83t-45-75q-26 36-45 75t-31 83Zm-200 0h118q9-38 22.5-73.5T376-782q-56 18-99.5 55T204-640Z"
    const val LINKEDIN = "M20.447 20.452h-3.554v-5.569c0-1.328-.027-3.037-1.852-3.037-1.853 0-2.136 1.445-2.136 2.939v5.667H9.351V9h3.414v1.561h.046c.477-.9 1.637-1.85 3.37-1.85 3.601 0 4.267 2.37 4.267 5.455v6.286zM5.337 7.433c-1.144 0-2.063-.926-2.063-2.065 0-1.138.92-2.063 2.063-2.063 1.14 0 2.064.925 2.064 2.063 0 1.139-.925 2.065-2.064 2.065zm1.782 13.019H3.555V9h3.564v11.452zM22.225 0H1.771C.792 0 0 .774 0 1.729v20.542C0 23.227.792 24 1.771 24h20.451C23.2 24 24 23.227 24 22.271V1.729C24 .774 23.2 0 22.222 0h.003z"
    const val GITHUB = "M12 .297c-6.63 0-12 5.373-12 12 0 5.303 3.438 9.8 8.205 11.385.6.113.82-.258.82-.577 0-.285-.01-1.04-.015-2.04-3.338.724-4.042-1.61-4.042-1.61C4.422 18.07 3.633 17.7 3.633 17.7c-1.087-.744.084-.729.084-.729 1.205.084 1.838 1.236 1.838 1.236 1.07 1.835 2.809 1.305 3.495.998.108-.776.417-1.305.76-1.605-2.665-.3-5.466-1.332-5.466-5.93 0-1.31.465-2.38 1.235-3.22-.135-.303-.54-1.523.105-3.176 0 0 1.005-.322 3.3 1.23.96-.267 1.98-.399 3-.405 1.02.006 2.04.138 3 .405 2.28-1.552 3.285-1.23 3.285-1.23.645 1.653.24 2.873.12 3.176.765.84 1.23 1.91 1.23 3.22 0 4.61-2.805 5.625-5.475 5.92.42.36.81 1.096.81 2.22 0 1.606-.015 2.896-.015 3.286 0 .315.21.69.825.57C20.565 22.092 24 17.592 24 12.297c0-6.627-5.373-12-12-12"
}

/** A4, printed with backgrounds: the hero bleeds to the edges of the first page. */
private val STYLE = """
@font-face { font-family: "Jakarta"; src: url("assets/plus_jakarta_sans.ttf") format("truetype"); font-weight: 200 800; }
@page { size: A4; margin: 12mm 0 12mm 0; }
@page :first { margin-top: 0; }
* { box-sizing: border-box; -webkit-print-color-adjust: exact; print-color-adjust: exact; }
:root { --ink: #161A2B; --text: #343A52; --muted: #646A80; --indigo: #3D35B5; --accent: #7C6CF6; --line: #E5E7F1; --chip: #EEF0FA; }
html, body { margin: 0; padding: 0; }
body { font-family: "Jakarta", sans-serif; font-size: 8.1pt; line-height: 1.38; color: var(--ink); }
a { color: inherit; text-decoration: none; }
h2 { display: flex; align-items: center; gap: 2mm; margin: 0 0 2.2mm; break-after: avoid; font-size: 7.6pt; font-weight: 800; letter-spacing: 1.1pt; text-transform: uppercase; color: var(--indigo); }
h2::before { content: ""; width: 5mm; height: 0.6mm; border-radius: 1mm; background: var(--accent); }
p { margin: 0; }

.hero { color: #FFFFFF; padding: 9mm 15mm 7mm;
  background: radial-gradient(circle at 88% 0%, rgba(124, 58, 237, 0.55), transparent 45%),
              radial-gradient(circle at 0% 100%, rgba(14, 116, 144, 0.35), transparent 40%),
              linear-gradient(135deg, #19154C, #3730A6); }
.hero-top { display: flex; justify-content: space-between; align-items: center; gap: 8mm; }
.identity { display: flex; align-items: center; gap: 5mm; flex: 1; min-width: 0; }
.photo { flex: none; width: 23mm; height: 23mm; border-radius: 50%; object-fit: cover; border: 0.8mm solid rgba(255, 255, 255, 0.85); }
h1 { margin: 0; font-size: 22pt; font-weight: 800; letter-spacing: -0.3pt; line-height: 1.1; white-space: nowrap; }
.headline { margin-top: 1.2mm; font-size: 10.5pt; white-space: nowrap; font-weight: 500; color: rgba(255, 255, 255, 0.85); }
.location { display: flex; align-items: center; gap: 1.4mm; margin-top: 2mm; font-size: 8.4pt; color: rgba(255, 255, 255, 0.75); }
.location svg { width: 3.4mm; height: 3.4mm; fill: currentColor; }
.contact { flex: none; width: 64mm; list-style: none; margin: 0; padding: 0; display: grid; gap: 1.4mm; font-size: 7.9pt; }
.contact a { display: flex; align-items: center; gap: 2.2mm; color: #FFFFFF; }
.contact svg { flex: none; width: 3.6mm; height: 3.6mm; fill: currentColor; opacity: 0.9; }
.stats { display: grid; grid-template-columns: repeat(3, 1fr); gap: 3mm; margin-top: 5mm; }
.stat { padding: 2.2mm 3.6mm; border-radius: 3mm; background: rgba(255, 255, 255, 0.1); border: 0.25mm solid rgba(255, 255, 255, 0.22); }
.stat b { display: block; font-size: 14pt; font-weight: 800; line-height: 1.15; }
.stat span { font-size: 7.6pt; color: rgba(255, 255, 255, 0.8); }

.clients { display: flex; align-items: center; gap: 4.5mm; padding: 3.4mm 15mm; border-bottom: 0.25mm solid var(--line); }
.clients .label { flex: none; font-size: 6.8pt; font-weight: 800; letter-spacing: 0.8pt; text-transform: uppercase; color: var(--muted); }
.clients .logos { display: flex; flex-wrap: wrap; align-items: center; gap: 2mm 4mm; }
.clients img { height: 4.2mm; max-width: 19mm; object-fit: contain; }

.intro { display: grid; grid-template-columns: 1fr 55mm; gap: 9mm; padding: 5mm 15mm 1mm; }
.intro p { color: var(--text); margin-bottom: 1.5mm; }
.intro p.lead { font-size: 9pt; font-weight: 700; color: var(--ink); }
.block + .block, div > .block { margin-top: 4mm; }
aside .block:first-child { margin-top: 0; }
.checks { list-style: none; margin: 0; padding: 0; }
.checks li { position: relative; margin-bottom: 1mm; padding-left: 5mm; color: var(--text); }
.checks li::before { content: ""; position: absolute; left: 0.4mm; top: 1.15mm; width: 2.4mm; height: 1.2mm;
  border-left: 0.5mm solid var(--accent); border-bottom: 0.5mm solid var(--accent); transform: rotate(-45deg); }
.chips { display: flex; flex-wrap: wrap; gap: 1.3mm; }
.chip { display: inline-block; padding: 0.7mm 2.2mm; border-radius: 10mm; background: var(--chip); color: var(--indigo); font-size: 7.3pt; font-weight: 600; }
.chip.small { padding: 0.3mm 1.7mm; font-size: 6.5pt; }
.facts { list-style: none; margin: 0; padding: 0; }
.facts li { margin-bottom: 1.4mm; }
.facts b { display: block; font-weight: 700; }
.facts span { display: block; font-size: 7.8pt; color: var(--muted); }

.experience { padding: 3.5mm 15mm 0; }
.job { position: relative; padding: 2.6mm 0 2.6mm 16.5mm; border-top: 0.25mm solid var(--line); }
.experience h2 + .job { border-top: 0; padding-top: 0.5mm; }
.experience h2 + .job .logo { top: 0.7mm; }
.logo { position: absolute; left: 0; top: 2.8mm; display: flex; align-items: center; justify-content: center; width: 13mm; height: 9mm; padding: 0.9mm;
  border-radius: 2.4mm; border: 0.25mm solid var(--line); background: #FFFFFF; }
.logo img { max-width: 100%; max-height: 100%; object-fit: contain; }
.logo.mono { border: 0; font-size: 8.5pt; font-weight: 800; }
.tone0 { background: #E8E6FB; color: #3D35B5; } .tone1 { background: #E1F1F5; color: #0E6175; } .tone2 { background: #F3E8FB; color: #6B2FA3; }
.job-head { display: flex; justify-content: space-between; align-items: flex-start; gap: 4mm; break-inside: avoid; break-after: avoid; }
.job h3 { margin: 0; font-size: 9.6pt; font-weight: 800; }
.badge { display: inline-block; margin-left: 2mm; padding: 0.3mm 1.8mm; border-radius: 10mm; background: #E3F6EC; color: #16794A;
  font-size: 6.4pt; font-weight: 800; letter-spacing: 0.6pt; text-transform: uppercase; vertical-align: 1.2pt; }
.role { margin-top: 0.2mm; font-size: 8.3pt; font-weight: 600; color: var(--indigo); }
.job-head .when { flex: none; text-align: right; font-size: 7.7pt; color: var(--muted); }
.desc { margin-top: 1.1mm; color: var(--text); break-after: avoid; }
.job ul { margin: 0.8mm 0 0; padding-left: 4mm; }
.job li { margin-bottom: 0.3mm; color: var(--text); break-inside: avoid; }
.job li::marker { color: var(--accent); }
.tech { display: flex; flex-wrap: wrap; gap: 1mm; margin-top: 1.3mm; break-inside: avoid; }

.earlier { margin-top: 3.5mm; padding: 3.5mm 4.5mm 2.5mm; border-radius: 3mm; background: #F6F7FB; break-inside: avoid; }
.earlier .summary { margin-bottom: 2mm; font-weight: 700; }
.earlier .summary .chip { margin-left: 0.6mm; font-weight: 600; }
.earlier ul { list-style: none; margin: 0; padding: 0; }
.earlier li { display: flex; justify-content: space-between; gap: 4mm; padding: 0.9mm 0; border-top: 0.25mm solid var(--line); color: var(--text); }
.earlier li:first-child { border-top: 0; }
.earlier .when { flex: none; color: var(--muted); font-size: 7.7pt; }

footer { display: flex; justify-content: space-between; margin: 6mm 15mm 0; padding-top: 2.6mm; border-top: 0.25mm solid var(--line);
  font-size: 7.3pt; color: var(--muted); break-inside: avoid; }
footer a { color: var(--indigo); font-weight: 600; }
""".trimStart()
