package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.AstronautProfileState
import com.example.data.MissionId
import com.example.data.MissionRunResult
import com.example.ui.components.GlassSpaceCard
import com.example.ui.components.StarRatingRow
import com.example.ui.theme.BorderNavy
import com.example.ui.theme.CosmicPurple
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElevatedNavy
import com.example.ui.theme.MissionBlue
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SoftIceWhite
import com.example.ui.theme.StarlightAmber
import com.example.ui.theme.SuccessEmerald

@Composable
fun ResultsScreen(
    result: MissionRunResult,
    profileState: AstronautProfileState,
    onReplayClick: () -> Unit,
    onNextMissionClick: (MissionId) -> Unit,
    onMissionSelectClick: () -> Unit,
    onHomeClick: () -> Unit
) {
    val nextMission = result.missionId.nextMissionOrNull()
    val encouragement = when (result.starsEarned) {
        3 -> "Outstanding work, ${profileState.astronautName}! You mastered ${result.missionId.title} like a true commander."
        2 -> "Great mission, ${profileState.astronautName}! You have strong space instincts—replay anytime for all 3 stars!"
        else -> "Mission complete, ${profileState.astronautName}! Every astronaut learns through testing and curiosity—try again to boost your stars!"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 640.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Trophy Emblem
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(listOf(MissionBlue, CosmicPurple))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.EmojiEvents,
                    contentDescription = "Mission complete trophy",
                    tint = StarlightAmber,
                    modifier = Modifier.size(44.dp)
                )
            }

            Text(
                text = "Mission Complete!",
                style = MaterialTheme.typography.displaySmall,
                color = PureWhite,
                textAlign = TextAlign.Center
            )
            Text(
                text = "MISSION 0${result.missionId.number}: ${result.missionId.title.uppercase()}",
                style = MaterialTheme.typography.labelLarge,
                color = ElectricBlue,
                textAlign = TextAlign.Center
            )

            // Stars & Final Score Card
            GlassSpaceCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = StarlightAmber.copy(alpha = 0.55f)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "STARS EARNED",
                        style = MaterialTheme.typography.labelMedium,
                        color = StarlightAmber
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    StarRatingRow(stars = result.starsEarned, starSize = 42.dp)
                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = ElevatedNavy,
                        border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.45f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 14.dp, horizontal = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "FINAL MISSION SCORE",
                                style = MaterialTheme.typography.labelMedium,
                                color = ElectricBlue
                            )
                            Text(
                                text = "${result.score} PTS",
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.Bold,
                                color = PureWhite
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = result.summaryDetail,
                                style = MaterialTheme.typography.bodyMedium,
                                color = SoftIceWhite,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Encouragement & Science Takeaway
            GlassSpaceCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = SuccessEmerald.copy(alpha = 0.45f)
            ) {
                Text(
                    text = encouragement,
                    style = MaterialTheme.typography.titleMedium,
                    color = PureWhite
                )
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = SuccessEmerald.copy(alpha = 0.12f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "SCIENCE TAKEAWAY",
                            style = MaterialTheme.typography.labelMedium,
                            color = SuccessEmerald
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = result.missionId.keyTakeaway,
                            style = MaterialTheme.typography.bodyMedium,
                            color = SoftIceWhite
                        )
                    }
                }
            }

            // Overall Academy Progress Snapshot
            GlassSpaceCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Rank: ${profileState.rank.title}",
                            style = MaterialTheme.typography.titleMedium,
                            color = PureWhite
                        )
                        Text(
                            text = "Completed Missions: ${profileState.completedMissionsCount} / 4",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ElectricBlue
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = ElevatedNavy
                    ) {
                        Text(
                            text = "${profileState.totalStars} / 12 ★",
                            style = MaterialTheme.typography.labelLarge,
                            color = StarlightAmber,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            // Action Buttons
            if (nextMission != null) {
                Button(
                    onClick = { onNextMissionClick(nextMission) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("results_next_mission_button"),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MissionBlue,
                        contentColor = PureWhite
                    )
                ) {
                    Text(
                        text = "Next Mission: ${nextMission.title}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onReplayClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("results_replay_button"),
                    shape = MaterialTheme.shapes.medium,
                    border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.7f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PureWhite)
                ) {
                    Icon(Icons.Filled.Replay, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Replay")
                }

                OutlinedButton(
                    onClick = onMissionSelectClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("results_missions_button"),
                    shape = MaterialTheme.shapes.medium,
                    border = BorderStroke(1.dp, CosmicPurple.copy(alpha = 0.7f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PureWhite)
                ) {
                    Icon(Icons.Filled.RocketLaunch, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Missions")
                }
            }

            OutlinedButton(
                onClick = onHomeClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("results_home_button"),
                shape = MaterialTheme.shapes.medium,
                border = BorderStroke(1.dp, BorderNavy),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = SoftIceWhite)
            ) {
                Icon(Icons.Filled.Home, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Back to Home")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
