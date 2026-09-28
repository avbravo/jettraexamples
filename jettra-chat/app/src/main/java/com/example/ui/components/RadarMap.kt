package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.Grid3x3
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import kotlin.random.Random
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserEntity
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RadarMap(
    radarAngle: Float,
    isSearching: Boolean,
    nearbyPeers: List<UserEntity>,
    onSelectPeerChat: (String) -> Unit,
    onCallPeer: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedPeerOnRadar by remember { mutableStateOf<UserEntity?>(null) }

    // Pulsing circle scale animation for active radar targets
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_scale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_alpha"
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .testTag("radar_map_container")
    ) {
        val width = constraints.maxWidth.toFloat()
        val height = constraints.maxHeight.toFloat()
        val centerX = width / 2
        val centerY = height / 2
        val maxRadius = minOf(width, height) * 0.45f

        val density = LocalDensity.current

        // Background sci-fi coordinates grid
        Canvas(modifier = Modifier.fillMaxSize()) {
            val gridStep = 60.dp.toPx()
            
            // Draw grid lines
            val dashPattern = PathEffect.dashPathEffect(floatArrayOf(5f, 15f), 0f)
            
            for (x in 0..(size.width / gridStep).toInt()) {
                drawLine(
                    color = CyberGray.copy(alpha = 0.15f),
                    start = Offset(x * gridStep, 0f),
                    end = Offset(x * gridStep, size.height),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = dashPattern
                )
            }
            for (y in 0..(size.height / gridStep).toInt()) {
                drawLine(
                    color = CyberGray.copy(alpha = 0.15f),
                    start = Offset(0f, y * gridStep),
                    end = Offset(size.width, y * gridStep),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = dashPattern
                )
            }

            // Draw concentric radar range circles
            val ringCount = 4
            for (i in 1..ringCount) {
                val radius = maxRadius * (i.toFloat() / ringCount)
                drawCircle(
                    color = HologramCyan.copy(alpha = if (i == ringCount) 0.35f else 0.15f),
                    radius = radius,
                    center = Offset(centerX, centerY),
                    style = Stroke(
                        width = (if (i == ringCount) 1.5.dp else 1.dp).toPx(),
                        pathEffect = if (i % 2 == 0) null else dashPattern
                    )
                )
            }

            // Crosshair lines
            drawLine(
                color = HologramCyan.copy(alpha = 0.25f),
                start = Offset(centerX - maxRadius - 20, centerY),
                end = Offset(centerX + maxRadius + 20, centerY),
                strokeWidth = 1.dp.toPx()
            )
            drawLine(
                color = HologramCyan.copy(alpha = 0.25f),
                start = Offset(centerX, centerY - maxRadius - 20),
                end = Offset(centerX, centerY + maxRadius + 20),
                strokeWidth = 1.dp.toPx()
            )

            // Dynamic Sweeping glowing radar bar
            if (isSearching) {
                val angleRad = Math.toRadians(radarAngle.toDouble())
                val endX = centerX + maxRadius * cos(angleRad).toFloat()
                val endY = centerY + maxRadius * sin(angleRad).toFloat()

                // Glow line sweep
                drawLine(
                    brush = Brush.linearGradient(
                        colors = listOf(HologramCyan.copy(alpha = 0.8f), Color.Transparent),
                        start = Offset(centerX, centerY),
                        end = Offset(endX, endY)
                    ),
                    start = Offset(centerX, centerY),
                    end = Offset(endX, endY),
                    strokeWidth = 4.dp.toPx()
                )

                // Sweep wedge slice drawing
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Transparent,
                            HologramCyan.copy(alpha = 0.12f)
                        ),
                        center = Offset(centerX, centerY)
                    ),
                    startAngle = radarAngle - 35f,
                    sweepAngle = 35f,
                    useCenter = true,
                    size = androidx.compose.ui.geometry.Size(maxRadius * 2, maxRadius * 2),
                    topLeft = Offset(centerX - maxRadius, centerY - maxRadius)
                )
            }
        }

        // Draw central terminal anchor node
        Box(
            modifier = Modifier
                .size(16.dp)
                .align(Alignment.Center)
                .background(HologramCyan, CircleShape)
                .border(2.dp, ObsidianBlack, CircleShape)
        )

        // Plot nearby peer dots
        nearbyPeers.forEach { peer ->
            // Coordinates based on centerX, centerY and mapped ratio of radius
            val relativeX = centerX + (peer.xRatio * maxRadius)
            val relativeY = centerY + (peer.yRatio * maxRadius)

            val xDp = with(density) { relativeX.toDp() }
            val yDp = with(density) { relativeY.toDp() }

            // Target Pulse ring if this peer is active and connected
            Box(
                modifier = Modifier
                    .offset(xDp - 20.dp, yDp - 20.dp)
                    .size(40.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = (if (peer.signalStrength > 80) NeonGreen else HologramCyan)
                            .copy(alpha = pulseAlpha),
                        radius = size.minDimension / 2 * pulseScale,
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                }

                // Peer dot button
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .align(Alignment.Center)
                        .clip(CircleShape)
                        .background(if (peer == selectedPeerOnRadar) LaserPurple else if (peer.signalStrength > 80) NeonGreen else HologramCyan)
                        .border(1.5.dp, ObsidianBlack, CircleShape)
                        .clickable { selectedPeerOnRadar = peer }
                        .testTag("radar_target_${peer.phoneNumber}")
                )
            }
        }

        // Selected peer HUD detail card Overlay at bottom of radar map
        selectedPeerOnRadar?.let { peer ->
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 20.dp, vertical = 24.dp)
                    .fillMaxWidth()
                    .border(1.dp, HologramCyan.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .testTag("peer_telemetry_hud"),
                colors = CardDefaults.cardColors(containerColor = TransObsidianCard),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(NeonGreen, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "NODO EN RANGO DE ENLACE",
                                color = HologramCyan,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )
                        }
                        IconButton(
                            onClick = { selectedPeerOnRadar = null },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Text("×", color = Color.White, fontSize = 20.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = peer.username,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )

                    Text(
                        text = "TEL: ${peer.phoneNumber}",
                        color = CyberGray,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Tactical HUD Stats
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, CyberGray.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                            .background(SpaceNavy.copy(alpha = 0.5f))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("POTENCIA-P2P", color = CyberGray, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                            Text("${peer.signalStrength} dBm", color = NeonGreen, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily.Monospace)
                        }
                        Column {
                            Text("ESTADO-RED", color = CyberGray, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                            Text(peer.status, color = HologramCyan, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily.Monospace)
                        }
                        Column {
                            Text("DIRECCIÓN-IP", color = CyberGray, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                            Text("10.0.8.${Random.nextInt(5, 254)}", color = Color.White, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Connection Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                onSelectPeerChat(peer.phoneNumber)
                                selectedPeerOnRadar = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = LaserPurple),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Message, contentDescription = "Abrir chat")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("CONECTAR", fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }

                        Button(
                            onClick = {
                                onCallPeer(peer.phoneNumber)
                                selectedPeerOnRadar = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = HologramCyan),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Videocam, contentDescription = "Videoconferencia")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("VIDEOCALL", fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = ObsidianBlack)
                        }
                    }
                }
            }
        }

        // Radar Scanning Controls floating panel top-right
        Card(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .border(0.5.dp, HologramCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
            colors = CardDefaults.cardColors(containerColor = TransObsidianCard)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Radio,
                    contentDescription = "Estado Escaneo",
                    tint = if (isSearching) NeonGreen else CyberGray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isSearching) "ESCANEO ACTIVO" else "ESCANEO PAUSADO",
                    color = if (isSearching) NeonGreen else CyberGray,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
