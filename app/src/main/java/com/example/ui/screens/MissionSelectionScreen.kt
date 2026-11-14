package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.AstronautProfileState
import com.example.data.MissionId
import com.example.ui.components.GlassSpaceCard
import com.example.ui.components.MissionControlTopBar
import com.example.ui.components.StarRatingRow
import com.example.ui.theme.CosmicPurple
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElevatedNavy
import com.example.ui.theme.MissionBlue
import com.example.ui.theme.MutedStarText
import com.example.ui.theme.NebulaViolet
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SoftIceWhite
import com.example.ui.theme.SuccessEmerald

@Composable
fun MissionSelectionScreen(
    profileState: AstronautProfileState,
    onSelectMission: (MissionId) -> Unit,
    onProfileClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MissionControlTopBar(
            title = "Mission Selection",
            subtitle = "${profileState.astronautName.uppercase()} • ${profileState.rank.title.uppercase()}",
            onBackClick = onBackClick,
            trailingStars = profileState.totalStars
        )

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.TopCenter
        ) {
            val isWide = maxWidth >= 640.dp
            Column(
                modifier = Modifier
                    .widthIn(max = 920.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Briefing Header Banner
                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = ElevatedNavy.copy(alpha = 0.8f),
                    border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .clickable(onClick = onProfileClick)
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Choose Your Next Space Mission",
                                style = MaterialTheme.typography.titleLarge,
                                color = PureWhite
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Complete all 4 training simulations to earn 12 stars and reach Junior Commander.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = SoftIceWhite
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MissionBlue.copy(alpha = 0.25f),
                            border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "${profileState.completedMissionsCount}/4\nDone",
                                style = MaterialTheme.typography.labelMedium,
                                color = ElectricBlue,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                if (isWide) {
                    val pairs = MissionId.entries.chunked(2)
                    pairs.forEach { rowMissions ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            rowMissions.forEach { mission ->
                                MissionCard(
                                    mission = mission,
                                    stars = profileState.missionStars[mission] ?: 0,
                                    bestScore = profileState.missionBestScores[mission] ?: 0,
                                    onLaunch = { onSelectMission(mission) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                } else {
                    MissionId.entries.forEach { mission ->
                        MissionCard(
                            mission = mission,
                            stars = profileState.missionStars[mission] ?: 0,
                            bestScore = profileState.missionBestScores[mission] ?: 0,
                            onLaunch = { onSelectMission(mission) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun MissionCard(
    mission: MissionId,
    stars: Int,
    bestScore: Int,
    onLaunch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (accent, icon) = missionVisuals(mission)
    val completed = stars > 0

    GlassSpaceCard(
        modifier = modifier
            .clickable(onClick = onLaunch)
            .testTag("mission_card_${mission.number}"),
        borderColor = if (completed) accent.copy(alpha = 0.7f) else accent.copy(alpha = 0.35f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(accent, CosmicPurple))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = mission.title,
                        tint = PureWhite,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "MISSION 0${mission.number}",
                        style = MaterialTheme.typography.labelMedium,
                        color = accent
                    )
                    Text(
                        text = mission.title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = PureWhite
                    )
                }
            }

            StarRatingRow(stars = stars, starSize = 20.dp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(50),
                color = accent.copy(alpha = 0.16f)
            ) {
                Text(
                    text = mission.topicTag,
                    style = MaterialTheme.typography.labelMedium,
                    color = accent,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
            if (completed) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = SuccessEmerald.copy(alpha = 0.16f)
                ) {
                    Text(
                        text = "Best: $bestScore pts",
                        style = MaterialTheme.typography.labelMedium,
                        color = SuccessEmerald,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = mission.briefDescription,
            style = MaterialTheme.typography.bodyMedium,
            color = SoftIceWhite
        )

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = onLaunch,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("launch_mission_${mission.number}_button"),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (completed) ElevatedNavy else MissionBlue,
                contentColor = PureWhite
            ),
            border = if (completed) BorderStroke(1.dp, accent.copy(alpha = 0.6f)) else null
        ) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (completed) "Replay Mission ${mission.number}" else "Launch Mission ${mission.number}",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

private fun missionVisuals(mission: MissionId): Pair<Color, ImageVector> = when (mission) {
    MissionId.MOON_BASE -> ElectricBlue to Icons.Filled.Apartment
    MissionId.ROVER_ROUTE -> CosmicPurple to Icons.Filled.SmartToy
    MissionId.ORBIT_BASICS -> NebulaViolet to Icons.Filled.Public
    MissionId.LIFE_SUPPORT -> SuccessEmerald to Icons.Filled.Favorite
}
