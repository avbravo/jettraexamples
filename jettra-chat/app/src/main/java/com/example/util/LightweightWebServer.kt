package com.example.util

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.example.data.local.ChatDao
import com.example.data.local.MessageEntity
import com.example.data.local.UserEntity
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.net.URLDecoder
import java.util.Enumeration
import kotlin.random.Random

class LightweightWebServer(
    private val context: Context,
    private val chatDao: ChatDao,
    private val infoProvider: () -> NodeInfo
) {
    data class NodeInfo(
        val myPhone: String,
        val myName: String,
        val selectedChatPhone: String?
    )

    private var server: HttpServer? = null
    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val messageAdapter = moshi.adapter(MessageEntity::class.java)

    fun start(port: Int = 9092) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                server = HttpServer.create(InetSocketAddress(port), 0)
                server?.createContext("/client", ClientHtmlHandler())
                server?.createContext("/api/status", ApiStatusHandler())
                server?.createContext("/api/messages", ApiMessagesHandler())
                server?.createContext("/api/message", ApiMessageHandler())
                server?.createContext("/api/media", ApiMediaHandler())
                server?.executor = java.util.concurrent.Executors.newCachedThreadPool()
                server?.start()
                
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(context, "Servidor de malla ligero iniciado en puerto $port", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(context, "Error al iniciar servidor ligero: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun stop() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                server?.stop(0)
                server = null
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getLocalIpAddress(): String {
        try {
            val interfaces: Enumeration<NetworkInterface> = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val networkInterface = interfaces.nextElement()
                val addresses = networkInterface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val address = addresses.nextElement()
                    if (!address.isLoopbackAddress && address is java.net.Inet4Address) {
                        return address.hostAddress ?: "127.0.0.1"
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "127.0.0.1"
    }

    private inner class ClientHtmlHandler : HttpHandler {
        override fun handle(exchange: HttpExchange) {
            if ("GET" != exchange.requestMethod) {
                exchange.sendResponseHeaders(405, -1)
                return
            }
            try {
                val html = getHtmlPage()
                val bytes = html.toByteArray(Charsets.UTF_8)
                exchange.responseHeaders.set("Content-Type", "text/html; charset=utf-8")
                exchange.sendResponseHeaders(200, bytes.size.toLong())
                val os = exchange.responseBody
                os.write(bytes)
                os.close()
            } catch (e: Exception) {
                e.printStackTrace()
                exchange.sendResponseHeaders(500, -1)
            }
        }
    }

    private inner class ApiStatusHandler : HttpHandler {
        override fun handle(exchange: HttpExchange) {
            try {
                val info = infoProvider()
                val responseMap = mapOf(
                    "username" to info.myName,
                    "phoneNumber" to info.myPhone,
                    "selectedChatPhone" to info.selectedChatPhone,
                    "localIp" to getLocalIpAddress()
                )
                val json = moshi.adapter(Map::class.java).toJson(responseMap)
                val bytes = json.toByteArray(Charsets.UTF_8)
                exchange.responseHeaders.set("Content-Type", "application/json")
                exchange.sendResponseHeaders(200, bytes.size.toLong())
                val os = exchange.responseBody
                os.write(bytes)
                os.close()
            } catch (e: Exception) {
                e.printStackTrace()
                exchange.sendResponseHeaders(500, -1)
            }
        }
    }

    private inner class ApiMessagesHandler : HttpHandler {
        override fun handle(exchange: HttpExchange) {
            try {
                val info = infoProvider()
                val selectedChat = info.selectedChatPhone
                
                CoroutineScope(Dispatchers.IO).launch {
                    val messages = if (!selectedChat.isNullOrBlank()) {
                        chatDao.getMessagesForChat(selectedChat).firstOrNull() ?: emptyList()
                    } else {
                        chatDao.getAllMessages().firstOrNull() ?: emptyList()
                    }
                    
                    val list = messages.map { msg ->
                        mapOf(
                            "id" to msg.id,
                            "chatId" to msg.chatId,
                            "senderId" to msg.senderId,
                            "senderName" to msg.senderName,
                            "content" to msg.content,
                            "timestamp" to msg.timestamp,
                            "fileUri" to msg.fileUri,
                            "fileName" to msg.fileName,
                            "fileType" to msg.fileType,
                            "fileSize" to msg.fileSize
                        )
                    }
                    val json = moshi.adapter(List::class.java).toJson(list)
                    val bytes = json.toByteArray(Charsets.UTF_8)
                    exchange.responseHeaders.set("Content-Type", "application/json")
                    exchange.sendResponseHeaders(200, bytes.size.toLong())
                    val os = exchange.responseBody
                    os.write(bytes)
                    os.close()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                exchange.sendResponseHeaders(500, -1)
            }
        }
    }

    private inner class ApiMessageHandler : HttpHandler {
        override fun handle(exchange: HttpExchange) {
            if ("POST" != exchange.requestMethod) {
                exchange.sendResponseHeaders(405, -1)
                return
            }
            try {
                val body = exchange.requestBody.bufferedReader().use { it.readText() }
                val adapter = moshi.adapter(Map::class.java)
                val map = adapter.fromJson(body)
                
                if (map != null) {
                    val content = map["content"] as? String ?: ""
                    val senderPhone = map["senderPhone"] as? String ?: ""
                    val senderName = map["senderName"] as? String ?: ""
                    val chatId = map["chatId"] as? String ?: ""
                    val fileUri = map["fileUri"] as? String
                    val fileName = map["fileName"] as? String
                    val fileType = map["fileType"] as? String
                    val fileSize = map["fileSize"] as? String
                    val timestamp = (map["timestamp"] as? Double)?.toLong() ?: System.currentTimeMillis()

                    if (senderPhone.isNotBlank() && content.isNotBlank()) {
                        CoroutineScope(Dispatchers.IO).launch {
                            // Save sender to DB if not exists
                            var existing = chatDao.getUserByPhone(senderPhone)
                            if (existing == null) {
                                val randomAngle = Random.nextFloat() * 2f * Math.PI.toFloat()
                                val distance = 0.3f + Random.nextFloat() * 0.5f
                                existing = UserEntity(
                                    phoneNumber = senderPhone,
                                    username = senderName,
                                    isNearby = true,
                                    avatarType = Random.nextInt(1, 4),
                                    xRatio = Math.cos(randomAngle.toDouble()).toFloat() * distance,
                                    yRatio = Math.sin(randomAngle.toDouble()).toFloat() * distance,
                                    status = "GRID_ACTIVE",
                                    signalStrength = 100,
                                    lastActive = System.currentTimeMillis()
                                )
                                chatDao.insertUser(existing)
                            }

                            // Build local message entry
                            // If chatId is self or empty, map it to the sender's thread
                            val info = infoProvider()
                            val localChatId = if (chatId.isBlank() || chatId == info.myPhone) senderPhone else chatId

                            val messageEntity = MessageEntity(
                                chatId = localChatId,
                                senderId = senderPhone,
                                senderName = senderName,
                                content = content,
                                timestamp = timestamp,
                                fileUri = fileUri,
                                fileName = fileName,
                                fileType = fileType,
                                fileSize = fileSize,
                                isSent = true
                            )
                            chatDao.insertMessage(messageEntity)

                            // Show standard system notification
                            NotificationHelper.showNotification(context, senderName, content)
                        }

                        val response = mapOf("status" to "OK", "message" to "Mensaje enrutado con éxito")
                        val jsonBytes = moshi.adapter(Map::class.java).toJson(response).toByteArray(Charsets.UTF_8)
                        exchange.responseHeaders.set("Content-Type", "application/json")
                        exchange.sendResponseHeaders(200, jsonBytes.size.toLong())
                        val os = exchange.responseBody
                        os.write(jsonBytes)
                        os.close()
                    } else {
                        exchange.sendResponseHeaders(400, -1)
                    }
                } else {
                    exchange.sendResponseHeaders(400, -1)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                exchange.sendResponseHeaders(500, -1)
            }
        }
    }

    private inner class ApiMediaHandler : HttpHandler {
        override fun handle(exchange: HttpExchange) {
            val query = exchange.requestURI.query
            if ("GET" == exchange.requestMethod) {
                // Serve audio/video/image media files!
                try {
                    val fileParamValue = query?.split("&")
                        ?.firstOrNull { it.startsWith("file=") }
                        ?.split("=")
                        ?.getOrNull(1)
                        ?.let { URLDecoder.decode(it, "UTF-8") }

                    if (fileParamValue == null) {
                        exchange.sendResponseHeaders(400, -1)
                        return
                    }

                    val mediaFile = File(context.filesDir, fileParamValue)
                    if (!mediaFile.exists()) {
                        // Check if file is one of preloaded files or in standard resource list, fallback
                        exchange.sendResponseHeaders(404, -1)
                        return
                    }

                    exchange.responseHeaders.set("Content-Type", getMimeType(mediaFile.name))
                    exchange.sendResponseHeaders(200, mediaFile.length())
                    val os = exchange.responseBody
                    mediaFile.inputStream().use { input ->
                        input.copyTo(os)
                    }
                    os.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                    exchange.sendResponseHeaders(500, -1)
                }
            } else if ("POST" == exchange.requestMethod) {
                // Receive incoming file upload from web browser or external node!
                try {
                    val boundary = exchange.requestHeaders.getFirst("Content-Type")
                        ?.split(";")
                        ?.firstOrNull { it.trim().startsWith("boundary=") }
                        ?.split("=")
                        ?.getOrNull(1)

                    val fileName = "media_upload_${System.currentTimeMillis()}"
                    val targetFile = File(context.filesDir, fileName)
                    
                    // Simple, robust stream pipeline
                    val input = exchange.requestBody
                    targetFile.outputStream().use { output ->
                        input.copyTo(output)
                    }

                    val info = mapOf(
                        "status" to "OK",
                        "fileUri" to targetFile.name,
                        "fileName" to targetFile.name
                    )
                    val json = moshi.adapter(Map::class.java).toJson(info)
                    val bytes = json.toByteArray(Charsets.UTF_8)
                    exchange.responseHeaders.set("Content-Type", "application/json")
                    exchange.sendResponseHeaders(200, bytes.size.toLong())
                    val os = exchange.responseBody
                    os.write(bytes)
                    os.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                    exchange.sendResponseHeaders(500, -1)
                }
            } else {
                exchange.sendResponseHeaders(455, -1)
            }
        }
    }

    private fun getMimeType(fileName: String): String {
        return when {
            fileName.endsWith(".mp3", true) -> "audio/mpeg"
            fileName.endsWith(".wav", true) -> "audio/wav"
            fileName.endsWith(".mp4", true) -> "video/mp4"
            fileName.endsWith(".png", true) -> "image/png"
            fileName.endsWith(".jpg", true) || fileName.endsWith(".jpeg", true) -> "image/jpeg"
            else -> "application/octet-stream"
        }
    }

    private fun getHtmlPage(): String {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>MALLA CONTROL CONSOLE</title>
                <style>
                    body {
                        background-color: #0B0E14;
                        color: #00FFCC;
                        font-family: 'Courier New', Courier, monospace;
                        padding: 20px;
                        margin: 0;
                    }
                    .container {
                        max-width: 800px;
                        margin: 0 auto;
                        border: 1px solid rgba(0, 255, 204, 0.3);
                        border-radius: 8px;
                        background-color: #03070A;
                        box-shadow: 0 0 15px rgba(0, 255, 204, 0.1);
                        display: flex;
                        flex-direction: column;
                        height: 90vh;
                    }
                    .header {
                        padding: 15px;
                        border-bottom: 1px solid rgba(0, 255, 204, 0.3);
                        display: flex;
                        justify-content: space-between;
                        align-items: center;
                        background: linear-gradient(180deg, #070B11 0%, #03070A 100%);
                    }
                    .status-badge {
                        background-color: #0a2f1d;
                        color: #00FF66;
                        padding: 4px 8px;
                        border-radius: 4px;
                        font-size: 11px;
                        border: 1px solid #00FF66;
                        letter-spacing: 1px;
                        animation: pulse 1.5s infinite;
                    }
                    @keyframes pulse {
                        0% { opacity: 0.6; }
                        50% { opacity: 1; }
                        100% { opacity: 0.6; }
                    }
                    .chat-area {
                        flex: 1;
                        overflow-y: auto;
                        padding: 15px;
                        display: flex;
                        flex-direction: column;
                        gap: 12px;
                        background: radial-gradient(circle, #050a12 0%, #03070A 100%);
                    }
                    .message-row {
                        padding: 8px 12px;
                        border-radius: 6px;
                        max-width: 80%;
                        word-wrap: break-word;
                        font-size: 13px;
                        line-height: 1.4;
                    }
                    .msg-inbound {
                        background-color: rgba(0, 255, 204, 0.08);
                        border: 1px solid rgba(0, 255, 204, 0.2);
                        align-self: flex-start;
                        color: #E2E8F0;
                    }
                    .msg-outbound {
                        background-color: rgba(147, 51, 234, 0.15);
                        border: 1px solid rgba(147, 51, 234, 0.4);
                        align-self: flex-end;
                        color: #F8FAFC;
                    }
                    .sender-tag {
                        font-size: 10px;
                        color: #A0AEC0;
                        margin-bottom: 3px;
                        font-weight: bold;
                    }
                    .file-link {
                        color: #00FFCC;
                        text-decoration: underline;
                        margin-top: 5px;
                        display: block;
                        font-size: 12px;
                    }
                    .input-panel {
                        padding: 15px;
                        border-top: 1px solid rgba(0, 255, 204, 0.2);
                        display: flex;
                        gap: 10px;
                        background-color: #070B11;
                    }
                    input[type="text"] {
                        flex: 1;
                        background-color: #000;
                        border: 1px solid rgba(0, 255, 204, 0.4);
                        color: #FFF;
                        padding: 10px;
                        border-radius: 4px;
                        font-family: inherit;
                    }
                    input[type="text"]:focus {
                        outline: none;
                        border-color: #00FFCC;
                        box-shadow: 0 0 5px rgba(0, 255, 204, 0.3);
                    }
                    button {
                        background-color: #9333EA;
                        color: white;
                        border: none;
                        padding: 10px 20px;
                        font-family: inherit;
                        font-weight: bold;
                        border-radius: 4px;
                        cursor: pointer;
                        text-transform: uppercase;
                        letter-spacing: 1px;
                        transition: background 0.2s;
                    }
                    button:hover {
                        background-color: #A855F7;
                    }
                    .media-btn {
                        background-color: transparent;
                        border: 1px solid #00FFCC;
                        color: #00FFCC;
                    }
                    .media-btn:hover {
                        background-color: rgba(0, 255, 204, 0.1);
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <div>
                            <h3 style="margin: 0; font-size: 15px; letter-spacing: 1px;">⚡ CENTRAL_DESCENTRALIZADA_BRIDGE (PORT: 9092)</h3>
                            <div style="font-size: 10px; color: #718096; margin-top: 5px;">CONECTADO A: <span id="node-name">CARGANDO...</span> (<span id="node-phone">...</span>)</div>
                        </div>
                        <div class="status-badge">ENLACE ACTIVO</div>
                    </div>
                    
                    <div class="chat-area" id="log-box">
                        <!-- Messages will load here -->
                    </div>

                    <div class="input-panel">
                        <input type="text" id="msg-input" placeholder="Transmitir cadena de datos crudos sobre la malla..." onkeydown="if(event.key==='Enter') sendMessage()">
                        <button onclick="sendMessage()">ENVIAR</button>
                    </div>
                </div>

                <script>
                    const nodeNameEl = document.getElementById('node-name');
                    const nodePhoneEl = document.getElementById('node-phone');
                    const logBox = document.getElementById('log-box');
                    const msgInput = document.getElementById('msg-input');

                    let myPhone = '';
                    let peerPhone = '';

                    async function loadStatus() {
                        try {
                            const res = await fetch('/api/status');
                            const data = await res.json();
                            nodeNameEl.textContent = data.username.toUpperCase();
                            nodePhoneEl.textContent = data.phoneNumber;
                            myPhone = data.phoneNumber;
                            peerPhone = data.selectedChatPhone || '';
                            loadMessages();
                        } catch (e) {
                            console.error(e);
                        }
                    }

                    async function loadMessages() {
                        try {
                            const res = await fetch('/api/messages');
                            const data = await res.json();
                            logBox.innerHTML = '';
                            data.forEach(msg => {
                                const row = document.createElement('div');
                                row.className = 'message-row ' + (msg.senderId === 'self' ? 'msg-outbound' : 'msg-inbound');
                                
                                const meta = document.createElement('div');
                                meta.className = 'sender-tag';
                                meta.textContent = (msg.senderName || msg.senderId) + ' - ' + new Date(msg.timestamp).toLocaleTimeString();
                                
                                const text = document.createElement('div');
                                text.textContent = msg.content;
                                
                                row.appendChild(meta);
                                row.appendChild(text);

                                if (msg.fileUri) {
                                    const link = document.createElement('a');
                                    link.className = 'file-link';
                                    link.href = '/api/media?file=' + encodeURIComponent(msg.fileUri);
                                    link.target = '_blank';
                                    link.textContent = '📎 DESCARGAR ADJUNTO: ' + msg.fileName;
                                    row.appendChild(link);
                                }

                                logBox.appendChild(row);
                            });
                            logBox.scrollTop = logBox.scrollHeight;
                        } catch (e) {
                            console.error(e);
                        }
                    }

                    async function sendMessage() {
                        const content = msgInput.value.trim();
                        if (!content) return;
                        msgInput.value = '';

                        try {
                            await fetch('/api/message', {
                                method: 'POST',
                                headers: { 'Content-Type': 'application/json' },
                                body: JSON.stringify({
                                    content: content,
                                    senderPhone: 'self_web',
                                    senderName: 'NODO WEB',
                                    chatId: peerPhone || 'Global'
                                })
                            });
                            loadMessages();
                        } catch (e) {
                            console.error(e);
                        }
                    }

                    loadStatus();
                    setInterval(loadMessages, 3000);
                </script>
            </body>
            </html>
        """.trimIndent()
    }
}
