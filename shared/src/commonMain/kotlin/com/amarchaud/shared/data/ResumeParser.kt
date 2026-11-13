package com.amarchaud.shared.data

import kotlinx.serialization.json.Json

private val resumeJson = Json { ignoreUnknownKeys = true }

/** Parses the content of a resume JSON file. */
fun parseResume(content: String): ResumeModel = resumeJson.decodeFromString(content)
