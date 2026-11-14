package com.example.ui.missions

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.MissionId
import com.example.data.MissionRunResult
import com.example.ui.components.FeedbackTone
import com.example.ui.components.GlassSpaceCard
import com.example.ui.components.MissionControlTopBar
import com.example.ui.components.ScienceFeedbackCard
import com.example.ui.theme.AlertCoral
import com.example.ui.theme.CosmicPurple
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElevatedNavy
import com.example.ui.theme.MissionBlue
import com.example.ui.theme.MutedStarText
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SoftIceWhite
import com.example.ui.theme.SpaceBlack
import com.example.ui.theme.StarlightAmber
import com.example.ui.theme.SuccessEmerald
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

private enum class OrbitOutcome {
    CRASH, CIRCULAR_ORBIT, ELLIPTICAL_ORBIT, ESCAPE
}

private data class OrbitSimulationResult(
    val points: List<Offset>,
    val outcome: OrbitOutcome
)

private data class OrbitChallenge(
    val title: String,
    val goalText: String,
    val targetOutcome: OrbitOutcome,
    val hintText: String
)

private val orbitChallenges = listOf(
    OrbitChallenge(
        title = "Challenge 1: Low Earth Orbit",
        goalText = "Place the satellite into a steady Circular Orbit around Earth! Hint: Aim near 0° (sideways) around 7.8 km/s.",
        targetOutcome = OrbitOutcome.CIRCULAR_ORBIT,
        hintText = "Sideways speed balances Earth's downward gravity so the satellite keeps falling around the curve of Earth!"
    ),
    OrbitChallenge(
        title = "Challenge 2: High Elliptical Orbit",
        goalText = "Boost speed slightly (around 8.8–9.8 km/s) near 0° to stretch the orbit into an oval (Elliptical Orbit) without escaping!",
        targetOutcome = OrbitOutcome.ELLIPTICAL_ORBIT,
        hintText = "More forward speed swings the satellite farther out before gravity pulls it back around."
    ),
    OrbitChallenge(
        title = "Challenge 3: Deep Space Escape",
        goalText = "Reach Earth's Escape Velocity (11.2+ km/s) to send a space probe past Earth's gravity toward the Moon!",
        targetOutcome = OrbitOutcome.ESCAPE,
        hintText = "Above ~11.1 km/s, Earth's gravity can no longer pull the spacecraft back into a closed loop."
    )
)

/**
 * Integrates 2D Newtonian gravity in normalized units where Earth radius = 1.0
 * and circular velocity at r = 1.55 is 7.8 km/s (mu = 94.3).
 */
private fun simulateTrajectory(speedKmS: Float, angleDeg: Float): OrbitSimulationResult {
    val mu = 94.3f
    val rStart = 1.55f
    var x = 0f
    var y = -rStart

    val rad = angleDeg * (PI.toFloat() / 180f)
    // Scale velocity into simulation units/sec so 7.8 km/s -> circular speed
    var vx = speedKmS * cos(rad)
    var vy = -speedKmS * sin(rad) // positive angle tilts outward (upward/negative y)

    val points = mutableListOf(Offset(x, y))
    val dt = 0.012f
    val steps = 320

    var crashed = false
    var escaped = false
    var minR = rStart
    var maxR = rStart

    for (i in 0 until steps) {
        val r = sqrt(x * x + y * y)
        if (r <= 1.04f) {
            crashed = true
            break
        }
        if (r >= 4.2f) {
            escaped = true
            break
        }
        if (r < minR) minR = r
        if (r > maxR) maxR = r

        val accel = -mu / (r * r * r)
        val ax = accel * x
        val ay = accel * y

        vx += ax * dt
        vy += ay * dt
        x += vx * dt
        y += vy * dt

        points.add(Offset(x, y))
    }

    // Specific orbital energy check: E = v^2 / 2 - mu / r
    val energy = 0.5f * speedKmS * speedKmS - mu / rStart
    val outcome = when {
        crashed -> OrbitOutcome.CRASH
        escaped || energy >= 0f -> OrbitOutcome.ESCAPE
        (maxR - minR) <= 0.35f -> OrbitOutcome.CIRCULAR_ORBIT
        else -> OrbitOutcome.ELLIPTICAL_ORBIT
    }

    return OrbitSimulationResult(points = points, outcome = outcome)
}

