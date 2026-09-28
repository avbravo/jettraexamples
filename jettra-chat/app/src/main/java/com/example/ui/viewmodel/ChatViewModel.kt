package com.example.ui.viewmodel

import android.app.Application
import android.media.MediaPlayer
import android.provider.ContactsContract
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.MessageEntity
import com.example.data.local.UserEntity
import com.example.data.local.ChannelEntity
import com.example.data.repository.ChatRepository
import com.example.util.LightweightWebServer
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.random.Random

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ChatRepository
    
    // UI states
    val allUsers: StateFlow<List<UserEntity>>
    val nearbyUsers: StateFlow<List<UserEntity>>
    val allChannels: StateFlow<List<ChannelEntity>>

    // User profile states (synced to SharedPreferences)
    var myPhoneNumber by mutableStateOf("")
        private set
    var myUsername by mutableStateOf("")
        private set
    var notificationsEnabled by mutableStateOf(true)
        private set
    var myLocalIpAddress by mutableStateOf("127.0.0.1")
        private set

    // Device contacts state
    var deviceContacts by mutableStateOf<List<DeviceContact>>(emptyList())
        private set

    // Active state
    var selectedUserPhone by mutableStateOf<String?>(null)
        private set

    // Radar scanning animation state
    var isSearchingNearby by mutableStateOf(true)
        private set
    var radarAngle by mutableStateOf(0f)
        private set

    // Videoconference active call states
    var isCallActive by mutableStateOf(false)
        private set
    var isCallMuted by mutableStateOf(false)
        private set
    var isCallCameraOn by mutableStateOf(true)
        private set
    var currentCallUser by mutableStateOf<UserEntity?>(null)
        private set
    var callDurationSeconds by mutableStateOf(0)
        private set

    // Audio file playback states
    var activePlayingMsgId by mutableStateOf<Long?>(null)
        private set
    var isAudioPlaying by mutableStateOf(false)
        private set
    var audioProgress by mutableStateOf(0f)
        private set
    var audioDurationString by mutableStateOf("0:00")
        private set

    // Video playback active overlay state
    var activeVideoMsg by mutableStateOf<MessageEntity?>(null)
        private set

    // Available themed files to simulate attachments easily in the emulator
    val customThemedFiles = listOf(
        ThemedFile("Aether_Transmitter_Code.mp3", "AUDIO", "2.1 MB", "Frecuencias de baliza cuántica"),
        ThemedFile("Cyber_Grid_Harmonics.mp3", "AUDIO", "1.4 MB", "Armónicos de banda Lm de baja latencia"),
        ThemedFile("Orbital_Drone_Scan.mp4", "VIDEO", "6.2 MB", "Muestra de transmisión infrarroja orbital"),
        ThemedFile("Mesh_Network_Flux.mp4", "VIDEO", "4.8 MB", "Visualizador de topología de nodos P2P"),
        ThemedFile("Subspace_Payload.zip", "FILE", "115 KB", "Algoritmo criptográfico cuántico")
    )

    private var radarJob: Job? = null
    private var callTimerJob: Job? = null
    private var audioProgressJob: Job? = null
    private var syncJob: Job? = null
    private var webServer: LightweightWebServer? = null

    init {
        val database = AppDatabase.getDatabase(application)
        repository = ChatRepository(database.chatDao(), application)
        allUsers = repository.allUsers.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        nearbyUsers = repository.nearbyUsers.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        allChannels = repository.allChannels.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        // Initialize user profile settings
        val prefs = application.getSharedPreferences("mesh_chat_prefs", android.content.Context.MODE_PRIVATE)
        val defaultPhone = "+1 555-" + String.format("%04d", Random.nextInt(1000, 9999))
        val defaultUser = "Nodo_" + String.format("%03d", Random.nextInt(100, 999))
        myPhoneNumber = prefs.getString("my_phone", defaultPhone) ?: defaultPhone
        myUsername = prefs.getString("my_username", defaultUser) ?: defaultUser
        notificationsEnabled = prefs.getBoolean("notifications_enabled", true)
        if (!prefs.contains("my_phone")) {
            prefs.edit().putString("my_phone", myPhoneNumber).putString("my_username", myUsername).apply()
        }

        // Initialize and start lightweight P2P mesh server
        webServer = LightweightWebServer(application, database.chatDao()) {
            LightweightWebServer.NodeInfo(
                myPhone = myPhoneNumber,
                myName = myUsername,
                selectedChatPhone = selectedUserPhone
            )
        }
        webServer?.start()

        startRadarSweep()
        startCloudSyncPolling()
    }

    private fun startCloudSyncPolling() {
        syncJob?.cancel()
        syncJob = viewModelScope.launch {
            // Periodically register/upload our IP address to other nodes
            launch {
                while (true) {
                    try {
                        val ip = webServer?.getLocalIpAddress()
                        if (!ip.isNullOrBlank()) {
                            myLocalIpAddress = ip
                            if (ip != "127.0.0.1") {
                                repository.cloudSyncService.registerNodeIp(myPhoneNumber, myUsername, ip)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    delay(15000) // Register node IP over Wi-Fi/Mobile Data every 15s
                }
            }

            while (true) {
                try {
                    repository.cloudSyncService.pollMessages(myPhoneNumber) { senderName, content ->
                        if (notificationsEnabled) {
                            com.example.util.NotificationHelper.showNotification(getApplication(), senderName, content)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                delay(3000) // Fast real-time cloud synchronization polling
            }
        }
    }

    fun updateMyProfile(name: String, phone: String) {
        if (name.isNotBlank() && phone.isNotBlank()) {
            myUsername = name
            myPhoneNumber = phone
            val prefs = getApplication<Application>().getSharedPreferences("mesh_chat_prefs", android.content.Context.MODE_PRIVATE)
            prefs.edit().putString("my_phone", phone).putString("my_username", name).apply()
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        notificationsEnabled = enabled
        val prefs = getApplication<Application>().getSharedPreferences("mesh_chat_prefs", android.content.Context.MODE_PRIVATE)
        prefs.edit().putBoolean("notifications_enabled", enabled).apply()
    }

    // Active Chat Selection
    fun selectChat(phone: String?) {
        selectedUserPhone = phone
        // Reset player when switching chats
        stopAudioPlayback()
    }

    // Fetch messages for active selected user
    fun getActiveChatMessages(): Flow<List<MessageEntity>> {
        val phone = selectedUserPhone ?: return flowOf(emptyList())
        return repository.getMessagesForChat(phone)
    }

    // Send text message
    fun sendTextMessage(content: String) {
        val phone = selectedUserPhone ?: return
        if (content.isBlank()) return
        viewModelScope.launch {
            repository.sendMessage(phone, content, myPhoneNumber, myUsername)
        }
    }

    // Send file attachment
    fun sendAttachment(file: ThemedFile) {
        val phone = selectedUserPhone ?: return
        viewModelScope.launch {
            repository.sendMessage(
                chatId = phone,
                content = "Archivo enviado: ${file.name} - ${file.description}",
                myPhone = myPhoneNumber,
                myName = myUsername,
                fileUri = file.name,
                fileName = file.name,
                fileType = file.type,
                fileSize = file.size
            )
        }
    }

    // Send custom local file
    fun sendLocalCustomFile(name: String, type: String, size: String) {
        val phone = selectedUserPhone ?: return
        viewModelScope.launch {
            repository.sendMessage(
                chatId = phone,
                content = "Compartiendo archivo local: $name",
                myPhone = myPhoneNumber,
                myName = myUsername,
                fileUri = "local://$name",
                fileName = name,
                fileType = type,
                fileSize = size
            )
        }
    }

    // Message controls (Retract / Delete)
    fun retractMessage(msgId: Long) {
        viewModelScope.launch {
            repository.retractMessage(msgId)
        }
    }

    fun deleteMessage(msgId: Long) {
        viewModelScope.launch {
            repository.deleteMessage(msgId)
        }
    }

    fun clearActiveChatHistory() {
        val phone = selectedUserPhone ?: return
        viewModelScope.launch {
            repository.clearHistory(phone)
        }
    }

    // Add contact
    fun registerNewContact(username: String, phone: String, onCompleted: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.addContact(username, phone)
            onCompleted(result)
        }
    }

    // Channels Actions
    fun createChannel(name: String, description: String) {
        viewModelScope.launch {
            repository.createChannel(name, description)
        }
    }

    fun updateChannel(channel: ChannelEntity) {
        viewModelScope.launch {
            repository.updateChannel(channel)
        }
    }

    fun deleteChannel(channel: ChannelEntity) {
        viewModelScope.launch {
            repository.deleteChannel(channel)
        }
    }

    // Radar Scanning Logic
    private fun startRadarSweep() {
        radarJob?.cancel()
        radarJob = viewModelScope.launch {
            while (isSearchingNearby) {
                radarAngle = (radarAngle + 2.5f) % 360f
                delay(16) // ~60fps
            }
        }
    }

    fun toggleRadarSearch() {
        isSearchingNearby = !isSearchingNearby
        if (isSearchingNearby) {
            startRadarSweep()
        } else {
            radarJob?.cancel()
        }
    }

    // Video call triggers
    fun initiateVideoconference(peerPhone: String) {
        viewModelScope.launch {
            val userList = allUsers.value
            val user = userList.find { it.phoneNumber == peerPhone }
            if (user != null) {
                currentCallUser = user
                isCallActive = true
                isCallMuted = false
                isCallCameraOn = true
                callDurationSeconds = 0
                startCallTimer()
            }
        }
    }

    fun terminateVideoconference() {
        callTimerJob?.cancel()
        isCallActive = false
        currentCallUser = null
        callDurationSeconds = 0
    }

    fun toggleCallMute() {
        isCallMuted = !isCallMuted
    }

    fun toggleCallCamera() {
        isCallCameraOn = !isCallCameraOn
    }

    private fun startCallTimer() {
        callTimerJob?.cancel()
        callTimerJob = viewModelScope.launch {
            while (isCallActive) {
                delay(1000)
                callDurationSeconds++
            }
        }
    }

    // Audio Playback Player controller (Simulates telemetry and sound waves in real-time)
    fun toggleAudioPlayback(msgId: Long, fileName: String?) {
        if (activePlayingMsgId == msgId) {
            // Already active playing, toggle
            isAudioPlaying = !isAudioPlaying
            if (!isAudioPlaying) {
                audioProgressJob?.cancel()
            } else {
                startAudioProgressSimulation()
            }
        } else {
            // Start play new file
            stopAudioPlayback()
            activePlayingMsgId = msgId
            isAudioPlaying = true
            audioProgress = 0f
            audioDurationString = "0:${Random.nextInt(15, 59)}" // Simulate dynamic file length nicely
            startAudioProgressSimulation()
        }
    }

    private fun startAudioProgressSimulation() {
        audioProgressJob?.cancel()
        audioProgressJob = viewModelScope.launch {
            while (isAudioPlaying && audioProgress < 1.0f) {
                delay(300)
                audioProgress += 0.02f
            }
            if (audioProgress >= 1.0f) {
                stopAudioPlayback()
            }
        }
    }

    fun stopAudioPlayback() {
        audioProgressJob?.cancel()
        activePlayingMsgId = null
        isAudioPlaying = false
        audioProgress = 0f
    }

    // Video display
    fun displayVideo(message: MessageEntity?) {
        activeVideoMsg = message
    }

    // Load actual device contacts from ContactsContract Provider
    fun loadDeviceContacts() {
        val context = getApplication<Application>()
        val list = mutableListOf<DeviceContact>()
        try {
            val cursor = context.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                arrayOf(
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER
                ),
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
            )
            cursor?.use {
                val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                while (it.moveToNext()) {
                    val name = if (nameIndex >= 0) it.getString(nameIndex) else "Contacto"
                    val number = if (numberIndex >= 0) it.getString(numberIndex) else ""
                    if (number.isNotBlank()) {
                        list.add(DeviceContact(name ?: "Contacto", number))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        deviceContacts = list.distinctBy { it.phoneNumber }
    }

    override fun onCleared() {
        super.onCleared()
        radarJob?.cancel()
        callTimerJob?.cancel()
        audioProgressJob?.cancel()
        syncJob?.cancel()
        webServer?.stop()
    }
}

data class ThemedFile(
    val name: String,
    val type: String, // AUDIO, VIDEO, IMAGE, FILE
    val size: String,
    val description: String
)

data class DeviceContact(
    val name: String,
    val phoneNumber: String
)
