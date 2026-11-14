package com.amarchaud.shared.data

import kotlinx.serialization.Serializable

/**
 * Root of `res/raw/resume_template.json`.
 *
 * Every field added after the first version of the template is optional, so older JSON files keep
 * being parsed.
 */
@Serializable
data class ResumeModel(
    val info: InfoModel,
    val about: AboutModel? = null,
    val experiences: List<ExperienceModel>,
    val skills: List<String>,
    /**
     * Main technology of the career, e.g. `Android`: the usage chart only counts the positions using it,
     * and the other positions are summarized apart.
     */
    val focus: String? = null,
    val education: List<EducationModel> = emptyList(),
    val languages: List<LanguageModel> = emptyList(),
    val contact: ContactModel,
)

@Serializable
data class InfoModel(
    val name: String,
    val job: String,
    /** Shorter job title, for the collapsed header (e.g. `Android Developer`); [job] when missing. */
    val shortJob: String? = null,
    val location: String? = null,
    /** Profile photo, relative to `composeResources/files`: `photo.jpg`. Initials are shown without it. */
    val photo: String? = null,
    /** Key figures shown in the header. When empty, figures are computed from the resume. */
    val stats: List<StatModel> = emptyList(),
)

@Serializable
data class StatModel(
    val value: String,
    val label: String,
)

@Serializable
data class AboutModel(
    val summary: List<String> = emptyList(),
    val strengths: List<String> = emptyList(),
)

@Serializable
data class ExperienceModel(
    val company: String,
    val role: String,
    /** Start month, formatted as `yyyy-MM`. Takes precedence over [period]. */
    val start: String? = null,
    /** End month, formatted as `yyyy-MM`. `null` with a [start] means "present". */
    val end: String? = null,
    /** Free-text period, used when [start] is not provided. */
    val period: String? = null,
    val location: String? = null,
    val description: String? = null,
    val highlights: List<String> = emptyList(),
    val technologies: List<String> = emptyList(),
    /** Google Play application id, used to open the store listing. */
    val appId: String? = null,
    /** Company logo, relative to `composeResources/files`: `logos/acme.png`. */
    val logo: String? = null,
)

@Serializable
data class EducationModel(
    val school: String,
    val degree: String,
    val period: String? = null,
)

@Serializable
data class LanguageModel(
    val name: String,
    val level: String,
)

@Serializable
data class ContactModel(
    val email: String,
    val phone: String? = null,
    val githubUrl: String? = null,
    val linkedInUrl: String? = null,
    /** The resume as a PDF, offered for download from the header and the contact screen. */
    val resumePdfUrl: String? = null,
    /** Short call-to-action displayed on the contact screen. */
    val message: String? = null,
)
