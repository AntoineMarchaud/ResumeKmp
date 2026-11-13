package com.amarchaud.shared.data

/** Languages the resume can be written in, each one with its own JSON file. */
enum class Language(val resumeFile: String) {
    English("files/resume_template.json"),
    French("files/resume_template_fr.json"),
}
