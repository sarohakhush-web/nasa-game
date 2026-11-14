package com.example.ui.missions

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.LocalDrink
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.MissionId
import com.example.data.MissionRunResult
import com.example.ui.components.FeedbackTone
import com.example.ui.components.GlassSpaceCard
import com.example.ui.components.MissionControlTopBar
import com.example.ui.components.ScienceFeedbackCard
import com.example.ui.theme.AlertCoral
import com.example.ui.theme.BorderNavy
import com.example.ui.theme.CardNavy
import com.example.ui.theme.CosmicPurple
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElevatedNavy
import com.example.ui.theme.MissionBlue
import com.example.ui.theme.MutedStarText
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SoftIceWhite
import com.example.ui.theme.StarlightAmber
import com.example.ui.theme.SuccessEmerald

private data class LifeSupportAction(
    val title: String,
    val detail: String,
    val deltaO2: Int,
    val deltaWater: Int,
    val deltaPower: Int,
    val isOptimal: Boolean,
    val points: Int,
    val explanation: String
)

private data class LifeSupportEvent(
    val solTitle: String,
    val situation: String,
    val actions: List<LifeSupportAction>
)

private val lifeSupportEvents = listOf(
    LifeSupportEvent(
        solTitle = "Sol 1: Orbital Night Shadow",
        situation = "Your spacecraft just entered Earth's shadow! Solar panels stopped charging, dropping Power, while the crew still needs Oxygen.",
        actions = listOf(
            LifeSupportAction(
                title = "Dim Cabin Lights & Run Eco-Scrubber",
                detail = "Save electricity on non-essential lights so batteries keep O₂ flowing.",
                deltaO2 = +5, deltaWater = -5, deltaPower = +10,
                isOptimal = true, points = 200,
                explanation = "Smart power budgeting! Turning off non-essential heaters and lights keeps critical air scrubbers running through orbital night."
            ),
            LifeSupportAction(
                title = "Max Out Oxygen Electrolysis",
                detail = "Run full electrolysis using heavy battery power.",
                deltaO2 = +20, deltaWater = -25, deltaPower = -40,
                isOptimal = false, points = 80,
                explanation = "Careful! Electrolysis splits water into oxygen using lots of electricity—running it at full blast during a shadow drains power and water fast."
            ),
            LifeSupportAction(
                title = "Turn Off Air Circulation Fans",
                detail = "Shut down ventilation completely to save batteries.",
                deltaO2 = -30, deltaWater = 0, deltaPower = +15,
                isOptimal = false, points = 60,
                explanation = "Too dangerous! In microgravity, exhaled carbon dioxide pools around astronauts' heads unless fans keep cabin air moving."
            )
        )
    ),
    LifeSupportEvent(
        solTitle = "Sol 2: Water Recycler Clog",
        situation = "The cabin humidity condenser filter is clogged, dropping clean Water reserves. Keep Water, Oxygen, and Power balanced!",
        actions = listOf(
            LifeSupportAction(
                title = "Flush & Clean Condenser Filter",
                detail = "Use moderate power to clear the loop and recover cabin humidity.",
                deltaO2 = 0, deltaWater = +20, deltaPower = -10,
                isOptimal = true, points = 200,
                explanation = "Great teamwork! Real space stations capture astronauts' breath moisture and wash water, recycling over 93% of all water onboard."
            ),
            LifeSupportAction(
                title = "Vent Dirty Water Into Space",
                detail = "Dump waste water out the airlock instead of recycling.",
                deltaO2 = -5, deltaWater = -30, deltaPower = +5,
                isOptimal = false, points = 60,
                explanation = "Never waste water in space! Every drop dumped into space is gone forever and also means less water for making oxygen."
            ),
            LifeSupportAction(
                title = "Boil Extra Water at High Heat",
                detail = "Run high-wattage heaters continuously.",
                deltaO2 = -5, deltaWater = +10, deltaPower = -35,
                isOptimal = false, points = 80,
                explanation = "Works slightly, but boiling takes huge amounts of electrical power. Vacuum distillation at lower temperatures saves energy!"
            )
        )
    ),
    LifeSupportEvent(
        solTitle = "Sol 3: Spacewalk Airlock Prep",
        situation = "Two astronauts are heading outside for a solar panel repair! Depressurizing the airlock uses Oxygen and Power.",
        actions = listOf(
            LifeSupportAction(
                title = "Pump Airlock Gas Back Into Storage",
                detail = "Use a recovery pump to save cabin air before opening the outer hatch.",
                deltaO2 = -5, deltaWater = 0, deltaPower = -10,
                isOptimal = true, points = 200,
                explanation = "Spot on! Real airlocks pump most of the air back into cabin tanks before opening to space, saving precious oxygen!"
            ),
            LifeSupportAction(
                title = "Open Outer Hatch Immediately",
                detail = "Let all airlock oxygen rush out into space to save pump power.",
                deltaO2 = -35, deltaWater = 0, deltaPower = +5,
                isOptimal = false, points = 60,
                explanation = "Huge oxygen loss! Letting pressurized air escape into space wastes breathable gas that is hard to replace."
            ),
            LifeSupportAction(
                title = "Run Double Electrolysis Backup",
                detail = "Split extra water reserves to refill the airlock later.",
                deltaO2 = +10, deltaWater = -25, deltaPower = -25,
                isOptimal = false, points = 90,
                explanation = "Recovers oxygen, but uses lots of water and electricity compared to simply pumping the airlock gas back inside."
            )
        )
    ),
    LifeSupportEvent(
        solTitle = "Sol 4: Solar Array Alignment",
        situation = "Your repaired solar wings are ready! Choose how to use this fresh burst of sunlight energy across all life support systems.",
        actions = listOf(
            LifeSupportAction(
                title = "Balance Charge + Electrolysis + Filter",
                detail = "Rotate solar wings toward the Sun and gently top up O₂, H₂O, and Power.",
                deltaO2 = +15, deltaWater = +10, deltaPower = +20,
                isOptimal = true, points = 200,
                explanation = "Balanced mission control! Sun-tracking solar arrays power both water recycling and oxygen generation so all three systems thrive together."
            ),
            LifeSupportAction(
                title = "Charge Batteries Only",
                detail = "Keep water & oxygen systems paused until batteries hit 100%.",
                deltaO2 = -15, deltaWater = -15, deltaPower = +30,
                isOptimal = false, points = 80,
                explanation = "Power is full, but pausing air and water systems makes crew reserves drop unnecessarily."
            ),
            LifeSupportAction(
                title = "Ignore Solar Tracking",
                detail = "Leave solar panels angled away from sunlight.",
                deltaO2 = -10, deltaWater = -10, deltaPower = -25,
                isOptimal = false, points = 60,
                explanation = "Missed opportunity! Solar panels generate maximum power only when angled straight toward the Sun."
            )
        )
    ),
    LifeSupportEvent(
        solTitle = "Sol 5: Crew Exercise & Splashdown Prep",
        situation = "Astronauts exercise 2 hours daily in space to keep bones strong—which uses more Oxygen and Water. Bring all gauges safely home!",
        actions = listOf(
            LifeSupportAction(
                title = "Activate Sabatier Reactor + Sweat Recycler",
                detail = "Turn crew CO₂ and moisture back into breathable O₂ and clean water.",
                deltaO2 = +15, deltaWater = +15, deltaPower = -10,
                isOptimal = true, points = 200,
                explanation = "Master-level space science! The Sabatier system on the ISS combines waste CO₂ with hydrogen to make fresh water in a closed loop!"
            ),
            LifeSupportAction(
                title = "Cancel All Crew Hydration",
                detail = "Tell astronauts not to drink water after exercising.",
                deltaO2 = -10, deltaWater = +5, deltaPower = 0,
                isOptimal = false, points = 60,
                explanation = "Unsafe for crew health! Astronauts must stay hydrated in space; smart recycling keeps both water and astronauts healthy."
            ),
            LifeSupportAction(
                title = "Run Full Cabin Heaters Instead",
                detail = "Warm up the lab module using reserve power.",
                deltaO2 = -15, deltaWater = -10, deltaPower = -25,
                isOptimal = false, points = 70,
                explanation = "Extra cabin heat doesn't replenish oxygen or water and uses up battery power."
            )
        )
    )
)

