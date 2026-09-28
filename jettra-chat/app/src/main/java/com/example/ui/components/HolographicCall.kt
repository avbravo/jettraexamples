package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserEntity
import com.example.ui.theme.*
import kotlin.math.sin

@Composable
fun HolographicCall(
    peer: UserEntity,
    durationSeconds: Int,
    isMuted: Boolean,
    isCameraOn: Boolean,
    onToggleMute: () -> Unit,
    onToggleCamera: () -> Unit,
    onHangup: () -> Unit,
    modifier: Modifier = Modifier
) {
    val durationText = "${durationSeconds / 60}:${(durationSeconds % 60).toString().padStart(2, '0')}"

    // Dynamic vertical scanline animation
    val infiniteTransition = rememberInfiniteTransition(label = "scanline")
    val scanProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scan_y"
    )

    // Animated holographic interference/noise bar height
    val noiseHeight by infiniteTransition.animateFloat(
        initialValue = 2.dp.value,
        targetValue = 8.dp.value,
        animationSpec = infiniteRepeatable(
            animation = tween(150, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "noise_h"
    )

    // Animated sine waves representing dynamic audio feed
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .testTag("videoconference_overlay")
    ) {
        // Main Peer Holographic Video Feed (Simulated futuristic HUD telemetry)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .border(1.5.dp, HologramCyan.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .background(SpaceNavy)
        ) {
            // Draw holographic scanning lines & target reticle
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2, size.height / 2)
                val dotRadius = size.minDimension * 0.28f

                // Draw scan lines
                val scanY = size.height * scanProgress
                drawLine(
                    color = HologramCyan.copy(alpha = 0.45f),
                    start = Offset(0f, scanY),
                    end = Offset(size.width, scanY),
                    strokeWidth = 3.dp.toPx()
                )

                // Sub-ambient radar/target guidelines
                drawCircle(
                    color = LaserPurple.copy(alpha = 0.15f),
                    radius = dotRadius,
                    center = center
                )

                drawCircle(
                    color = HologramCyan.copy(alpha = 0.2f),
                    radius = dotRadius + 40.dp.toPx(),
                    center = center,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                        width = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                    )
                )

                // Target brackets
                val bracketSize = 30.dp.toPx()
                val inset = dotRadius + 30.dp.toPx()
                
                // Top Left bracket
                drawLine(HologramCyan, Offset(center.x - inset, center.y - inset), Offset(center.x - inset + bracketSize, center.y - inset), strokeWidth = 2.dp.toPx())
                drawLine(HologramCyan, Offset(center.x - inset, center.y - inset), Offset(center.x - inset, center.y - inset + bracketSize), strokeWidth = 2.dp.toPx())

                // Bottom Right bracket
                drawLine(HologramCyan, Offset(center.x + inset - bracketSize, center.y + inset), Offset(center.x + inset, center.y + inset), strokeWidth = 2.dp.toPx())
                drawLine(HologramCyan, Offset(center.x + inset, center.y + inset - bracketSize), Offset(center.x + inset, center.y + inset), strokeWidth = 2.dp.toPx())
            }

            // Peer avatar matrix center layout
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(bottom = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Glowy virtual matrix face scans
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .border(
                            2.dp,
                            Brush.sweepGradient(listOf(HologramCyan, LaserPurple, HologramCyan)),
                            CircleShape
                        )
                        .background(TransObsidianCard, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    // Face simulation canvas
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val c = Offset(size.width / 2, size.height / 2)
                        // Outer glowing dots
                        drawCircle(HologramCyan.copy(alpha = 0.15f), size.width / 2 - 10, c)
                        
                        // Futuristic face outline vector drawing
                        val path = androidx.compose.ui.graphics.Path().apply {
                            // Head shape
                            moveTo(size.width * 0.5f, size.height * 0.25f)
                            cubicTo(
                                size.width * 0.28f, size.height * 0.25f,
                                size.width * 0.25f, size.height * 0.55f,
                                size.width * 0.35f, size.height * 0.72f
                            )
                            lineTo(size.width * 0.5f, size.height * 0.85f)
                            lineTo(size.width * 0.65f, size.height * 0.72f)
                            cubicTo(
                                size.width * 0.75f, size.height * 0.55f,
                                size.width * 0.72f, size.height * 0.25f,
                                size.width * 0.5f, size.height * 0.25f
                            )
                            close()
                        }
                        drawPath(path, color = HologramCyan.copy(alpha = 0.4f), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx()))

                        // Hologram eyes/visor line
                        drawLine(
                            color = LaserPurple,
                            start = Offset(size.width * 0.35f, size.height * 0.45f),
                            end = Offset(size.width * 0.65f, size.height * 0.45f),
                            strokeWidth = 3.dp.toPx()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = peer.username,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                Text(
                    text = "CANAL DE ENLACE DIRECTO: ENCRIPTADO",
                    color = NeonGreen,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp),
                    letterSpacing = 1.sp
                )
            }

            // Top HUD Banner with Telemetry
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .align(Alignment.TopCenter)
                    .background(TransObsidianCard.copy(alpha = 0.85f), RoundedCornerShape(12.dp))
                    .border(0.5.dp, HologramCyan.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ENLACE P2P - ACTIVO",
                        color = HologramCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "VELOCIDAD: 124.5 Mbps  Ping: 12ms",
                        color = CyberGray,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Box(
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(SignalRed, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = durationText,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Left Sidebar Telemetry Ticker
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 24.dp)
            ) {
                listOf(
                    "AUD: SYNCED",
                    "VID: COMP",
                    "CRYPTO: AES",
                    "SIG: CRITICAL",
                    "BUB: ${peer.signalStrength}dB"
                ).forEach { metric ->
                    Text(
                        text = ">> $metric",
                        color = HologramCyan.copy(alpha = 0.6f),
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }

            // Picture in Picture feed (Self View) - Bottom-Right corner
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 110.dp, end = 20.dp)
                    .width(100.dp)
                    .height(130.dp)
                    .border(1.dp, LaserPurple.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .background(ObsidianBlack),
                contentAlignment = Alignment.Center
            ) {
                if (isCameraOn) {
                    // Local simulated camera image
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        // Drawing local camera grid
                        val w = size.width
                        val h = size.height
                        
                        drawRect(
                            brush = Brush.linearGradient(
                                colors = listOf(LaserPurple.copy(alpha = 0.25f), Color.Transparent),
                                start = Offset(0f, 0f),
                                end = Offset(w, h)
                            )
                        )

                        // Draw miniature scanline
                        val miniY = h * ((scanProgress * 1.5f) % 1f)
                        drawLine(LaserPurple.copy(alpha = 0.5f), Offset(0f, miniY), Offset(w, miniY), strokeWidth = 1.dp.toPx())

                        // Miniature face symbol
                        drawCircle(LaserPurple.copy(alpha = 0.3f), 15f, Offset(w / 2, h / 2 - 10f))
                        drawLine(LaserPurple.copy(alpha = 0.3f), Offset(w / 2 - 15f, h / 2 + 10f), Offset(w / 2 + 15f, h / 2 + 10f), strokeWidth = 2f)
                    }
                    Text(
                        text = "TU CÁMARA",
                        color = LaserPurple,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 4.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.VideocamOff,
                        contentDescription = "Cámara apagada",
                        tint = SignalRed,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "CÁMARA OFF",
                        color = SignalRed,
                        fontSize = 8.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 4.dp)
                    )
                }
            }

            // Real-time audio waveform visualizer at bottom of the feed
            Canvas(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 110.dp)
                    .fillMaxWidth(0.5f)
                    .height(24.dp)
            ) {
                val step = size.width / 24
                for (i in 0..24) {
                    val angle = Math.toRadians((i * 15 + waveOffset).toDouble())
                    val heightSample = (sin(angle) * (size.height / 2)).toFloat() + (size.height / 2)
                    
                    drawLine(
                        color = if (isMuted) SignalRed.copy(alpha = 0.4f) else NeonGreen.copy(alpha = 0.7f),
                        start = Offset(i * step, size.height),
                        end = Offset(i * step, heightSample),
                        strokeWidth = 2.dp.toPx()
                    )
                }
            }

            // Bottom Call Action Controls Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp, start = 16.dp, end = 16.dp)
                    .align(Alignment.BottomCenter),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Toggle Audio Mic
                FilledTonalIconButton(
                    onClick = onToggleMute,
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = if (isMuted) SignalRed else Color.White.copy(alpha = 0.12f),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .size(52.dp)
                        .testTag("call_mute_toggle")
                ) {
                    Icon(
                        imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = "Mutear micrófono"
                    )
                }

                Spacer(modifier = Modifier.width(28.dp))

                // Hangup Call
                FloatingActionButton(
                    onClick = onHangup,
                    containerColor = SignalRed,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier
                        .size(64.dp)
                        .testTag("call_hangup_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CallEnd,
                        contentDescription = "Finalizar videoconferencia",
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(28.dp))

                // Toggle Self Video Camera
                FilledTonalIconButton(
                    onClick = onToggleCamera,
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = if (!isCameraOn) SignalRed else Color.White.copy(alpha = 0.12f),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .size(52.dp)
                        .testTag("call_camera_toggle")
                ) {
                    Icon(
                        imageVector = if (isCameraOn) Icons.Default.Videocam else Icons.Default.VideocamOff,
                        contentDescription = "Apagar cámara"
                    )
                }
            }
        }
    }
}
