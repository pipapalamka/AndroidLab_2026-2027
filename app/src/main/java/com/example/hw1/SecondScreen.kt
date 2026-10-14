package com.example.hw1

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun SecondScreen(
    enteredPassword: String,
    generatedPassword: String,
    onNavigateBack: () -> Unit
) {
    val passwordsMatch = enteredPassword == generatedPassword

    val imageUrl = if (passwordsMatch) {
        "https://cdn.forbes.ru/forbes-static/new/2023/09/Pobeda-logo-svg-65154124c6d42.jpg"
    } else {
        "https://photobooth.cdn.sports.ru/preset/post/6/f7/18df8f6284bdcb319fd8b3efd1b19.jpeg?f=webp&q=90&s=2x&w=730"
    }

    var showPassword by remember { mutableStateOf(false) }
    var copied by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = if (passwordsMatch) "Пароли совпадают" else "Пароли не совпадают",
                modifier = Modifier
                    .size(220.dp)
                    .padding(8.dp)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = if (passwordsMatch) "Пароли совпадают" else "Пароли не совпадают",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = generatedPassword,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Сгенерированный пароль") },
                    visualTransformation = if (showPassword)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                Button(onClick = { showPassword = !showPassword }) {
                    Text(if (showPassword) "Скрыть" else "Показать")
                }
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
                    clipboardManager.setText(AnnotatedString(generatedPassword))
                    copied = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (copied) "Скопировано" else "Скопировать пароль")
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Назад")
            }
        }
    }
}