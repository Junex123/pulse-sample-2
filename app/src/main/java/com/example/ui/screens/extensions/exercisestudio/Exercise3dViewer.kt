package com.example.ui.screens.extensions.exercisestudio

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.foundation.clickable
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import android.annotation.SuppressLint
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BiomechanicalKeyframe
import com.example.data.model.Exercise3d
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * 3D Vector representing a point in 3D biomechanical coordinate space.
 */
data class Vec3(val x: Float, val y: Float, val z: Float) {
    operator fun plus(o: Vec3) = Vec3(x + o.x, y + o.y, z + o.z)
    operator fun minus(o: Vec3) = Vec3(x - o.x, y - o.y, z - o.z)
    operator fun times(s: Float) = Vec3(x * s, y * s, z * s)
}

/**
 * 3D Limb/Bone segment with depth for painter's algorithm z-sorting.
 */
data class RenderSegment3D(
    val start: Vec3,
    val end: Vec3,
    val radius: Float,
    val color: Color,
    val isPrimaryMuscle: Boolean = false,
    val isSecondaryMuscle: Boolean = false
) {
    val midZ: Float get() = (start.z + end.z) / 2f
}

/**
 * JavaScript interface bridge communicating between Android Compose and 3D WebGL runtime.
 */
class Android3dBridge(
    private val onModelLoaded: (String, Int, Int) -> Unit,
    private val onProgress: (Float, String) -> Unit,
    private val onError: (String) -> Unit
) {
    @JavascriptInterface
    fun onModelLoaded(name: String, boneCount: Int, animCount: Int) {
        onModelLoaded(name, boneCount, animCount)
    }

    @JavascriptInterface
    fun onProgressUpdate(progress: Float, phase: String) {
        onProgress(progress, phase)
    }

    @JavascriptInterface
    fun onModelError(err: String) {
        onError(err)
    }
}

/**
 * Hardware-accelerated 3D WebGL renderer hosting genuine rigged 3D human character models (GLB).
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun GenuineGlb3dWebView(
    exerciseId: String,
    isPlaying: Boolean,
    playbackSpeed: Float,
    cameraPreset: String,
    modifier: Modifier = Modifier,
    onModelLoaded: (String, Int, Int) -> Unit = { _, _, _ -> },
    onProgressUpdate: (Float, String) -> Unit = { _, _ -> }
) {
    var webViewRef by remember { mutableStateOf<WebView?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            try {
                webViewRef?.evaluateJavascript("if (window.cleanupViewer) window.cleanupViewer();", null)
                webViewRef?.stopLoading()
                webViewRef?.destroy()
            } catch (_: Exception) {}
            webViewRef = null
        }
    }

    LaunchedEffect(isPlaying) {
        webViewRef?.evaluateJavascript(
            if (isPlaying) "if (window.play) window.play();" else "if (window.pause) window.pause();",
            null
        )
    }

    LaunchedEffect(playbackSpeed) {
        webViewRef?.evaluateJavascript("if (window.setSpeed) window.setSpeed($playbackSpeed);", null)
    }

    LaunchedEffect(exerciseId) {
        webViewRef?.evaluateJavascript("if (window.setExercise) window.setExercise('$exerciseId');", null)
    }

    LaunchedEffect(cameraPreset) {
        if (cameraPreset.isNotEmpty()) {
            if (cameraPreset == "reset") {
                webViewRef?.evaluateJavascript("if (window.resetCamera) window.resetCamera();", null)
            } else {
                webViewRef?.evaluateJavascript("if (window.setCameraPreset) window.setCameraPreset('$cameraPreset');", null)
            }
        }
    }

    AndroidView(
        modifier = modifier.testTag("genuine_glb_3d_webview"),
        factory = { ctx ->
            WebView(ctx).apply {
                settings.javaScriptEnabled = true
                settings.allowFileAccess = true
                settings.allowFileAccessFromFileURLs = true
                settings.allowUniversalAccessFromFileURLs = true
                settings.domStorageEnabled = true
                setBackgroundColor(0x00000000)

                addJavascriptInterface(
                    Android3dBridge(
                        onModelLoaded = { name, bones, anims -> onModelLoaded(name, bones, anims) },
                        onProgress = { p, ph -> onProgressUpdate(p, ph) },
                        onError = {}
                    ),
                    "AndroidBridge"
                )

                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        view?.evaluateJavascript("if (window.setExercise) window.setExercise('$exerciseId');", null)
                        view?.evaluateJavascript("if (window.setSpeed) window.setSpeed($playbackSpeed);", null)
                        if (cameraPreset.isNotEmpty()) {
                            view?.evaluateJavascript("if (window.setCameraPreset) window.setCameraPreset('$cameraPreset');", null)
                        }
                    }
                }

                loadUrl("file:///android_asset/3d/viewer.html")
                webViewRef = this
            }
        },
        update = { webView ->
            webViewRef = webView
        }
    )
}

/**
 * Interactive 3D Exercise Model Viewer.
 *
 * Supports:
 * - Genuine 3D Human Skeletal Rig (GLB) with hardware-accelerated WebGL linear blend skinning
 * - Biomechanical Rig Fallback with coordinate projection & simulated muscle vectors
 * - Mode toggle allowing instant switching between Genuine 3D and Biomechanical Rig
 * - 360 degree touch orbit rotation (pitch & yaw)
 * - Preset view angles: Front, 45 Iso, Side (Profile), Top
 * - Playback controls: Play, Pause, Restart, Loop
 * - Playback speed: 0.5x, 0.75x, 1x, 1.25x
 * - Timeline scrubbing
 * - Honest asset disclosure without fake claims
 */
