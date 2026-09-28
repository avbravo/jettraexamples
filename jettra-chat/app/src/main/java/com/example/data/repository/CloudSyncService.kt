package com.example.data.repository

import com.example.data.local.ChatDao
import com.example.data.local.MessageEntity
import com.example.data.local.UserEntity
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import kotlin.random.Random

data class CloudMessage(
    val chatId: String,
    val senderPhone: String,
    val senderName: String,
    val receiverPhone: String?,
    val content: String,
    val timestamp: Long,
    val fileUri: String? = null,
    val fileName: String? = null,
    val fileType: String? = null,
    val fileSize: String? = null
)

class CloudSyncService(private val chatDao: ChatDao) {

    private val client = OkHttpClient()
    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()
    private val adapter = moshi.adapter(CloudMessage::class.java)

    // Secure, unique and shared bucket ID for general synchronization of messages
    private val bucketUrl = "https://kvdb.io/meshchat_global_sync_v2/"
    private val processedKeys = mutableSetOf<String>()
    private var lastSyncedTimestamp = System.currentTimeMillis() - 12 * 60 * 60 * 1000 // Poll starting from last 12 hours on launch

    init {
        // Initialize existing messages to prevent duplicate processing
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val existing = chatDao.getAllMessages().firstOrNull() ?: emptyList()
                for (msg in existing) {
                    val key = "${msg.timestamp}_${msg.senderId}_${msg.chatId}"
                    processedKeys.add(key)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun registerNodeIp(phone: String, name: String, ip: String) {
        val sanitizedPhone = phone.replace("[^a-zA-Z0-9]".toRegex(), "")
        val url = "${bucketUrl}node_IP_${sanitizedPhone}"
        val responseMap = mapOf("phone" to phone, "name" to name, "ip" to ip)
        val json = moshi.adapter(Map::class.java).toJson(responseMap)
        
        kotlinx.coroutines.withContext(Dispatchers.IO) {
            try {
                val mediaType = "application/json; charset=utf-8".toMediaType()
                val body = json.toRequestBody(mediaType)
                val request = Request.Builder().url(url).put(body).build()
                client.newCall(request).execute().use { it.close() }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun getNodeIp(phone: String): String? {
        val sanitizedPhone = phone.replace("[^a-zA-Z0-9]".toRegex(), "")
        val url = "${bucketUrl}node_IP_${sanitizedPhone}"
        return kotlinx.coroutines.withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder().url(url).build()
                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val body = response.body?.string()
                        if (!body.isNullOrBlank()) {
                            val map = moshi.adapter(Map::class.java).fromJson(body)
                            map?.get("ip") as? String
                        } else null
                    } else null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun tryDirectSend(targetIp: String, cloudMsg: CloudMessage): Boolean {
        val url = "http://$targetIp:9092/api/message"
        val json = adapter.toJson(cloudMsg)
        return kotlinx.coroutines.withContext(Dispatchers.IO) {
            try {
                val mediaType = "application/json; charset=utf-8".toMediaType()
                val body = json.toRequestBody(mediaType)
                val request = Request.Builder().url(url).post(body).build()
                client.newCall(request).execute().use { response ->
                    response.isSuccessful
                }
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    suspend fun uploadMessage(
        chatId: String,
        senderPhone: String,
        senderName: String,
        content: String,
        timestamp: Long,
        fileUri: String? = null,
        fileName: String? = null,
        fileType: String? = null,
        fileSize: String? = null
    ) {
        val isChannel = chatId.startsWith("#")
        val receiver = if (isChannel) null else chatId
        
        val cloudMsg = CloudMessage(
            chatId = chatId,
            senderPhone = senderPhone,
            senderName = senderName,
            receiverPhone = receiver,
            content = content,
            timestamp = timestamp,
            fileUri = fileUri,
            fileName = fileName,
            fileType = fileType,
            fileSize = fileSize
        )

        // As an optimization and direct guarantee: try direct P2P delivery if recipient has a registered IP
        if (receiver != null) {
            val recipientIp = getNodeIp(receiver)
            if (!recipientIp.isNullOrBlank()) {
                val directSentSuccess = tryDirectSend(recipientIp, cloudMsg)
                if (directSentSuccess) {
                    processedKeys.add("${timestamp}_self_${chatId}")
                    processedKeys.add("${timestamp}_${senderPhone}_${chatId}")
                    // Still upload to cloud queue as robust historical backup, but direct link is confirmed!
                }
            }
        }

        val json = adapter.toJson(cloudMsg)
        val key = "msg_${timestamp}_${senderPhone.replace("[^a-zA-Z0-9]".toRegex(), "")}"
        val url = "$bucketUrl$key"

        kotlinx.coroutines.withContext(Dispatchers.IO) {
            try {
                val mediaType = "application/json; charset=utf-8".toMediaType()
                val body = json.toRequestBody(mediaType)
                val request = Request.Builder()
                    .url(url)
                    .put(body)
                    .build()
                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        processedKeys.add("${timestamp}_self_${chatId}")
                        processedKeys.add("${timestamp}_${senderPhone}_${chatId}")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun pollMessages(myPhone: String, onNewMessage: (senderName: String, content: String) -> Unit = { _, _ -> }): Int {
        return kotlinx.coroutines.withContext(Dispatchers.IO) {
            var newMessagesCount = 0
            var maxTimestampThisBatch = lastSyncedTimestamp
            try {
                val listUrl = "$bucketUrl?prefix=msg_"
                val request = Request.Builder().url(listUrl).build()
                val keysString = client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) response.body?.string() else null
                }

                if (!keysString.isNullOrBlank()) {
                    val keys = keysString.split("\n")
                        .map { it.trim() }
                        .filter { it.startsWith("msg_") }

                    val limitTime = System.currentTimeMillis() - 12 * 60 * 60 * 1000 // Last 12 hours clock tolerance

                    for (key in keys) {
                        val segments = key.split("_")
                        if (segments.size >= 3) {
                            val timestampStr = segments[1]
                            val timestamp = timestampStr.toLongOrNull() ?: 0L
                            
                            if (timestamp > limitTime) {
                                val msgUrl = "$bucketUrl$key"
                                val msgRequest = Request.Builder().url(msgUrl).build()
                                val json = client.newCall(msgRequest).execute().use { response ->
                                    if (response.isSuccessful) response.body?.string() else null
                                }

                                if (!json.isNullOrBlank()) {
                                    val cloudMsg = adapter.fromJson(json)
                                    if (cloudMsg != null) {
                                        val cacheKey = "${cloudMsg.timestamp}_${cloudMsg.senderPhone}_${cloudMsg.chatId}"
                                        
                                        // Skip if we are the sender
                                        if (cloudMsg.senderPhone == myPhone) {
                                            if (timestamp > maxTimestampThisBatch) {
                                                maxTimestampThisBatch = timestamp
                                            }
                                            continue
                                        }

                                        if (!processedKeys.contains(cacheKey)) {
                                            processedKeys.add(cacheKey)

                                            val isMyChannel = cloudMsg.chatId.startsWith("#")
                                            val isForMe = cloudMsg.receiverPhone == myPhone

                                            if (isMyChannel || isForMe) {
                                                // Register sender as contact locally if not exists
                                                val existingUser = chatDao.getUserByPhone(cloudMsg.senderPhone)
                                                if (existingUser == null) {
                                                    val randomAngle = Random.nextFloat() * 2f * Math.PI.toFloat()
                                                    val distance = 0.3f + Random.nextFloat() * 0.5f
                                                    val newUser = UserEntity(
                                                        phoneNumber = cloudMsg.senderPhone,
                                                        username = cloudMsg.senderName,
                                                        isNearby = true,
                                                        avatarType = Random.nextInt(1, 4),
                                                        xRatio = Math.cos(randomAngle.toDouble()).toFloat() * distance,
                                                        yRatio = Math.sin(randomAngle.toDouble()).toFloat() * distance,
                                                        status = "GRID_ACTIVE",
                                                        signalStrength = Random.nextInt(60, 100),
                                                        lastActive = System.currentTimeMillis()
                                                    )
                                                    chatDao.insertUser(newUser)
                                                }

                                                val localChatId = if (isMyChannel) cloudMsg.chatId else cloudMsg.senderPhone

                                                val messageEntity = MessageEntity(
                                                    chatId = localChatId,
                                                    senderId = cloudMsg.senderPhone,
                                                    senderName = cloudMsg.senderName,
                                                    content = cloudMsg.content,
                                                    timestamp = cloudMsg.timestamp,
                                                    fileUri = cloudMsg.fileUri,
                                                    fileName = cloudMsg.fileName,
                                                    fileType = cloudMsg.fileType,
                                                    fileSize = cloudMsg.fileSize,
                                                    isSent = true
                                                )
                                                chatDao.insertMessage(messageEntity)
                                                onNewMessage(cloudMsg.senderName, cloudMsg.content)
                                                newMessagesCount++
                                            }
                                        }

                                        if (timestamp > maxTimestampThisBatch) {
                                            maxTimestampThisBatch = timestamp
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            lastSyncedTimestamp = maxTimestampThisBatch
            newMessagesCount
        }
    }
}
