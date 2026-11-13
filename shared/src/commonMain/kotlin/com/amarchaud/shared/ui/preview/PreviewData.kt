package com.amarchaud.shared.ui.preview

import com.amarchaud.shared.data.AboutModel
import com.amarchaud.shared.data.ContactModel
import com.amarchaud.shared.data.EducationModel
import com.amarchaud.shared.data.ExperienceModel
import com.amarchaud.shared.data.InfoModel
import com.amarchaud.shared.data.LanguageModel
import com.amarchaud.shared.data.ResumeModel
import com.amarchaud.shared.data.StatModel

/** Fake resume used by Compose previews. */
internal object PreviewData {
    val resume = ResumeModel(
        info = InfoModel(
            name = "Alex Martin",
            job = "Senior Android Developer",
            location = "Lyon, France",
            stats = listOf(
                StatModel("12", "Years in software"),
                StatModel("8", "Years on Android"),
                StatModel("10M+", "Downloads"),
            ),
        ),
        about = AboutModel(
            summary = listOf(
                "Android developer focused on polished, accessible and fast apps.",
                "I love design systems, Jetpack Compose and well tested architectures.",
            ),
            strengths = listOf(
                "Jetpack Compose migrations",
                "Clean Architecture and MVVM",
                "Production quality and monitoring",
            ),
        ),
        experiences = listOf(
            ExperienceModel(
                company = "Acme Mobile",
                role = "Lead Android Developer",
                start = "2024-03",
                location = "Lyon, France",
                description = "Leading the Android team of a consumer app.",
                highlights = listOf("Migrated the app to Jetpack Compose", "Built a multi-theme design system"),
                technologies = listOf("Kotlin", "Jetpack Compose", "Hilt", "Coroutines & Flow"),
                appId = "com.example.app",
            ),
            ExperienceModel(
                company = "Globex Corporation",
                role = "Android Developer",
                start = "2020-01",
                end = "2024-02",
                location = "Paris, France",
                highlights = listOf("Shipped the new onboarding", "Reduced cold start time by 40%"),
                technologies = listOf("Kotlin", "MVVM", "Room", "Retrofit"),
            ),
            ExperienceModel(
                company = "Initech",
                role = "Software Engineer",
                period = "2014 - 2019",
                technologies = listOf("Java", "C++"),
            ),
        ),
        skills = listOf("Kotlin", "Jetpack Compose", "Coroutines & Flow", "Clean Architecture", "Hilt", "Room"),
        education = listOf(EducationModel("Université de Lyon", "Master's degree, Computer Science", "2009 – 2014")),
        languages = listOf(LanguageModel("French", "Native"), LanguageModel("English", "Fluent")),
        contact = ContactModel(
            email = "alex.martin@example.com",
            githubUrl = "https://github.com/example",
            linkedInUrl = "https://www.linkedin.com/in/example",
        ),
    )
}
