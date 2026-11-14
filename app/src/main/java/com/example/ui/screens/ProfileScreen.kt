package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.AstronautProfileState
import com.example.data.MissionId
import com.example.ui.components.GlassSpaceCard
import com.example.ui.components.MissionControlTopBar
import com.example.ui.components.StarRatingRow
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

@Composable
fun ProfileScreen(
    profileState: AstronautProfileState,
    onSaveName: (String) -> Unit,
    onResetProgress: () -> Unit,
    onStartMissions: () -> Unit,
    onBackClick: () -> Unit
) {
    var nameInput by remember { mutableStateOf(profileState.astronautName) }
    var nameSavedToast by remember { mutableStateOf(false) }
    var showResetConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(profileState.astronautName) {
        if (profileState.isLoaded) {
            nameInput = profileState.astronautName
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MissionControlTopBar(
            title = "Astronaut Profile",
            subtitle = "CADET DOSSIER",
            onBackClick = onBackClick,
            trailingStars = profileState.totalStars
        )

        Column(
            modifier = Modifier
                .widthIn(max = 680.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Astronaut Name Card
            GlassSpaceCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Astronaut Callsign",
                    style = MaterialTheme.typography.titleLarge,
                    color = PureWhite
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Enter your astronaut name—progress saves automatically on this device.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedStarText
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = {
                            if (it.length <= 24) {
                                nameInput = it
                                nameSavedToast = false
                            }
                        },
                        singleLine = true,
                        label = { Text("Astronaut Name") },
                        placeholder = { Text("Cadet Nova") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = BorderNavy,
                            focusedTextColor = PureWhite,
                            unfocusedTextColor = PureWhite,
                            focusedLabelColor = ElectricBlue,
                            unfocusedLabelColor = MutedStarText,
                            cursorColor = ElectricBlue
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("astronaut_name_input")
                    )

                    Button(
                        onClick = {
                            onSaveName(nameInput)
                            nameSavedToast = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MissionBlue,
                            contentColor = PureWhite
                        ),
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier
                            .height(54.dp)
                            .testTag("save_name_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Save name",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save")
                    }
                }

                if (nameSavedToast) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Callsign updated to ${nameInput.trim().ifEmpty { "Cadet Nova" }}!",
                        style = MaterialTheme.typography.labelMedium,
                        color = SuccessEmerald
                    )
                }
            }

            // Rank & Stats Card
            GlassSpaceCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = ElectricBlue.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "CURRENT RANK",
                            style = MaterialTheme.typography.labelMedium,
                            color = ElectricBlue
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = profileState.rank.title,
                            style = MaterialTheme.typography.headlineMedium,
                            color = PureWhite
                        )
                    }
                    Surface(
                        shape = CircleShape,
                        color = CosmicPurple.copy(alpha = 0.22f),
                        border = BorderStroke(1.dp, CosmicPurple)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MilitaryTech,
                            contentDescription = "Rank emblem",
                            tint = StarlightAmber,
                            modifier = Modifier
                                .padding(12.dp)
                                .size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                val nextTarget = profileState.rank.nextRankStars
                val progress = if (nextTarget == null) {
                    1f
                } else {
                    val span = (nextTarget - profileState.rank.minStars).coerceAtLeast(1)
                    ((profileState.totalStars - profileState.rank.minStars).toFloat() / span).coerceIn(0f, 1f)
                }

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(50)),
                    color = ElectricBlue,
                    trackColor = ElevatedNavy,
                    strokeCap = StrokeCap.Round
                )

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (nextTarget == null) {
                        "Maximum rank achieved! You mastered all space missions."
                    } else {
                        "${nextTarget - profileState.totalStars} more star(s) to reach the next rank"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = SoftIceWhite
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 3 Stat Tiles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatTile(
                        label = "Total Stars",
                        value = "${profileState.totalStars} / 12",
                        accent = StarlightAmber,
                        modifier = Modifier.weight(1f)
                    )
                    StatTile(
                        label = "Completed",
                        value = "${profileState.completedMissionsCount} / 4",
                        accent = SuccessEmerald,
                        modifier = Modifier.weight(1f)
                    )
                    StatTile(
                        label = "Best Score",
                        value = "${profileState.totalBestScore}",
                        accent = ElectricBlue,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Beginner Astronaut Badge Showcase
            GlassSpaceCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = CosmicPurple.copy(alpha = 0.5f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        border = BorderStroke(
                            2.dp,
                            Brush.linearGradient(listOf(StarlightAmber, CosmicPurple))
                        ),
                        modifier = Modifier.size(86.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_beginner_badge),
                            contentDescription = "Beginner Astronaut Badge",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = StarlightAmber.copy(alpha = 0.16f)
                        ) {
                            Text(
                                text = "UNLOCKED BADGE",
                                style = MaterialTheme.typography.labelMedium,
                                color = StarlightAmber,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Beginner Astronaut Badge",
                            style = MaterialTheme.typography.titleLarge,
                            color = PureWhite
                        )
                        Text(
                            text = "Awarded for joining the Junior Astronaut Mission Trainer academy.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SoftIceWhite
                        )
                    }
                }
            }

            // Mission Log Summary
            GlassSpaceCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.EmojiEvents,
                        contentDescription = null,
                        tint = StarlightAmber,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Mission Training Record",
                        style = MaterialTheme.typography.titleLarge,
                        color = PureWhite
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                MissionId.entries.forEach { mission ->
                    val stars = profileState.missionStars[mission] ?: 0
                    val best = profileState.missionBestScores[mission] ?: 0
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = ElevatedNavy.copy(alpha = 0.7f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Mission ${mission.number}: ${mission.title}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = PureWhite
                                )
                                Text(
                                    text = if (stars > 0) "Best Score: $best pts" else "Not completed yet",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (stars > 0) ElectricBlue else MutedStarText
                                )
                            }
                            StarRatingRow(stars = stars, starSize = 18.dp)
                        }
                    }
                }
            }

            // Actions
            Button(
                onClick = onStartMissions,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("profile_go_missions_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MissionBlue,
                    contentColor = PureWhite
                ),
                shape = MaterialTheme.shapes.large
            ) {
                Icon(Icons.Filled.RocketLaunch, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Go to Mission Selection", style = MaterialTheme.typography.titleMedium)
            }

            OutlinedButton(
                onClick = { showResetConfirm = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("reset_progress_button"),
                border = BorderStroke(1.dp, AlertCoral.copy(alpha = 0.6f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AlertCoral),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Reset Mission Progress")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            containerColor = CardNavy,
            titleContentColor = PureWhite,
            textContentColor = SoftIceWhite,
            title = { Text("Reset Training Progress?") },
            text = { Text("This clears your earned stars and mission scores on this device. Your Beginner Astronaut Badge and name stay.") },
            confirmButton = {
                Button(
                    onClick = {
                        onResetProgress()
                        showResetConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AlertCoral)
                ) {
                    Text("Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) {
                    Text("Cancel", color = ElectricBlue)
                }
            }
        )
    }
}

@Composable
private fun StatTile(
    label: String,
    value: String,
    accent: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = ElevatedNavy.copy(alpha = 0.85f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = accent
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = SoftIceWhite
            )
        }
    }
}
