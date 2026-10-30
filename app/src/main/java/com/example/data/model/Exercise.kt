package com.example.data.model

data class Exercise(
    val id: String,
    val name: String,
    val targetMuscle: String,
    val equipment: String,
    val instructions: String,
    val substitutions: List<String>,
    val difficulty: String = "Beginner"
)

val DEFAULT_EXERCISES = listOf(
    Exercise(
        id = "barbell_bench_press",
        name = "Barbell Bench Press",
        targetMuscle = "Chest",
        equipment = "Barbell",
        instructions = "Lie flat on the bench with eyes under the bar. Grip bar slightly wider than shoulder-width. Lower bar smoothly to mid-chest, keeping elbows tucked ~45 degrees, then press up forcefully.",
        substitutions = listOf("Dumbbell Bench Press", "Push-ups", "Chest Press Machine"),
        difficulty = "Intermediate"
    ),
    Exercise(
        id = "dumbbell_bench_press",
        name = "Dumbbell Bench Press",
        targetMuscle = "Chest",
        equipment = "Dumbbell",
        instructions = "Lie on a flat bench holding dumbbells at chest level. Press upwards in an arc until arms are extended, then slowly descend feeling the stretch in pectorals.",
        substitutions = listOf("Barbell Bench Press", "Incline Dumbbell Press", "Push-ups"),
        difficulty = "Beginner"
    ),
    Exercise(
        id = "incline_dumbbell_press",
        name = "Incline Dumbbell Press",
        targetMuscle = "Chest",
        equipment = "Dumbbell",
        instructions = "Set bench to 30-45 degree incline. Press dumbbells upward, focusing on engaging upper chest fibers. Lower under control.",
        substitutions = listOf("Incline Barbell Press", "Low-to-High Cable Fly"),
        difficulty = "Beginner"
    ),
    Exercise(
        id = "push_up",
        name = "Push-ups",
        targetMuscle = "Chest",
        equipment = "Bodyweight",
        instructions = "Maintain a rigid plank line from head to heels. Lower chest to floor while elbows point back diagonally, then push the ground away.",
        substitutions = listOf("Knee Push-ups", "Incline Push-ups", "Dumbbell Press"),
        difficulty = "Beginner"
    ),
    Exercise(
        id = "barbell_squat",
        name = "Barbell Back Squat",
        targetMuscle = "Legs",
        equipment = "Barbell",
        instructions = "Rest bar comfortably on upper traps. Feet shoulder-width apart, toes flared slightly. Inhale, brace core, push hips back and knees out until thighs are parallel to ground, then drive through midfoot.",
        substitutions = listOf("Goblet Squat", "Leg Press", "Bulgarian Split Squat"),
        difficulty = "Intermediate"
    ),
    Exercise(
        id = "goblet_squat",
        name = "Dumbbell Goblet Squat",
        targetMuscle = "Legs",
        equipment = "Dumbbell",
        instructions = "Hold one dumbbell vertically against upper chest. Squat between knees keeping torso upright. Excellent for mastering squat depth.",
        substitutions = listOf("Barbell Squat", "Bodyweight Squat"),
        difficulty = "Beginner"
    ),
    Exercise(
        id = "romanian_deadlift",
        name = "Romanian Deadlift",
        targetMuscle = "Legs",
        equipment = "Dumbbell",
        instructions = "Stand holding weights in front of thighs with slight knee bend. Push hips back as if touching a wall behind you until hamstrings stretch, then squeeze glutes to return.",
        substitutions = listOf("Barbell RDL", "Single-Leg RDL", "Glute Bridge"),
        difficulty = "Beginner"
    ),
    Exercise(
        id = "deadlift",
        name = "Barbell Deadlift",
        targetMuscle = "Back",
        equipment = "Barbell",
        instructions = "Stand with feet hip-width under bar. Hinge down and grip bar. Pull slack out of bar, brace lats and abdomen, and pull bar up along shins by extending hips and knees.",
        substitutions = listOf("Trap Bar Deadlift", "Romanian Deadlift", "Dumbbell Deadlift"),
        difficulty = "Advanced"
    ),
    Exercise(
        id = "pull_up",
        name = "Pull-up",
        targetMuscle = "Back",
        equipment = "Bodyweight",
        instructions = "Grip overhead pull-up bar slightly wider than shoulders. Drive elbows down toward hips to lift chest toward the bar. Lower under full control.",
        substitutions = listOf("Lat Pulldown", "Assisted Pull-up", "Inverted Row"),
        difficulty = "Intermediate"
    ),
    Exercise(
        id = "lat_pulldown",
        name = "Cable Lat Pulldown",
        targetMuscle = "Back",
        equipment = "Cable",
        instructions = "Sit upright with knees locked under pads. Grasp bar wide. Pull bar down toward upper collarbone while squeezing shoulder blades together.",
        substitutions = listOf("Pull-up", "Dumbbell Pullover"),
        difficulty = "Beginner"
    ),
    Exercise(
        id = "dumbbell_row",
        name = "Single-Arm Dumbbell Row",
        targetMuscle = "Back",
        equipment = "Dumbbell",
        instructions = "Support knee and hand on flat bench. Pull dumbbell up towards hip pocket keeping elbow tucked, pausing at the top for peak contraction.",
        substitutions = listOf("Barbell Row", "Seated Cable Row", "Chest-Supported Row"),
        difficulty = "Beginner"
    ),
    Exercise(
        id = "overhead_press",
        name = "Dumbbell Overhead Press",
        targetMuscle = "Shoulders",
        equipment = "Dumbbell",
        instructions = "Sit or stand tall. Press dumbbells straight up overhead until arms lock out, avoiding excessive lower back arching. Lower slowly to ear level.",
        substitutions = listOf("Barbell Overhead Press", "Machine Shoulder Press", "Pike Push-up"),
        difficulty = "Beginner"
    ),
    Exercise(
        id = "lateral_raise",
        name = "Dumbbell Lateral Raise",
        targetMuscle = "Shoulders",
        equipment = "Dumbbell",
        instructions = "Hold light dumbbells at sides. Raise arms out to sides with slight forward lean until parallel with floor. Lead with elbows.",
        substitutions = listOf("Cable Lateral Raise", "Resistance Band Lateral Raise"),
        difficulty = "Beginner"
    ),
    Exercise(
        id = "bicep_curl",
        name = "Dumbbell Bicep Curl",
        targetMuscle = "Arms",
        equipment = "Dumbbell",
        instructions = "Keep upper arms stationary at sides. Curl dumbbells toward shoulders while supinating wrists. Lower slowly.",
        substitutions = listOf("Barbell Curl", "Hammer Curl", "Cable Curl"),
        difficulty = "Beginner"
    ),
    Exercise(
        id = "tricep_rope_pushdown",
        name = "Tricep Cable Pushdown",
        targetMuscle = "Arms",
        equipment = "Cable",
        instructions = "Grip rope attachment at chest level with elbows pinned to sides. Extend arms downward, flaring rope ends slightly at lockout.",
        substitutions = listOf("Overhead Tricep Extension", "Bench Dips", "Diamond Push-ups"),
        difficulty = "Beginner"
    ),
    Exercise(
        id = "plank",
        name = "Forearm Plank",
        targetMuscle = "Core",
        equipment = "Bodyweight",
        instructions = "Rest on forearms and toes. Keep spine neutral, glutes clenched, and naval pulled towards spine. Breathe steadily.",
        substitutions = listOf("Deadbug", "Ab Wheel Rollout", "Hollow Body Hold"),
        difficulty = "Beginner"
    )
)
