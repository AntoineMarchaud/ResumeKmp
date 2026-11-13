package com.amarchaud.shared.data

import com.amarchaud.shared.resources.Res

/** Source of the resume, bundled with the app as Compose resources, one JSON file per language. */
class ResumeRepository {

    suspend fun loadResume(language: Language): ResumeModel =
        parseResume(Res.readBytes(language.resumeFile).decodeToString())

    /**
     * Every available translation. English is required; the other languages are optional, so a
     * resume written in a single language keeps working.
     */
    suspend fun loadResumes(): Map<Language, ResumeModel> = buildMap {
        put(Language.English, loadResume(Language.English))
        Language.entries.filter { it != Language.English }.forEach { language ->
            runCatching { loadResume(language) }.getOrNull()?.let { put(language, it) }
        }
    }
}
