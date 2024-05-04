package com.example.foodandart.ui.screens.home

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.foodandart.R
import com.example.foodandart.ui.screens.home.position.LocationService
import com.example.foodandart.ui.screens.login.sign_up.utils.PermissionStatus
import com.example.foodandart.ui.screens.login.sign_up.utils.rememberPermission

@Composable
fun FilterChips(
    contentPadding: PaddingValues,
    viewModel: HomeViewModel,
    locationService: LocationService
) {

    val chips = mapOf(
        Pair("restaurants", stringResource(id = R.string.restaurants)),
        Pair("museums", stringResource(id = R.string.museums)),
        Pair("packages", stringResource(id = R.string.packages))
    )

    val positionState = viewModel.position.toBoolean()

    var showPermissionDeniedAlert by remember { mutableStateOf(false) }

    var showPermissionPermanentlyDeniedSnackBar by remember { mutableStateOf(false) }

    var showLocationDisabledAlert by remember { mutableStateOf(false) }
    val locationPermission = rememberPermission(
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) { status ->

        when (status) {
            PermissionStatus.Unknown -> {}
            PermissionStatus.Granted -> {
                locationService.requestCurrentLocation()
                viewModel.setChip((!positionState).toString(), "position")
                viewModel.showCardByPosition()
            }
            PermissionStatus.Denied -> showPermissionDeniedAlert = true
            PermissionStatus.PermanentlyDenied -> showPermissionPermanentlyDeniedSnackBar = true
        }
    }

    fun requestLocation() {
        if (locationPermission.status.isGranted) {
            locationService.requestCurrentLocation()
            viewModel.showCardByPosition()
        } else {
            locationPermission.launchPermissionRequest()
        }
    }

    if (viewModel.geoPoint.latitude != 0.0 && positionState) {
        Log.d("Cards", "Chiamo pos")
        viewModel.showCardByPosition()
    }
    LazyRow(
        modifier = Modifier
            .padding(contentPadding)
            .padding(16.dp, 0.dp),
    )
    {
        chips.forEach { pair ->
            item {
                Chip(pair, viewModel)
            }
        }
        item {
            FilterChip(
                selected = positionState,
                onClick = {
                    if (!positionState) {
                        requestLocation()
                    }
                    if (positionState || locationPermission.status == PermissionStatus.Granted) {
                        viewModel.setChip((!positionState).toString(), "position")
                    }
                },
                label = { Text(text = stringResource(id = R.string.position)) },
                leadingIcon = if (positionState) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Done icon",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else {
                    null
                },
                modifier = Modifier.padding(8.dp, 0.dp)
            )
        }
    }

    if (showPermissionDeniedAlert) {
        AlertDialog(
            title = { Text("Location Permission Denied") },
            text = { Text("Location Permission must be enabled") },
            confirmButton = {
                TextButton(onClick = {
                    locationPermission.launchPermissionRequest()
                    showPermissionDeniedAlert = false
                }) {
                    Text("Grant")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDeniedAlert = false }) {
                    Text("Dismiss")
                }
            },
            onDismissRequest = {
                showPermissionDeniedAlert = false
            }
        )
    }

    if (showLocationDisabledAlert) {
        AlertDialog(
            title = { Text("Location Disabled") },
            text = { Text("Location must be enabled") },
            confirmButton = {
                TextButton(onClick = {
                    locationService.openLocationSettings()
                    showLocationDisabledAlert = false
                }) {
                    Text("Grant")
                }
            },
            onDismissRequest = {
                showLocationDisabledAlert = false
            }
        )
    }

    val ctx = LocalContext.current
    if (showPermissionPermanentlyDeniedSnackBar) {
        LaunchedEffect(snackbarHostState) {
            val res = snackbarHostState.showSnackbar(
                "Location permission is required",
                "Go to Settings",
                duration = SnackbarDuration.Long
            )
            if (res == SnackbarResult.ActionPerformed) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    data = Uri.fromParts("package", ctx.packageName, null)
                }
                if (intent.resolveActivity(ctx.packageManager) != null) {
                    ctx.startActivity(intent)
                }
                showPermissionPermanentlyDeniedSnackBar = false
            }
        }
    }
}


@Composable
fun Chip(pair: Map.Entry<String, String>, vm: HomeViewModel) {
    val state = vm.getVar(pair.key).toBoolean()
    FilterChip(
        selected = state,
        onClick = { vm.setChip((!state).toString(), pair.key) },
        label = { Text(text = pair.value) },
        leadingIcon = if (state) {
            {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Done icon",
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        } else {
            null
        },
        modifier = Modifier.padding(8.dp, 0.dp)
    )
}