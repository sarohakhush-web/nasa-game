package com.example.ui.missions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
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
import com.example.ui.theme.CosmicPurple
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElevatedNavy
import com.example.ui.theme.MissionBlue
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SoftIceWhite
import com.example.ui.theme.SpaceNavy
import com.example.ui.theme.StarlightAmber
import com.example.ui.theme.SuccessEmerald

private data class GridPos(val row: Int, val col: Int)

private data class RoverSector(
    val title: String,
    val instruction: String,
    val gridSize: Int,
    val start: GridPos,
    val target: GridPos,
    val obstacles: Set<GridPos>,
    val samples: Set<GridPos>,
    val parMoves: Int
)

private val roverSectors = listOf(
    RoverSector(
        title = "Sector 1: Crater Rim",
        instruction = "Guide the rover to the green Science Flag! Avoid deep craters (red) and collect glowing rock samples (gold) for bonus points.",
        gridSize = 6,
        start = GridPos(5, 0),
        target = GridPos(0, 5),
        obstacles = setOf(
            GridPos(4, 1), GridPos(3, 1), GridPos(2, 3),
            GridPos(1, 3), GridPos(4, 4), GridPos(1, 5), GridPos(0, 2)
        ),
        samples = setOf(GridPos(2, 1), GridPos(3, 4)),
        parMoves = 10
    ),
    RoverSector(
        title = "Sector 2: South Pole Ice Ridge",
        instruction = "Navigate around icy boulders to reach the Polar Beacon! Collect ice cores along the safe ridge.",
        gridSize = 6,
        start = GridPos(5, 2),
        target = GridPos(0, 1),
        obstacles = setOf(
            GridPos(4, 2), GridPos(3, 2), GridPos(3, 0),
            GridPos(2, 4), GridPos(1, 1), GridPos(1, 2),
            GridPos(4, 4), GridPos(0, 3)
        ),
        samples = setOf(GridPos(2, 1), GridPos(1, 4)),
        parMoves = 11
    )
)

