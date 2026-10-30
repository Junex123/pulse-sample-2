package com.example.data.model

data class ExtensionDescriptor(
    val id: String,
    val version: String,
    val name: String,
    val tagLine: String,
    val description: String,
    val category: String, // Nutrition, Recovery, Habits, Analytics, Cardio, Anatomy
    val tier: String = "Free", // Free, Pro Preview
    val isComingSoon: Boolean = false,
    val permissionsRequired: List<String>,
    val dependencies: List<String> = emptyList(),
    val featureList: List<String>,
    val defaultEnabled: Boolean = false
)

data class ExtensionRuntimeState(
    val id: String,
    val isInstalled: Boolean,
    val isEnabled: Boolean,
    val installedAtMillis: Long = System.currentTimeMillis()
)

val EXTENSION_REGISTRY = listOf(
    ExtensionDescriptor(
        id = "ext_exercise_studio_3d",
        version = "1.0.0",
        name = "3D Exercise Studio",
        tagLine = "Interactive 3D exercise demonstrations & technique",
        description = "Explore exercises through animated 3D demonstrations and learn proper technique.",
        category = "Training",
        tier = "Free",
        permissionsRequired = listOf("3D Graphics Hardware Acceleration"),
        featureList = listOf(
            "Interactive 360° 3D human exercise demonstrations",
            "Primary and secondary muscle engagement highlighting",
            "Adjustable playback speed (0.5x, 0.75x, 1x, 1.25x) & angle controls",
            "Direct integration with active workouts and routine builder"
        ),
        defaultEnabled = true
    ),
    ExtensionDescriptor(
        id = "ext_nutrition",
        version = "1.2.0",
        name = "Nutrition Lab",
        tagLine = "Calorie & Macronutrient tracking engine",
        description = "Log meals with real-time breakdowns of protein, carbohydrates, and fats. Set custom daily targets to support hypertrophy or fat loss.",
        category = "Nutrition",
        tier = "Free",
        permissionsRequired = listOf("Local Health Data Storage", "Nutrition Journaling Access"),
        featureList = listOf(
            "Daily calorie & macro goal tracker",
            "Breakfast, Lunch, Dinner & Snack meal logging",
            "Automatic macro ratio percentages",
            "Interactive dashboard macro widget"
        )
    ),
    ExtensionDescriptor(
        id = "ext_recovery",
        version = "1.1.4",
        name = "Recovery Studio",
        tagLine = "Sleep quality & training readiness insights",
        description = "Assess nervous system readiness, track sleep duration, and log muscle soreness to prevent overtraining before your heavy sessions.",
        category = "Recovery",
        tier = "Free",
        permissionsRequired = listOf("Local Health Data Storage", "Readiness Assessment Consent"),
        featureList = listOf(
            "Subjective readiness score (0-100)",
            "Sleep duration & quality log",
            "Targeted muscle soreness tracker",
            "Dynamic recovery dashboard status card"
        )
    ),
    ExtensionDescriptor(
        id = "ext_habits",
        version = "2.0.1",
        name = "Habit Builder",
        tagLine = "Consistent daily hydration & lifestyle routines",
        description = "Build unbroken streaks for hydration, mobility, post-workout protein, and evening wind-down habits.",
        category = "Habits",
        tier = "Free",
        permissionsRequired = listOf("Local Notification Reminders"),
        featureList = listOf(
            "Quick-tap habit completion counters",
            "Hydration milestone logging (ml / glasses)",
            "Active streak counters with milestones",
            "Compact daily habit card on Home dashboard"
        )
    ),
    ExtensionDescriptor(
        id = "ext_analytics",
        version = "1.0.8",
        name = "Progress Analytics",
        tagLine = "Body metrics & volume trend graphs",
        description = "Visualize weight fluctuation trends, body composition metrics, and total weekly training volume over time.",
        category = "Analytics",
        tier = "Pro Preview",
        permissionsRequired = listOf("Local Storage", "Metric Graph Generation"),
        featureList = listOf(
            "Body weight trend tracking & moving averages",
            "Weekly volume load progression graphs",
            "Personal records summary view",
            "Exportable progress snapshot"
        )
    ),
    ExtensionDescriptor(
        id = "ext_endurance",
        version = "1.0.2",
        name = "Endurance Lab",
        tagLine = "Cardio, running & cycling interval splits",
        description = "Record cardio sessions, distance, pace, and estimated energy expenditure alongside your resistance training.",
        category = "Cardio",
        tier = "Free",
        permissionsRequired = listOf("Local Activity Log"),
        featureList = listOf(
            "Running, cycling, and walking logs",
            "Pace per kilometer calculation",
            "Cardio duration and distance summaries",
            "Combined aerobic + strength workload score"
        )
    ),
    ExtensionDescriptor(
        id = "ext_anatomy",
        version = "0.9.0-alpha",
        name = "Anatomy 3D",
        tagLine = "Interactive anatomical muscle group visualization",
        description = "Explore 3D muscle biomechanics, agonist/antagonist relationships, and joint angles for each exercise.",
        category = "Anatomy",
        tier = "Pro Preview",
        isComingSoon = true,
        permissionsRequired = listOf("3D Hardware Acceleration"),
        featureList = listOf(
            "Interactive muscle anatomy rendering",
            "Exercise biomechanics simulation",
            "Hypertrophy tension map"
        )
    )
)
