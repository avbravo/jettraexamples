package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val phoneNumber: String, // Phone number or username acting as connection key
    val username: String,
    val isNearby: Boolean = false,
    val avatarType: Int = 0, // Choice of graphic matrix avatar
    val xRatio: Float = 0f,  // Radar mapping offset X (-1f to 1f)
    val yRatio: Float = 0f,  // Radar mapping offset Y (-1f to 1f)
    val status: String = "GRID_ACTIVE",
    val signalStrength: Int = 100, // Simulated network dBm
    val lastActive: Long = System.currentTimeMillis()
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val chatId: String,      // Thread identifier (peer's phone number or channelId)
    val senderId: String,    // Owner of the message (self or peer phone number)
    val senderName: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val fileUri: String? = null,   // Location or mock stream of file
    val fileName: String? = null,
    val fileType: String? = null,  // "IMAGE", "AUDIO", "VIDEO", "FILE"
    val fileSize: String? = null,  // Human-readable size e.g., "3.5 MB"
    val isDeleted: Boolean = false, // Message retraction support
    val isSent: Boolean = true      // Status indicator
)

@Entity(tableName = "channels")
data class ChannelEntity(
    @PrimaryKey val channelId: String, // String ID (e.g. "#general", "#radar", etc.)
    val name: String,
    val description: String = "",
    val nodeCount: Int = 1,
    val lastActive: Long = System.currentTimeMillis()
)