@Composable
fun Mission3OrbitBasicsScreen(
    onMissionComplete: (MissionRunResult) -> Unit,
    onBackClick: () -> Unit
) {
    var challengeIndex by remember { mutableIntStateOf(0) }
    val challenge = orbitChallenges[challengeIndex]

    var speedKmS by remember { mutableFloatStateOf(5.8f) }
    var angleDeg by remember { mutableFloatStateOf(-12f) }

    var launchCounter by remember { mutableIntStateOf(0) }
    var attemptsThisStage by remember(challengeIndex) { mutableIntStateOf(0) }
    var stageCleared by remember(challengeIndex) { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }

    val previewSim = remember(speedKmS, angleDeg) {
        simulateTrajectory(speedKmS, angleDeg)
    }

    var activeSim by remember { mutableStateOf<OrbitSimulationResult?>(null) }
    val animProgress = remember { Animatable(0f) }

    var feedbackVisible by remember(challengeIndex) { mutableStateOf(false) }
    var feedbackTone by remember(challengeIndex) { mutableStateOf(FeedbackTone.INFO) }
    var feedbackHeadline by remember(challengeIndex) { mutableStateOf("") }
    var feedbackText by remember(challengeIndex) { mutableStateOf("") }

    LaunchedEffect(launchCounter) {
        if (launchCounter > 0 && activeSim != null) {
            animProgress.snapTo(0f)
            animProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1900, easing = LinearEasing)
            )
        }
    }

    fun launchSatellite() {
        val sim = simulateTrajectory(speedKmS, angleDeg)
        activeSim = sim
        attemptsThisStage++
        launchCounter++

        val matched = sim.outcome == challenge.targetOutcome
        feedbackVisible = true

        if (matched) {
            if (!stageCleared) {
                val pts = when (attemptsThisStage) {
                    1 -> 300
                    2 -> 240
                    else -> 180
                }
                score += pts
                stageCleared = true
                feedbackTone = FeedbackTone.SUCCESS
                feedbackHeadline = "Target Achieved! (+$pts PTS)"
            } else {
                feedbackTone = FeedbackTone.SUCCESS
                feedbackHeadline = "Great Orbit!"
            }
            feedbackText = when (sim.outcome) {
                OrbitOutcome.CIRCULAR_ORBIT ->
                    "Perfect balance! Moving sideways at ${"%.1f".format(speedKmS)} km/s keeps the satellite falling smoothly around Earth instead of hitting it."
                OrbitOutcome.ELLIPTICAL_ORBIT ->
                    "Awesome elliptical orbit! Extra forward speed stretched the path outward before Earth's gravity curved it back."
                OrbitOutcome.ESCAPE ->
                    "Escape velocity reached! At ${"%.1f".format(speedKmS)} km/s, the probe breaks free of Earth's gravity well toward deep space."
                OrbitOutcome.CRASH -> ""
            }
        } else {
            feedbackTone = FeedbackTone.WARNING
            when (sim.outcome) {
                OrbitOutcome.CRASH -> {
                    feedbackHeadline = "Atmospheric Re-Entry!"
                    feedbackText = "Too slow or angled toward Earth! Without enough sideways speed (~7.8 km/s), gravity pulls the satellite down into the atmosphere."
                }
                OrbitOutcome.ESCAPE -> {
                    feedbackHeadline = "Escaped Into Deep Space!"
                    feedbackText = "Too fast or aimed too steeply outward! If your goal is a closed orbit, lower the speed below 10.5 km/s and aim closer to 0°."
                }
                OrbitOutcome.CIRCULAR_ORBIT -> {
                    feedbackHeadline = "Steady Circular Orbit"
                    feedbackText = "Nice circular orbit—now check Challenge goal: ${challenge.hintText}"
                }
                OrbitOutcome.ELLIPTICAL_ORBIT -> {
                    feedbackHeadline = "Elliptical Orbit"
                    feedbackText = "That created an oval orbit! Check the Challenge goal: ${challenge.hintText}"
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MissionControlTopBar(
            title = "Mission 3: Orbit Basics",
            subtitle = "STAGE ${challengeIndex + 1} OF ${orbitChallenges.size}",
            onBackClick = onBackClick,
            trailingScore = score
        )

        Column(
            modifier = Modifier
                .widthIn(max = 680.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GlassSpaceCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = challenge.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = ElectricBlue
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = challenge.goalText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SoftIceWhite
                )
            }

            // Interactive Earth & Satellite Simulation Canvas
            GlassSpaceCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = ElectricBlue.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "LIVE ORBITAL SIMULATION",
                        style = MaterialTheme.typography.labelMedium,
                        color = ElectricBlue
                    )
                    Text(
                        text = "v = ${"%.1f".format(speedKmS)} km/s • θ = ${angleDeg.roundToInt()}°",
                        style = MaterialTheme.typography.labelMedium,
                        color = StarlightAmber
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                OrbitSimulationCanvas(
                    previewPoints = previewSim.points,
                    activePoints = activeSim?.points,
                    activeOutcome = activeSim?.outcome,
                    animProgress = animProgress.value,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                        .clip(MaterialTheme.shapes.medium)
                )
            }

            // Speed & Direction Sliders
            GlassSpaceCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Launch Speed",
                        style = MaterialTheme.typography.titleMedium,
                        color = PureWhite
                    )
                    Text(
                        text = "${"%.1f".format(speedKmS)} km/s",
                        style = MaterialTheme.typography.labelLarge,
                        color = ElectricBlue
                    )
                }
                Slider(
                    value = speedKmS,
                    onValueChange = { speedKmS = it },
                    valueRange = 4.5f..12.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = ElectricBlue,
                        activeTrackColor = MissionBlue,
                        inactiveTrackColor = ElevatedNavy
                    ),
                    modifier = Modifier.testTag("orbit_speed_slider")
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Launch Direction (0° = Sideways)",
                        style = MaterialTheme.typography.titleMedium,
                        color = PureWhite
                    )
                    Text(
                        text = "${angleDeg.roundToInt()}°",
                        style = MaterialTheme.typography.labelLarge,
                        color = CosmicPurple
                    )
                }
                Slider(
                    value = angleDeg,
                    onValueChange = { angleDeg = it },
                    valueRange = -35f..35f,
                    colors = SliderDefaults.colors(
                        thumbColor = CosmicPurple,
                        activeTrackColor = CosmicPurple,
                        inactiveTrackColor = ElevatedNavy
                    ),
                    modifier = Modifier.testTag("orbit_angle_slider")
                )

                Spacer(modifier = Modifier.height(6.dp))
                Button(
                    onClick = ::launchSatellite,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("launch_satellite_button"),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MissionBlue,
                        contentColor = PureWhite
                    )
                ) {
                    Icon(Icons.Filled.RocketLaunch, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Launch Satellite", style = MaterialTheme.typography.titleMedium)
                }
            }

            ScienceFeedbackCard(
                visible = feedbackVisible,
                tone = feedbackTone,
                headline = feedbackHeadline,
                explanation = feedbackText
            )

            if (stageCleared) {
                val isLast = challengeIndex == orbitChallenges.lastIndex
                Button(
                    onClick = {
                        if (!isLast) {
                            challengeIndex++
                            activeSim = null
                        } else {
                            val stars = when {
                                score >= 750 -> 3
                                score >= 520 -> 2
                                else -> 1
                            }
                            onMissionComplete(
                                MissionRunResult(
                                    missionId = MissionId.ORBIT_BASICS,
                                    score = score,
                                    maxScore = 900,
                                    starsEarned = stars,
                                    summaryDetail = "Mastered circular orbits, elliptical paths, and escape velocity ($score/900 pts)."
                                )
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("orbit_next_button"),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SuccessEmerald,
                        contentColor = SpaceBlack
                    )
                ) {
                    Text(
                        text = if (isLast) "Complete Orbit Mission" else "Next Challenge",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun OrbitSimulationCanvas(
    previewPoints: List<Offset>,
    activePoints: List<Offset>?,
    activeOutcome: OrbitOutcome?,
    animProgress: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF101C42), SpaceBlack),
                center = Offset(size.width / 2f, size.height / 2f),
                radius = size.maxDimension * 0.7f
            )
        )

        val center = Offset(size.width / 2f, size.height / 2f)
        val unitPx = (size.minDimension * 0.16f)

        // Target circular orbit reference ring (at r = 1.55)
        drawCircle(
            color = SuccessEmerald.copy(alpha = 0.25f),
            radius = 1.55f * unitPx,
            center = center,
            style = Stroke(
                width = 1.5.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
            )
        )

        // Earth atmosphere glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(ElectricBlue.copy(alpha = 0.45f), Color.Transparent),
                center = center,
                radius = 1.35f * unitPx
            ),
            radius = 1.35f * unitPx,
            center = center
        )

        // Earth body
        drawCircle(
            brush = Brush.linearGradient(listOf(MissionBlue, Color(0xFF0EA5E9))),
            radius = unitPx,
            center = center
        )
        // Simple continent patches
        drawCircle(
            color = SuccessEmerald.copy(alpha = 0.7f),
            radius = unitPx * 0.35f,
            center = Offset(center.x - unitPx * 0.25f, center.y - unitPx * 0.15f)
        )
        drawCircle(
            color = SuccessEmerald.copy(alpha = 0.6f),
            radius = unitPx * 0.25f,
            center = Offset(center.x + unitPx * 0.3f, center.y + unitPx * 0.22f)
        )

        // Faint trajectory preview path
        if (previewPoints.size >= 2) {
            val previewPath = Path().apply {
                moveTo(center.x + previewPoints[0].x * unitPx, center.y + previewPoints[0].y * unitPx)
                previewPoints.drop(1).forEach { pt ->
                    lineTo(center.x + pt.x * unitPx, center.y + pt.y * unitPx)
                }
            }
            drawPath(
                path = previewPath,
                color = MutedStarText.copy(alpha = 0.4f),
                style = Stroke(
                    width = 1.5.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f))
                )
            )
        }

        // Active animated satellite trail & spacecraft
        val points = activePoints ?: previewPoints
        val visibleCount = if (activePoints != null) {
            (points.size * animProgress).toInt().coerceIn(1, points.size)
        } else {
            1
        }

        if (activePoints != null && visibleCount >= 2) {
            val trailColor = when (activeOutcome) {
                OrbitOutcome.CRASH -> AlertCoral
                OrbitOutcome.ESCAPE -> StarlightAmber
                else -> ElectricBlue
            }
            val trailPath = Path().apply {
                moveTo(center.x + points[0].x * unitPx, center.y + points[0].y * unitPx)
                for (i in 1 until visibleCount) {
                    lineTo(center.x + points[i].x * unitPx, center.y + points[i].y * unitPx)
                }
            }
            drawPath(
                path = trailPath,
                color = trailColor,
                style = Stroke(width = 2.5.dp.toPx())
            )
        }

        // Satellite marker at current position
        val satPt = points[(visibleCount - 1).coerceIn(0, points.lastIndex)]
        val satScreen = Offset(center.x + satPt.x * unitPx, center.y + satPt.y * unitPx)

        // Satellite solar wings + body
        drawLine(
            color = ElectricBlue,
            start = Offset(satScreen.x - 10.dp.toPx(), satScreen.y),
            end = Offset(satScreen.x + 10.dp.toPx(), satScreen.y),
            strokeWidth = 3.dp.toPx()
        )
        drawCircle(
            color = PureWhite,
            radius = 5.dp.toPx(),
            center = satScreen
        )
    }
}
