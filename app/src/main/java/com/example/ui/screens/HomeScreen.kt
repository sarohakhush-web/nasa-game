package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.AstronautProfileState
import com.example.data.NasaEducationalSources
import com.example.ui.components.GlassSpaceCard
import com.example.ui.theme.BorderNavy
import com.example.ui.theme.CardNavy
import com.example.ui.theme.CosmicPurple
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElevatedNavy
import com.example.ui.theme.MissionBlue
import com.example.ui.theme.MutedStarText
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SoftIceWhite
import com.example.ui.theme.SpaceBlack
import com.example.ui.theme.StarlightAmber

@Composable
fun HomeScreen(
    profileState: AstronautProfileState,
    onStartMissionClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val context = LocalContext.current
    var showHowToPlay by remember { mutableStateOf(false) }
    var astronautBoosted by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "astronaut_float")
    val floatY by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float_y"
    )
    val gentleTilt by infiniteTransition.animateFloat(
        initialValue = -2.5f,
        targetValue = 2.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "tilt"
    )
    val tapScale by animateFloatAsState(
        targetValue = if (astronautBoosted) 1.07f else 1f,
        animationSpec = spring(dampingRatio = 0.45f),
        finishedListener = { astronautBoosted = false },
        label = "tap_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 680.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Cadet Status Pill
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onProfileClick)
                    .testTag("home_profile_banner"),
                shape = RoundedCornerShape(50),
                color = CardNavy.copy(alpha = 0.9f),
                border = BorderStroke(1.dp, BorderNavy)
            ) {
                Row(
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(MissionBlue, CosmicPurple))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = "Astronaut profile",
                                tint = PureWhite,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = profileState.astronautName,
                                style = MaterialTheme.typography.titleMedium,
                                color = PureWhite
                            )
                            Text(
                                text = profileState.rank.title,
                                style = MaterialTheme.typography.labelMedium,
                                color = ElectricBlue
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(50),
                        color = ElevatedNavy
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "Stars earned",
                                tint = StarlightAmber,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${profileState.totalStars}/${profileState.maxPossibleStars}",
                                style = MaterialTheme.typography.labelMedium,
                                color = PureWhite
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Animated Astronaut Hero
            Box(
                modifier = Modifier
                    .size(210.dp)
                    .clickable { astronautBoosted = true },
                contentAlignment = Alignment.Center
            ) {
                // Cosmic glowing ring
                Box(
                    modifier = Modifier
                        .size(194.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    ElectricBlue.copy(alpha = 0.28f),
                                    CosmicPurple.copy(alpha = 0.14f),
                                    SpaceBlack.copy(alpha = 0f)
                                )
                            )
                        )
                )
                Surface(
                    modifier = Modifier
                        .size(172.dp)
                        .offset(y = floatY.dp)
                        .rotate(gentleTilt)
                        .scale(tapScale),
                    shape = CircleShape,
                    border = BorderStroke(
                        width = 2.dp,
                        brush = Brush.linearGradient(listOf(ElectricBlue, CosmicPurple))
                    ),
                    color = CardNavy
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_astronaut_hero),
                        contentDescription = "Animated junior astronaut character floating in space",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            AnimatedVisibility(visible = astronautBoosted) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = ElectricBlue.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.5f)),
                    modifier = Modifier.padding(top = 6.dp)
                ) {
                    Text(
                        text = "Thrusters checked! Ready for launch, ${profileState.astronautName}!",
                        style = MaterialTheme.typography.labelMedium,
                        color = ElectricBlue,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Game Title & Subtitle
            Text(
                text = stringResource(R.string.game_full_title),
                style = MaterialTheme.typography.displaySmall,
                color = PureWhite,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(50),
                color = CosmicPurple.copy(alpha = 0.2f),
                border = BorderStroke(1.dp, CosmicPurple.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = null,
                        tint = ElectricBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.game_subtitle),
                        style = MaterialTheme.typography.labelLarge,
                        color = SoftIceWhite
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Primary CTA: Start Mission
            Button(
                onClick = onStartMissionClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("start_mission_button"),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MissionBlue,
                    contentColor = PureWhite
                ),
                contentPadding = PaddingValues(horizontal = 24.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.RocketLaunch,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.cta_start_mission),
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Secondary Action Row: Astronaut Profile & How to Play
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onProfileClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("profile_button"),
                    shape = MaterialTheme.shapes.medium,
                    border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.6f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PureWhite)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = null,
                        tint = ElectricBlue,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Profile",
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                OutlinedButton(
                    onClick = { showHowToPlay = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("how_to_play_button"),
                    shape = MaterialTheme.shapes.medium,
                    border = BorderStroke(1.dp, CosmicPurple.copy(alpha = 0.7f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PureWhite)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                        contentDescription = null,
                        tint = CosmicPurple,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.cta_how_to_play),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // About the Game Section
            GlassSpaceCard(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = painterResource(id = R.drawable.img_space_banner),
                    contentDescription = "Spacecraft orbiting above Earth and the Moon",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(136.dp)
                        .clip(MaterialTheme.shapes.medium)
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = stringResource(R.string.about_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = PureWhite
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.about_body),
                    style = MaterialTheme.typography.bodyMedium,
                    color = SoftIceWhite
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Educational Sources & Non-Endorsement Notice
            GlassSpaceCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = CosmicPurple.copy(alpha = 0.45f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.School,
                        contentDescription = null,
                        tint = ElectricBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.sources_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = PureWhite
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Explore real space science articles and activities:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedStarText
                )
                Spacer(modifier = Modifier.height(10.dp))

                NasaEducationalSources.forEach { source ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(source.url))
                                context.startActivity(intent)
                            }
                            .testTag("source_link_${source.title.lowercase().replace(" ", "_")}"),
                        shape = MaterialTheme.shapes.small,
                        color = ElevatedNavy.copy(alpha = 0.75f),
                        border = BorderStroke(1.dp, BorderNavy)
                    ) {
                        Row(
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = source.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = ElectricBlue
                                )
                                Text(
                                    text = source.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = SoftIceWhite
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = "Open ${source.title}",
                                tint = ElectricBlue,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = stringResource(R.string.disclaimer_nasa),
                    style = MaterialTheme.typography.labelMedium,
                    color = MutedStarText
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showHowToPlay) {
        HowToPlayDialog(onDismiss = { showHowToPlay = false })
    }
}

@Composable
private fun HowToPlayDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardNavy,
        titleContentColor = PureWhite,
        textContentColor = SoftIceWhite,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.RocketLaunch,
                    contentDescription = null,
                    tint = ElectricBlue,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "How to Play",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "1. Customize your Cadet Name in your Astronaut Profile.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "2. Tap Start Mission and pick any of the 4 space training missions: Moon Base, Rover Route, Orbit Basics, or Life Support.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "3. Read each science clue carefully, experiment with controls, and learn why each engineering choice works.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "4. Earn up to 3 Stars per mission (12 total) to unlock higher Astronaut Ranks from Space Cadet to Junior Commander!",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = StarlightAmber
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MissionBlue,
                    contentColor = PureWhite
                ),
                modifier = Modifier.testTag("close_how_to_play_button")
            ) {
                Text("Got It!")
            }
        }
    )
}
