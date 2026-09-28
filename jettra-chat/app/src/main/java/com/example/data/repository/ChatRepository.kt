package com.example.data.repository

import android.content.Context
import com.example.data.local.ChatDao
import com.example.data.local.UserEntity
import com.example.data.local.MessageEntity
import com.example.data.local.ChannelEntity
import com.example.util.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.random.Random

class ChatRepository(private val chatDao: ChatDao, private val context: Context) {

    val allUsers: Flow<List<UserEntity>> = chatDao.getAllUsers()
    val nearbyUsers: Flow<List<UserEntity>> = chatDao.getNearbyUsers()
    val allChannels: Flow<List<ChannelEntity>> = chatDao.getAllChannels()
    val cloudSyncService = CloudSyncService(chatDao)

    // Add channel helper
    suspend fun createChannel(name: String, description: String, channelId: String = "#" + name.lowercase().trim().replace("\\s+".toRegex(), "_")) {
        val channel = ChannelEntity(
            channelId = channelId,
            name = name,
            description = description,
            nodeCount = Random.nextInt(3, 15),
            lastActive = System.currentTimeMillis()
        )
        chatDao.insertChannel(channel)
        
        // Broadcast custom genesis message internally for the P2P Mesh Group
        chatDao.insertMessage(
            MessageEntity(
                chatId = channelId,
                senderId = "SISTEMA",
                senderName = "SISTEMA DE ACOPLAMIENTO P2P",
                content = "CONEXIÓN DE CANAL DE GRUPO ESTABLECIDA // Canal: '$name' - Enlace seguro activo vía malla local descentralizada."
            )
        )
    }

    suspend fun updateChannel(channel: ChannelEntity) {
        chatDao.insertChannel(channel)
    }

    suspend fun deleteChannel(channel: ChannelEntity) {
        chatDao.deleteChannel(channel)
    }

