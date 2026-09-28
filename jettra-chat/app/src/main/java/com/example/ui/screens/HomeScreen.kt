package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.AppDatabase
import com.example.data.local.ChannelEntity
import com.example.data.local.UserEntity
import com.example.ui.components.RadarMap
import com.example.ui.theme.*
import com.example.ui.viewmodel.ChatViewModel
import kotlinx.coroutines.launch
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: ChatViewModel,
    onNavigateToChat: () -> Unit,
    onInitiateCall: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val allPeers by viewModel.allUsers.collectAsStateWithLifecycle()
    val nearbyPeers by viewModel.nearbyUsers.collectAsStateWithLifecycle()

    var activeTab by remember { mutableStateOf(0) } // 0: Radar, 1: Canales, 2: Directorio
    var isAddContactOpen by remember { mutableStateOf(false) }
    var isProfileDialogOpen by remember { mutableStateOf(false) }
    var editingChannel by remember { mutableStateOf<ChannelEntity?>(null) }

    val context = LocalContext.current
    var hasContactsPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val contactsPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasContactsPermission = isGranted
        if (isGranted) {
            viewModel.loadDeviceContacts()
        }
    }

    // Load device contacts automatically on launch or when tab shifts to 2
    LaunchedEffect(hasContactsPermission, activeTab) {
        if (hasContactsPermission && activeTab == 2) {
            viewModel.loadDeviceContacts()
        }
    }

    // Pulsing background grid overlay trigger
    val infiniteTransition = rememberInfiniteTransition(label = "hud")
    val hudPulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Scaffold(
        topBar = {
            // Futuristic Main HUD Terminal Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ObsidianBlack)
                    .border(0.5.dp, HologramCyan.copy(alpha = 0.2f))
                    .padding(horizontal = 16.dp, vertical = 20.dp)
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(NeonGreen, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "MESH TERMINAL v3.2",
                                color = OffWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 2.sp
                            )
                        }
                        Text(
                            "PROTOCOLO DESCENTRALIZADO // NO SERVER",
                            color = HologramCyan.copy(alpha = hudPulseAlpha),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    // Tactical stats badge and Profile Editor Trigger
                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { isProfileDialogOpen = true }
                            .padding(4.dp)
                    ) {
                        Text(
                            text = "ALIAS: ${viewModel.myUsername}",
                            color = NeonGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(HologramCyan, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "NODO: ${viewModel.myPhoneNumber}",
                                color = HologramCyan,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            if (activeTab == 2) {
                FloatingActionButton(
                    onClick = { isAddContactOpen = true },
                    containerColor = LaserPurple,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .padding(bottom = 16.dp, end = 16.dp)
                        .testTag("add_contact_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir Peer")
                }
            }
        },
        containerColor = ObsidianBlack,
        modifier = modifier.testTag("home_screen_scaffold")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Futuristic Segmented Tabs Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .border(1.dp, CyberGray.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
                    .background(SpaceNavy.copy(alpha = 0.6f))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Tab 0: Radar
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (activeTab == 0) HologramCyan.copy(alpha = 0.15f) else Color.Transparent)
                        .clickable { activeTab = 0 }
                        .padding(vertical = 10.dp)
                        .testTag("radar_tab_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Adjust,
                            contentDescription = "Radar",
                            tint = if (activeTab == 0) HologramCyan else CyberGray,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "RADAR",
                            color = if (activeTab == 0) HologramCyan else CyberGray,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Tab 1: Channels
                Box(
                    modifier = Modifier
                        .weight(1.1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (activeTab == 1) HologramCyan.copy(alpha = 0.15f) else Color.Transparent)
                        .clickable { activeTab = 1 }
                        .padding(vertical = 10.dp)
                        .testTag("chats_tab_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Message,
                            contentDescription = "Chats",
                            tint = if (activeTab == 1) HologramCyan else CyberGray,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "CANALES",
                            color = if (activeTab == 1) HologramCyan else CyberGray,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Tab 2: Contacts list
                Box(
                    modifier = Modifier
                        .weight(1.2f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (activeTab == 2) HologramCyan.copy(alpha = 0.15f) else Color.Transparent)
                        .clickable { activeTab = 2 }
                        .padding(vertical = 10.dp)
                        .testTag("directory_tab_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Contacts,
                            contentDescription = "Contactos",
                            tint = if (activeTab == 2) HologramCyan else CyberGray,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "DIRECTORIO",
                            color = if (activeTab == 2) HologramCyan else CyberGray,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            // Tab Content Display with Transition Animations!
            Box(modifier = Modifier.weight(1f)) {
                when (activeTab) {
                    0 -> {
                        // RADAR MAP VIEW
                        RadarMap(
                            radarAngle = viewModel.radarAngle,
                            isSearching = viewModel.isSearchingNearby,
                            nearbyPeers = nearbyPeers,
                            onSelectPeerChat = { phone ->
                                viewModel.selectChat(phone)
                                onNavigateToChat()
                            },
                            onCallPeer = { phone ->
                                onInitiateCall(phone)
                            }
                        )
                    }
                    1 -> {
                        // CHATS THREADS VIEW & GROUP CHANNELS DUAL LIST
                        var subTab by remember { mutableStateOf(0) } // 0: Privados, 1: Canales
                        val allChannels by viewModel.allChannels.collectAsStateWithLifecycle()
                        var isCreateChannelOpen by remember { mutableStateOf(false) }

                        Column(modifier = Modifier.fillMaxSize()) {
                            // Subsegment selector
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .border(0.5.dp, CyberGray.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                    .background(SpaceNavy.copy(alpha = 0.3f))
                                    .padding(2.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (subTab == 0) LaserPurple.copy(alpha = 0.2f) else Color.Transparent)
                                        .clickable { subTab = 0 }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "CHATS PEER-TO-PEER",
                                        color = if (subTab == 0) LaserPurple else CyberGray,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (subTab == 1) LaserPurple.copy(alpha = 0.2f) else Color.Transparent)
                                        .clickable { subTab = 1 }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "CANALES DE GRUPO",
                                        color = if (subTab == 1) LaserPurple else CyberGray,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }

                            if (subTab == 0) {
                                // Direct Peer Chats Threads
                                if (allPeers.isEmpty()) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        EmptyHUDState("NO HAY INTERCONEXIONES ACTIVAS")
                                    }
                                } else {
                                    LazyColumn(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 16.dp),
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        items(allPeers) { peer ->
                                            PeerChatCard(
                                                peer = peer,
                                                onConnect = {
                                                    viewModel.selectChat(peer.phoneNumber)
                                                    onNavigateToChat()
                                                },
                                                onInitiateCall = { onInitiateCall(peer.phoneNumber) }
                                            )
                                        }
                                    }
                                }
                            } else {
                                // Channels list
                                Column(modifier = Modifier.fillMaxSize()) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            "CANALES ACOPLADOS (${allChannels.size})",
                                            color = HologramCyan,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Button(
                                            onClick = { isCreateChannelOpen = true },
                                            colors = ButtonDefaults.buttonColors(containerColor = LaserPurple),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                            modifier = Modifier.defaultMinSize(minWidth = 1.dp, minHeight = 1.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    Icons.Default.Add,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    "CREAR CANAL",
                                                    color = Color.White,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    fontFamily = FontFamily.Monospace
                                                )
                                            }
                                        }
                                    }

                                    if (allChannels.isEmpty()) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            EmptyHUDState("SIN CANALES ACTIVOS")
                                        }
                                    } else {
                                        LazyColumn(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(horizontal = 16.dp),
                                            verticalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            items(allChannels) { channel ->
                                                ChannelItemCard(
                                                    channel = channel,
                                                    onConnect = {
                                                        viewModel.selectChat(channel.channelId)
                                                        onNavigateToChat()
                                                    },
                                                    onEdit = {
                                                        editingChannel = channel
                                                    },
                                                    onDelete = {
                                                        viewModel.deleteChannel(channel)
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Create Channel Dialog
                        if (isCreateChannelOpen) {
                            var channelName by remember { mutableStateOf("") }
                            var channelDesc by remember { mutableStateOf("") }
                            var hasError by remember { mutableStateOf(false) }

                            AlertDialog(
                                onDismissRequest = { isCreateChannelOpen = false },
                                containerColor = SpaceNavy,
                                modifier = Modifier.border(1.dp, HologramCyan.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                                title = {
                                    Text(
                                        "CREAR NUEVO CANAL P2P",
                                        color = HologramCyan,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                },
                                text = {
                                    Column {
                                        TextField(
                                            value = channelName,
                                            onValueChange = { channelName = it; hasError = false },
                                            label = { Text("Nombre del Canal", color = CyberGray, fontFamily = FontFamily.Monospace) },
                                            colors = TextFieldDefaults.colors(
                                                focusedContainerColor = ObsidianBlack,
                                                unfocusedContainerColor = ObsidianBlack,
                                                focusedTextColor = Color.White,
                                                unfocusedTextColor = Color.White,
                                                focusedIndicatorColor = HologramCyan,
                                                unfocusedIndicatorColor = CyberGray
                                            ),
                                            textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace, fontSize = 13.sp),
                                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                                        )
                                        TextField(
                                            value = channelDesc,
                                            onValueChange = { channelDesc = it },
                                            label = { Text("Descripción / Propósito", color = CyberGray, fontFamily = FontFamily.Monospace) },
                                            colors = TextFieldDefaults.colors(
                                                focusedContainerColor = ObsidianBlack,
                                                unfocusedContainerColor = ObsidianBlack,
                                                focusedTextColor = Color.White,
                                                unfocusedTextColor = Color.White,
                                                focusedIndicatorColor = HologramCyan,
                                                unfocusedIndicatorColor = CyberGray
                                            ),
                                            textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace, fontSize = 13.sp),
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        if (hasError) {
                                            Text(
                                                "El nombre del canal es obligatorio.",
                                                color = SignalRed,
                                                fontSize = 11.sp,
                                                fontFamily = FontFamily.Monospace,
                                                modifier = Modifier.padding(top = 6.dp)
                                            )
                                        }
                                    }
                                },
                                confirmButton = {
                                    Button(
                                        onClick = {
                                            if (channelName.isNotBlank()) {
                                                viewModel.createChannel(channelName, channelDesc)
                                                isCreateChannelOpen = false
                                            } else {
                                                hasError = true
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = LaserPurple)
                                    ) {
                                        Text("CREAR", color = Color.White, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { isCreateChannelOpen = false }) {
                                        Text("CANCELAR", color = CyberGray, fontFamily = FontFamily.Monospace)
                                    }
                                }
                            )
                        }

                        // Edit Channel Dialog
                        if (editingChannel != null) {
                            var editName by remember(editingChannel) { mutableStateOf(editingChannel?.name ?: "") }
                            var editDesc by remember(editingChannel) { mutableStateOf(editingChannel?.description ?: "") }
                            var hasEditError by remember { mutableStateOf(false) }

                            AlertDialog(
                                onDismissRequest = { editingChannel = null },
                                containerColor = SpaceNavy,
                                modifier = Modifier.border(1.dp, HologramCyan.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                                text = {
                                    Column {
                                        Text(
                                            "MODIFICAR CANAL DE GRUPO",
                                            color = HologramCyan,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace,
                                            letterSpacing = 1.sp,
                                            modifier = Modifier.padding(bottom = 16.dp)
                                        )

                                        TextField(
                                            value = editName,
                                            onValueChange = {
                                                editName = it
                                                hasEditError = false
                                            },
                                            label = { Text("Nombre del canal", color = CyberGray, fontFamily = FontFamily.Monospace) },
                                            colors = TextFieldDefaults.colors(
                                                focusedContainerColor = ObsidianBlack,
                                                unfocusedContainerColor = ObsidianBlack,
                                                focusedTextColor = Color.White,
                                                unfocusedTextColor = Color.White,
                                                focusedIndicatorColor = HologramCyan,
                                                unfocusedIndicatorColor = CyberGray
                                            ),
                                            textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace, fontSize = 14.sp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(bottom = 12.dp)
                                        )

                                        TextField(
                                            value = editDesc,
                                            onValueChange = {
                                                editDesc = it
                                            },
                                            label = { Text("Descripción", color = CyberGray, fontFamily = FontFamily.Monospace) },
                                            colors = TextFieldDefaults.colors(
                                                focusedContainerColor = ObsidianBlack,
                                                unfocusedContainerColor = ObsidianBlack,
                                                focusedTextColor = Color.White,
                                                unfocusedTextColor = Color.White,
                                                focusedIndicatorColor = HologramCyan,
                                                unfocusedIndicatorColor = CyberGray
                                            ),
                                            textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace, fontSize = 14.sp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(bottom = 12.dp)
                                        )

                                        if (hasEditError) {
                                            Text(
                                                "El nombre del canal no puede estar vacío.",
                                                color = SignalRed,
                                                fontSize = 11.sp,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                    }
                                },
                                confirmButton = {
                                    Button(
                                        onClick = {
                                            editingChannel?.let { original ->
                                                if (editName.isNotBlank()) {
                                                    viewModel.updateChannel(
                                                        original.copy(name = editName, description = editDesc)
                                                    )
                                                    editingChannel = null
                                                } else {
                                                    hasEditError = true
                                                }
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = LaserPurple)
                                    ) {
                                        Text("RECONFIGURAR", color = Color.White, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { editingChannel = null }) {
                                        Text("DESCARTAR", color = CyberGray, fontFamily = FontFamily.Monospace)
                                    }
                                }
                            )
                        }
                    }
                    2 -> {
                        // DIRECTORY REGISTERED & DEVICE CONTACTS COMBINED LIST WITH SEARCH
                        var searchQuery by remember { mutableStateOf("") }
                        val filteredPeers = allPeers.filter {
                            it.username.contains(searchQuery, ignoreCase = true) ||
                            it.phoneNumber.contains(searchQuery)
                        }
                        val filteredDeviceContacts = viewModel.deviceContacts.filter {
                            it.name.contains(searchQuery, ignoreCase = true) ||
                            it.phoneNumber.contains(searchQuery)
                        }

                        Column(modifier = Modifier.fillMaxSize()) {
                            // High-tech search bar
                            TextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Buscar",
                                        tint = HologramCyan,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { searchQuery = "" }) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Limpiar",
                                                tint = CyberGray,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                },
                                placeholder = {
                                    Text(
                                        "Buscar por nombre o número...",
                                        color = CyberGray,
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = SpaceNavy.copy(alpha = 0.5f),
                                    unfocusedContainerColor = SpaceNavy.copy(alpha = 0.3f),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedIndicatorColor = HologramCyan,
                                    unfocusedIndicatorColor = CyberGray.copy(alpha = 0.3f)
                                ),
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .border(0.5.dp, CyberGray.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                    .testTag("contacts_search_bar")
                            )

                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Section 1: Registered Network Nodes
                                item {
                                    Text(
                                        text = "NODOS ACOPLADOS P2P (${filteredPeers.size})",
                                        color = HologramCyan,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        letterSpacing = 1.sp,
                                        modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
                                    )
                                }

                                if (filteredPeers.isEmpty()) {
                                    item {
                                        Card(
                                            modifier = Modifier.fillMaxWidth().border(0.5.dp, CyberGray.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                                            colors = CardDefaults.cardColors(containerColor = TransObsidianCard)
                                        ) {
                                            Text(
                                                if (searchQuery.isEmpty()) "Sin nodos registrados o activos. Usa el botón '+' abajo para añadir uno con su número o acopla un contacto del dispositivo."
                                                else "No se encontraron nodos P2P para '$searchQuery'.",
                                                color = CyberGray,
                                                fontSize = 11.sp,
                                                fontFamily = FontFamily.Monospace,
                                                modifier = Modifier.padding(12.dp)
                                            )
                                        }
                                    }
                                } else {
                                    items(filteredPeers) { peer ->
                                        PeerDirectoryCard(
                                            peer = peer,
                                            onConnectChat = {
                                                viewModel.selectChat(peer.phoneNumber)
                                                onNavigateToChat()
                                            }
                                        )
                                    }
                                }

                                // Spacer divider
                                item {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(modifier = Modifier.weight(1f).height(0.5.dp).background(CyberGray.copy(alpha = 0.2f)))
                                        Text(
                                            " DISPOSITIVO ",
                                            color = CyberGray,
                                            fontSize = 10.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Box(modifier = Modifier.weight(1f).height(0.5.dp).background(CyberGray.copy(alpha = 0.2f)))
                                    }
                                }

                                // Section 2: Mobile Contacts
                                item {
                                    Text(
                                        text = "CONTACTOS DE TU MÓVIL",
                                        color = LaserPurple,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        letterSpacing = 1.sp,
                                        modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                                    )
                                }

                                if (hasContactsPermission) {
                                    if (filteredDeviceContacts.isEmpty()) {
                                        item {
                                            Text(
                                                if (searchQuery.isEmpty()) "Buscando contactos en tu agenda o libreta vacía..."
                                                else "No se encontraron contactos en tu móvil para '$searchQuery'.",
                                                color = CyberGray,
                                                fontSize = 11.sp,
                                                fontFamily = FontFamily.Monospace,
                                                modifier = Modifier.padding(vertical = 8.dp)
                                            )
                                        }
                                    } else {
                                        items(filteredDeviceContacts) { contact ->
                                            DeviceDirectoryCard(
                                                contact = contact,
                                                onConnectChat = {
                                                    viewModel.registerNewContact(contact.name, contact.phoneNumber) { success ->
                                                        if (success) {
                                                            viewModel.selectChat(contact.phoneNumber)
                                                            onNavigateToChat()
                                                        }
                                                    }
                                                }
                                            )
                                        }
                                    }
                                } else {
                                    item {
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .border(0.5.dp, LaserPurple.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                                            colors = CardDefaults.cardColors(containerColor = TransObsidianCard)
                                        ) {
                                            Column(modifier = Modifier.padding(14.dp)) {
                                                Text(
                                                    "VINCULAR LIBRETA DE DIRECCIONES",
                                                    color = OffWhite,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    fontFamily = FontFamily.Monospace
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    "Habilite el permiso para leer los contactos del móvil e importarlos automáticamente en su mapa táctico P2P descentralizado.",
                                                    color = CyberGray,
                                                    fontSize = 11.sp,
                                                    fontFamily = FontFamily.Monospace
                                                )
                                                Spacer(modifier = Modifier.height(12.dp))
                                                Button(
                                                    onClick = {
                                                        contactsPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = LaserPurple),
                                                    shape = RoundedCornerShape(6.dp)
                                                ) {
                                                    Text(
                                                        "DAR ACCESO A CONTACTOS",
                                                        color = Color.White,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        fontFamily = FontFamily.Monospace
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                }
            }
        }

        // AGREGAR PEER DIALOG (Add Node by phone or username!)
        if (isAddContactOpen) {
            var usernameText by remember { mutableStateOf("") }
            var phoneText by remember { mutableStateOf("") }
            var showErrorMsg by remember { mutableStateOf(false) }

            AlertDialog(
                onDismissRequest = { isAddContactOpen = false },
                modifier = Modifier
                    .border(1.dp, HologramCyan.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .testTag("add_contact_dialog"),
                containerColor = SpaceNavy,
                text = {
                    Column {
                        Text(
                            text = "ACOPLAR NUEVO PEER AL MAPA",
                            color = HologramCyan,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Username textfield
                        TextField(
                            value = usernameText,
                            onValueChange = {
                                usernameText = it
                                showErrorMsg = false
                            },
                            label = { Text("Nombre de usuario", color = CyberGray, fontFamily = FontFamily.Monospace) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = ObsidianBlack,
                                unfocusedContainerColor = ObsidianBlack,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedIndicatorColor = HologramCyan,
                                unfocusedIndicatorColor = CyberGray
                            ),
                            textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace, fontSize = 14.sp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .testTag("contact_username_input")
                        )

                        // Phone number textfield
                        TextField(
                            value = phoneText,
                            onValueChange = {
                                phoneText = it
                                showErrorMsg = false
                            },
                            label = { Text("Número de teléfono", color = CyberGray, fontFamily = FontFamily.Monospace) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = ObsidianBlack,
                                unfocusedContainerColor = ObsidianBlack,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedIndicatorColor = HologramCyan,
                                unfocusedIndicatorColor = CyberGray
                            ),
                            textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace, fontSize = 14.sp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("contact_phone_input")
                        )

                        if (showErrorMsg) {
                            Text(
                                text = "Llene todos los campos correctamente.",
                                color = SignalRed,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (usernameText.isNotBlank() && phoneText.isNotBlank()) {
                                viewModel.registerNewContact(usernameText, phoneText) { success ->
                                    if (success) {
                                        isAddContactOpen = false
                                    } else {
                                        showErrorMsg = true
                                    }
                                }
                            } else {
                                showErrorMsg = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LaserPurple),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("submit_contact_btn")
                    ) {
                        Text("VINCULAR PEER", color = Color.White, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { isAddContactOpen = false }) {
                        Text("CANCELAR", color = CyberGray, fontFamily = FontFamily.Monospace)
                    }
                }
            )
        }

        // --- WEB SYNC EMULATOR HUD DIALOG ---
        if (false) {
            var selectedTargetChatId by remember { mutableStateOf("") }
            var pcMessageText by remember { mutableStateOf("") }
            val simulationLogs = remember { mutableStateListOf<String>() } // To show terminal execution logs!
            val coroutineScope = rememberCoroutineScope()
            val database = AppDatabase.getDatabase(context)
            val clipboardManager = LocalClipboardManager.current

            AlertDialog(
                onDismissRequest = { },
                containerColor = SpaceNavy,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .border(1.5.dp, LaserPurple.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
                    .testTag("web_mode_bridge_dialog"),
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Laptop, contentDescription = null, tint = LaserPurple)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                "PUENTE DE ENLACE WEB DESKTOP",
                                color = OffWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        IconButton(onClick = { }) {
                            Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = CyberGray)
                        }
                    }
                },
                text = {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Text(
                                text = "MODO ESCRITORIO / ACCESO WEB DESDE PC",
                                color = LaserPurple,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Abra la aplicación en el navegador web de su computadora para ver todos sus contactos, canales activos y enviar/recibir mensajes en una pantalla más cómoda.",
                                color = CyberGray,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        // Web Server Network Data Card
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth().border(0.5.dp, HologramCyan.copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
                                colors = CardDefaults.cardColors(containerColor = ObsidianBlack)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        "OPCIÓN 1: ENLACE WEB DIRECTO (ACCESO NUBE)",
                                        color = NeonGreen,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(ObsidianBlack)
                                            .border(0.5.dp, NeonGreen.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                            .padding(10.dp)
                                    ) {
                                        Text(
                                            "https://ais-pre-mmnfmudsx7pengv4wuiu73-640609388288.us-east1.run.app",
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Button(
                                            onClick = {
                                                clipboardManager.setText(AnnotatedString("https://ais-pre-mmnfmudsx7pengv4wuiu73-640609388288.us-east1.run.app"))
                                                Toast.makeText(context, "¡Enlace Web de PC copiado!", Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = LaserPurple),
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(36.dp)
                                                .testTag("copy_cloud_link_btn"),
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.ContentCopy, contentDescription = "Copiar Enlace Nube", tint = Color.White, modifier = Modifier.size(13.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("COPIAR ENLACE NUBE", color = Color.White, fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(0.5.dp)
                                            .background(CyberGray.copy(alpha = 0.15f))
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))

                                    Text(
                                        "OPCIÓN 2: ENLACE LOCAL WI-FI (EMULADO)",
                                        color = HologramCyan,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(ObsidianBlack)
                                            .border(0.5.dp, HologramCyan.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                            .padding(10.dp)
                                    ) {
                                        Text(
                                            "http://${viewModel.myLocalIpAddress}:9092/client",
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Button(
                                            onClick = {
                                                clipboardManager.setText(AnnotatedString("http://${viewModel.myLocalIpAddress}:9092/client"))
                                                Toast.makeText(context, "¡Enlace Local Wi-Fi copiado!", Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = SpaceNavy),
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(36.dp)
                                                .border(0.5.dp, HologramCyan.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                                .testTag("copy_local_link_btn"),
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.ContentCopy, contentDescription = "Copiar Enlace Local", tint = HologramCyan, modifier = Modifier.size(13.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("COPIAR ENLACE LOCAL", color = HologramCyan, fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))
                                    // Simulated simple QR representation block
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        // Dynamic QR mock matrix
                                        Column(
                                            modifier = Modifier
                                                .background(Color.White)
                                                .padding(6.dp)
                                                .size(54.dp),
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            // Mock QR grid squares
                                            repeat(3) { row ->
                                                Row {
                                                    repeat(3) { col ->
                                                        Box(
                                                            modifier = Modifier
                                                                .size(12.dp)
                                                                .padding(1.dp)
                                                                .background(
                                                                    if ((row + col) % 2 == 0) Color.Black else Color.White
                                                                )
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                "ESCANEAR CÓDIGO TÁCTICO QR",
                                                color = OffWhite,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Monospace
                                            )
                                            Text(
                                                "Abre la versión web y sincroniza de forma remota.",
                                                color = CyberGray,
                                                fontSize = 9.sp,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // INTERACTIVE WEB NAVIGATOR EMULATOR SENSE
                        item {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "SIMULADOR DE COMPUTADORA DIRECTA (INTERFAZ WEB PC)",
                                color = NeonGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                "Prueba el canal bidireccional PC -> Móvil ejecutando comandos en esta terminal web emulada:",
                                color = CyberGray,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, LaserPurple.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                                colors = CardDefaults.cardColors(containerColor = SpaceNavy.copy(alpha = 0.6f))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    // PC Chrome Browser Tab Bar mimic
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(ObsidianBlack)
                                            .padding(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .background(SignalRed, CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .background(NeonGreen, CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Text(
                                            "🖥️ Navegador PC - Terminal Web",
                                            color = CyberGray,
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Target chat selector for the web client
                                    Text(
                                        "SELECCIONAR OBJETIVO DE RED DESDE PC:",
                                        color = OffWhite,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))

                                    // Let's build a nice pseudo dropdown or quick select list of channels and peers
                                    val targets = remember(allPeers, viewModel.allChannels.value) {
                                        val list = mutableListOf<Pair<String, String>>()
                                        viewModel.allChannels.value.forEach {
                                            list.add(it.channelId to "Canal: ${it.name}")
                                        }
                                        allPeers.forEach {
                                            list.add(it.phoneNumber to "Contacto: ${it.username}")
                                        }
                                        if (selectedTargetChatId.isEmpty() && list.isNotEmpty()) {
                                            selectedTargetChatId = list.first().first
                                        }
                                        list
                                    }

                                    if (targets.isNotEmpty()) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .border(0.5.dp, CyberGray.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                                .background(ObsidianBlack)
                                                .padding(8.dp)
                                                .clickable {
                                                    // Cycle through targets to mimic selection dropdown
                                                    val currentIndex = targets.indexOfFirst { it.first == selectedTargetChatId }
                                                    val nextIndex = (currentIndex + 1) % targets.size
                                                    selectedTargetChatId = targets[nextIndex].first
                                                },
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            val activeName = targets.find { it.first == selectedTargetChatId }?.second ?: "Seleccionar objetivo"
                                            Text(
                                                text = activeName,
                                                color = HologramCyan,
                                                fontSize = 11.sp,
                                                fontFamily = FontFamily.Monospace
                                            )
                                            Text(
                                                text = "[CAMBIAR]",
                                                color = LaserPurple,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Message input
                                    TextField(
                                        value = pcMessageText,
                                        onValueChange = { pcMessageText = it },
                                        placeholder = {
                                            Text(
                                                "Escribe un mensaje de computadora...",
                                                color = CyberGray,
                                                fontSize = 11.sp,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        },
                                        colors = TextFieldDefaults.colors(
                                            focusedContainerColor = ObsidianBlack,
                                            unfocusedContainerColor = ObsidianBlack,
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White,
                                            focusedIndicatorColor = HologramCyan,
                                            unfocusedIndicatorColor = CyberGray
                                        ),
                                        textStyle = androidx.compose.ui.text.TextStyle(
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 11.sp
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(56.dp)
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Button to transmit!
                                    Button(
                                        onClick = {
                                            if (pcMessageText.isNotBlank() && selectedTargetChatId.isNotBlank()) {
                                                val target = selectedTargetChatId
                                                val content = pcMessageText
                                                pcMessageText = ""
                                                coroutineScope.launch {
                                                    val targetName = targets.find { it.first == target }?.second ?: "Mesh Node"
                                                    database.chatDao().insertMessage(
                                                        com.example.data.local.MessageEntity(
                                                            chatId = target,
                                                            senderId = "pc_web_node",
                                                            senderName = "Computadora PC (Navegador)",
                                                            content = content
                                                        )
                                                    )
                                                    // Add nice logging to show transmission success!
                                                    simulationLogs.add("[PC TRANSMIT] Paquete de datos encapsulado.")
                                                    simulationLogs.add("[SYS LOG] Enviando bloque de 128-bits cifrado AES-256.")
                                                    simulationLogs.add("[LINK BRIDGE] Enlace exitoso con '$targetName'!")
                                                }
                                            }
                                        },
                                        enabled = pcMessageText.isNotBlank(),
                                        colors = ButtonDefaults.buttonColors(containerColor = LaserPurple),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            "ENVIAR MENSAJE DESDE PC (WEB)",
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }

                                    if (simulationLogs.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(ObsidianBlack)
                                                .padding(6.dp)
                                        ) {
                                            Column {
                                                simulationLogs.forEach { log ->
                                                    Text(
                                                        text = log,
                                                        color = if (log.contains("LOG") || log.contains("SYS")) NeonGreen else LaserPurple,
                                                        fontSize = 9.sp,
                                                        fontFamily = FontFamily.Monospace
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(containerColor = LaserPurple)
                    ) {
                        Text("CERRAR PUENTE", color = Color.White, fontFamily = FontFamily.Monospace)
                    }
                }
            )
        }

        // --- MI PERFIL EDIT DIALOG ---
        if (isProfileDialogOpen) {
            var aliasInput by remember { mutableStateOf(viewModel.myUsername) }
            var phoneInput by remember { mutableStateOf(viewModel.myPhoneNumber) }
            var notifsEnabled by remember { mutableStateOf(viewModel.notificationsEnabled) }
            var showProfileError by remember { mutableStateOf(false) }

            AlertDialog(
                onDismissRequest = { isProfileDialogOpen = false },
                containerColor = SpaceNavy,
                modifier = Modifier.border(1.dp, HologramCyan.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                title = {
                    Text(
                        "CONFIGURAR MI PERFIL DE RED",
                        color = HologramCyan,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "Modificar las credenciales de identificación local de este nodo para la transmisión descentralizada.",
                            color = CyberGray,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        TextField(
                            value = aliasInput,
                            onValueChange = { aliasInput = it; showProfileError = false },
                            label = { Text("Mi Alias (Nombre)", color = CyberGray, fontFamily = FontFamily.Monospace) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = ObsidianBlack,
                                unfocusedContainerColor = ObsidianBlack,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedIndicatorColor = HologramCyan,
                                unfocusedIndicatorColor = CyberGray
                            ),
                            textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace, fontSize = 13.sp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .testTag("my_alias_input")
                        )

                        TextField(
                            value = phoneInput,
                            onValueChange = { phoneInput = it; showProfileError = false },
                            label = { Text("Mi Número (Teléfono)", color = CyberGray, fontFamily = FontFamily.Monospace) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = ObsidianBlack,
                                unfocusedContainerColor = ObsidianBlack,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedIndicatorColor = HologramCyan,
                                unfocusedIndicatorColor = CyberGray
                            ),
                            textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace, fontSize = 13.sp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("my_phone_input")
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { 
                                    notifsEnabled = !notifsEnabled
                                }
                                .padding(vertical = 4.dp)
                                .testTag("toggle_notifications_row")
                        ) {
                            Checkbox(
                                checked = notifsEnabled,
                                onCheckedChange = { notifsEnabled = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = HologramCyan,
                                    uncheckedColor = CyberGray,
                                    checkmarkColor = ObsidianBlack
                                ),
                                modifier = Modifier.testTag("notifications_checkbox")
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Notificaciones de Enlace",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = if (notifsEnabled) "ALERTAS DE LLEGADA ACTIVAS" else "ALERTAS DE LLEGADA SILENCIADAS",
                                    color = if (notifsEnabled) HologramCyan else CyberGray,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        if (showProfileError) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "Ambos campos son obligatorios para conectar.",
                                color = SignalRed,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (aliasInput.isNotBlank() && phoneInput.isNotBlank()) {
                                viewModel.updateMyProfile(aliasInput.trim(), phoneInput.trim())
                                viewModel.toggleNotifications(notifsEnabled)
                                isProfileDialogOpen = false
                            } else {
                                showProfileError = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LaserPurple),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("save_profile_btn")
                    ) {
                        Text("GUARDAR", color = Color.White, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { isProfileDialogOpen = false }) {
                        Text("CANCELAR", color = CyberGray, fontFamily = FontFamily.Monospace)
                    }
                }
            )
        }
    }
}
}

// Sub components details and items

@Composable
fun EmptyHUDState(text: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.Grid3x3,
                contentDescription = "Empty",
                tint = CyberGray.copy(alpha = 0.5f),
                modifier = Modifier.size(54.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = text,
                color = CyberGray,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun PeerChatCard(
    peer: UserEntity,
    onConnect: () -> Unit,
    onInitiateCall: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(0.5.dp, CyberGray.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
            .clickable { onConnect() }
            .testTag("chat_card_${peer.phoneNumber}"),
        colors = CardDefaults.cardColors(containerColor = SpaceNavy.copy(alpha = 0.8f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Futuristic avatar thumbnail
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(TransObsidianCard, CircleShape)
                    .border(
                        1.5.dp,
                        if (peer.signalStrength > 0) HologramCyan else CyberGray,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Initial character or symbol
                Text(
                    text = peer.username.take(2).uppercase(),
                    color = if (peer.signalStrength > 0) HologramCyan else CyberGray,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )

                // Miniature online pip
                if (peer.signalStrength > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(10.dp)
                            .background(NeonGreen, CircleShape)
                            .border(1.5.dp, SpaceNavy, CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = peer.username,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "SECTOR: ${peer.phoneNumber}",
                    color = CyberGray,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Connection metrics or direct actions
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = onInitiateCall,
                    colors = IconButtonDefaults.iconButtonColors(contentColor = HologramCyan)
                ) {
                    Icon(Icons.Default.Videocam, contentDescription = "Conferencia")
                }
                IconButton(
                    onClick = onConnect,
                    colors = IconButtonDefaults.iconButtonColors(contentColor = LaserPurple)
                ) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Conectar")
                }
            }
        }
    }
}

@Composable
fun PeerDirectoryCard(
    peer: UserEntity,
    onConnectChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(0.5.dp, CyberGray.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = TransObsidianCard)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = peer.username,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "CANAL: ${peer.phoneNumber}",
                        color = CyberGray,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Box(
                    modifier = Modifier
                        .background(
                            (if (peer.isNearby) NeonGreen else CyberGray).copy(alpha = 0.1f),
                            RoundedCornerShape(4.dp)
                        )
                        .border(
                            0.5.dp,
                            if (peer.isNearby) NeonGreen else CyberGray,
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (peer.isNearby) "EN RANGO (MAP)" else "NIVEL DE ENLACE",
                        color = if (peer.isNearby) NeonGreen else CyberGray,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Wifi,
                        contentDescription = "Signal",
                        tint = if (peer.signalStrength > 0) HologramCyan else CyberGray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (peer.signalStrength > 0) "Potencia: ${peer.signalStrength} dBm" else "OFFLINE",
                        color = if (peer.signalStrength > 0) NeonGreen else CyberGray,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Button(
                    onClick = onConnectChat,
                    colors = ButtonDefaults.buttonColors(containerColor = HologramCyan),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "CONEXIÓN CHAT", 
                        color = ObsidianBlack,
                        fontSize = 11.sp, 
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

@Composable
fun DeviceDirectoryCard(
    contact: com.example.ui.viewmodel.DeviceContact,
    onConnectChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(0.5.dp, CyberGray.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = SpaceNavy.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rounded avatar placeholder with contact initials
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(TransObsidianCard, CircleShape)
                    .border(1.dp, LaserPurple, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = contact.name.take(2).uppercase(),
                    color = LaserPurple,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = contact.name,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = contact.phoneNumber,
                    color = CyberGray,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Button(
                onClick = onConnectChat,
                colors = ButtonDefaults.buttonColors(containerColor = LaserPurple),
                shape = RoundedCornerShape(6.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "ACOPLAR PEER",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
fun ChannelItemCard(
    channel: com.example.data.local.ChannelEntity,
    onConnect: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(0.5.dp, HologramCyan.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
            .clickable { onConnect() }
            .testTag("channel_card_${channel.channelId}"),
        colors = CardDefaults.cardColors(containerColor = TransObsidianCard)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(LaserPurple.copy(alpha = 0.15f), CircleShape)
                            .border(1.dp, LaserPurple, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "#",
                            color = LaserPurple,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = channel.name,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "NÚCLEOS REGISTRADOS: ${channel.nodeCount}",
                            color = CyberGray,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar Canal", tint = HologramCyan)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar Canal", tint = SignalRed)
                    }
                }
            }

            if (channel.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = channel.description,
                    color = OffWhite.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(CyberGray.copy(alpha = 0.15f))
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Link strength badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Grid3x3,
                        contentDescription = "Malla",
                        tint = NeonGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ENLACE P2P SEGURO",
                        color = NeonGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Button(
                    onClick = onConnect,
                    colors = ButtonDefaults.buttonColors(containerColor = LaserPurple),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "ENTRAR CANAL",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
