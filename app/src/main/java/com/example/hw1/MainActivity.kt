package com.example.hw1

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

const val CHANNEL_MIN = "channel_min"
const val CHANNEL_DEFAULT = "channel_default"
const val CHANNEL_HIGH = "channel_high"
const val EXTRA_FROM_NOTIFICATION = "extra_from_notification"
const val EXTRA_NOTIFICATION_TITLE = "extra_notification_title"
const val EXTRA_NOTIFICATION_TEXT = "extra_notification_text"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannels(this)

        if (savedInstanceState == null && intent.getBooleanExtra(EXTRA_FROM_NOTIFICATION, false)) {
            val title = intent.getStringExtra(EXTRA_NOTIFICATION_TITLE)
            if (!title.isNullOrEmpty()) {
                Toast.makeText(this, title, Toast.LENGTH_LONG).show()
            }
        }

        setContent {
            MaterialTheme {
                NotificationsApp()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent.getBooleanExtra(EXTRA_FROM_NOTIFICATION, false)) {
            val text = intent.getStringExtra(EXTRA_NOTIFICATION_TEXT)
            if (!text.isNullOrEmpty()) {
                Toast.makeText(this, text, Toast.LENGTH_LONG).show()
            }
        }
    }
}

fun hasNotificationPermission(context: Context): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.POST_NOTIFICATIONS
    ) == PackageManager.PERMISSION_GRANTED
}

fun openAppSettings(context: Context) {
    context.startActivity(
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:${context.packageName}")
        )
    )
}

fun createNotificationChannels(context: Context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    manager.createNotificationChannel(
        NotificationChannel(
            CHANNEL_MIN,
            context.getString(R.string.channel_min_name),
            NotificationManager.IMPORTANCE_MIN
        ).apply {
            description = context.getString(R.string.channel_min_description)
        }
    )

    manager.createNotificationChannel(
        NotificationChannel(
            CHANNEL_DEFAULT,
            context.getString(R.string.channel_default_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = context.getString(R.string.channel_default_description)
        }
    )

    manager.createNotificationChannel(
        NotificationChannel(
            CHANNEL_HIGH,
            context.getString(R.string.channel_high_name),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = context.getString(R.string.channel_high_description)
        }
    )
}

fun getActiveNotificationIds(manager: NotificationManager): Set<Int> {
    return manager.activeNotifications.map { it.id }.toSet()
}

fun generateNotificationId(manager: NotificationManager): Int {
    val activeIds = getActiveNotificationIds(manager)
    var id = 1
    while (activeIds.contains(id)) {
        id++
    }
    return id
}

fun showNotification(
    context: Context,
    id: Int,
    title: String,
    text: String,
    ongoing: Boolean,
    importanceIndex: Int
) {
    val channelId: String
    val priority: Int

    when (importanceIndex) {
        0 -> {
            channelId = CHANNEL_MIN
            priority = NotificationCompat.PRIORITY_MIN
        }
        2 -> {
            channelId = CHANNEL_HIGH
            priority = NotificationCompat.PRIORITY_HIGH
        }
        else -> {
            channelId = CHANNEL_DEFAULT
            priority = NotificationCompat.PRIORITY_DEFAULT
        }
    }

    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        putExtra(EXTRA_FROM_NOTIFICATION, true)
        putExtra(EXTRA_NOTIFICATION_TITLE, title)
        putExtra(EXTRA_NOTIFICATION_TEXT, text)
    }

    val pendingIntent = PendingIntent.getActivity(
        context,
        id,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_notification)
        .setContentTitle(title)
        .setContentText(text)
        .setStyle(NotificationCompat.BigTextStyle().bigText(text))
        .setContentIntent(pendingIntent)
        .setAutoCancel(!ongoing)
        .setOngoing(ongoing)
        .setPriority(priority)

    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    manager.notify(id, builder.build())
}

@Suppress("DEPRECATION")
fun requestLocation(
    context: Context,
    locationManager: LocationManager,
    onTextChange: (String) -> Unit,
    onListenerChange: (LocationListener?) -> Unit
) {
    onTextChange(context.getString(R.string.location_searching))

    val lastKnown = getLastKnownLocation(locationManager)
    if (lastKnown != null) {
        onTextChange(formatLocation(context, lastKnown))
        return
    }

    val provider = getAvailableProvider(locationManager)
    if (provider == null) {
        onTextChange(context.getString(R.string.location_unavailable))
        return
    }

    try {
        val listener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                onTextChange(formatLocation(context, location))
                locationManager.removeUpdates(this)
                onListenerChange(null)
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            }

            override fun onProviderEnabled(provider: String) {
            }

            override fun onProviderDisabled(provider: String) {
            }
        }
        onListenerChange(listener)
        locationManager.requestSingleUpdate(provider, listener, Looper.getMainLooper())
    } catch (_: SecurityException) {
        onTextChange(context.getString(R.string.location_permission_denied))
    } catch (_: Exception) {
        onTextChange(context.getString(R.string.location_unavailable))
    }
}

fun getLastKnownLocation(locationManager: LocationManager): Location? {
    return try {
        var best: Location? = null
        for (provider in locationManager.getProviders(true)) {
            val location = locationManager.getLastKnownLocation(provider) ?: continue
            val currentBest = best
            if (currentBest == null || location.time > currentBest.time) {
                best = location
            }
        }
        best
    } catch (_: SecurityException) {
        null
    }
}

fun getAvailableProvider(locationManager: LocationManager): String? {
    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
        return LocationManager.GPS_PROVIDER
    }
    if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
        return LocationManager.NETWORK_PROVIDER
    }
    return null
}

fun formatLocation(context: Context, location: Location): String {
    return context.getString(
        R.string.location_result,
        location.latitude,
        location.longitude
    )
}