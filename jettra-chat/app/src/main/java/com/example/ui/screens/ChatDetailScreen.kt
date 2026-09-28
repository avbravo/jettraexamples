package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.MessageEntity
import com.example.data.local.UserEntity
import com.example.ui.components.AudioPlayerMessage
import com.example.ui.components.VideoPlayerHUDOverlay
import com.example.ui.theme.*
import com.example.ui.viewmodel.ChatViewModel
import com.example.ui.viewmodel.ThemedFile
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ChatDetailScreen(
    viewModel: ChatViewModel,
    onBack: () -> Unit,
    onInitiateCall: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val phone = viewModel.selectedUserPhone ?: return
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val allChannels by viewModel.allChannels.collectAsStateWithLifecycle(emptyList())
    val activeMessages by viewModel.getActiveChatMessages().collectAsStateWithLifecycle(emptyList())

    val isChannel = phone.startsWith("#")
    val channel = if (isChannel) allChannels.find { it.channelId == phone } else null
    val peer = allUsers.find { it.phoneNumber == phone } ?: UserEntity(phone, if (isChannel) (channel?.name ?: phone) else "Terminal Nómada")

    var isAttachmentDrawerOpen by remember { mutableStateOf(false) }
    var textMessageInput by remember { mutableStateOf("") }

    // Dialog state for long pressing messages to retract/delete
    var messageToInspect by remember { mutableStateOf<MessageEntity?>(null) }

    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    // Auto scroll to bottom when new messages arrive
    LaunchedEffect(activeMessages.size) {
        if (activeMessages.isNotEmpty()) {
            lazyListState.animateScrollToItem(activeMessages.size - 1)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .testTag("chat_detail_container")
    ) {
        Scaffold(
            topBar = {
                // Top direct connection bar
                CenterAlignedTopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (isChannel) (channel?.name ?: phone) else peer.username,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(if (isChannel || peer.signalStrength > 0) NeonGreen else CyberGray, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isChannel) {
                                        "ENLACE SECTORIAL: ${channel?.nodeCount ?: 5} NODOS"
                                    } else {
                                        if (peer.signalStrength > 0) "CANAL EN RANGO SECURE" else "CANAL FUERA DE RANGO"
                                    },
                                    color = if (isChannel || peer.signalStrength > 0) NeonGreen else CyberGray,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.testTag("chat_back_btn")
                        ) {
                            Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Regresar", tint = HologramCyan)
                        }
                    },
                    actions = {
                        if (!isChannel) {
                            // Videoconference Call Trigger
                            IconButton(
                                onClick = { onInitiateCall(peer.phoneNumber) },
                                modifier = Modifier.testTag("chat_call_btn")
                            ) {
                                Icon(Icons.Default.Videocam, contentDescription = "Llamar", tint = HologramCyan)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = SpaceNavy,
                        titleContentColor = Color.White
                    ),
                    modifier = Modifier.border(0.5.dp, HologramCyan.copy(alpha = 0.2f))
                )
            },
            bottomBar = {
                // Futuristic input field and drawer attachments
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SpaceNavy)
                        .border(0.5.dp, HologramCyan.copy(alpha = 0.15f))
                        .navigationBarsPadding()
                ) {
                    // Sliding drawer of futuristic mock files to attachment easily!
                    AnimatedVisibility(
                        visible = isAttachmentDrawerOpen,
                        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(TransObsidianCard)
                                .border(0.5.dp, LaserPurple.copy(alpha = 0.25f))
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "DECK DE ARCHIVOS ENCRIPTADOS (P2P SENDER)",
                                color = LaserPurple,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(bottom = 12.dp),
                                letterSpacing = 1.sp
                            )

                            // Horizontally scrollable list of available files to send
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                viewModel.customThemedFiles.forEach { file ->
                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(84.dp)
                                            .border(
                                                1.dp,
                                                if (file.type == "AUDIO") HologramCyan.copy(alpha = 0.4f) else LaserPurple.copy(alpha = 0.4f),
                                                RoundedCornerShape(8.dp)
                                            )
                                            .clickable {
                                                viewModel.sendAttachment(file)
                                                isAttachmentDrawerOpen = false
                                                focusManager.clearFocus()
                                            }
                                            .testTag("attachment_item_${file.name}"),
                                        colors = CardDefaults.cardColors(containerColor = SpaceNavy.copy(alpha = 0.7f))
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(6.dp),
                                            verticalArrangement = Arrangement.SpaceBetween,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(
                                                imageVector = when (file.type) {
                                                    "AUDIO" -> Icons.Default.Audiotrack
                                                    "VIDEO" -> Icons.Default.VideoFile
                                                    else -> Icons.Default.InsertDriveFile
                                                },
                                                contentDescription = file.type,
                                                tint = if (file.type == "AUDIO") HologramCyan else LaserPurple,
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Text(
                                                text = file.name.take(18) + if (file.name.length > 18) ".." else "",
                                                color = Color.White,
                                                fontSize = 9.sp,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = file.size,
                                                color = CyberGray,
                                                fontSize = 8.sp,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Main typing bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // '+' Toggle drawer btn
                        IconButton(
                            onClick = { isAttachmentDrawerOpen = !isAttachmentDrawerOpen },
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    if (isAttachmentDrawerOpen) LaserPurple.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f),
                                    CircleShape
                                )
                                .testTag("toggle_attachments_drawer_btn")
                        ) {
                            Icon(
                                imageVector = if (isAttachmentDrawerOpen) Icons.Default.Close else Icons.Default.Add,
                                contentDescription = "Adjuntar archivos",
                                tint = if (isAttachmentDrawerOpen) LaserPurple else HologramCyan
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Styled futuristic TF
                        TextField(
                            value = textMessageInput,
                            onValueChange = { textMessageInput = it },
                            placeholder = {
                                Text(
                                    "Escribir comunicación cifrada...",
                                    color = CyberGray,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp
                                )
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = ObsidianBlack,
                                unfocusedContainerColor = ObsidianBlack,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedIndicatorColor = HologramCyan,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace, fontSize = 13.sp),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("chat_message_input")
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Send message rocket
                        IconButton(
                            onClick = {
                                if (textMessageInput.isNotBlank()) {
                                    viewModel.sendTextMessage(textMessageInput)
                                    textMessageInput = ""
                                    focusManager.clearFocus()
                                }
                            },
                            enabled = textMessageInput.isNotBlank(),
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = if (textMessageInput.isNotBlank()) HologramCyan else Color.White.copy(alpha = 0.05f),
                                contentColor = ObsidianBlack,
                                disabledContainerColor = Color.White.copy(alpha = 0.05f),
                                disabledContentColor = CyberGray
                            ),
                            modifier = Modifier
                                .size(40.dp)
                                .testTag("send_msg_btn")
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Enviar")
                        }
                    }
                }
            },
            containerColor = ObsidianBlack
        ) { innerPadding ->
            // Scrolling lists of messaging streams
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(activeMessages, key = { it.id }) { message ->
                    val isMyMessage = message.senderId == "self"
                    
                    MessageBubble(
                        message = message,
                        isMyMsg = isMyMessage,
                        isPlayingAudioState = viewModel.activePlayingMsgId == message.id && viewModel.isAudioPlaying,
                        audioProgressState = if (viewModel.activePlayingMsgId == message.id) viewModel.audioProgress else 0F,
                        audioDurationStringState = if (viewModel.activePlayingMsgId == message.id) viewModel.audioDurationString else "0:30",
                        onPlayAudio = { fileName ->
                            viewModel.toggleAudioPlayback(message.id, fileName)
                        },
                        onPlayVideo = {
                            viewModel.displayVideo(message)
                        },
                        onLongPress = {
                            // Retraction system trigger
                            messageToInspect = message
                        }
                    )
                }
            }
        }

        // Long press retraction dialog
        messageToInspect?.let { msg ->
            AlertDialog(
                onDismissRequest = { messageToInspect = null },
                modifier = Modifier
                    .border(1.dp, SignalRed.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .testTag("retraction_dialog"),
                containerColor = SpaceNavy,
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.DeleteForever,
                            contentDescription = "Alerta",
                            tint = SignalRed,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "DIAGNÓSTICO CONSOLA P2P",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "¿Deseas eliminar este paquete de la base de datos o retractarlo de la sesión de malla?",
                            color = CyberGray,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.Center
                        )
                    }
                },
                confirmButton = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = {
                                viewModel.retractMessage(msg.id)
                                messageToInspect = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SignalRed),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                                .testTag("retract_msg_option")
                        ) {
                            Text("RETRACTAR DE AMBOS NODOS", fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }

                        Button(
                            onClick = {
                                viewModel.deleteMessage(msg.id)
                                messageToInspect = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SpaceNavy),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyberGray),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                                .testTag("delete_msg_option")
                        ) {
                            Text("BORRAR DE MI HISTORIAL LOCAL", color = OffWhite, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            )
        }

        // FULLSCREEN SIMULATED VIDEO PLAYER HUD DISPLAY OVERLAY
        viewModel.activeVideoMsg?.let { videoMsg ->
            VideoPlayerHUDOverlay(
                message = videoMsg,
                onClose = { viewModel.displayVideo(null) }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageBubble(
    message: MessageEntity,
    isMyMsg: Boolean,
    isPlayingAudioState: Boolean,
    audioProgressState: Float,
    audioDurationStringState: String,
    onPlayAudio: (String?) -> Unit,
    onPlayVideo: () -> Unit,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bubbleColor = if (isMyMsg) SpaceNavy else TransObsidianCard
    val alignment = if (isMyMsg) Alignment.CenterEnd else Alignment.CenterStart
    val scanColor = if (isMyMsg) LaserPurple else HologramCyan

    Box(
        modifier = modifier
            .fillMaxWidth()
            .testTag("message_bubble_${message.id}"),
        contentAlignment = alignment
    ) {
        Column(
            horizontalAlignment = if (isMyMsg) Alignment.End else Alignment.Start
        ) {
            // Header stats above bubble
            Row(
                modifier = Modifier.padding(bottom = 3.dp, start = 4.dp, end = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isMyMsg) "TU NODO" else message.senderName,
                    color = scanColor,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "UTC ${formatUTCTimestamp(message.timestamp)}",
                    color = CyberGray,
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Message Bubble Card
            Card(
                colors = CardDefaults.cardColors(containerColor = bubbleColor),
                shape = RoundedCornerShape(
                    topStart = 12.dp,
                    topEnd = 12.dp,
                    bottomStart = if (isMyMsg) 12.dp else 2.dp,
                    bottomEnd = if (isMyMsg) 2.dp else 12.dp
                ),
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .border(
                        0.5.dp,
                        scanColor.copy(alpha = if (message.isDeleted) 0.15f else 0.35f),
                        RoundedCornerShape(
                            topStart = 12.dp,
                            topEnd = 12.dp,
                            bottomStart = if (isMyMsg) 12.dp else 2.dp,
                            bottomEnd = if (isMyMsg) 2.dp else 12.dp
                        )
                    )
                    .combinedClickable(
                        onLongClick = onLongPress,
                        onClick = {
                            if (message.fileType == "VIDEO") {
                                onPlayVideo()
                            }
                        }
                    )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    if (message.isDeleted) {
                        // Message soft-deleted status
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LockReset,
                                contentDescription = "Retractado",
                                tint = SignalRed.copy(alpha = 0.6f),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "[PAQUETE RETRACTADO DEL NODO]",
                                color = SignalRed.copy(alpha = 0.6f),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        // Regular message display
                        if (message.fileType == "AUDIO") {
                            AudioPlayerMessage(
                                message = message,
                                isPlaying = isPlayingAudioState,
                                progress = audioProgressState,
                                durationString = audioDurationStringState,
                                onPlayPauseToggle = { onPlayAudio(message.fileUri) }
                            )
                        } else if (message.fileType == "VIDEO") {
                            Column {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(130.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(ObsidianBlack)
                                        .border(0.5.dp, TechOrange.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayCircleFilled,
                                        contentDescription = "Video",
                                        tint = TechOrange,
                                        modifier = Modifier.size(44.dp)
                                    )
                                    Text(
                                        text = "VIDEO-STREAM FEED // TAP TO LOAD",
                                        color = CyberGray,
                                        fontSize = 8.sp,
                                        fontFamily = FontFamily.Monospace,
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .padding(bottom = 8.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = message.content,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        } else {
                            // Standard Text
                            Text(
                                text = message.content,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}

// Quick timestamp formatting helper
fun formatUTCTimestamp(timestamp: Long): String {
    val date = java.util.Date(timestamp)
    val sdf = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.US)
    sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
    return sdf.format(date)
}