    // Initialize: eliminate demonstration peer nodes as requested
    init {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val demoNumbers = listOf(
                    "+55 0101-2099",
                    "+1 888-000-0110",
                    "+44 777-526-7248",
                    "+81 90-8888-9999",
                    "+1 234-567-8900"
                )
                for (num in demoNumbers) {
                    val existingUser = chatDao.getUserByPhone(num)
                    if (existingUser != null) {
                        chatDao.deleteUser(existingUser)
                        chatDao.clearChatHistory(num)
                    }
                }

                // Prepopulate futuristic decentralized channels
                val existingChannels = chatDao.getAllChannels().first()
                if (existingChannels.isEmpty()) {
                    createChannel("Malla General Transmisor", "Canal de broadcast principal para todos los nodos acoplados de la red.", "#general")
                    createChannel("Soporte de Banda Lm", "Coordinación de espectro táctico y diagnósticos de latencia cuántica.", "#soporte_banda")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Messages Fetching
    fun getMessagesForChat(chatId: String): Flow<List<MessageEntity>> = chatDao.getMessagesForChat(chatId)

    // Send Message
    suspend fun sendMessage(
        chatId: String,
        content: String,
        myPhone: String,
        myName: String,
        fileUri: String? = null,
        fileName: String? = null,
        fileType: String? = null,
        fileSize: String? = null
    ) {
        val isChannel = chatId.startsWith("#")
        val user = if (isChannel) null else chatDao.getUserByPhone(chatId)
        val peerName = if (isChannel) {
            val channel = chatDao.getChannelById(chatId)
            channel?.name ?: "Canal Malla"
        } else {
            user?.username ?: "Peer Node"
        }

        val timestamp = System.currentTimeMillis()
        val myMsg = MessageEntity(
            chatId = chatId,
            senderId = "self",
            senderName = myName,
            content = content,
            timestamp = timestamp,
            fileUri = fileUri,
            fileName = fileName,
            fileType = fileType,
            fileSize = fileSize
        )
        chatDao.insertMessage(myMsg)

        // Asynchronously upload message to cloud key-value sync pool
        CoroutineScope(Dispatchers.IO).launch {
            cloudSyncService.uploadMessage(
                chatId = chatId,
                senderPhone = myPhone,
                senderName = myName,
                content = content,
                timestamp = timestamp,
                fileUri = fileUri,
                fileName = fileName,
                fileType = fileType,
                fileSize = fileSize
            )
        }

        // Simulate Peer Network Response if not channel and we want simulated grid activity
        CoroutineScope(Dispatchers.IO).launch {
            delay(1500) // mesh network propagation delay
            val replies = listOf(
                "Recibido por acoplamiento directo de radioondas. Reducción de latencia a ${Random.nextInt(5, 25)}ms.",
                "Señal de malla confirmada. Paquete integrado en el libro mayor local.",
                "Transmisión de archivos completada con éxito. Verificando suma de comprobación MD5...",
                "¡Excelente! Protocolo de telemetría funcionando. Iniciemos enlace de videoconferencia para probar ancho de banda.",
                "Enlace seguro. Nodo de tránsito local guardó de forma descentralizada el bloque de datos.",
                "Envié una respuesta de telemetría sonora. Escucha esta frecuencia cuántica.",
                "Entendido. Borra el mensaje anterior si deseas desconectar el nodo intermedio de almacenamiento."
            )
            val randomReply = replies[Random.nextInt(replies.size)]
            
            // Randomly select sender details for group chat simulation
            val responseSenderId = if (isChannel) "relay_node_${Random.nextInt(1, 6)}" else chatId
            val responseSenderName = if (isChannel) {
                val participantNames = listOf("Astra Core [Malla]", "Vektor X [Malla]", "Echo Nova [Alpha]", "Kira.Net [Malla]", "Relay Delta-6")
                participantNames[Random.nextInt(participantNames.size)]
            } else {
                peerName
            }

            // If user sent a message, create peer message
            val peerMsg = if (fileType == "AUDIO") {
                MessageEntity(
                    chatId = chatId,
                    senderId = responseSenderId,
                    senderName = responseSenderName,
                    content = "Señal de audio regenerada por el sintetizador de tu terminal local.",
                    fileUri = "ambient_space_sig.mp3",
                    fileName = "Aether_Telemetry_Signal.mp3",
                    fileType = "AUDIO",
                    fileSize = "1.4 MB"
                )
            } else if (fileType == "VIDEO") {
                MessageEntity(
                    chatId = chatId,
                    senderId = responseSenderId,
                    senderName = responseSenderName,
                    content = "Recibí la transmisión de video. Aquí está la respuesta de escaneo espacial.",
                    fileUri = "hologram_visual_feed.mp4",
                    fileName = "Scan_Telemetry_Drones.mp4",
                    fileType = "VIDEO",
                    fileSize = "4.2 MB"
                )
            } else {
                MessageEntity(
                    chatId = chatId,
                    senderId = responseSenderId,
                    senderName = responseSenderName,
                    content = randomReply
                )
            }
            chatDao.insertMessage(peerMsg)
            NotificationHelper.showNotification(context, peerMsg.senderName, peerMsg.content)
        }
    }

    suspend fun retractMessage(messageId: Long) {
        chatDao.retractMessage(messageId)
    }

    suspend fun deleteMessage(messageId: Long) {
        chatDao.deleteMessageById(messageId)
    }

    suspend fun addContact(username: String, phoneNumber: String): Boolean {
        if (phoneNumber.isBlank() || username.isBlank()) return false
        val user = UserEntity(
            phoneNumber = phoneNumber,
            username = username,
            isNearby = Random.nextBoolean(), // Randomly place nearby!
            avatarType = Random.nextInt(1, 4),
            xRatio = Random.nextFloat() * 1.8f - 0.9f, // spread between -0.9 and 0.9
            yRatio = Random.nextFloat() * 1.8f - 0.9f,
            status = "PEER_REGISTERED",
            signalStrength = Random.nextInt(50, 100),
            lastActive = System.currentTimeMillis()
        )
        chatDao.insertUser(user)
        return true
    }

    suspend fun clearHistory(chatId: String) {
        chatDao.clearChatHistory(chatId)
    }
}
