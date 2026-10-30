package com.example.data.model

data class MuscleRole(
    val name: String,
    val commonName: String,
    val anatomicalFunction: String,
    val isPrimary: Boolean
)

enum class AnimationEngineType {
    BIOMECHANICAL_3D_OPENGL,
    GLB_ASSET,
    REMOTE_API_STREAM
}

data class ExerciseAssetMetadata(
    val engineType: AnimationEngineType = AnimationEngineType.BIOMECHANICAL_3D_OPENGL,
    val assetIdentifier: String,
    val providerName: String = "Pulse Biomechanical 3D Engine",
    val licenseType: String = "Open Permissive / Pulse Native Core (Apache 2.0)",
    val attribution: String = "Pulse Health Engine & Biomechanical Anatomical Rig",
    val isLocalAssetReady: Boolean = true,
    val apiDocsUrl: String? = null
)

data class BiomechanicalKeyframe(
    val progress: Float, // 0.0 to 1.0
    val phaseName: String,
    val shoulderFlexion: Float, // degrees
    val shoulderAbduction: Float,
    val elbowFlexion: Float,
    val hipFlexion: Float,
    val kneeFlexion: Float,
    val spineFlexion: Float,
    val equipmentYOffset: Float = 0f,
    val equipmentWidthOffset: Float = 0f,
    val primaryTension: Float = 1.0f,
    val secondaryTension: Float = 0.5f
)

data class Exercise3d(
    val id: String,
    val name: String,
    val category: String, // Chest, Back, Shoulders, Biceps, Triceps, Legs, Core
    val equipment: String, // Barbell, Dumbbell, Cable, Machine, Bodyweight
    val difficulty: String, // Beginner, Intermediate, Advanced
    val summary: String,
    val primaryMuscles: List<MuscleRole>,
    val secondaryMuscles: List<MuscleRole>,
    val setupInstructions: List<String>,
    val executionSteps: List<String>,
    val breathingGuidance: String,
    val commonMistakes: List<String>,
    val safetyNotes: String,
    val assetMetadata: ExerciseAssetMetadata,
    val keyframes: List<BiomechanicalKeyframe>
)