@Composable
fun Mission4LifeSupportScreen(
    onMissionComplete: (MissionRunResult) -> Unit,
    onBackClick: () -> Unit
) {
    var solIndex by remember { mutableIntStateOf(0) }
    var oxygen by remember { mutableIntStateOf(70) }
    var water by remember { mutableIntStateOf(70) }
    var power by remember { mutableIntStateOf(75) }
    var score by remember { mutableIntStateOf(0) }
    var selectedActionIndex by remember { mutableStateOf<Int?>(null) }

    val event = lifeSupportEvents[solIndex]
    val selectedAction = selectedActionIndex?.let { event.actions[it] }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MissionControlTopBar(
            title = "Mission 4: Life Support",
            subtitle = "SOL ${solIndex + 1} OF ${lifeSupportEvents.size}",
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
            // Live Resource Gauges HUD
            GlassSpaceCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = ElectricBlue.copy(alpha = 0.45f)
            ) {
                Text(
                    text = "SPACECRAFT LIFE SUPPORT TELEMETRY",
                    style = MaterialTheme.typography.labelMedium,
                    color = ElectricBlue
                )
                Spacer(modifier = Modifier.height(10.dp))

                ResourceGaugeRow(
                    label = "Oxygen (O₂)",
                    percent = oxygen,
                    icon = Icons.Filled.Air,
                    baseColor = ElectricBlue
                )
                Spacer(modifier = Modifier.height(10.dp))
                ResourceGaugeRow(
                    label = "Water (H₂O)",
                    percent = water,
                    icon = Icons.Filled.LocalDrink,
                    baseColor = Color(0xFF22D3EE)
                )
                Spacer(modifier = Modifier.height(10.dp))
                ResourceGaugeRow(
                    label = "Power (kW)",
                    percent = power,
                    icon = Icons.Filled.Bolt,
                    baseColor = StarlightAmber
                )
            }

            // Current Sol Event
            GlassSpaceCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = event.solTitle,
                    style = MaterialTheme.typography.titleLarge,
                    color = CosmicPurple
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = event.situation,
                    style = MaterialTheme.typography.bodyLarge,
                    color = PureWhite
                )
            }

            // Action Choices
            event.actions.forEachIndexed { index, action ->
                val isSelected = selectedActionIndex == index
                val border = when {
                    selectedAction == null -> BorderNavy
                    isSelected && action.isOptimal -> SuccessEmerald
                    isSelected && !action.isOptimal -> AlertCoral
                    action.isOptimal -> SuccessEmerald.copy(alpha = 0.45f)
                    else -> BorderNavy.copy(alpha = 0.4f)
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = selectedAction == null) {
                            selectedActionIndex = index
                            oxygen = (oxygen + action.deltaO2).coerceIn(10, 100)
                            water = (water + action.deltaWater).coerceIn(10, 100)
                            power = (power + action.deltaPower).coerceIn(10, 100)
                            val stabilityBonus = if (oxygen >= 35 && water >= 35 && power >= 35) 30 else 0
                            score += action.points + stabilityBonus
                        }
                        .testTag("life_support_option_$index"),
                    shape = MaterialTheme.shapes.medium,
                    color = CardNavy.copy(alpha = 0.9f),
                    border = BorderStroke(1.5.dp, border)
                ) {
                    Column(
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .padding(14.dp)
                    ) {
                        Text(
                            text = action.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = PureWhite
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = action.detail,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MutedStarText
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            DeltaPill("O₂", action.deltaO2)
                            DeltaPill("H₂O", action.deltaWater)
                            DeltaPill("PWR", action.deltaPower)
                        }
                    }
                }
            }

            if (selectedAction != null) {
                ScienceFeedbackCard(
                    visible = true,
                    tone = if (selectedAction.isOptimal) FeedbackTone.SUCCESS else FeedbackTone.WARNING,
                    headline = if (selectedAction.isOptimal) "Systems Balanced!" else "Resource Strain!",
                    explanation = selectedAction.explanation,
                    pointsBadge = "+${selectedAction.points} PTS"
                )

                val isLast = solIndex == lifeSupportEvents.lastIndex
                Button(
                    onClick = {
                        if (!isLast) {
                            solIndex++
                            selectedActionIndex = null
                        } else {
                            val stars = when {
                                score >= 950 -> 3
                                score >= 620 -> 2
                                else -> 1
                            }
                            onMissionComplete(
                                MissionRunResult(
                                    missionId = MissionId.LIFE_SUPPORT,
                                    score = score,
                                    maxScore = 1150,
                                    starsEarned = stars,
                                    summaryDetail = "Final levels — O₂: $oxygen%, H₂O: $water%, Power: $power% ($score pts)."
                                )
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("life_support_next_button"),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MissionBlue,
                        contentColor = PureWhite
                    )
                ) {
                    Text(
                        text = if (isLast) "Complete Life Support Mission" else "Next Mission Sol",
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
private fun ResourceGaugeRow(
    label: String,
    percent: Int,
    icon: ImageVector,
    baseColor: Color
) {
    val animatedProgress by animateFloatAsState(
        targetValue = percent / 100f,
        animationSpec = tween(400),
        label = label
    )
    val statusColor = when {
        percent < 30 -> AlertCoral
        percent < 50 -> StarlightAmber
        else -> baseColor
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = statusColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium,
                    color = PureWhite
                )
            }
            Text(
                text = "$percent%",
                style = MaterialTheme.typography.labelLarge,
                color = statusColor
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(50)),
            color = statusColor,
            trackColor = ElevatedNavy,
            strokeCap = StrokeCap.Round
        )
    }
}

@Composable
private fun DeltaPill(label: String, delta: Int) {
    val color = when {
        delta > 0 -> SuccessEmerald
        delta < 0 -> AlertCoral
        else -> MutedStarText
    }
    val text = if (delta > 0) "$label +$delta%" else "$label $delta%"
    Surface(
        shape = RoundedCornerShape(50),
        color = color.copy(alpha = 0.14f)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}
