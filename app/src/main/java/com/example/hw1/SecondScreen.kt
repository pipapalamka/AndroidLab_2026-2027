package com.example.hw1

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

@Composable
fun SecondScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val locationManager = remember {
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }
    var locationText by rememberSaveable {
        mutableStateOf(context.getString(R.string.location_hint))
    }
    var locationListener by remember {
        mutableStateOf<LocationListener?>(null)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        if (result[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            requestLocation(
                context = context,
                locationManager = locationManager,
                onTextChange = { locationText = it },
                onListenerChange = { locationListener = it }
            )
        } else {
            locationText = context.getString(R.string.location_permission_denied)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            locationListener?.let { listener ->
                try {
                    locationManager.removeUpdates(listener)
                } catch (_: SecurityException) {
                }
            }
        }
    }

    fun onGetLocationClick() {
        locationListener?.let { listener ->
            try {
                locationManager.removeUpdates(listener)
            } catch (_: SecurityException) {
            }
        }
        locationListener = null

        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            requestLocation(
                context = context,
                locationManager = locationManager,
                onTextChange = { locationText = it },
                onListenerChange = { locationListener = it }
            )
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        TextButton(onClick = onBack) {
            Text(stringResource(R.string.back))
        }

        Text(
            text = stringResource(R.string.second_screen_title),
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = locationText,
            onValueChange = {},
            readOnly = true,
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onGetLocationClick() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.get_location))
        }
    }
}