@Composable
fun Exercise3dViewer(
    exercise: Exercise3d?,
    modifier: Modifier = Modifier,
    initialPlayState: Boolean = true
) {
    if (exercise == null) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(340.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF0F172A)),
            contentAlignment = Alignment.Center
        ) {
            Text("Select an exercise to view 3D demonstration", color = Color(0xFF94A3B8))
        }
        return
    }

    // Playback state
    var isPlaying by remember { mutableStateOf(initialPlayState) }
    var isLooping by remember { mutableStateOf(true) }
    var playbackSpeed by remember { mutableFloatStateOf(1.0f) }
    var progress by remember { mutableFloatStateOf(0.0f) }
    var isLoading by remember { mutableStateOf(false) }

    // Camera 3D Orbit angles
    var cameraAzimuth by remember { mutableFloatStateOf(25f) } // degrees around Y axis
    var cameraElevation by remember { mutableFloatStateOf(10f) } // degrees up/down
    var cameraPreset by remember { mutableStateOf("angle") }

    // Frame loop for smooth animation
    LaunchedEffect(isPlaying, playbackSpeed, isLooping, exercise.id) {
        var lastFrameTimeNanos = 0L
        while (isPlaying) {
            withFrameNanos { frameTimeNanos ->
                if (lastFrameTimeNanos > 0L) {
                    val dtSeconds = (frameTimeNanos - lastFrameTimeNanos) / 1_000_000_000f
                    val step = (dtSeconds / 3.5f) * playbackSpeed
                    var next = progress + step
                    if (next >= 1.0f) {
                        if (isLooping) {
                            next %= 1.0f
                        } else {
                            next = 1.0f
                            isPlaying = false
                        }
                    }
                    progress = next
                }
                lastFrameTimeNanos = frameTimeNanos
            }
        }
    }

    // Interpolate current keyframe
    val keyframes = exercise.keyframes
    val currentKeyframe = interpolateKeyframe(keyframes, progress)

    CardSurfaceViewer(
        modifier = modifier,
        exercise = exercise,
        progress = progress,
        currentPhase = currentKeyframe.phaseName,
        isPlaying = isPlaying,
        isLooping = isLooping,
        playbackSpeed = playbackSpeed,
        cameraAzimuth = cameraAzimuth,
        cameraElevation = cameraElevation,
        cameraPreset = cameraPreset,
        isLoading = isLoading,
        onTogglePlay = { isPlaying = !isPlaying },
        onToggleLoop = { isLooping = !isLooping },
        onRestart = { progress = 0.0f; isPlaying = true },
        onChangeSpeed = { playbackSpeed = it },
        onSetCameraAngle = { az, el, preset ->
            cameraAzimuth = az
            cameraElevation = el
            cameraPreset = preset
        },
        onDragCamera = { dx, dy ->
            cameraAzimuth = (cameraAzimuth + dx * 0.4f) % 360f
            cameraElevation = (cameraElevation - dy * 0.4f).coerceIn(-65f, 65f)
            cameraPreset = ""
        },
        onProgressScrub = { newProg ->
            progress = newProg
        },
        exerciseKeyframe = currentKeyframe
    )
}

