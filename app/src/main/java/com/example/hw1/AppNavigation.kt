package com.example.hw1

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun NotificationsApp() {
    var showSecondScreen by rememberSaveable { mutableStateOf(false) }

    BackHandler(enabled = showSecondScreen) {
        showSecondScreen = false
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            FirstScreen(onOpenSecondScreen = { showSecondScreen = true })

            if (showSecondScreen) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SecondScreen(onBack = { showSecondScreen = false })
                }
            }
        }
    }
}