val EXERCISE_3D_LIBRARY: List<Exercise3d> = listOf(
    // 1. Barbell Bench Press
    Exercise3d(
        id = "barbell_bench_press",
        name = "Barbell Bench Press",
        category = "Chest",
        equipment = "Barbell",
        difficulty = "Intermediate",
        summary = "The premier upper-body compound movement developing horizontal pushing force and chest thickness.",
        primaryMuscles = listOf(
            MuscleRole(
                name = "Pectoralis Major",
                commonName = "Chest (Sternal & Clavicular)",
                anatomicalFunction = "Prime mover for horizontal adduction and shoulder internal rotation under heavy load.",
                isPrimary = true
            )
        ),
        secondaryMuscles = listOf(
            MuscleRole(
                name = "Anterior Deltoid",
                commonName = "Front Shoulder",
                anatomicalFunction = "Assists shoulder flexion during the ascent drive.",
                isPrimary = false
            ),
            MuscleRole(
                name = "Triceps Brachii",
                commonName = "Triceps (Lateral & Medial heads)",
                anatomicalFunction = "Extends the elbow joint to achieve full lockout.",
                isPrimary = false
            )
        ),
        setupInstructions = listOf(
            "Lie supine on the bench with eyes directly aligned underneath the racked barbell.",
            "Firmly plant both feet flat on the floor to generate leg drive.",
            "Retract and depress shoulder blades, driving them firmly into the bench pad.",
            "Grip the bar slightly wider than shoulder-width with wrists stacked straight."
        ),
        executionSteps = listOf(
            "Unrack the barbell with extended elbows and stabilize the bar directly over mid-chest.",
            "Inhale and lower the bar under strict control, keeping elbows tucked at ~45-60 degrees from torso.",
            "Touch the bar lightly to lower sternum without bouncing off the rib cage.",
            "Drive forcefully through midfoot and chest, pressing the bar upward in a slight natural J-curve."
        ),
        breathingGuidance = "Inhale and brace core (Valsalva) on the eccentric descent; exhale forcefully past the sticking point near the top of the press.",
        commonMistakes = listOf(
            "Flaring elbows straight out at 90 degrees, transferring destructive shear stress to the rotator cuff.",
            "Allowing the lower back to over-arch excessively, lifting buttocks off the bench.",
            "Bouncing the loaded bar off the sternum to artificially generate momentum."
        ),
        safetyNotes = "Always maintain a closed thumb-around grip (never a suicide thumbless grip) and set bench safety pins to rib level.",
        assetMetadata = ExerciseAssetMetadata(
            engineType = AnimationEngineType.GLB_ASSET,
            assetIdentifier = "models/human_character.glb",
            providerName = "Khronos Group / Cesium (CC-BY 4.0)",
            licenseType = "Creative Commons Attribution 4.0 (CC-BY 4.0)",
            attribution = "CesiumMan rigged 3D human character by Cesium / Analytical Graphics Inc. licensed under CC-BY 4.0.",
            isLocalAssetReady = true
        ),
        keyframes = listOf(
            BiomechanicalKeyframe(0.0f, "Lockout / Starting Position", shoulderFlexion = 85f, shoulderAbduction = 45f, elbowFlexion = 5f, hipFlexion = 80f, kneeFlexion = 90f, spineFlexion = 0f, equipmentYOffset = 0.55f, primaryTension = 0.4f, secondaryTension = 0.3f),
            BiomechanicalKeyframe(0.45f, "Eccentric Lowering to Chest", shoulderFlexion = 20f, shoulderAbduction = 55f, elbowFlexion = 88f, hipFlexion = 80f, kneeFlexion = 90f, spineFlexion = 0f, equipmentYOffset = 0.12f, primaryTension = 0.95f, secondaryTension = 0.7f),
            BiomechanicalKeyframe(0.55f, "Isometric Chest Stretch", shoulderFlexion = 15f, shoulderAbduction = 55f, elbowFlexion = 92f, hipFlexion = 80f, kneeFlexion = 90f, spineFlexion = 0f, equipmentYOffset = 0.08f, primaryTension = 1.0f, secondaryTension = 0.8f),
            BiomechanicalKeyframe(1.0f, "Concentric Ascent Drive", shoulderFlexion = 85f, shoulderAbduction = 45f, elbowFlexion = 5f, hipFlexion = 80f, kneeFlexion = 90f, spineFlexion = 0f, equipmentYOffset = 0.55f, primaryTension = 0.4f, secondaryTension = 0.3f)
        )
    ),

    // 2. Incline Dumbbell Press
    Exercise3d(
        id = "incline_dumbbell_press",
        name = "Incline Dumbbell Press",
        category = "Chest",
        equipment = "Dumbbell",
        difficulty = "Beginner",
        summary = "Targets the clavicular upper portion of the chest with independent unilateral stabilization.",
        primaryMuscles = listOf(
            MuscleRole(
                name = "Pectoralis Major (Clavicular Head)",
                commonName = "Upper Chest",
                anatomicalFunction = "Performs shoulder flexion and horizontal adduction on an inclined plane.",
                isPrimary = true
            )
        ),
        secondaryMuscles = listOf(
            MuscleRole(
                name = "Anterior Deltoid",
                commonName = "Front Shoulder",
                anatomicalFunction = "Heavily recruited due to the 30-degree incline angle.",
                isPrimary = false
            ),
            MuscleRole(
                name = "Triceps Brachii",
                commonName = "Triceps",
                anatomicalFunction = "Assists in extending elbows at top of movement.",
                isPrimary = false
            )
        ),
        setupInstructions = listOf(
            "Adjust the incline bench between 30 and 45 degrees.",
            "Sit with dumbbells resting upright on thighs.",
            "Kick dumbbells up one at a time with knees while lying back onto pad.",
            "Retract shoulder blades and plant feet firmly on the ground."
        ),
        executionSteps = listOf(
            "Begin with dumbbells at shoulder height, palms angled slightly inward (semi-pronated).",
            "Press weights upward smoothly in a convergent arc without clacking them together.",
            "Squeeze upper chest at top without locking elbows violently.",
            "Lower weights smoothly over 2-3 seconds until a deep stretch is felt in upper chest."
        ),
        breathingGuidance = "Inhale deeply as you lower the dumbbells to shoulder height; exhale powerfully as you press them upward.",
        commonMistakes = listOf(
            "Setting bench incline higher than 45 degrees, which shifts work primarily onto the anterior deltoids.",
            "Bouncing out of the bottom stretch using shoulder momentum.",
            "Letting elbows flare excessively outward."
        ),
        safetyNotes = "Never drop dumbbells laterally to the floor; bring knees up to meet the weights at the end of the set.",
        assetMetadata = ExerciseAssetMetadata(
            engineType = AnimationEngineType.GLB_ASSET,
            assetIdentifier = "models/human_character.glb",
            providerName = "Khronos Group / Cesium (CC-BY 4.0)",
            licenseType = "Creative Commons Attribution 4.0 (CC-BY 4.0)",
            attribution = "CesiumMan rigged 3D human character by Cesium / Analytical Graphics Inc. licensed under CC-BY 4.0.",
            isLocalAssetReady = true
        ),
        keyframes = listOf(
            BiomechanicalKeyframe(0.0f, "Lockout Overhead", shoulderFlexion = 100f, shoulderAbduction = 40f, elbowFlexion = 10f, hipFlexion = 75f, kneeFlexion = 85f, spineFlexion = -15f, equipmentYOffset = 0.65f, equipmentWidthOffset = 0.22f, primaryTension = 0.5f, secondaryTension = 0.4f),
            BiomechanicalKeyframe(0.5f, "Lowering to Shoulder Pocket", shoulderFlexion = 40f, shoulderAbduction = 60f, elbowFlexion = 95f, hipFlexion = 75f, kneeFlexion = 85f, spineFlexion = -15f, equipmentYOffset = 0.20f, equipmentWidthOffset = 0.45f, primaryTension = 1.0f, secondaryTension = 0.75f),
            BiomechanicalKeyframe(1.0f, "Press Arc to Apex", shoulderFlexion = 100f, shoulderAbduction = 40f, elbowFlexion = 10f, hipFlexion = 75f, kneeFlexion = 85f, spineFlexion = -15f, equipmentYOffset = 0.65f, equipmentWidthOffset = 0.22f, primaryTension = 0.5f, secondaryTension = 0.4f)
        )
    ),

    // 3. Dumbbell Lateral Raise
    Exercise3d(
        id = "lateral_raise",
        name = "Dumbbell Lateral Raise",
        category = "Shoulders",
        equipment = "Dumbbell",
        difficulty = "Beginner",
        summary = "Isolates the lateral head of the deltoid to develop shoulder width and V-taper aesthetics.",
        primaryMuscles = listOf(
            MuscleRole(
                name = "Lateral Deltoid",
                commonName = "Side Shoulder",
                anatomicalFunction = "Abducts the arm in the scapular plane from 15 to 90 degrees.",
                isPrimary = true
            )
        ),
        secondaryMuscles = listOf(
            MuscleRole(
                name = "Supraspinatus",
                commonName = "Rotator Cuff",
                anatomicalFunction = "Initiates the first 15 degrees of arm abduction.",
                isPrimary = false
            ),
            MuscleRole(
                name = "Upper Trapezius",
                commonName = "Traps",
                anatomicalFunction = "Assists upward scapular rotation past 70 degrees.",
                isPrimary = false
            )
        ),
        setupInstructions = listOf(
            "Stand with feet shoulder-width apart, holding light dumbbells in front of thighs.",
            "Establish a slight forward hip hinge (~10-15 degrees) to align with the scapular plane.",
            "Unlock elbows slightly and maintain this fixed bend throughout the repetition."
        ),
        executionSteps = listOf(
            "Raise dumbbells outward and slightly forward in the scapular plane.",
            "Lead with the elbows, keeping thumbs level or angled slightly down.",
            "Pause briefly when upper arms reach parallel with the floor.",
            "Lower the weights slowly under active muscular control over 2 seconds."
        ),
        breathingGuidance = "Exhale smoothly as you raise the dumbbells laterally; inhale as you lower them with control.",
        commonMistakes = listOf(
            "Using body sway and hip thrust to fling heavy dumbbells upward.",
            "Shrugging traps upward before deltoids engage.",
            "Turning wrists upward like a pouring motion, which can cause shoulder impingement."
        ),
        safetyNotes = "Keep the movement strictly in the scapular plane (30 degrees forward of midline) to protect the subacromial space.",
        assetMetadata = ExerciseAssetMetadata(
            engineType = AnimationEngineType.GLB_ASSET,
            assetIdentifier = "models/human_character.glb",
            providerName = "Khronos Group / Cesium (CC-BY 4.0)",
            licenseType = "Creative Commons Attribution 4.0 (CC-BY 4.0)",
            attribution = "CesiumMan rigged 3D human character by Cesium / Analytical Graphics Inc. licensed under CC-BY 4.0.",
            isLocalAssetReady = true
        ),
        keyframes = listOf(
            BiomechanicalKeyframe(0.0f, "Bottom Rest Position", shoulderFlexion = 10f, shoulderAbduction = 12f, elbowFlexion = 15f, hipFlexion = 10f, kneeFlexion = 5f, spineFlexion = 0f, equipmentYOffset = 0.05f, equipmentWidthOffset = 0.22f, primaryTension = 0.1f, secondaryTension = 0.1f),
            BiomechanicalKeyframe(0.5f, "Peak Lateral Abduction", shoulderFlexion = 25f, shoulderAbduction = 88f, elbowFlexion = 18f, hipFlexion = 10f, kneeFlexion = 5f, spineFlexion = 0f, equipmentYOffset = 0.52f, equipmentWidthOffset = 0.72f, primaryTension = 1.0f, secondaryTension = 0.45f),
            BiomechanicalKeyframe(1.0f, "Controlled Eccentric Return", shoulderFlexion = 10f, shoulderAbduction = 12f, elbowFlexion = 15f, hipFlexion = 10f, kneeFlexion = 5f, spineFlexion = 0f, equipmentYOffset = 0.05f, equipmentWidthOffset = 0.22f, primaryTension = 0.1f, secondaryTension = 0.1f)
        )
    ),

    // 4. Arnold Press
    Exercise3d(
        id = "arnold_press",
        name = "Arnold Press",
        category = "Shoulders",
        equipment = "Dumbbell",
        difficulty = "Intermediate",
        summary = "Classic compound shoulder builder combining wrist rotation with overhead pressing for complete 360 deltoid stimulation.",
        primaryMuscles = listOf(
            MuscleRole(
                name = "Anterior & Lateral Deltoids",
                commonName = "Front & Side Shoulders",
                anatomicalFunction = "Drives shoulder flexion, abduction, and rotation through an extended range of motion.",
                isPrimary = true
            )
        ),
        secondaryMuscles = listOf(
            MuscleRole(
                name = "Triceps Brachii",
                commonName = "Triceps",
                anatomicalFunction = "Provides elbow extension during the top half of the press.",
                isPrimary = false
            ),
            MuscleRole(
                name = "Serratus Anterior",
                commonName = "Boxer's Muscle",
                anatomicalFunction = "Stabilizes and protracts the scapula as arms reach overhead.",
                isPrimary = false
            )
        ),
        setupInstructions = listOf(
            "Sit on an upright bench with vertical back support.",
            "Hold dumbbells in front of shoulders, palms facing your chest (supinated) like top of a bicep curl.",
            "Keep core braced and spine pressed flat against pad."
        ),
        executionSteps = listOf(
            "Initiate movement by pressing weights upward while smoothly rotating palms outward.",
            "By the midpoint, elbows flare outward into standard overhead press position.",
            "Finish pressing overhead with palms facing forward at full extension.",
            "Reverse the rotation seamlessly as you lower dumbbells back to chin height."
        ),
        breathingGuidance = "Exhale steadily throughout the outward rotational press; inhale during the descent and internal rotation.",
        commonMistakes = listOf(
            "Rotating wrists too early before elbows begin ascending.",
            "Arching lower back excessively to compensate for tight thoracic mobility.",
            "Using weights too heavy to control the rotational transition."
        ),
        safetyNotes = "Warm up rotator cuffs thoroughly and maintain smooth, fluid rotational momentum without jerkiness.",
        assetMetadata = ExerciseAssetMetadata(
            engineType = AnimationEngineType.GLB_ASSET,
            assetIdentifier = "models/human_character.glb",
            providerName = "Khronos Group / Cesium (CC-BY 4.0)",
            licenseType = "Creative Commons Attribution 4.0 (CC-BY 4.0)",
            attribution = "CesiumMan rigged 3D human character by Cesium / Analytical Graphics Inc. licensed under CC-BY 4.0.",
            isLocalAssetReady = true
        ),
        keyframes = listOf(
            BiomechanicalKeyframe(0.0f, "Supinated Starting Pocket", shoulderFlexion = 30f, shoulderAbduction = 10f, elbowFlexion = 110f, hipFlexion = 85f, kneeFlexion = 85f, spineFlexion = 0f, equipmentYOffset = 0.35f, equipmentWidthOffset = 0.20f, primaryTension = 0.4f, secondaryTension = 0.3f),
            BiomechanicalKeyframe(0.5f, "Rotational Flare & Press", shoulderFlexion = 65f, shoulderAbduction = 60f, elbowFlexion = 60f, hipFlexion = 85f, kneeFlexion = 85f, spineFlexion = 0f, equipmentYOffset = 0.60f, equipmentWidthOffset = 0.42f, primaryTension = 0.95f, secondaryTension = 0.6f),
            BiomechanicalKeyframe(1.0f, "Pronated Overhead Lockout", shoulderFlexion = 110f, shoulderAbduction = 40f, elbowFlexion = 8f, hipFlexion = 85f, kneeFlexion = 85f, spineFlexion = 0f, equipmentYOffset = 0.85f, equipmentWidthOffset = 0.30f, primaryTension = 0.6f, secondaryTension = 0.5f)
        )
    ),

    // 5. Lat Pulldown
    Exercise3d(
        id = "lat_pulldown",
        name = "Cable Lat Pulldown",
        category = "Back",
        equipment = "Cable",
        difficulty = "Beginner",
        summary = "Vertical pulling exercise essential for building back width, lat flare, and upper pulling strength.",
        primaryMuscles = listOf(
            MuscleRole(
                name = "Latissimus Dorsi",
                commonName = "Lats",
                anatomicalFunction = "Adducts and extends the humerus toward the spine.",
                isPrimary = true
            )
        ),
        secondaryMuscles = listOf(
            MuscleRole(
                name = "Biceps Brachii & Brachialis",
                commonName = "Biceps & Forearms",
                anatomicalFunction = "Flexes the elbow joint throughout the pull.",
                isPrimary = false
            ),
            MuscleRole(
                name = "Rhomboids & Lower Traps",
                commonName = "Mid Back",
                anatomicalFunction = "Retracts and depresses the scapulae at bottom contraction.",
                isPrimary = false
            )
        ),
        setupInstructions = listOf(
            "Adjust thigh pad snugly against upper thighs to anchor lower body.",
            "Grip the wide lat bar with overhand grip 1.5x shoulder width.",
            "Sit tall with slight backward torso lean (~10-15 degrees)."
        ),
        executionSteps = listOf(
            "Depress shoulder blades first before bending elbows.",
            "Drive elbows downward and inward toward back pockets.",
            "Pull bar smoothly to upper chest / collarbone level.",
            "Allow the bar to rise under control for a full overhead stretch of the lats."
        ),
        breathingGuidance = "Exhale as you pull the bar downward toward collarbone; inhale during the overhead controlled stretch.",
        commonMistakes = listOf(
            "Swinging torso backward to use momentum like a rowing machine.",
            "Pulling the bar behind the neck, placing extreme torque on the cervical spine.",
            "Allowing shoulders to roll forward at the bottom."
        ),
        safetyNotes = "Never pull the bar behind the head; always pull to the front collarbone with a braced core.",
        assetMetadata = ExerciseAssetMetadata(
            engineType = AnimationEngineType.GLB_ASSET,
            assetIdentifier = "models/human_character.glb",
            providerName = "Khronos Group / Cesium (CC-BY 4.0)",
            licenseType = "Creative Commons Attribution 4.0 (CC-BY 4.0)",
            attribution = "CesiumMan rigged 3D human character by Cesium / Analytical Graphics Inc. licensed under CC-BY 4.0.",
            isLocalAssetReady = true
        ),
        keyframes = listOf(
            BiomechanicalKeyframe(0.0f, "Full Overhead Lat Stretch", shoulderFlexion = 125f, shoulderAbduction = 60f, elbowFlexion = 5f, hipFlexion = 85f, kneeFlexion = 90f, spineFlexion = -5f, equipmentYOffset = 0.95f, primaryTension = 0.3f, secondaryTension = 0.2f),
            BiomechanicalKeyframe(0.5f, "Contracted Peak Pull", shoulderFlexion = 10f, shoulderAbduction = 40f, elbowFlexion = 105f, hipFlexion = 85f, kneeFlexion = 90f, spineFlexion = -12f, equipmentYOffset = 0.42f, primaryTension = 1.0f, secondaryTension = 0.7f),
            BiomechanicalKeyframe(1.0f, "Controlled Extension Return", shoulderFlexion = 125f, shoulderAbduction = 60f, elbowFlexion = 5f, hipFlexion = 85f, kneeFlexion = 90f, spineFlexion = -5f, equipmentYOffset = 0.95f, primaryTension = 0.3f, secondaryTension = 0.2f)
        )
    ),

    // 6. Seated Cable Row
    Exercise3d(
        id = "seated_cable_row",
        name = "Seated Cable Row",
        category = "Back",
        equipment = "Cable",
        difficulty = "Beginner",
        summary = "Horizontal rowing movement targeting mid-back thickness, rhomboids, and lat contraction.",
        primaryMuscles = listOf(
            MuscleRole(
                name = "Rhomboids & Middle Trapezius",
                commonName = "Mid Back",
                anatomicalFunction = "Retracts shoulder blades toward the midline of spine.",
                isPrimary = true
            ),
            MuscleRole(
                name = "Latissimus Dorsi",
                commonName = "Lats",
                anatomicalFunction = "Extends the shoulder during the horizontal pull.",
                isPrimary = true
            )
        ),
        secondaryMuscles = listOf(
            MuscleRole(
                name = "Biceps Brachii",
                commonName = "Biceps",
                anatomicalFunction = "Flexes elbow during pulling phase.",
                isPrimary = false
            ),
            MuscleRole(
                name = "Erector Spinae",
                commonName = "Lower Back",
                anatomicalFunction = "Isometrically maintains upright spinal posture.",
                isPrimary = false
            )
        ),
        setupInstructions = listOf(
            "Sit on machine with feet secured against footrests, knees slightly bent.",
            "Reach forward with neutral spine and grip close V-bar handle.",
            "Push back with legs to set upright torso perpendicular to floor."
        ),
        executionSteps = listOf(
            "With chest proud and shoulders back, pull handle toward abdomen / naval.",
            "Drive elbows straight back close to ribcage.",
            "Squeeze shoulder blades together forcefully at full contraction for 1 second.",
            "Slowly extend arms forward, allowing scapulae to protract without rounding lumbar spine."
        ),
        breathingGuidance = "Exhale as you row the handle into abdomen; inhale as you let arms extend forward.",
        commonMistakes = listOf(
            "Hyperextending lower back violently at the finish of each repetition.",
            "Shrugging shoulders into ears rather than driving elbows back.",
            "Rounding the spine when extending forward."
        ),
        safetyNotes = "Keep knees unlocked at all times to prevent hamstring strain and lumbar rounding.",
        assetMetadata = ExerciseAssetMetadata(
            engineType = AnimationEngineType.GLB_ASSET,
            assetIdentifier = "models/human_character.glb",
            providerName = "Khronos Group / Cesium (CC-BY 4.0)",
            licenseType = "Creative Commons Attribution 4.0 (CC-BY 4.0)",
            attribution = "CesiumMan rigged 3D human character by Cesium / Analytical Graphics Inc. licensed under CC-BY 4.0.",
            isLocalAssetReady = true
        ),
        keyframes = listOf(
            BiomechanicalKeyframe(0.0f, "Forward Scapular Stretch", shoulderFlexion = 60f, shoulderAbduction = 10f, elbowFlexion = 15f, hipFlexion = 80f, kneeFlexion = 30f, spineFlexion = 5f, equipmentYOffset = 0.20f, primaryTension = 0.3f, secondaryTension = 0.2f),
            BiomechanicalKeyframe(0.5f, "Full Retraction & Naval Pull", shoulderFlexion = -15f, shoulderAbduction = 15f, elbowFlexion = 110f, hipFlexion = 90f, kneeFlexion = 30f, spineFlexion = -2f, equipmentYOffset = 0.22f, primaryTension = 1.0f, secondaryTension = 0.65f),
            BiomechanicalKeyframe(1.0f, "Controlled Extension Return", shoulderFlexion = 60f, shoulderAbduction = 10f, elbowFlexion = 15f, hipFlexion = 80f, kneeFlexion = 30f, spineFlexion = 5f, equipmentYOffset = 0.20f, primaryTension = 0.3f, secondaryTension = 0.2f)
        )
    ),

    // 7. Barbell Squat
    Exercise3d(
        id = "barbell_squat",
        name = "Barbell Back Squat",
        category = "Legs",
        equipment = "Barbell",
        difficulty = "Intermediate",
        summary = "The king of lower-body compound movements building quadriceps mass, glute power, and systemic strength.",
        primaryMuscles = listOf(
            MuscleRole(
                name = "Quadriceps Femoris",
                commonName = "Front Thighs (Quads)",
                anatomicalFunction = "Primary knee extensors producing forceful ascent out of the hole.",
                isPrimary = true
            ),
            MuscleRole(
                name = "Gluteus Maximus",
                commonName = "Glutes",
                anatomicalFunction = "Powerful hip extension during the concentric drive.",
                isPrimary = true
            )
        ),
        secondaryMuscles = listOf(
            MuscleRole(
                name = "Hamstrings & Adductor Magnus",
                commonName = "Back Thighs & Groin",
                anatomicalFunction = "Assists in hip extension and pelvis stabilization.",
                isPrimary = false
            ),
            MuscleRole(
                name = "Core & Spinal Erectors",
                commonName = "Abs & Lower Back",
                anatomicalFunction = "Maintains rigid intra-abdominal pressure and upright torso posture.",
                isPrimary = false
            )
        ),
        setupInstructions = listOf(
            "Position barbell across upper traps (high bar) or across rear deltoids (low bar).",
            "Grip the bar tightly with elbows pulled down and back to create an upper-back shelf.",
            "Step back and set feet slightly wider than shoulder width with toes angled 15-30 degrees out."
        ),
        executionSteps = listOf(
            "Take a deep diaphragmatic breath into abdomen and brace core tight.",
            "Descend by simultaneously breaking at hips and knees, pushing knees out in line with toes.",
            "Squat down until hip crease descends parallel with or slightly below knee joint.",
            "Drive powerfully through midfoot, keeping chest proud and knees tracking outward."
        ),
        breathingGuidance = "Inhale deeply and brace core before descending; hold intra-abdominal pressure through the bottom turnaround, exhaling past the sticking point.",
        commonMistakes = listOf(
            "Knees caving inward (valgus collapse), putting high strain on ACL and menisci.",
            "Heels lifting off the floor due to poor ankle dorsiflexion.",
            "Good-morning squat: Hips shooting up before chest during ascent."
        ),
        safetyNotes = "Always squat inside a power rack with safety spotter arms set just below your bottom depth.",
        assetMetadata = ExerciseAssetMetadata(
            engineType = AnimationEngineType.GLB_ASSET,
            assetIdentifier = "models/human_character.glb",
            providerName = "Khronos Group / Cesium (CC-BY 4.0)",
            licenseType = "Creative Commons Attribution 4.0 (CC-BY 4.0)",
            attribution = "CesiumMan rigged 3D human character by Cesium / Analytical Graphics Inc. licensed under CC-BY 4.0.",
            isLocalAssetReady = true
        ),
        keyframes = listOf(
            BiomechanicalKeyframe(0.0f, "Standing Tall / Top Lockout", shoulderFlexion = 0f, shoulderAbduction = 20f, elbowFlexion = 70f, hipFlexion = 0f, kneeFlexion = 0f, spineFlexion = 0f, equipmentYOffset = 0.85f, primaryTension = 0.2f, secondaryTension = 0.2f),
            BiomechanicalKeyframe(0.5f, "Parallel Squat Depth", shoulderFlexion = 0f, shoulderAbduction = 20f, elbowFlexion = 70f, hipFlexion = 100f, kneeFlexion = 105f, spineFlexion = 25f, equipmentYOffset = 0.42f, primaryTension = 1.0f, secondaryTension = 0.8f),
            BiomechanicalKeyframe(1.0f, "Concentric Drive to Top", shoulderFlexion = 0f, shoulderAbduction = 20f, elbowFlexion = 70f, hipFlexion = 0f, kneeFlexion = 0f, spineFlexion = 0f, equipmentYOffset = 0.85f, primaryTension = 0.2f, secondaryTension = 0.2f)
        )
    ),

    // 8. Romanian Deadlift
    Exercise3d(
        id = "romanian_deadlift",
        name = "Romanian Deadlift",
        category = "Legs",
        equipment = "Barbell",
        difficulty = "Intermediate",
        summary = "Posterior chain hinge movement developing massive hamstring flexibility, glute tension, and hip extension.",
        primaryMuscles = listOf(
            MuscleRole(
                name = "Hamstrings (Biceps Femoris, Semitendinosus)",
                commonName = "Hamstrings",
                anatomicalFunction = "Lengthens under eccentric load during hip hinge and produces powerful hip extension.",
                isPrimary = true
            ),
            MuscleRole(
                name = "Gluteus Maximus",
                commonName = "Glutes",
                anatomicalFunction = "Contracts to bring pelvis back to neutral at the top.",
                isPrimary = true
            )
        ),
        secondaryMuscles = listOf(
            MuscleRole(
                name = "Erector Spinae",
                commonName = "Spinal Erectors",
                anatomicalFunction = "Maintains rigid neutral spinal curvature throughout the hinge.",
                isPrimary = false
            ),
            MuscleRole(
                name = "Forearm Flexors & Traps",
                commonName = "Grip & Upper Back",
                anatomicalFunction = "Holds the heavy barbell securely against thighs.",
                isPrimary = false
            )
        ),
        setupInstructions = listOf(
            "Stand holding barbell in front of thighs with shoulder-width double overhand grip.",
            "Set feet hip-width apart with toes pointing straight ahead.",
            "Roll shoulders back, engage lats, and unlock knees slightly (15-20 degree fixed bend)."
        ),
        executionSteps = listOf(
            "Push hips backward as if trying to touch an imaginary wall behind you.",
            "Keep the barbell skimming tightly against thighs and shins throughout the descent.",
            "Lower until you feel a deep stretch in hamstrings, typically just below the knee.",
            "Drive hips forward through midfoot, squeezing glutes hard at the top without hyperextending back."
        ),
        breathingGuidance = "Inhale and brace abdominal wall at the top; hold breath during the hinge descent; exhale forcefully as hips lock out.",
        commonMistakes = listOf(
            "Rounding the lumbar spine like a conventional deadlift off the floor.",
            "Letting the bar drift away from legs, creating immense shear force on lower back.",
            "Bending knees further as you descend, turning the movement into a squat."
        ),
        safetyNotes = "Never allow the lower back to round; depth should be determined solely by hamstring flexibility.",
        assetMetadata = ExerciseAssetMetadata(
            engineType = AnimationEngineType.GLB_ASSET,
            assetIdentifier = "models/human_character.glb",
            providerName = "Khronos Group / Cesium (CC-BY 4.0)",
            licenseType = "Creative Commons Attribution 4.0 (CC-BY 4.0)",
            attribution = "CesiumMan rigged 3D human character by Cesium / Analytical Graphics Inc. licensed under CC-BY 4.0.",
            isLocalAssetReady = true
        ),
        keyframes = listOf(
            BiomechanicalKeyframe(0.0f, "Upright Standing Lockout", shoulderFlexion = 0f, shoulderAbduction = 10f, elbowFlexion = 5f, hipFlexion = 0f, kneeFlexion = 15f, spineFlexion = 0f, equipmentYOffset = 0.45f, primaryTension = 0.2f, secondaryTension = 0.2f),
            BiomechanicalKeyframe(0.5f, "Deep Posterior Hip Hinge", shoulderFlexion = 10f, shoulderAbduction = 10f, elbowFlexion = 5f, hipFlexion = 85f, kneeFlexion = 20f, spineFlexion = 0f, equipmentYOffset = 0.12f, primaryTension = 1.0f, secondaryTension = 0.75f),
            BiomechanicalKeyframe(1.0f, "Glute Squeeze & Hip Drive", shoulderFlexion = 0f, shoulderAbduction = 10f, elbowFlexion = 5f, hipFlexion = 0f, kneeFlexion = 15f, spineFlexion = 0f, equipmentYOffset = 0.45f, primaryTension = 0.2f, secondaryTension = 0.2f)
        )
    ),

    // 9. Dumbbell Biceps Curl
    Exercise3d(
        id = "bicep_curl",
        name = "Dumbbell Biceps Curl",
        category = "Biceps",
        equipment = "Dumbbell",
        difficulty = "Beginner",
        summary = "Classic arm builder emphasizing peak biceps peak contraction through wrist supination.",
        primaryMuscles = listOf(
            MuscleRole(
                name = "Biceps Brachii (Short & Long Heads)",
                commonName = "Biceps",
                anatomicalFunction = "Flexes the elbow and supinates the radioulnar joint.",
                isPrimary = true
            )
        ),
        secondaryMuscles = listOf(
            MuscleRole(
                name = "Brachialis & Brachioradialis",
                commonName = "Forearms & Outer Arm",
                anatomicalFunction = "Assists elbow flexion, particularly during the initial range.",
                isPrimary = false
            )
        ),
        setupInstructions = listOf(
            "Stand tall with feet hip-width apart holding a dumbbell in each hand at sides.",
            "Palms face thighs in neutral grip with elbows pinned slightly forward of hips.",
            "Brace core and keep shoulders pulled back."
        ),
        executionSteps = listOf(
            "Keeping upper arms stationary, curl weights upward toward shoulders.",
            "As dumbbells pass hips, rotate wrists outward (supinate) so palms face ceiling.",
            "Squeeze biceps forcefully at peak contraction at shoulder level.",
            "Lower weights smoothly back down over 2-3 seconds, rotating back to neutral."
        ),
        breathingGuidance = "Exhale as you curl and supinate the dumbbells; inhale as you lower the weights under control.",
        commonMistakes = listOf(
            "Swinging torso and hips to heave the weights upward.",
            "Letting elbows drift backward or flare outward.",
            "Dropping weights quickly without controlling the eccentric descent."
        ),
        safetyNotes = "Keep wrists neutral without allowing them to bend backward under the load.",
        assetMetadata = ExerciseAssetMetadata(
            engineType = AnimationEngineType.GLB_ASSET,
            assetIdentifier = "models/human_character.glb",
            providerName = "Khronos Group / Cesium (CC-BY 4.0)",
            licenseType = "Creative Commons Attribution 4.0 (CC-BY 4.0)",
            attribution = "CesiumMan rigged 3D human character by Cesium / Analytical Graphics Inc. licensed under CC-BY 4.0.",
            isLocalAssetReady = true
        ),
        keyframes = listOf(
            BiomechanicalKeyframe(0.0f, "Extended Arms at Sides", shoulderFlexion = 0f, shoulderAbduction = 10f, elbowFlexion = 5f, hipFlexion = 0f, kneeFlexion = 5f, spineFlexion = 0f, equipmentYOffset = 0.05f, equipmentWidthOffset = 0.25f, primaryTension = 0.1f, secondaryTension = 0.1f),
            BiomechanicalKeyframe(0.5f, "Supinated Peak Contraction", shoulderFlexion = 15f, shoulderAbduction = 10f, elbowFlexion = 130f, hipFlexion = 0f, kneeFlexion = 5f, spineFlexion = 0f, equipmentYOffset = 0.48f, equipmentWidthOffset = 0.25f, primaryTension = 1.0f, secondaryTension = 0.5f),
            BiomechanicalKeyframe(1.0f, "Controlled Eccentric Lowering", shoulderFlexion = 0f, shoulderAbduction = 10f, elbowFlexion = 5f, hipFlexion = 0f, kneeFlexion = 5f, spineFlexion = 0f, equipmentYOffset = 0.05f, equipmentWidthOffset = 0.25f, primaryTension = 0.1f, secondaryTension = 0.1f)
        )
    ),

    // 10. Triceps Cable Pushdown
    Exercise3d(
        id = "tricep_rope_pushdown",
        name = "Triceps Cable Pushdown",
        category = "Triceps",
        equipment = "Cable",
        difficulty = "Beginner",
        summary = "Constant cable tension exercise isolating all three heads of the triceps with full elbow extension.",
        primaryMuscles = listOf(
            MuscleRole(
                name = "Triceps Brachii (Lateral, Long, Medial)",
                commonName = "Triceps Horseshoe",
                anatomicalFunction = "Primary extensor of the elbow joint.",
                isPrimary = true
            )
        ),
        secondaryMuscles = listOf(
            MuscleRole(
                name = "Anconeus",
                commonName = "Elbow Stabilizer",
                anatomicalFunction = "Stabilizes the elbow joint at full extension.",
                isPrimary = false
            ),
            MuscleRole(
                name = "Rectus Abdominis",
                commonName = "Abs",
                anatomicalFunction = "Isometrically counters the upward pull of the cable stack.",
                isPrimary = false
            )
        ),
        setupInstructions = listOf(
            "Attach a rope attachment to the top pulley of a cable station.",
            "Grip rope near knotted ends and step back slightly.",
            "Hinge forward 10-15 degrees at hips and pin elbows firmly to your sides."
        ),
        executionSteps = listOf(
            "Start with forearms just above parallel with elbows locked at your ribcage.",
            "Push the rope down by extending elbows until arms are completely straight.",
            "At the bottom of the movement, spread the rope ends apart slightly.",
            "Hold the peak contraction for 1 second before controlling the return to 90 degrees."
        ),
        breathingGuidance = "Exhale as you push the rope downward to full lockout; inhale as you let hands return upward under control.",
        commonMistakes = listOf(
            "Letting elbows drift forward and backward, turning the move into a shoulder press.",
            "Using body weight to press the rope down rather than isolating the triceps.",
            "Incomplete extension at the bottom."
        ),
        safetyNotes = "Keep wrists firm and avoid snapping elbows into violent hyperextension.",
        assetMetadata = ExerciseAssetMetadata(
            engineType = AnimationEngineType.GLB_ASSET,
            assetIdentifier = "models/human_character.glb",
            providerName = "Khronos Group / Cesium (CC-BY 4.0)",
            licenseType = "Creative Commons Attribution 4.0 (CC-BY 4.0)",
            attribution = "CesiumMan rigged 3D human character by Cesium / Analytical Graphics Inc. licensed under CC-BY 4.0.",
            isLocalAssetReady = true
        ),
        keyframes = listOf(
            BiomechanicalKeyframe(0.0f, "Starting 90 Degree Elbow Bend", shoulderFlexion = 15f, shoulderAbduction = 10f, elbowFlexion = 95f, hipFlexion = 15f, kneeFlexion = 10f, spineFlexion = 0f, equipmentYOffset = 0.42f, equipmentWidthOffset = 0.15f, primaryTension = 0.3f, secondaryTension = 0.2f),
            BiomechanicalKeyframe(0.5f, "Full Tricep Extension & Flare", shoulderFlexion = 15f, shoulderAbduction = 12f, elbowFlexion = 5f, hipFlexion = 15f, kneeFlexion = 10f, spineFlexion = 0f, equipmentYOffset = 0.05f, equipmentWidthOffset = 0.28f, primaryTension = 1.0f, secondaryTension = 0.4f),
            BiomechanicalKeyframe(1.0f, "Controlled Upward Release", shoulderFlexion = 15f, shoulderAbduction = 10f, elbowFlexion = 95f, hipFlexion = 15f, kneeFlexion = 10f, spineFlexion = 0f, equipmentYOffset = 0.42f, equipmentWidthOffset = 0.15f, primaryTension = 0.3f, secondaryTension = 0.2f)
        )
    ),

    // 11. Leg Extension Machine
    Exercise3d(
        id = "leg_extension",
        name = "Leg Extension Machine",
        category = "Legs",
        equipment = "Machine",
        difficulty = "Beginner",
        summary = "Strict open-chain quadriceps isolation targeting the rectus femoris and vastus lateralis teardrop.",
        primaryMuscles = listOf(
            MuscleRole(
                name = "Quadriceps Femoris (Rectus Femoris, Vastus Medialis)",
                commonName = "Quads (Teardrop)",
                anatomicalFunction = "Isolated open-kinetic-chain knee extension.",
                isPrimary = true
            )
        ),
        secondaryMuscles = listOf(
            MuscleRole(
                name = "Tensor Fasciae Latae",
                commonName = "Hip Stabilizer",
                anatomicalFunction = "Assists in stabilizing the pelvis during seated extension.",
                isPrimary = false
            )
        ),
        setupInstructions = listOf(
            "Adjust backrest so knees align directly with the machine's pivot axis.",
            "Set shin pad to rest comfortably against the lower shins just above ankles.",
            "Grip side handles securely to keep hips planted flat in seat."
        ),
        executionSteps = listOf(
            "Extend knees smoothly to raise shin pad until legs are fully straight.",
            "Squeeze quadriceps hard at top of motion for 1 full second.",
            "Avoid locking knees violently with snapping force.",
            "Lower weight stack under control over 2-3 seconds without letting plates slam."
        ),
        breathingGuidance = "Exhale smoothly as you extend legs to horizontal; inhale as you slowly lower the shin pad.",
        commonMistakes = listOf(
            "Using momentum and kicking weights upward with excessive speed.",
            "Letting buttocks lift off the seat during heavy reps.",
            "Positioning knees ahead of or behind the machine's axis of rotation."
        ),
        safetyNotes = "Do not violently hyperextend knees at the peak; maintain smooth muscular control.",
        assetMetadata = ExerciseAssetMetadata(
            engineType = AnimationEngineType.GLB_ASSET,
            assetIdentifier = "models/human_character.glb",
            providerName = "Khronos Group / Cesium (CC-BY 4.0)",
            licenseType = "Creative Commons Attribution 4.0 (CC-BY 4.0)",
            attribution = "CesiumMan rigged 3D human character by Cesium / Analytical Graphics Inc. licensed under CC-BY 4.0.",
            isLocalAssetReady = true
        ),
        keyframes = listOf(
            BiomechanicalKeyframe(0.0f, "Bottom Seated Pad Rest", shoulderFlexion = 0f, shoulderAbduction = 10f, elbowFlexion = 30f, hipFlexion = 85f, kneeFlexion = 95f, spineFlexion = 0f, equipmentYOffset = 0.05f, primaryTension = 0.2f, secondaryTension = 0.1f),
            BiomechanicalKeyframe(0.5f, "Horizontal Knee Extension Lockout", shoulderFlexion = 0f, shoulderAbduction = 10f, elbowFlexion = 30f, hipFlexion = 85f, kneeFlexion = 5f, spineFlexion = 0f, equipmentYOffset = 0.45f, primaryTension = 1.0f, secondaryTension = 0.3f),
            BiomechanicalKeyframe(1.0f, "Controlled Eccentric Lowering", shoulderFlexion = 0f, shoulderAbduction = 10f, elbowFlexion = 30f, hipFlexion = 85f, kneeFlexion = 95f, spineFlexion = 0f, equipmentYOffset = 0.05f, primaryTension = 0.2f, secondaryTension = 0.1f)
        )
    ),

    // 12. Lying Leg Curl
    Exercise3d(
        id = "leg_curl",
        name = "Lying Leg Curl",
        category = "Legs",
        equipment = "Machine",
        difficulty = "Beginner",
        summary = "Direct open-chain hamstring isolation focusing on knee flexion and posterior leg thickness.",
        primaryMuscles = listOf(
            MuscleRole(
                name = "Hamstrings (Biceps Femoris, Semitendinosus)",
                commonName = "Hamstrings",
                anatomicalFunction = "Flexes the knee joint against resistance.",
                isPrimary = true
            )
        ),
        secondaryMuscles = listOf(
            MuscleRole(
                name = "Gastrocnemius",
                commonName = "Calves",
                anatomicalFunction = "Assists knee flexion when ankle is dorsiflexed.",
                isPrimary = false
            ),
            MuscleRole(
                name = "Gracilis & Sartorius",
                commonName = "Inner Thigh Assist",
                anatomicalFunction = "Synergistic knee flexors.",
                isPrimary = false
            )
        ),
        setupInstructions = listOf(
            "Lie prone on bench with kneecaps resting just off the edge of the pad.",
            "Adjust roller pad to rest comfortably against back of lower calves/Achilles.",
            "Grip side handles and press hips firmly into the bench."
        ),
        executionSteps = listOf(
            "Curl heels toward buttocks by flexing knees smoothly.",
            "Keep hips glued to pad; do not allow lower back to arch upward.",
            "Squeeze hamstrings at the peak of the curl for 1 second.",
            "Lower pad slowly until legs are nearly fully extended."
        ),
        breathingGuidance = "Exhale as you curl the pad toward glutes; inhale as you extend legs under control.",
        commonMistakes = listOf(
            "Lifting hips and pelvis off the pad to compensate for weak hamstrings.",
            "Using calf momentum to kick the pad upward.",
            "Letting weights crash down on the stack."
        ),
        safetyNotes = "Keep pelvis pressed down into the bench to avoid excessive compressive stress on lumbar vertebrae.",
        assetMetadata = ExerciseAssetMetadata(
            engineType = AnimationEngineType.GLB_ASSET,
            assetIdentifier = "models/human_character.glb",
            providerName = "Khronos Group / Cesium (CC-BY 4.0)",
            licenseType = "Creative Commons Attribution 4.0 (CC-BY 4.0)",
            attribution = "CesiumMan rigged 3D human character by Cesium / Analytical Graphics Inc. licensed under CC-BY 4.0.",
            isLocalAssetReady = true
        ),
        keyframes = listOf(
            BiomechanicalKeyframe(0.0f, "Prone Extended Starting Line", shoulderFlexion = 30f, shoulderAbduction = 10f, elbowFlexion = 45f, hipFlexion = 10f, kneeFlexion = 5f, spineFlexion = 0f, equipmentYOffset = 0.05f, primaryTension = 0.2f, secondaryTension = 0.1f),
            BiomechanicalKeyframe(0.5f, "Peak Hamstring Knee Flexion", shoulderFlexion = 30f, shoulderAbduction = 10f, elbowFlexion = 45f, hipFlexion = 10f, kneeFlexion = 120f, spineFlexion = 0f, equipmentYOffset = 0.40f, primaryTension = 1.0f, secondaryTension = 0.4f),
            BiomechanicalKeyframe(1.0f, "Controlled Extension Return", shoulderFlexion = 30f, shoulderAbduction = 10f, elbowFlexion = 45f, hipFlexion = 10f, kneeFlexion = 5f, spineFlexion = 0f, equipmentYOffset = 0.05f, primaryTension = 0.2f, secondaryTension = 0.1f)
        )
    ),

    // 13. Calf Raises
    Exercise3d(
        id = "calf_raises",
        name = "Standing Calf Raises",
        category = "Legs",
        equipment = "Machine",
        difficulty = "Beginner",
        summary = "Vertical ankle plantarflexion movement building calf density, achilles tendon resilience, and ankle stability.",
        primaryMuscles = listOf(
            MuscleRole(
                name = "Gastrocnemius",
                commonName = "Calf (Diamond Belly)",
                anatomicalFunction = "Performs powerful ankle plantarflexion with straight knee.",
                isPrimary = true
            ),
            MuscleRole(
                name = "Soleus",
                commonName = "Deep Calf",
                anatomicalFunction = "Assists in plantarflexion and postural stabilization.",
                isPrimary = true
            )
        ),
        secondaryMuscles = listOf(
            MuscleRole(
                name = "Tibialis Posterior & Plantaris",
                commonName = "Ankle Stabilizers",
                anatomicalFunction = "Supports foot arch and ankle alignment.",
                isPrimary = false
            )
        ),
        setupInstructions = listOf(
            "Place balls of feet on edge of calf block with heels hanging off freely.",
            "Rest shoulder pads comfortably across shoulders with spine neutral.",
            "Maintain knees straight with a micro-bend (never locked)."
        ),
        executionSteps = listOf(
            "Lower heels down into a deep calf stretch over 2 seconds.",
            "Pause in the stretched bottom position for 1 full second.",
            "Drive up through the big toe knuckle into full plantarflexion.",
            "Hold the top contraction at maximum height for 1-2 seconds before descending."
        ),
        breathingGuidance = "Exhale as you press onto the balls of your feet; inhale as you descend into deep calf stretch.",
        commonMistakes = listOf(
            "Bouncing rapidly at the bottom to exploit the Achilles stretch reflex rather than using muscular tension.",
            "Rolling onto outer edges of feet rather than driving through big toes.",
            "Bending knees to jump the weight up."
        ),
        safetyNotes = "Pause for at least 1 second at the bottom of every rep to eliminate passive tendon bounce and protect Achilles.",
        assetMetadata = ExerciseAssetMetadata(
            engineType = AnimationEngineType.GLB_ASSET,
            assetIdentifier = "models/human_character.glb",
            providerName = "Khronos Group / Cesium (CC-BY 4.0)",
            licenseType = "Creative Commons Attribution 4.0 (CC-BY 4.0)",
            attribution = "CesiumMan rigged 3D human character by Cesium / Analytical Graphics Inc. licensed under CC-BY 4.0.",
            isLocalAssetReady = true
        ),
        keyframes = listOf(
            BiomechanicalKeyframe(0.0f, "Deep Heel Stretch Below Block", shoulderFlexion = 0f, shoulderAbduction = 10f, elbowFlexion = 60f, hipFlexion = 0f, kneeFlexion = 5f, spineFlexion = 0f, equipmentYOffset = -0.08f, primaryTension = 0.5f, secondaryTension = 0.2f),
            BiomechanicalKeyframe(0.5f, "Peak Plantarflexion Elevation", shoulderFlexion = 0f, shoulderAbduction = 10f, elbowFlexion = 60f, hipFlexion = 0f, kneeFlexion = 5f, spineFlexion = 0f, equipmentYOffset = 0.15f, primaryTension = 1.0f, secondaryTension = 0.4f),
            BiomechanicalKeyframe(1.0f, "Controlled Descent to Stretch", shoulderFlexion = 0f, shoulderAbduction = 10f, elbowFlexion = 60f, hipFlexion = 0f, kneeFlexion = 5f, spineFlexion = 0f, equipmentYOffset = -0.08f, primaryTension = 0.5f, secondaryTension = 0.2f)
        )
    ),

    // 14. Forearm Plank
    Exercise3d(
        id = "plank",
        name = "Forearm Plank",
        category = "Core",
        equipment = "Bodyweight",
        difficulty = "Beginner",
        summary = "Fundamental isometric anti-extension core exercise training transverse abdominis and spinal stability.",
        primaryMuscles = listOf(
            MuscleRole(
                name = "Rectus Abdominis & Transverse Abdominis",
                commonName = "Core & Deep Abs",
                anatomicalFunction = "Resists spinal extension and increases intra-abdominal bracing.",
                isPrimary = true
            )
        ),
        secondaryMuscles = listOf(
            MuscleRole(
                name = "Obliques (Internal & External)",
                commonName = "Side Abs",
                anatomicalFunction = "Resists rotational and lateral shear forces.",
                isPrimary = false
            ),
            MuscleRole(
                name = "Gluteus Maximus & Quadriceps",
                commonName = "Glutes & Quads",
                anatomicalFunction = "Locks pelvis and knees in rigid neutral alignment.",
                isPrimary = false
            )
        ),
        setupInstructions = listOf(
            "Place forearms on the ground with elbows directly below shoulders.",
            "Extend legs straight back, resting on balls of feet hip-width apart.",
            "Maintain a straight line from back of head through shoulders, hips, and heels."
        ),
        executionSteps = listOf(
            "Tuck pelvis slightly (posterior pelvic tilt) and squeeze glutes hard.",
            "Pull belly button in toward spine to brace core like preparing for a punch.",
            "Actively push the floor away through forearms to avoid collapsing between shoulder blades.",
            "Breathe steadily into the ribcage while maintaining maximum abdominal tension."
        ),
        breathingGuidance = "Breathe steadily and diaphragmatically through the nose without relaxing abdominal wall brace.",
        commonMistakes = listOf(
            "Allowing hips to sag toward floor, putting immense shear stress on lumbar spine.",
            "Piking hips high into the air like a downward dog.",
            "Holding your breath and letting abdominal pressure collapse."
        ),
        safetyNotes = "Discontinue the set the instant your lower back begins to sag; poor form under fatigue can strain lumbar discs.",
        assetMetadata = ExerciseAssetMetadata(
            engineType = AnimationEngineType.GLB_ASSET,
            assetIdentifier = "models/human_character.glb",
            providerName = "Khronos Group / Cesium (CC-BY 4.0)",
            licenseType = "Creative Commons Attribution 4.0 (CC-BY 4.0)",
            attribution = "CesiumMan rigged 3D human character by Cesium / Analytical Graphics Inc. licensed under CC-BY 4.0.",
            isLocalAssetReady = true
        ),
        keyframes = listOf(
            BiomechanicalKeyframe(0.0f, "Rigid Neutral Plank Line", shoulderFlexion = 80f, shoulderAbduction = 15f, elbowFlexion = 90f, hipFlexion = 0f, kneeFlexion = 0f, spineFlexion = -2f, equipmentYOffset = 0.0f, primaryTension = 0.95f, secondaryTension = 0.8f),
            BiomechanicalKeyframe(0.5f, "Active Isometric Core Squeeze", shoulderFlexion = 80f, shoulderAbduction = 15f, elbowFlexion = 90f, hipFlexion = 0f, kneeFlexion = 0f, spineFlexion = -2f, equipmentYOffset = 0.0f, primaryTension = 1.0f, secondaryTension = 0.85f),
            BiomechanicalKeyframe(1.0f, "Steady Sustained Tension", shoulderFlexion = 80f, shoulderAbduction = 15f, elbowFlexion = 90f, hipFlexion = 0f, kneeFlexion = 0f, spineFlexion = -2f, equipmentYOffset = 0.0f, primaryTension = 0.95f, secondaryTension = 0.8f)
        )
    )
)