@Composable
private fun CardSurfaceViewer(
    modifier: Modifier,
    exercise: Exercise3d,
    progress: Float,
    currentPhase: String,
    isPlaying: Boolean,
    isLooping: Boolean,
    playbackSpeed: Float,
    cameraAzimuth: Float,
    cameraElevation: Float,
    cameraPreset: String,
    isLoading: Boolean,
    onTogglePlay: () -> Unit,
    onToggleLoop: () -> Unit,
    onRestart: () -> Unit,
    onChangeSpeed: (Float) -> Unit,
    onSetCameraAngle: (Float, Float, String) -> Unit,
    onDragCamera: (Float, Float) -> Unit,
    onProgressScrub: (Float) -> Unit,
    exerciseKeyframe: BiomechanicalKeyframe
) {
    // Mode toggle: Default to Genuine 3D if local GLB is ready
    var isGlbMode by remember(exercise.id) { mutableStateOf(exercise.assetMetadata.isLocalAssetReady) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
            .testTag("exercise_3d_viewer_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF090D16))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Top Status Bar: Engine Mode Selector & Camera Angle Presets
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mode switcher: Genuine 3D Character vs Biomechanical Math Rig
                Row(
                    modifier = Modifier
                        .background(Color(0xFF1E293B), RoundedCornerShape(10.dp))
                        .padding(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isGlbMode) Color(0xFF4F46E5) else Color.Transparent)
                            .clickable { isGlbMode = true }
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                            .testTag("mode_genuine_3d_btn")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = if (isGlbMode) Color.White else Color(0xFF94A3B8),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Genuine 3D (GLB)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isGlbMode) Color.White else Color(0xFF94A3B8)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (!isGlbMode) Color(0xFF334155) else Color.Transparent)
                            .clickable { isGlbMode = false }
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                            .testTag("mode_biomechanical_btn")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timeline,
                                contentDescription = null,
                                tint = if (!isGlbMode) Color.White else Color(0xFF94A3B8),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Biomechanical Rig",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (!isGlbMode) Color.White else Color(0xFF94A3B8)
                            )
                        }
                    }
                }

                // Preset camera buttons
                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    CameraAngleButton(label = "Front", isActive = cameraPreset == "front" || (cameraPreset.isEmpty() && cameraAzimuth in -10f..10f)) {
                        onSetCameraAngle(0f, 0f, "front")
                    }
                    CameraAngleButton(label = "45°", isActive = cameraPreset == "angle" || (cameraPreset.isEmpty() && cameraAzimuth in 20f..50f)) {
                        onSetCameraAngle(35f, 15f, "angle")
                    }
                    CameraAngleButton(label = "Side", isActive = cameraPreset == "side" || (cameraPreset.isEmpty() && cameraAzimuth in 75f..105f)) {
                        onSetCameraAngle(90f, 0f, "side")
                    }
                    CameraAngleButton(label = "Rear", isActive = cameraPreset == "rear" || (cameraPreset.isEmpty() && (cameraAzimuth > 160f || cameraAzimuth < -160f))) {
                        onSetCameraAngle(180f, 0f, "rear")
                    }
                    CameraAngleButton(label = "Top", isActive = cameraPreset == "top" || (cameraPreset.isEmpty() && cameraElevation > 40f)) {
                        onSetCameraAngle(0f, 55f, "top")
                    }
                    CameraAngleButton(label = "Reset", isActive = false) {
                        onSetCameraAngle(35f, 15f, "reset")
                    }
                }
            }

            // 3D Viewport: Genuine GLB Character or Biomechanical Fallback
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(310.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF0F172A), Color(0xFF020617))
                        )
                    )
                    .testTag("exercise_3d_viewport"),
                contentAlignment = Alignment.Center
            ) {
                if (isGlbMode) {
                    GenuineGlb3dWebView(
                        exerciseId = exercise.id,
                        isPlaying = isPlaying,
                        playbackSpeed = playbackSpeed,
                        cameraPreset = cameraPreset,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    onDragCamera(dragAmount.x, dragAmount.y)
                                }
                            }
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height

                            // Soft ambient gradient center pool
                            drawCircle(
                                brush = Brush.radialGradient(
                                    listOf(Color(0xFF1E293B).copy(alpha = 0.6f), Color.Transparent),
                                    center = Offset(w / 2f, h * 0.72f),
                                    radius = w * 0.45f
                                ),
                                radius = w * 0.45f,
                                center = Offset(w / 2f, h * 0.72f)
                            )

                            // Ground isometric grid lines
                            val groundY = h * 0.78f
                            val gridColor = Color(0xFF334155).copy(alpha = 0.35f)
                            for (i in -4..4) {
                                val xOffset = i * 42f
                                drawLine(
                                    color = gridColor,
                                    start = Offset(w / 2f + xOffset * 0.6f, groundY - 20f),
                                    end = Offset(w / 2f + xOffset * 1.5f, groundY + 50f),
                                    strokeWidth = 1f
                                )
                            }
                            drawLine(
                                color = Color(0xFF475569).copy(alpha = 0.5f),
                                start = Offset(w * 0.15f, groundY),
                                end = Offset(w * 0.85f, groundY),
                                strokeWidth = 1.5f
                            )

                            // Render 3D Human Skeletal Mesh and Equipment
                            drawBiomechanicalRig3D(
                                kf = exerciseKeyframe,
                                exercise = exercise,
                                azimuthDeg = cameraAzimuth,
                                elevationDeg = cameraElevation,
                                viewportWidth = w,
                                viewportHeight = h
                            )
                        }
                    }
                }

                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(36.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 3.dp
                    )
                }

                // Phase Badge in top-left corner
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .background(Color(0xCC0F172A), RoundedCornerShape(8.dp))
                        .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = currentPhase,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFE2E8F0)
                    )
                }

                // Interactive 360 degree drag hint overlay in bottom-right corner
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                        .background(Color(0x80000000), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ViewInAr,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "360° Touch Orbit",
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            // Honesty and Licensing Transparency Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0D1524))
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isGlbMode) Icons.Default.Person else Icons.Default.Timeline,
                        contentDescription = null,
                        tint = if (isGlbMode) Color(0xFF10B981) else Color(0xFF60A5FA),
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isGlbMode) {
                            "Genuine 3D Skeletal Rig • Khronos CC-BY 4.0"
                        } else {
                            if (exercise.assetMetadata.isLocalAssetReady) {
                                "Biomechanical Fallback Engine (Native 3D Math)"
                            } else {
                                "Biomechanical Math Rig (Genuine GLB Pending Provider)"
                            }
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isGlbMode) Color(0xFF6EE7B7) else Color(0xFF93C5FD)
                    )
                }

                Text(
                    text = if (isGlbMode) "GLB Active" else "Fallback Active",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF94A3B8)
                )
            }

            if (isGlbMode) {
                Text(
                    text = "*Realistic 3D human character with skeletal linear blend skinning. Primary & secondary muscle targets are categorized in anatomical cards below.",
                    fontSize = 10.sp,
                    color = Color(0xFF94A3B8),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp),
                    lineHeight = 13.sp
                )
            }

            // Timeline Scrub Bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = Color(0xFF6366F1),
                trackColor = Color(0xFF1E293B)
            )

            // Bottom Controller Strip: Play, Pause, Speed, Restart, Loop
            Surface(
                color = Color(0xFF0F172A),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Play/Pause & Restart controls
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = onTogglePlay,
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF6366F1))
                                    .testTag("playback_toggle_btn")
                            ) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (isPlaying) "Pause" else "Play",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            IconButton(
                                onClick = onRestart,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF1E293B))
                                    .testTag("playback_restart_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Restart",
                                    tint = Color(0xFFCBD5E1),
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            IconButton(
                                onClick = onToggleLoop,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(if (isLooping) Color(0xFF312E81) else Color(0xFF1E293B))
                                    .testTag("playback_loop_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Loop,
                                    contentDescription = "Loop",
                                    tint = if (isLooping) Color(0xFFA5B4FC) else Color(0xFF64748B),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        // Speed selector pills
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Speed:", fontSize = 11.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.SemiBold)
                            val speeds = listOf(0.5f, 0.75f, 1.0f, 1.25f)
                            speeds.forEach { speedVal ->
                                val isSelected = playbackSpeed == speedVal
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) Color(0xFF4F46E5) else Color(0xFF1E293B))
                                        .border(1.dp, if (isSelected) Color(0xFF818CF8) else Color.Transparent, RoundedCornerShape(8.dp))
                                        .pointerInput(speedVal) {
                                            detectDragGestures { _, _ -> }
                                        }
                                        .clickable { onChangeSpeed(speedVal) }
                                        .padding(horizontal = 7.dp, vertical = 4.dp)
                                        .testTag("speed_${speedVal}x"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${speedVal}x",
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.White else Color(0xFF94A3B8)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Muscle Highlighting Legend
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFFFF5722)))
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("Primary Target Muscle", fontSize = 11.sp, color = Color(0xFFFF8A65), fontWeight = FontWeight.SemiBold)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFF26A69A)))
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("Secondary Synergist", fontSize = 11.sp, color = Color(0xFF80CBC4), fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CameraAngleButton(label: String, isActive: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isActive) Color(0xFF3B82F6) else Color(0xFF1E293B))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isActive) Color.White else Color(0xFF94A3B8)
        )
    }
}

