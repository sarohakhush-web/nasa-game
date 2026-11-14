package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AlertCoral
import com.example.ui.theme.BorderNavy
import com.example.ui.theme.CardNavy
import com.example.ui.theme.CosmicPurple
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.MissionBlue
import com.example.ui.theme.MutedStarText
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SoftIceWhite
import com.example.ui.theme.SpaceBlack
import com.example.ui.theme.SpaceNavy
import com.example.ui.theme.StarlightAmber
import com.example.ui.theme.SuccessEmerald
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

private data class StarDot(
    val xFraction: Float,
    val yFraction: Float,
    val radiusDp: Float,
    val phase: Float,
    val isAccent: Boolean
)

@Composable
fun SpaceStarfieldBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val stars = remember {
        val rng = Random(42)
        List(65) { index ->
            StarDot(
                xFraction = rng.nextFloat(),
                yFraction = rng.nextFloat(),
                radiusDp = 0.8f + rng.nextFloat() * 1.8f,
                phase = rng.nextFloat() * 2f * PI.toFloat(),
                isAccent = index % 9 == 0
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "starfield")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "star_pulse"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(SpaceBlack, SpaceNavy, Color(0xFF0A102B), SpaceBlack)
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Subtle purple & blue nebula glows
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        CosmicPurple.copy(alpha = 0.16f),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.85f, size.height * 0.14f),
                    radius = size.minDimension * 0.55f
                ),
                center = Offset(size.width * 0.85f, size.height * 0.14f),
                radius = size.minDimension * 0.55f
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        MissionBlue.copy(alpha = 0.14f),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.12f, size.height * 0.78f),
                    radius = size.minDimension * 0.6f
                ),
                center = Offset(size.width * 0.12f, size.height * 0.78f),
                radius = size.minDimension * 0.6f
            )

            // Distant ringed planet silhouette in top-right background
            val planetCenter = Offset(size.width * 0.88f, size.height * 0.13f)
            val planetRadius = size.minDimension * 0.065f
            drawCircle(
                brush = Brush.linearGradient(
                    colors = listOf(
                        CosmicPurple.copy(alpha = 0.35f),
                        MissionBlue.copy(alpha = 0.18f)
                    )
                ),
                radius = planetRadius,
                center = planetCenter
            )
            drawArc(
                color = ElectricBlue.copy(alpha = 0.22f),
                startAngle = -20f,
                sweepAngle = 220f,
                useCenter = false,
                topLeft = Offset(planetCenter.x - planetRadius * 1.7f, planetCenter.y - planetRadius * 0.45f),
                size = androidx.compose.ui.geometry.Size(planetRadius * 3.4f, planetRadius * 0.9f),
                style = Stroke(width = 2.dp.toPx())
            )

            // Twinkling stars
            stars.forEach { star ->
                val alpha = 0.28f + 0.65f * ((sin(pulse + star.phase) + 1f) / 2f)
                val color = if (star.isAccent) ElectricBlue.copy(alpha = alpha) else PureWhite.copy(alpha = alpha)
                drawCircle(
                    color = color,
                    radius = star.radiusDp.dp.toPx(),
                    center = Offset(star.xFraction * size.width, star.yFraction * size.height)
                )
            }
        }
        content()
    }
}

@Composable
fun GlassSpaceCard(
    modifier: Modifier = Modifier,
    borderColor: Color = BorderNavy,
    containerColor: Color = CardNavy.copy(alpha = 0.88f),
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = containerColor,
        border = BorderStroke(1.dp, borderColor),
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

@Composable
fun MissionControlTopBar(
    title: String,
    subtitle: String? = null,
    onBackClick: (() -> Unit)? = null,
    trailingStars: Int? = null,
    trailingScore: Int? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            if (onBackClick != null) {
                Surface(
                    shape = CircleShape,
                    color = CardNavy.copy(alpha = 0.9f),
                    border = BorderStroke(1.dp, BorderNavy)
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("top_bar_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back",
                            tint = PureWhite
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = PureWhite
                )
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelMedium,
                        color = ElectricBlue
                    )
                }
            }
        }

        if (trailingScore != null) {
            Surface(
                shape = RoundedCornerShape(50),
                color = CardNavy,
                border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PTS ",
                        style = MaterialTheme.typography.labelMedium,
                        color = ElectricBlue
                    )
                    Text(
                        text = trailingScore.toString(),
                        style = MaterialTheme.typography.labelLarge,
                        color = PureWhite
                    )
                }
            }
        } else if (trailingStars != null) {
            Surface(
                shape = RoundedCornerShape(50),
                color = CardNavy,
                border = BorderStroke(1.dp, StarlightAmber.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Total stars",
                        tint = StarlightAmber,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$trailingStars / 12",
                        style = MaterialTheme.typography.labelMedium,
                        color = PureWhite
                    )
                }
            }
        }
    }
}

@Composable
fun StarRatingRow(
    stars: Int,
    maxStars: Int = 3,
    starSize: Dp = 20.dp,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..maxStars) {
            val earned = i <= stars
            Icon(
                imageVector = if (earned) Icons.Filled.Star else Icons.Outlined.StarOutline,
                contentDescription = if (earned) "Earned star" else "Empty star",
                tint = if (earned) StarlightAmber else MutedStarText.copy(alpha = 0.45f),
                modifier = Modifier.size(starSize)
            )
        }
    }
}

enum class FeedbackTone {
    SUCCESS, WARNING, INFO
}

@Composable
fun ScienceFeedbackCard(
    visible: Boolean,
    tone: FeedbackTone,
    headline: String,
    explanation: String,
    pointsBadge: String? = null,
    modifier: Modifier = Modifier
) {
    val accent = when (tone) {
        FeedbackTone.SUCCESS -> SuccessEmerald
        FeedbackTone.WARNING -> AlertCoral
        FeedbackTone.INFO -> ElectricBlue
    }
    val icon = when (tone) {
        FeedbackTone.SUCCESS -> Icons.Filled.CheckCircle
        FeedbackTone.WARNING -> Icons.Filled.WarningAmber
        FeedbackTone.INFO -> Icons.Filled.Info
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(220)) + expandVertically(tween(240)),
        exit = fadeOut(tween(150)) + shrinkVertically(tween(180)),
        modifier = modifier
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = accent.copy(alpha = 0.14f),
            border = BorderStroke(1.5.dp, accent.copy(alpha = 0.7f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = headline,
                            tint = accent,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = headline,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                    }
                    if (!pointsBadge.isNullOrBlank()) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = accent.copy(alpha = 0.22f)
                        ) {
                            Text(
                                text = pointsBadge,
                                style = MaterialTheme.typography.labelMedium,
                                color = accent,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.padding(top = 6.dp))
                Text(
                    text = explanation,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SoftIceWhite
                )
            }
        }
    }
}
