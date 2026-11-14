package com.example.ui.missions

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Air
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.MissionId
import com.example.data.MissionRunResult
import com.example.ui.components.FeedbackTone
import com.example.ui.components.GlassSpaceCard
import com.example.ui.components.MissionControlTopBar
import com.example.ui.components.ScienceFeedbackCard
import com.example.ui.theme.BorderNavy
import com.example.ui.theme.CardNavy
import com.example.ui.theme.CosmicPurple
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElevatedNavy
import com.example.ui.theme.MissionBlue
import com.example.ui.theme.MutedStarText
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SoftIceWhite
import com.example.ui.theme.SpaceNavy
import com.example.ui.theme.StarlightAmber
import com.example.ui.theme.SuccessEmerald
import com.example.ui.theme.AlertCoral

private data class BaseOption(
    val title: String,
    val detail: String,
    val isCorrect: Boolean,
    val points: Int,
    val scienceFeedback: String
)

private data class BaseDecisionStep(
    val category: String,
    val icon: ImageVector,
    val prompt: String,
    val options: List<BaseOption>
)

private val moonBaseSteps = listOf(
    BaseDecisionStep(
        category = "1. Shelter",
        icon = Icons.Filled.Home,
        prompt = "The Moon has no breathable air and experiences extreme temperatures (+120°C to -130°C) plus space radiation. Which shelter keeps astronauts safest?",
        options = listOf(
            BaseOption(
                title = "Pressurized Dome + Lunar Soil Shield",
                detail = "Airtight habitat covered with thick Moon dust (regolith) for insulation and radiation protection.",
                isCorrect = true,
                points = 200,
                scienceFeedback = "Correct! A pressurized dome holds breathable air inside, while a layer of lunar soil (regolith) blocks solar radiation and keeps temperatures steady."
            ),
            BaseOption(
                title = "Nylon Camping Tent",
                detail = "Lightweight fabric tent staked into Moon craters.",
                isCorrect = false,
                points = 60,
                scienceFeedback = "Not safe! A camping tent cannot hold air pressure in a vacuum and offers zero protection from micrometeoroids or cosmic radiation."
            ),
            BaseOption(
                title = "Unshielded Thin Glass Pod",
                detail = "Clear fragile glass dome with no thermal insulation.",
                isCorrect = false,
                points = 60,
                scienceFeedback = "Too risky! Without radiation shielding and thermal control, sunlight would overheat the pod and space radiation would endanger the crew."
            )
        )
    ),
    BaseDecisionStep(
        category = "2. Oxygen",
        icon = Icons.Filled.Air,
        prompt = "The Moon has almost no atmosphere. How should your Moon base supply breathable oxygen (O₂) for the crew?",
        options = listOf(
            BaseOption(
                title = "Open Windows at Sunrise",
                detail = "Let fresh lunar air drift inside the base each morning.",
                isCorrect = false,
                points = 60,
                scienceFeedback = "Dangerous! The Moon is surrounded by a near-vacuum with no breathable oxygen—opening a window would let all the base air escape!"
            ),
            BaseOption(
                title = "Oxygen Tanks + Water Electrolysis",
                detail = "Store pressurized O₂ and use electricity to split water (H₂O) into oxygen and hydrogen.",
                isCorrect = true,
                points = 200,
                scienceFeedback = "Spot on! Real space stations use electrolysis to split water molecules (H₂O) into breathable oxygen (O₂) plus backup pressurized tanks."
            ),
            BaseOption(
                title = "Two Small Desk Fans",
                detail = "Circulate Moon dust inside the dome to create air.",
                isCorrect = false,
                points = 60,
                scienceFeedback = "Not quite! Fans only move existing air around—they cannot create oxygen molecules out of a vacuum or Moon dust."
            )
        )
    ),
    BaseDecisionStep(
        category = "3. Power",
        icon = Icons.Filled.Bolt,
        prompt = "A lunar day lasts 14 Earth days of bright sunlight, followed by 14 days of darkness. What power system keeps the base running non-stop?",
        options = listOf(
            BaseOption(
                title = "High-Speed Wind Turbines",
                detail = "Spinning blades powered by lunar breezes.",
                isCorrect = false,
                points = 60,
                scienceFeedback = "Won't spin! Because the Moon has no atmosphere, there is no wind at all to turn wind turbine blades."
            ),
            BaseOption(
                title = "Solar Arrays + Battery Storage Bank",
                detail = "Collect intense sunlight during the 14-day lunar day and store energy for the 14-day night.",
                isCorrect = true,
                points = 200,
                scienceFeedback = "Excellent engineering! With no clouds on the Moon, solar panels collect bright sunlight, and high-capacity batteries keep life support running through the cold lunar night."
            ),
            BaseOption(
                title = "Outdoor Coal Campfire",
                detail = "Burn fuel outdoors to heat water and spin steam.",
                isCorrect = false,
                points = 60,
                scienceFeedback = "Impossible on the Moon! Fire needs oxygen in the air to burn, and there is no oxygen outside on the lunar surface."
            )
        )
    ),
    BaseDecisionStep(
        category = "4. Water",
        icon = Icons.Filled.LocalDrink,
        prompt = "Shipping heavy water from Earth is very expensive. How can astronauts maintain a steady water supply on the Moon?",
        options = listOf(
            BaseOption(
                title = "Polar Ice Mining + Water Recycler",
                detail = "Harvest frozen water in shadowed polar craters and recycle 90%+ of base moisture.",
                isCorrect = true,
                points = 200,
                scienceFeedback = "Brilliant! NASA's Artemis program targets the Moon's South Pole specifically because permanently shadowed craters hold ancient water ice, and recycling saves every drop!"
            ),
            BaseOption(
                title = "Collect Lunar Rainwater",
                detail = "Set buckets outside to catch Moon rainstorms.",
                isCorrect = false,
                points = 60,
                scienceFeedback = "Dry forever! Without an atmosphere or weather, it never rains on the Moon."
            ),
            BaseOption(
                title = "Single-Use Water Bottles Only",
                detail = "Drink bottled water and throw away all used water.",
                isCorrect = false,
                points = 60,
                scienceFeedback = "Wasteful! Launching water to the Moon costs thousands of dollars per liter, so space bases must filter and reuse nearly all water."
            )
        )
    ),
    BaseDecisionStep(
        category = "5. Food",
        icon = Icons.Filled.Restaurant,
        prompt = "Astronauts need nutritious meals for long lunar missions. Which food plan works best on a Moon base?",
        options = listOf(
            BaseOption(
                title = "Plant Seeds Directly Outside",
                detail = "Scatter vegetable seeds onto bare Moon rocks outside.",
                isCorrect = false,
                points = 60,
                scienceFeedback = "Plants can't survive outside! Seeds need liquid water, warmth, air, and radiation shielding found only inside a controlled habitat."
            ),
            BaseOption(
                title = "Daily Pizza Delivery Rockets",
                detail = "Launch a fresh rocket from Earth every single evening.",
                isCorrect = false,
                points = 60,
                scienceFeedback = "Way too slow and costly! A trip to the Moon takes about 3 days, so astronauts need long-lasting rations plus on-base food growth."
            ),
            BaseOption(
                title = "Hydroponic Pod + Freeze-Dried Rations",
                detail = "Grow leafy greens in nutrient water under LED lights alongside compact space meals.",
                isCorrect = true,
                points = 200,
                scienceFeedback = "Awesome choice! Hydroponics grows fresh crops without heavy soil, uses little water, and pairs well with lightweight freeze-dried space food."
            )
        )
    )
)

