package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.components.HolographicCall
import com.example.ui.screens.ChatDetailScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.ChatViewModel
import com.example.util.NotificationHelper

class MainActivity : ComponentActivity() {

    // Launcher for critical audio and camera transmissions
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraGranted = permissions[Manifest.permission.CAMERA] ?: false
        val audioGranted = permissions[Manifest.permission.RECORD_AUDIO] ?: false
        if (cameraGranted && audioGranted) {
            Toast.makeText(this, "SISTEMA P2P: Dispositivos de captura enlazados.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "MODO SEGURO ACTIVO (Sin permisos físicos)", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Create the notification channel
        NotificationHelper.createNotificationChannel(this)

        // Prompt for camera, record audio, and notifications to check hardware for videoconferences
        checkP2PPermissions()

        setContent {
            MyApplicationTheme {
                val viewModel: ChatViewModel = viewModel()
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BoxWithCallOverlay(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = "home"
                        ) {
                            composable("home") {
                                HomeScreen(
                                    viewModel = viewModel,
                                    onNavigateToChat = {
                                        navController.navigate("chat")
                                    },
                                    onInitiateCall = { peerPhone ->
                                        viewModel.initiateVideoconference(peerPhone)
                                    }
                                )
                            }
                            composable("chat") {
                                ChatDetailScreen(
                                    viewModel = viewModel,
                                    onBack = {
                                        navController.popBackStack()
                                        viewModel.selectChat(null)
                                    },
                                    onInitiateCall = { peerPhone ->
                                        viewModel.initiateVideoconference(peerPhone)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun checkP2PPermissions() {
        val requiredList = mutableListOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            requiredList.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        val missing = requiredList.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (missing.isNotEmpty()) {
            requestPermissionLauncher.launch(missing.toTypedArray())
        }
    }
}

@Composable
fun BoxWithCallOverlay(
    viewModel: ChatViewModel,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    androidx.compose.foundation.layout.Box(modifier = modifier.fillMaxSize()) {
        // Render Active Navigation destination
        content()

        // Overlaid global Holographic Videoconference HUD if active
        if (viewModel.isCallActive) {
            val callUser = viewModel.currentCallUser
            if (callUser != null) {
                HolographicCall(
                    peer = callUser,
                    durationSeconds = viewModel.callDurationSeconds,
                    isMuted = viewModel.isCallMuted,
                    isCameraOn = viewModel.isCallCameraOn,
                    onToggleMute = { viewModel.toggleCallMute() },
                    onToggleCamera = { viewModel.toggleCallCamera() },
                    onHangup = { viewModel.terminateVideoconference() }
                )
            }
        }
    }
}