/**
 * 3D Engine Draw Pipeline:
 * - Rotates points in 3D around Y and X axes.
 * - Applies 3D perspective projection onto the 2D Canvas viewport.
 * - Sorts segments by Z depth (painter's algorithm) to prevent inverted rendering.
 * - Renders anatomical limb joints, muscle highlights, and equipment.
 */
private fun DrawScope.drawBiomechanicalRig3D(
    kf: BiomechanicalKeyframe,
    exercise: Exercise3d,
    azimuthDeg: Float,
    elevationDeg: Float,
    viewportWidth: Float,
    viewportHeight: Float
) {
    val centerX = viewportWidth / 2f
    val centerY = viewportHeight * 0.52f
    val scale = viewportWidth * 0.38f

    val azRad = Math.toRadians(azimuthDeg.toDouble()).toFloat()
    val elRad = Math.toRadians(elevationDeg.toDouble()).toFloat()

    val cosAz = cos(azRad)
    val sinAz = sin(azRad)
    val cosEl = cos(elRad)
    val sinEl = sin(elRad)

    fun project(v: Vec3): Offset {
        // Rotate around Y-axis (Azimuth)
        val x1 = v.x * cosAz + v.z * sinAz
        val z1 = -v.x * sinAz + v.z * cosAz

        // Rotate around X-axis (Elevation)
        val y2 = v.y * cosEl - z1 * sinEl
        val z2 = v.y * sinEl + z1 * cosEl

        // Perspective camera transform
        val camDist = 3.5f
        val factor = camDist / (camDist + z2)

        val px = centerX + x1 * scale * factor
        val py = centerY - y2 * scale * factor
        return Offset(px, py)
    }

    // Determine exercise-specific anatomical muscle engagement
    val isChestTarget = exercise.category.equals("Chest", ignoreCase = true)
    val isBackTarget = exercise.category.equals("Back", ignoreCase = true)
    val isShoulderTarget = exercise.category.equals("Shoulders", ignoreCase = true)
    val isLegsTarget = exercise.category.equals("Legs", ignoreCase = true)
    val isBicepsTarget = exercise.category.equals("Biceps", ignoreCase = true)
    val isTricepsTarget = exercise.category.equals("Triceps", ignoreCase = true)
    val isCoreTarget = exercise.category.equals("Core", ignoreCase = true)

    val primaryGlow = Color(0xFFFF5722) // Electric Coral
    val secondaryGlow = Color(0xFF26A69A) // Cyan / Teal
    val neutralBone = Color(0xFF475569) // Slate Blue
    val skinAccent = Color(0xFF94A3B8) // Light Slate

    // Base Skeletal Coordinates relative to Pelvis at (0, 0, 0)
    val pelvis = Vec3(0f, -0.1f, 0f)

    // Spine & Torso with slight spine flexion
    val spineAngleRad = Math.toRadians(kf.spineFlexion.toDouble()).toFloat()
    val midSpine = Vec3(0f, 0.25f, -sin(spineAngleRad) * 0.15f)
    val upperChest = Vec3(0f, 0.60f, -sin(spineAngleRad) * 0.25f)
    val head = Vec3(0f, 0.85f, upperChest.z)

    // Shoulder joints
    val shoulderSpan = 0.32f
    val leftShoulder = upperChest + Vec3(-shoulderSpan, 0f, 0f)
    val rightShoulder = upperChest + Vec3(shoulderSpan, 0f, 0f)

    // Compute Left/Right Elbow and Hand positions based on shoulder and elbow angles
    val shFlRad = Math.toRadians(kf.shoulderFlexion.toDouble()).toFloat()
    val shAbRad = Math.toRadians(kf.shoulderAbduction.toDouble()).toFloat()
    val elFlRad = Math.toRadians(kf.elbowFlexion.toDouble()).toFloat()

    val upperArmLen = 0.35f
    val forearmLen = 0.32f

    // Elbow vectors
    val leftElbow = leftShoulder + Vec3(
        -sin(shAbRad) * upperArmLen,
        -cos(shFlRad) * upperArmLen,
        sin(shFlRad) * upperArmLen
    )
    val rightElbow = rightShoulder + Vec3(
        sin(shAbRad) * upperArmLen,
        -cos(shFlRad) * upperArmLen,
        sin(shFlRad) * upperArmLen
    )

    // Hands
    val leftHand = leftElbow + Vec3(
        -sin(shAbRad * 0.5f) * forearmLen * 0.5f,
        sin(elFlRad) * forearmLen * 0.7f,
        cos(elFlRad) * forearmLen * 0.8f
    )
    val rightHand = rightElbow + Vec3(
        sin(shAbRad * 0.5f) * forearmLen * 0.5f,
        sin(elFlRad) * forearmLen * 0.7f,
        cos(elFlRad) * forearmLen * 0.8f
    )

    // Legs / Knees / Feet based on hip and knee flexion
    val hipFlRad = Math.toRadians(kf.hipFlexion.toDouble()).toFloat()
    val knFlRad = Math.toRadians(kf.kneeFlexion.toDouble()).toFloat()

    val hipSpan = 0.18f
    val thighLen = 0.44f
    val calfLen = 0.42f

    val leftHip = pelvis + Vec3(-hipSpan, 0f, 0f)
    val rightHip = pelvis + Vec3(hipSpan, 0f, 0f)

    val leftKnee = leftHip + Vec3(
        -0.04f,
        -cos(hipFlRad) * thighLen,
        sin(hipFlRad) * thighLen
    )
    val rightKnee = rightHip + Vec3(
        0.04f,
        -cos(hipFlRad) * thighLen,
        sin(hipFlRad) * thighLen
    )

    val leftFoot = leftKnee + Vec3(
        0f,
        -cos(knFlRad * 0.6f) * calfLen,
        -sin(knFlRad * 0.6f) * calfLen * 0.5f
    )
    val rightFoot = rightKnee + Vec3(
        0f,
        -cos(knFlRad * 0.6f) * calfLen,
        -sin(knFlRad * 0.6f) * calfLen * 0.5f
    )

    // Build segments with colors
    val segments = mutableListOf<RenderSegment3D>()

    // Torso segments
    val chestColor = if (isChestTarget) primaryGlow else if (isShoulderTarget) secondaryGlow else neutralBone
    val backColor = if (isBackTarget) primaryGlow else neutralBone
    val coreColor = if (isCoreTarget) primaryGlow else neutralBone

    segments.add(RenderSegment3D(pelvis, midSpine, 18f, coreColor, isPrimaryMuscle = isCoreTarget))
    segments.add(RenderSegment3D(midSpine, upperChest, 22f, chestColor, isPrimaryMuscle = isChestTarget))
    segments.add(RenderSegment3D(upperChest, head, 14f, skinAccent))

    // Shoulders
    val shoulderColor = if (isShoulderTarget) primaryGlow else if (isChestTarget) secondaryGlow else neutralBone
    segments.add(RenderSegment3D(upperChest, leftShoulder, 16f, shoulderColor, isPrimaryMuscle = isShoulderTarget))
    segments.add(RenderSegment3D(upperChest, rightShoulder, 16f, shoulderColor, isPrimaryMuscle = isShoulderTarget))

    // Arms
    val armColor = if (isBicepsTarget) primaryGlow else if (isTricepsTarget) primaryGlow else if (isChestTarget || isBackTarget) secondaryGlow else neutralBone
    segments.add(RenderSegment3D(leftShoulder, leftElbow, 15f, armColor, isPrimaryMuscle = isBicepsTarget || isTricepsTarget))
    segments.add(RenderSegment3D(rightShoulder, rightElbow, 15f, armColor, isPrimaryMuscle = isBicepsTarget || isTricepsTarget))

    segments.add(RenderSegment3D(leftElbow, leftHand, 12f, skinAccent))
    segments.add(RenderSegment3D(rightElbow, rightHand, 12f, skinAccent))

    // Legs
    val quadColor = if (isLegsTarget) primaryGlow else neutralBone
    segments.add(RenderSegment3D(pelvis, leftHip, 14f, neutralBone))
    segments.add(RenderSegment3D(pelvis, rightHip, 14f, neutralBone))

    segments.add(RenderSegment3D(leftHip, leftKnee, 18f, quadColor, isPrimaryMuscle = isLegsTarget))
    segments.add(RenderSegment3D(rightHip, rightKnee, 18f, quadColor, isPrimaryMuscle = isLegsTarget))

    val calfColor = if (exercise.id == "calf_raises") primaryGlow else neutralBone
    segments.add(RenderSegment3D(leftKnee, leftFoot, 14f, calfColor, isPrimaryMuscle = exercise.id == "calf_raises"))
    segments.add(RenderSegment3D(rightKnee, rightFoot, 14f, calfColor, isPrimaryMuscle = exercise.id == "calf_raises"))

    // Sort segments by camera-depth (painter's algorithm)
    val sortedSegments = segments.sortedBy { it.midZ }

    // Render sorted bones/muscles
    sortedSegments.forEach { seg ->
        val p1 = project(seg.start)
        val p2 = project(seg.end)

        // Draw limb bone/muscle
        drawLine(
            color = seg.color,
            start = p1,
            end = p2,
            strokeWidth = seg.radius * (scale / 180f),
            cap = StrokeCap.Round
        )

        // Draw muscle glow effect if active
        if (seg.isPrimaryMuscle || seg.isSecondaryMuscle) {
            drawLine(
                color = seg.color.copy(alpha = 0.35f),
                start = p1,
                end = p2,
                strokeWidth = (seg.radius + 12f) * (scale / 180f),
                cap = StrokeCap.Round
            )
        }
    }

    // Render Head 3D Sphere
    val pHead = project(head)
    drawCircle(
        color = skinAccent,
        radius = 18f * (scale / 180f),
        center = pHead
    )

    // Render Equipment in 3D
    when (exercise.equipment) {
        "Barbell" -> {
            // Olympic Barbell: Steel bar connecting hands + weight plates at ends
            val barYOffset = kf.equipmentYOffset
            val barCenter = if (exercise.id == "barbell_squat") upperChest + Vec3(0f, 0.05f, -0.05f) else (leftHand + rightHand) * 0.5f

            val barHalfLength = 0.85f
            val barLeft = barCenter + Vec3(-barHalfLength, 0f, 0f)
            val barRight = barCenter + Vec3(barHalfLength, 0f, 0f)

            val pBarLeft = project(barLeft)
            val pBarRight = project(barRight)

            // Chrome Bar
            drawLine(
                color = Color(0xFFE2E8F0),
                start = pBarLeft,
                end = pBarRight,
                strokeWidth = 5f,
                cap = StrokeCap.Round
            )

            // Left Weight Plates (20kg Red Olympic discs)
            val plateColor = Color(0xFFDC2626)
            drawCircle(
                color = plateColor,
                radius = 22f * (scale / 180f),
                center = pBarLeft
            )
            drawCircle(
                color = Color(0xFF1E293B),
                radius = 7f * (scale / 180f),
                center = pBarLeft
            )

            // Right Weight Plates
            drawCircle(
                color = plateColor,
                radius = 22f * (scale / 180f),
                center = pBarRight
            )
            drawCircle(
                color = Color(0xFF1E293B),
                radius = 7f * (scale / 180f),
                center = pBarRight
            )
        }

        "Dumbbell" -> {
            // Render Hex Dumbbells in Left & Right Hands
            val pLHand = project(leftHand)
            val pRHand = project(rightHand)

            val dumbbellColor = Color(0xFF334155)
            val hexColor = Color(0xFF1E293B)

            // Left Dumbbell
            drawCircle(color = hexColor, radius = 14f * (scale / 180f), center = pLHand + Offset(-8f, 0f))
            drawCircle(color = hexColor, radius = 14f * (scale / 180f), center = pLHand + Offset(8f, 0f))
            drawLine(color = Color(0xFFCBD5E1), start = pLHand + Offset(-10f, 0f), end = pLHand + Offset(10f, 0f), strokeWidth = 3f)

            // Right Dumbbell
            drawCircle(color = hexColor, radius = 14f * (scale / 180f), center = pRHand + Offset(-8f, 0f))
            drawCircle(color = hexColor, radius = 14f * (scale / 180f), center = pRHand + Offset(8f, 0f))
            drawLine(color = Color(0xFFCBD5E1), start = pRHand + Offset(-10f, 0f), end = pRHand + Offset(10f, 0f), strokeWidth = 3f)
        }

        "Cable" -> {
            // Cable Pulley Line & Attachment Bar
            val pLHand = project(leftHand)
            val pRHand = project(rightHand)
            val pTopPulley = project(Vec3(0f, 1.4f, 0f))

            // Cable wire from overhead pulley
            drawLine(color = Color(0xFF94A3B8), start = pTopPulley, end = (pLHand + pRHand) * 0.5f, strokeWidth = 2.5f)

            // Lat bar / handle
            drawLine(
                color = Color(0xFFE2E8F0),
                start = pLHand + Offset(-15f, 0f),
                end = pRHand + Offset(15f, 0f),
                strokeWidth = 6f,
                cap = StrokeCap.Round
            )
        }
    }
}