@Composable
fun Mission1MoonBaseScreen(
    onMissionComplete: (MissionRunResult) -> Unit,
    onBackClick: () -> Unit
) {
    var currentStepIndex by remember { mutableIntStateOf(0) }
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var score by remember { mutableIntStateOf(0) }
    val builtModules = remember { mutableStateListOf<Boolean>() }

    val step = moonBaseSteps[currentStepIndex]
    val selectedOption = selectedOptionIndex?.let { step.options[it] }
    val progress by animateFloatAsState(
        targetValue = (currentStepIndex + if (selectedOption != null) 1 else 0) / moonBaseSteps.size.toFloat(),
        animationSpec = tween(350),
        label = "moon_base_progress"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MissionControlTopBar(
            title = "Mission 1: Moon Base",
            subtitle = "STEP ${currentStepIndex + 1} OF ${moonBaseSteps.size}",
            onBackClick = onBackClick,
            trailingScore = score
        )

        Column(
            modifier = Modifier
                .widthIn(max = 680.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(50)),
                color = ElectricBlue,
                trackColor = ElevatedNavy,
                strokeCap = StrokeCap.Round
            )

            // Live Visual Lunar Base Schematic Canvas
            GlassSpaceCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = ElectricBlue.copy(alpha = 0.45f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "LUNAR BASE BLUEPRINT",
                        style = MaterialTheme.typography.labelMedium,
                        color = ElectricBlue
                    )
                    Text(
                        text = "${builtModules.size}/5 Systems Ready",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (builtModules.size == 5) SuccessEmerald else SoftIceWhite
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                MoonBaseBlueprintCanvas(
                    shelterBuilt = builtModules.getOrNull(0) != null,
                    oxygenBuilt = builtModules.getOrNull(1) != null,
                    powerBuilt = builtModules.getOrNull(2) != null,
                    waterBuilt = builtModules.getOrNull(3) != null,
                    foodBuilt = builtModules.getOrNull(4) != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(MaterialTheme.shapes.medium)
                )
            }

            // Question / Decision Card
            GlassSpaceCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = step.icon,
                        contentDescription = null,
                        tint = ElectricBlue,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = step.category,
                        style = MaterialTheme.typography.titleLarge,
                        color = ElectricBlue
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = step.prompt,
                    style = MaterialTheme.typography.bodyLarge,
                    color = PureWhite
                )
            }

            // Options
            step.options.forEachIndexed { index, option ->
                val isSelected = selectedOptionIndex == index
                val borderColor = when {
                    selectedOption == null -> BorderNavy
                    isSelected && option.isCorrect -> SuccessEmerald
                    isSelected && !option.isCorrect -> AlertCoral
                    option.isCorrect -> SuccessEmerald.copy(alpha = 0.45f)
                    else -> BorderNavy.copy(alpha = 0.4f)
                }
                val bgColor = when {
                    isSelected && option.isCorrect -> SuccessEmerald.copy(alpha = 0.14f)
                    isSelected && !option.isCorrect -> AlertCoral.copy(alpha = 0.14f)
                    else -> CardNavy.copy(alpha = 0.9f)
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = selectedOption == null) {
                            selectedOptionIndex = index
                            score += option.points
                            builtModules.add(option.isCorrect)
                        }
                        .testTag("moon_option_$index"),
                    shape = MaterialTheme.shapes.medium,
                    color = bgColor,
                    border = BorderStroke(1.5.dp, borderColor)
                ) {
                    Row(
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = option.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = PureWhite
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = option.detail,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MutedStarText
                            )
                        }
                        if (isSelected) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = "Selected option",
                                tint = if (option.isCorrect) SuccessEmerald else AlertCoral
                            )
                        }
                    }
                }
            }

            // Instant Feedback Card
            if (selectedOption != null) {
                ScienceFeedbackCard(
                    visible = true,
                    tone = if (selectedOption.isCorrect) FeedbackTone.SUCCESS else FeedbackTone.WARNING,
                    headline = if (selectedOption.isCorrect) "Mission-Ready Choice!" else "Engineering Hazard!",
                    explanation = selectedOption.scienceFeedback,
                    pointsBadge = "+${selectedOption.points} PTS"
                )

                val isLast = currentStepIndex == moonBaseSteps.lastIndex
                Button(
                    onClick = {
                        if (!isLast) {
                            currentStepIndex++
                            selectedOptionIndex = null
                        } else {
                            val stars = when {
                                score >= 850 -> 3
                                score >= 550 -> 2
                                else -> 1
                            }
                            onMissionComplete(
                                MissionRunResult(
                                    missionId = MissionId.MOON_BASE,
                                    score = score,
                                    maxScore = 1000,
                                    starsEarned = stars,
                                    summaryDetail = "Planned all 5 lunar survival systems with $score/1000 engineering points."
                                )
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("moon_next_button"),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MissionBlue,
                        contentColor = PureWhite
                    )
                ) {
                    Text(
                        text = if (isLast) "Complete Moon Base Mission" else "Next Decision",
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
private fun MoonBaseBlueprintCanvas(
    shelterBuilt: Boolean,
    oxygenBuilt: Boolean,
    powerBuilt: Boolean,
    waterBuilt: Boolean,
    foodBuilt: Boolean,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        // Space sky
        drawRect(
            brush = Brush.verticalGradient(
                listOf(SpaceNavy, Color(0xFF0F193B))
            )
        )
        // Distant Earth in upper right
        drawCircle(
            color = ElectricBlue.copy(alpha = 0.7f),
            radius = 12.dp.toPx(),
            center = Offset(size.width * 0.88f, size.height * 0.24f)
        )
        // Lunar regolith horizon
        val groundY = size.height * 0.72f
        drawRect(
            brush = Brush.verticalGradient(listOf(Color(0xFF475569), Color(0xFF334155))),
            topLeft = Offset(0f, groundY),
            size = Size(size.width, size.height - groundY)
        )

        // 1. Shelter Dome (center)
        val domeColor = if (shelterBuilt) PureWhite else MutedStarText.copy(alpha = 0.25f)
        drawArc(
            color = domeColor,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = Offset(size.width * 0.36f, groundY - 46.dp.toPx()),
            size = Size(size.width * 0.28f, 92.dp.toPx())
        )
        if (shelterBuilt) {
            drawRoundRect(
                color = ElectricBlue,
                topLeft = Offset(size.width * 0.46f, groundY - 18.dp.toPx()),
                size = Size(size.width * 0.08f, 18.dp.toPx()),
                cornerRadius = CornerRadius(6f, 6f)
            )
        }

        // 2. Oxygen Tanks (left of dome)
        val o2Color = if (oxygenBuilt) ElectricBlue else MutedStarText.copy(alpha = 0.2f)
        drawRoundRect(
            color = o2Color,
            topLeft = Offset(size.width * 0.24f, groundY - 32.dp.toPx()),
            size = Size(14.dp.toPx(), 32.dp.toPx()),
            cornerRadius = CornerRadius(8f, 8f)
        )
        drawRoundRect(
            color = o2Color,
            topLeft = Offset(size.width * 0.29f, groundY - 28.dp.toPx()),
            size = Size(14.dp.toPx(), 28.dp.toPx()),
            cornerRadius = CornerRadius(8f, 8f)
        )

        // 3. Solar Array (far left)
        val powerColor = if (powerBuilt) StarlightAmber else MutedStarText.copy(alpha = 0.2f)
        drawLine(
            color = powerColor,
            start = Offset(size.width * 0.12f, groundY),
            end = Offset(size.width * 0.12f, groundY - 26.dp.toPx()),
            strokeWidth = 3.dp.toPx()
        )
        drawRect(
            color = if (powerBuilt) MissionBlue else MutedStarText.copy(alpha = 0.2f),
            topLeft = Offset(size.width * 0.05f, groundY - 38.dp.toPx()),
            size = Size(size.width * 0.14f, 14.dp.toPx())
        )
        drawRect(
            color = powerColor,
            topLeft = Offset(size.width * 0.05f, groundY - 38.dp.toPx()),
            size = Size(size.width * 0.14f, 14.dp.toPx()),
            style = Stroke(width = 1.5.dp.toPx())
        )

        // 4. Water Extractor (right of dome)
        val waterColor = if (waterBuilt) Color(0xFF22D3EE) else MutedStarText.copy(alpha = 0.2f)
        drawRect(
            color = waterColor,
            topLeft = Offset(size.width * 0.68f, groundY - 30.dp.toPx()),
            size = Size(22.dp.toPx(), 30.dp.toPx())
        )

        // 5. Hydroponic Food Pod (far right)
        val foodColor = if (foodBuilt) SuccessEmerald else MutedStarText.copy(alpha = 0.2f)
        drawArc(
            color = foodColor,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = Offset(size.width * 0.78f, groundY - 28.dp.toPx()),
            size = Size(size.width * 0.16f, 56.dp.toPx())
        )
    }
}