@Composable
fun Mission2RoverRouteScreen(
    onMissionComplete: (MissionRunResult) -> Unit,
    onBackClick: () -> Unit
) {
    var sectorIndex by remember { mutableIntStateOf(0) }
    val sector = roverSectors[sectorIndex]

    var roverPos by remember(sectorIndex) { mutableStateOf(sector.start) }
    val moveHistory = remember(sectorIndex) { mutableStateListOf<GridPos>() }
    val collectedSamples = remember(sectorIndex) { mutableStateListOf<GridPos>() }
    var sectorCleared by remember(sectorIndex) { mutableStateOf(false) }

    var totalScore by remember { mutableIntStateOf(0) }
    var feedbackTone by remember { mutableStateOf(FeedbackTone.INFO) }
    var feedbackHeadline by remember { mutableStateOf("Rover Ready") }
    var feedbackMessage by remember {
        mutableStateOf("Use the directional controls below to move 1 tile at a time toward the flag.")
    }

    fun attemptMove(dRow: Int, dCol: Int) {
        if (sectorCleared) return
        val next = GridPos(roverPos.row + dRow, roverPos.col + dCol)
        if (next.row !in 0 until sector.gridSize || next.col !in 0 until sector.gridSize) {
            feedbackTone = FeedbackTone.WARNING
            feedbackHeadline = "Map Boundary Reached"
            feedbackMessage = "Stay inside the scanned radar zone so Mission Control doesn't lose radio contact!"
            return
        }
        if (next in sector.obstacles) {
            feedbackTone = FeedbackTone.WARNING
            feedbackHeadline = "Hazard Alert: Crater / Boulder!"
            feedbackMessage = "Rovers cannot drive over steep craters or sharp boulders—real rovers use stereo hazard cameras to steer around them."
            return
        }

        moveHistory.add(roverPos)
        roverPos = next

        if (next in sector.samples && next !in collectedSamples) {
            collectedSamples.add(next)
            totalScore += 150
            feedbackTone = FeedbackTone.SUCCESS
            feedbackHeadline = "Science Sample Collected!"
            feedbackMessage = "Awesome! Gathering lunar rock and ice samples helps scientists learn how planets formed (+150 pts)."
        } else if (next == sector.target) {
            sectorCleared = true
            val moveCount = moveHistory.size
            val efficiencyBonus = if (moveCount <= sector.parMoves + 2) 250 else 150
            totalScore += efficiencyBonus
            feedbackTone = FeedbackTone.SUCCESS
            feedbackHeadline = "Science Target Reached!"
            feedbackMessage = "Reached in $moveCount moves! Careful path planning saved rover battery power (+$efficiencyBonus pts)."
        } else {
            feedbackTone = FeedbackTone.INFO
            feedbackHeadline = "Clear Terrain"
            feedbackMessage = "Moving safely to (${next.row + 1}, ${next.col + 1}). Moves taken: ${moveHistory.size}."
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MissionControlTopBar(
            title = "Mission 2: Rover Route",
            subtitle = "${sector.title.uppercase()} • MOVES: ${moveHistory.size}",
            onBackClick = onBackClick,
            trailingScore = totalScore
        )

        Column(
            modifier = Modifier
                .widthIn(max = 620.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GlassSpaceCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = sector.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = ElectricBlue
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = sector.instruction,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SoftIceWhite
                )
            }

            // 6x6 Interactive Grid
            GlassSpaceCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = CosmicPurple.copy(alpha = 0.5f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 360.dp)
                        .align(Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    for (r in 0 until sector.gridSize) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            for (c in 0 until sector.gridSize) {
                                val pos = GridPos(r, c)
                                val isRover = pos == roverPos
                                val isTarget = pos == sector.target
                                val isObstacle = pos in sector.obstacles
                                val isSample = pos in sector.samples && pos !in collectedSamples

                                val cellBg = when {
                                    isRover -> MissionBlue
                                    isTarget -> SuccessEmerald.copy(alpha = 0.25f)
                                    isObstacle -> AlertCoral.copy(alpha = 0.22f)
                                    isSample -> StarlightAmber.copy(alpha = 0.2f)
                                    else -> ElevatedNavy.copy(alpha = 0.75f)
                                }
                                val cellBorder = when {
                                    isRover -> ElectricBlue
                                    isTarget -> SuccessEmerald
                                    isObstacle -> AlertCoral.copy(alpha = 0.6f)
                                    isSample -> StarlightAmber.copy(alpha = 0.7f)
                                    else -> BorderNavy.copy(alpha = 0.5f)
                                }

                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    color = cellBg,
                                    border = BorderStroke(1.dp, cellBorder)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        when {
                                            isRover -> Icon(
                                                imageVector = Icons.Filled.SmartToy,
                                                contentDescription = "Rover",
                                                tint = PureWhite,
                                                modifier = Modifier.size(24.dp)
                                            )
                                            isTarget -> Icon(
                                                imageVector = Icons.Filled.Flag,
                                                contentDescription = "Science Target",
                                                tint = SuccessEmerald,
                                                modifier = Modifier.size(22.dp)
                                            )
                                            isObstacle -> Icon(
                                                imageVector = Icons.Filled.Landscape,
                                                contentDescription = "Crater obstacle",
                                                tint = AlertCoral,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            isSample -> Icon(
                                                imageVector = Icons.Filled.Diamond,
                                                contentDescription = "Science sample",
                                                tint = StarlightAmber,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // D-Pad Movement Controls
            GlassSpaceCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // D-Pad
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        RoverDirButton(
                            icon = Icons.Filled.KeyboardArrowUp,
                            desc = "Move rover up",
                            tag = "rover_up_button",
                            enabled = !sectorCleared,
                            onClick = { attemptMove(-1, 0) }
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            RoverDirButton(
                                icon = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                desc = "Move rover left",
                                tag = "rover_left_button",
                                enabled = !sectorCleared,
                                onClick = { attemptMove(0, -1) }
                            )
                            RoverDirButton(
                                icon = Icons.Filled.KeyboardArrowDown,
                                desc = "Move rover down",
                                tag = "rover_down_button",
                                enabled = !sectorCleared,
                                onClick = { attemptMove(1, 0) }
                            )
                            RoverDirButton(
                                icon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                desc = "Move rover right",
                                tag = "rover_right_button",
                                enabled = !sectorCleared,
                                onClick = { attemptMove(0, 1) }
                            )
                        }
                    }

                    // Undo & Reset Controls
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        OutlinedButton(
                            onClick = {
                                if (moveHistory.isNotEmpty() && !sectorCleared) {
                                    roverPos = moveHistory.removeAt(moveHistory.lastIndex)
                                    feedbackTone = FeedbackTone.INFO
                                    feedbackHeadline = "Stepped Back"
                                    feedbackMessage = "Moved back to previous safe tile."
                                }
                            },
                            enabled = moveHistory.isNotEmpty() && !sectorCleared,
                            border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.5f)),
                            modifier = Modifier.minimumInteractiveComponentSize()
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Undo")
                        }

                        OutlinedButton(
                            onClick = {
                                roverPos = sector.start
                                moveHistory.clear()
                                // Subtract only samples collected in current sector if resetting mid-sector
                                totalScore -= (collectedSamples.size * 150)
                                if (totalScore < 0) totalScore = 0
                                collectedSamples.clear()
                                sectorCleared = false
                                feedbackTone = FeedbackTone.INFO
                                feedbackHeadline = "Sector Reset"
                                feedbackMessage = "Rover returned to launch pad."
                            },
                            enabled = !sectorCleared && moveHistory.isNotEmpty(),
                            border = BorderStroke(1.dp, CosmicPurple.copy(alpha = 0.6f)),
                            modifier = Modifier.minimumInteractiveComponentSize()
                        ) {
                            Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reset")
                        }
                    }
                }
            }

            // Instant Science Feedback
            ScienceFeedbackCard(
                visible = true,
                tone = feedbackTone,
                headline = feedbackHeadline,
                explanation = feedbackMessage
            )

            if (sectorCleared) {
                val isLastSector = sectorIndex == roverSectors.lastIndex
                Button(
                    onClick = {
                        if (!isLastSector) {
                            sectorIndex++
                            feedbackTone = FeedbackTone.INFO
                            feedbackHeadline = "Sector 2 Loaded"
                            feedbackMessage = "Ready to explore the South Pole Ice Ridge!"
                        } else {
                            val stars = when {
                                totalScore >= 900 -> 3
                                totalScore >= 600 -> 2
                                else -> 1
                            }
                            onMissionComplete(
                                MissionRunResult(
                                    missionId = MissionId.ROVER_ROUTE,
                                    score = totalScore,
                                    maxScore = 1100,
                                    starsEarned = stars,
                                    summaryDetail = "Navigated both rover obstacle courses with $totalScore total science & efficiency points."
                                )
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("rover_next_button"),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MissionBlue,
                        contentColor = PureWhite
                    )
                ) {
                    Text(
                        text = if (isLastSector) "Complete Rover Mission" else "Next Sector: Ice Ridge",
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
private fun RoverDirButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    desc: String,
    tag: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = CircleShape,
        color = if (enabled) MissionBlue else ElevatedNavy.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.7f)),
        modifier = Modifier
            .size(52.dp)
            .testTag(tag)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = desc,
                tint = PureWhite,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
