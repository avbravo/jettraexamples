package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import kotlin.random.Random
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.MessageEntity
import com.example.ui.theme.*
import kotlin.math.sin

@Composable
fun AudioPlayerMessage(
    message: MessageEntity,
    isPlaying: Boolean,
    progress: Float,
    durationString: String,
    onPlayPauseToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Generate animated height states for sound wave bands if playing
    val animationTransition = rememberInfiniteTransition(label = "waveform_anim")
    val waveHeights = List(16) { index ->
        val frequencyOffset = (index * 20).toLong()
        animationTransition.animateFloat(
            initialValue = 0.1f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 300 + (index * 15),
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bar_$index"
        )
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, HologramCyan.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
            .background(SpaceNavy.copy(alpha = 0.8f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Play/Pause Button
        FilledIconButton(
            onClick = onPlayPauseToggle,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = if (isPlaying) LaserPurple else HologramCyan,
                contentColor = if (isPlaying) Color.White else ObsidianBlack
            ),
            modifier = Modifier
                .size(40.dp)
                .testTag("audio_toggle_${message.id}")
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Pausar" else "Reproducir",
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Center Waveform and Information Area
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = message.fileName ?: "Frecuencia_Desconocida.mp3",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = message.fileSize ?: "---",
                    color = CyberGray,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Waveform and linear progress layout
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(26.dp)
            ) {
                // Waveform drawing
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    waveHeights.forEachIndexed { i, heightState ->
                        // Calculate if bar is currently "passed" by the progress playhead
                        val barProgressPoint = i.toFloat() / waveHeights.size
                        val activeColor = if (isPlaying) LaserPurple else HologramCyan
                        val inactiveColor = CyberGray.copy(alpha = 0.3f)
                        val color = if (progress >= barProgressPoint) activeColor else inactiveColor

                        val scaledHeight = if (isPlaying) heightState.value else 0.2f

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(scaledHeight)
                                .clip(RoundedCornerShape(2.dp))
                                .background(color)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Numeric playback tracker
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Playhead calculated duration
                val currentSec = ((progress * 30).toInt()) % 60
                val progressText = "0:${currentSec.toString().padStart(2, '0')}"

                Text(
                    text = progressText,
                    color = HologramCyan,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = durationString,
                    color = CyberGray,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
fun VideoPlayerHUDOverlay(
    message: MessageEntity,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPlayingVideo by remember { mutableStateOf(true) }
    var videoProgressState by remember { mutableStateOf(0.15f) }

    // Simulate steady video duration playback progress
    LaunchedEffect(isPlayingVideo) {
        if (isPlayingVideo) {
            while (videoProgressState < 1.0f) {
                kotlinx.coroutines.delay(1000)
                videoProgressState += 0.05f
            }
            isPlayingVideo = false
            videoProgressState = 0f
        }
    }

    val infiniteAnimation = rememberInfiniteTransition(label = "video_grain")
    val gridOpacity by infiniteAnimation.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.22f,
        animationSpec = infiniteRepeatable(
            animation = tween(150, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "grain"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack.copy(alpha = 0.95f))
            .padding(16.dp)
            .testTag("video_player_hud_overlay"),
        contentAlignment = Alignment.Center
    ) {
        // Player container Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
                .border(1.dp, LaserPurple.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = SpaceNavy),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // TOP HUD Navigation
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(TransObsidianCard)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(TechOrange, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "VIDEODEC DEL LADO DEL CLIENTE // ${message.fileName}",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("close_video_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar reproductor",
                            tint = Color.White
                        )
                    }
                }

                // Holographic Video Screen Content
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(ObsidianBlack)
                ) {
                    // Futuristic Matrix display
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height

                        // Grid overlays drawing
                        val cols = 12
                        val colWidth = w / cols
                        for (i in 0..cols) {
                            drawLine(
                                color = LaserPurple.copy(alpha = gridOpacity),
                                start = Offset(i * colWidth, 0f),
                                end = Offset(i * colWidth, h),
                                strokeWidth = 1.dp.toPx()
                            )
                        }

                        // Simulated spatial flight coordinates vector
                        drawCircle(LaserPurple.copy(alpha = 0.1f), w * 0.25f, Offset(w / 2, h / 2))
                        
                        // Sweeping flight path line
                        drawLine(
                            color = LaserPurple.copy(alpha = 0.35f),
                            start = Offset(w * 0.15f, h * 0.5f + sin(videoProgressState * 10f) * 100f),
                            end = Offset(w * 0.85f, h * 0.5f - sin(videoProgressState * 10f) * 100f),
                            strokeWidth = 2.dp.toPx()
                        )
                    }

                    // Simulated Drone Telemetry Info overlays
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(16.dp)
                    ) {
                        Text("FPS: 60.0", color = NeonGreen, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        Text("BITRATE: ${Random.nextInt(4800, 5200)} kbps", color = HologramCyan, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        Text("RESOLUCIÓN: 1080p (HEVC RAW)", color = Color.White, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    }

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text("NODO-P2P: SECTOR 4", color = CyberGray, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        Text("ESTADO: EN BUFFER", color = NeonGreen, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    }

                    // Large Center play state toggle overlay
                    IconButton(
                        onClick = { isPlayingVideo = !isPlayingVideo },
                        modifier = Modifier
                            .size(72.dp)
                            .align(Alignment.Center)
                            .background(ObsidianBlack.copy(alpha = 0.5f), CircleShape)
                            .border(1.5.dp, HologramCyan, CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isPlayingVideo) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlayingVideo) "Pause" else "Play",
                            tint = HologramCyan,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                // BOTTOM CONTROLS DECK
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(TransObsidianCard)
                        .padding(16.dp)
                ) {
                    // Playback seekbar slider
                    Slider(
                        value = videoProgressState,
                        onValueChange = { videoProgressState = it },
                        colors = SliderDefaults.colors(
                            thumbColor = LaserPurple,
                            activeTrackColor = LaserPurple,
                            inactiveTrackColor = CyberGray.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val durationInSeconds = 125
                        val currentPlayedSec = (videoProgressState * durationInSeconds).toInt()
                        
                        val minCurr = currentPlayedSec / 60
                        val secCurr = currentPlayedSec % 60
                        val minDur = durationInSeconds / 60
                        val secDur = durationInSeconds % 60

                        Text(
                            text = "$minCurr:${secCurr.toString().padStart(2, '0')} / $minDur:${secDur.toString().padStart(2, '0')}",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )

                        Text(
                            text = "OFFLINE SOURCE",
                            color = TechOrange,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .border(1.dp, TechOrange.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}
