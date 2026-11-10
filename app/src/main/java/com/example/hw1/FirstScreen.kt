package com.example.hw1

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.delay

@Composable
fun FirstScreen(onOpenSecondScreen: () -> Unit) {
    val context = LocalContext.current
    val notificationManager = remember {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    var permissionGranted by remember {
        mutableStateOf(hasNotificationPermission(context))
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        permissionGranted = granted
    }

    LaunchedEffect(Unit) {
        if (!permissionGranted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                permissionGranted = hasNotificationPermission(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    if (!permissionGranted) {
        PermissionWarning(onOpenSettings = { openAppSettings(context) })
    } else {
        NotificationContent(
            notificationManager = notificationManager,
            onOpenSecondScreen = onOpenSecondScreen
        )
    }
}

@Composable
private fun PermissionWarning(onOpenSettings: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.permission_warning),
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onOpenSettings,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.open_settings))
        }
    }
}

@Composable
private fun NotificationContent(
    notificationManager: NotificationManager,
    onOpenSecondScreen: () -> Unit
) {
    val context = LocalContext.current
    val importanceOptions = stringArrayResource(R.array.importance_options)

    var title by rememberSaveable { mutableStateOf("") }
    var text by rememberSaveable { mutableStateOf("") }
    var ongoing by rememberSaveable { mutableStateOf(false) }
    var importanceIndex by rememberSaveable { mutableIntStateOf(1) }
    var importanceExpanded by remember { mutableStateOf(false) }
    var activeIds by remember {
        mutableStateOf(getActiveNotificationIds(notificationManager))
    }
    var notificationId by rememberSaveable { mutableStateOf("") }

    val hasActiveNotifications = activeIds.isNotEmpty()
    val separator = stringResource(R.string.active_ids_separator)

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            activeIds = getActiveNotificationIds(notificationManager)
        }
    }

    LaunchedEffect(hasActiveNotifications) {
        if (!hasActiveNotifications) {
            notificationId = ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text(stringResource(R.string.title_input_label)) },
            placeholder = { Text(stringResource(R.string.hint_notification_title)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text(stringResource(R.string.text_input_label)) },
            placeholder = { Text(stringResource(R.string.hint_notification_text)) },
            minLines = 4,
            maxLines = 8,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = ongoing,
                onCheckedChange = { ongoing = it }
            )
            Text(stringResource(R.string.non_cancellable))
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.importance_label),
            style = MaterialTheme.typography.titleSmall
        )

        Box {
            OutlinedButton(
                onClick = { importanceExpanded = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(importanceOptions[importanceIndex])
            }

            DropdownMenu(
                expanded = importanceExpanded,
                onDismissRequest = { importanceExpanded = false }
            ) {
                importanceOptions.forEachIndexed { index, item ->
                    DropdownMenuItem(
                        text = { Text(item) },
                        onClick = {
                            importanceIndex = index
                            importanceExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = send@{
                val trimmedTitle = title.trim()
                val trimmedText = text.trim()

                if (trimmedTitle.isEmpty() || trimmedText.isEmpty()) {
                    Toast.makeText(context, R.string.fill_title_and_text, Toast.LENGTH_SHORT).show()
                    return@send
                }

                val id = generateNotificationId(notificationManager)
                showNotification(
                    context = context,
                    id = id,
                    title = trimmedTitle,
                    text = trimmedText,
                    ongoing = ongoing,
                    importanceIndex = importanceIndex
                )
                Toast.makeText(
                    context,
                    context.getString(R.string.notification_sent, id),
                    Toast.LENGTH_SHORT
                ).show()
                activeIds = getActiveNotificationIds(notificationManager)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.send_notification))
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onOpenSecondScreen,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.open_second_screen))
        }

        Spacer(modifier = Modifier.height(24.dp))

        HorizontalDivider()

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.optional_section),
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (hasActiveNotifications) {
                stringResource(R.string.active_ids, activeIds.joinToString(separator))
            } else {
                stringResource(R.string.no_active_notifications)
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = notificationId,
            onValueChange = { newValue ->
                if (newValue.all { it.isDigit() }) {
                    notificationId = newValue
                }
            },
            enabled = hasActiveNotifications,
            placeholder = { Text(stringResource(R.string.hint_notification_id)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = edit@{
                    if (notificationId.isBlank()) {
                        Toast.makeText(context, R.string.enter_notification_id, Toast.LENGTH_SHORT).show()
                        return@edit
                    }

                    val id = notificationId.toIntOrNull()
                    if (id == null || id <= 0) {
                        Toast.makeText(context, R.string.invalid_notification_id, Toast.LENGTH_SHORT).show()
                        return@edit
                    }

                    if (!activeIds.contains(id)) {
                        Toast.makeText(context, R.string.notification_not_found, Toast.LENGTH_SHORT).show()
                        return@edit
                    }

                    val trimmedTitle = title.trim()
                    val trimmedText = text.trim()
                    if (trimmedTitle.isEmpty() || trimmedText.isEmpty()) {
                        Toast.makeText(context, R.string.fill_title_and_text, Toast.LENGTH_SHORT).show()
                        return@edit
                    }

                    showNotification(
                        context = context,
                        id = id,
                        title = trimmedTitle,
                        text = trimmedText,
                        ongoing = ongoing,
                        importanceIndex = importanceIndex
                    )
                    Toast.makeText(
                        context,
                        context.getString(R.string.notification_updated, id),
                        Toast.LENGTH_SHORT
                    ).show()
                    activeIds = getActiveNotificationIds(notificationManager)
                },
                enabled = hasActiveNotifications,
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.edit_notification))
            }

            Button(
                onClick = delete@{
                    if (notificationId.isBlank()) {
                        Toast.makeText(context, R.string.enter_notification_id, Toast.LENGTH_SHORT).show()
                        return@delete
                    }

                    val id = notificationId.toIntOrNull()
                    if (id == null || id <= 0) {
                        Toast.makeText(context, R.string.invalid_notification_id, Toast.LENGTH_SHORT).show()
                        return@delete
                    }

                    if (!activeIds.contains(id)) {
                        Toast.makeText(context, R.string.notification_not_found, Toast.LENGTH_SHORT).show()
                        return@delete
                    }

                    notificationManager.cancel(id)
                    Toast.makeText(
                        context,
                        context.getString(R.string.notification_deleted, id),
                        Toast.LENGTH_SHORT
                    ).show()
                    activeIds = getActiveNotificationIds(notificationManager)
                },
                enabled = hasActiveNotifications,
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.delete_notification))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                notificationManager.cancelAll()
                Toast.makeText(
                    context,
                    R.string.all_notifications_deleted,
                    Toast.LENGTH_SHORT
                ).show()
                activeIds = getActiveNotificationIds(notificationManager)
            },
            enabled = hasActiveNotifications,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.delete_all_notifications))
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}