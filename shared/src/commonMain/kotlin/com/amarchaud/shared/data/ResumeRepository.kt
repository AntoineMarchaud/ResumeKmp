package com.amarchaud.shared.data

import com.amarchaud.shared.resources.Res

/** Source of the resume, bundled with the app as a Compose resource. */
class ResumeRepository {

    suspend fun loadResume(): ResumeModel =
        parseResume(Res.readBytes(RESUME_FILE).decodeToString())

    private companion object {
        const val RESUME_FILE = "files/resume_template.json"
    }
}