/**
 * Linear keyframe interpolation solver.
 */
private fun interpolateKeyframe(keyframes: List<BiomechanicalKeyframe>, progress: Float): BiomechanicalKeyframe {
    if (keyframes.isEmpty()) {
        return BiomechanicalKeyframe(0f, "Standard Phase", 0f, 0f, 0f, 0f, 0f, 0f)
    }
    if (keyframes.size == 1) return keyframes.first()

    val clamped = progress.coerceIn(0f, 1f)

    // Find surrounding keyframes
    var k1 = keyframes.first()
    var k2 = keyframes.last()

    for (i in 0 until keyframes.size - 1) {
        if (clamped >= keyframes[i].progress && clamped <= keyframes[i + 1].progress) {
            k1 = keyframes[i]
            k2 = keyframes[i + 1]
            break
        }
    }

    val span = k2.progress - k1.progress
    val t = if (span > 0f) (clamped - k1.progress) / span else 0f

    // Smooth cosine interpolation for biomechanical organic fluidity
    val smoothT = (1f - cos(t * PI.toFloat())) / 2f

    fun lerp(a: Float, b: Float) = a + (b - a) * smoothT

    return BiomechanicalKeyframe(
        progress = clamped,
        phaseName = if (t < 0.5f) k1.phaseName else k2.phaseName,
        shoulderFlexion = lerp(k1.shoulderFlexion, k2.shoulderFlexion),
        shoulderAbduction = lerp(k1.shoulderAbduction, k2.shoulderAbduction),
        elbowFlexion = lerp(k1.elbowFlexion, k2.elbowFlexion),
        hipFlexion = lerp(k1.hipFlexion, k2.hipFlexion),
        kneeFlexion = lerp(k1.kneeFlexion, k2.kneeFlexion),
        spineFlexion = lerp(k1.spineFlexion, k2.spineFlexion),
        equipmentYOffset = lerp(k1.equipmentYOffset, k2.equipmentYOffset),
        equipmentWidthOffset = lerp(k1.equipmentWidthOffset, k2.equipmentWidthOffset),
        primaryTension = lerp(k1.primaryTension, k2.primaryTension),
        secondaryTension = lerp(k1.secondaryTension, k2.secondaryTension)
    )
}
