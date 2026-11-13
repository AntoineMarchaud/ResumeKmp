package com.amarchaud.shared.ui.i18n

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.intl.Locale
import com.amarchaud.shared.data.Language

/** Texts of the interface in this language. */
val Language.strings: Strings
    get() = when (this) {
        Language.English -> EnglishStrings
        Language.French -> FrenchStrings
    }

/** French when the device or the browser prefers French, English otherwise. */
fun detectLanguage(): Language = if (Locale.current.language == "fr") Language.French else Language.English

/**
 * Texts of the interface. Plain Kotlin rather than Compose resources: those always follow the
 * system language, while the resume lets the visitor switch language at runtime.
 */
@Immutable
data class Strings(
    val languageName: String,
    val sectionAbout: String,
    val sectionExperience: String,
    val sectionSkills: String,
    val sectionContact: String,
    val headerDynamicColor: String,
    val statPositions: String,
    val statApps: String,
    val statSkills: String,
    val aboutTitle: String,
    val aboutStrengths: String,
    val aboutEducation: String,
    val aboutLanguages: String,
    val experienceTitle: String,
    val experienceHint: String,
    val experienceCurrent: String,
    val experiencePresent: String,
    val experienceExpand: String,
    val experienceCollapse: String,
    val experienceViewOnPlay: String,
    val skillsTitle: String,
    val skillsSubtitle: String,
    val skillsUsageTitle: String,
    val skillsUsageSubtitle: String,
    val contactTitle: String,
    val contactCtaTitle: String,
    val contactCtaDefault: String,
    val contactSendEmail: String,
    val contactEmail: String,
    val contactPhone: String,
    val contactLinkedIn: String,
    val contactGitHub: String,
    val contactCopyEmail: String,
    val contactEmailCopied: String,
    val positionsCount: (Int) -> String,
    val durationYears: (Int) -> String,
    val durationMonths: (Int) -> String,
    /** Abbreviated month names, January first. */
    val monthNames: List<String>,
)

val EnglishStrings = Strings(
    languageName = "English",
    sectionAbout = "About",
    sectionExperience = "Experience",
    sectionSkills = "Skills",
    sectionContact = "Contact",
    headerDynamicColor = "Material You colors",
    statPositions = "Positions",
    statApps = "Apps on Google Play",
    statSkills = "Skills",
    aboutTitle = "About me",
    aboutStrengths = "What I bring",
    aboutEducation = "Education",
    aboutLanguages = "Languages",
    experienceTitle = "Experience",
    experienceHint = "Open a position to see the details",
    experienceCurrent = "Current",
    experiencePresent = "Present",
    experienceExpand = "Show details",
    experienceCollapse = "Hide details",
    experienceViewOnPlay = "View on Google Play",
    skillsTitle = "Skills",
    skillsSubtitle = "Technologies and practices I work with",
    skillsUsageTitle = "Most used in production",
    skillsUsageSubtitle = "Share of positions where each technology was used",
    contactTitle = "Contact",
    contactCtaTitle = "Let’s work together",
    contactCtaDefault = "Feel free to reach out by email or on social networks.",
    contactSendEmail = "Send an email",
    contactEmail = "Email",
    contactPhone = "Phone",
    contactLinkedIn = "LinkedIn",
    contactGitHub = "GitHub",
    contactCopyEmail = "Copy email address",
    contactEmailCopied = "Email address copied",
    positionsCount = { if (it == 1) "1 position" else "$it positions" },
    durationYears = { if (it == 1) "1 yr" else "$it yrs" },
    durationMonths = { if (it == 1) "1 mo" else "$it mos" },
    monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"),
)

val FrenchStrings = Strings(
    languageName = "Français",
    sectionAbout = "Profil",
    sectionExperience = "Parcours",
    sectionSkills = "Compétences",
    sectionContact = "Contact",
    headerDynamicColor = "Couleurs Material You",
    statPositions = "Postes",
    statApps = "Apps sur Google Play",
    statSkills = "Compétences",
    aboutTitle = "À propos",
    aboutStrengths = "Ce que j’apporte",
    aboutEducation = "Formation",
    aboutLanguages = "Langues",
    experienceTitle = "Expérience",
    experienceHint = "Ouvrez un poste pour voir le détail",
    experienceCurrent = "En cours",
    experiencePresent = "Aujourd’hui",
    experienceExpand = "Voir le détail",
    experienceCollapse = "Masquer le détail",
    experienceViewOnPlay = "Voir sur Google Play",
    skillsTitle = "Compétences",
    skillsSubtitle = "Technologies et pratiques que j’utilise",
    skillsUsageTitle = "Les plus utilisées en production",
    skillsUsageSubtitle = "Part des postes où chaque technologie a été utilisée",
    contactTitle = "Contact",
    contactCtaTitle = "Travaillons ensemble",
    contactCtaDefault = "N’hésitez pas à me contacter par email ou sur les réseaux.",
    contactSendEmail = "M’envoyer un email",
    contactEmail = "Email",
    contactPhone = "Téléphone",
    contactLinkedIn = "LinkedIn",
    contactGitHub = "GitHub",
    contactCopyEmail = "Copier l’adresse email",
    contactEmailCopied = "Adresse email copiée",
    positionsCount = { if (it <= 1) "$it poste" else "$it postes" },
    durationYears = { if (it <= 1) "$it an" else "$it ans" },
    durationMonths = { "$it mois" },
    monthNames = listOf("janv.", "févr.", "mars", "avr.", "mai", "juin", "juil.", "août", "sept.", "oct.", "nov.", "déc."),
)

val LocalStrings = staticCompositionLocalOf { EnglishStrings }
