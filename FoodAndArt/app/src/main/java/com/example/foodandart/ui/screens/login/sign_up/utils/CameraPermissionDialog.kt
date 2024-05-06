package com.example.foodandart.ui.screens.login.sign_up.utils

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.example.foodandart.R
import com.example.foodandart.ui.screens.login.sign_up.SignUpViewModel

@Composable
fun Camera( snackbarHostState:  SnackbarHostState, viewModel: SignUpViewModel) {
    var showPermissionDeniedAlert by remember { mutableStateOf(false) }
    var showPermissionPermanentlyDeniedSnackBar by remember { mutableStateOf(false) }

    val ctx = LocalContext.current
    val cameraLauncher = rememberCameraLauncher {}

    val cameraPermission = rememberPermission(
        Manifest.permission.CAMERA
    ) { status ->

        when (status) {
            PermissionStatus.Unknown -> {}
            PermissionStatus.Granted -> { cameraLauncher.captureImage() }
            PermissionStatus.Denied -> showPermissionDeniedAlert = true
            PermissionStatus.PermanentlyDenied -> showPermissionPermanentlyDeniedSnackBar = true
        }
    }
    Box(modifier = Modifier.clickable { takePicture(cameraPermission, cameraLauncher) }) {
        if (cameraLauncher.captureImageUri.path?.isNotEmpty() == true) {
            Log.d("Dest", "Path arr ${cameraLauncher.captureImageUri.path.toString()}")
            val uri = cameraLauncher.captureImageUri
            viewModel.image = uri
            val painter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current)
                    .data(data = uri)
                    .apply(block = fun ImageRequest.Builder.() {
                        transformations(CircleCropTransformation())
                    }).build()
            )
            Image(
                painter = painter,
                "Captured image",
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape),
                alignment = Alignment.Center,
                contentScale = ContentScale.Fit
            )
        } else {
            Image(
                Icons.Outlined.AccountCircle,
                "empty Image",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(150.dp),
                alignment = Alignment.Center
            )
        }
    }
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    )
    Button(onClick = { takePicture(cameraPermission, cameraLauncher) }) {
        Text(stringResource(id = R.string.picture))
    }

    if (showPermissionDeniedAlert) {
        AlertDialog(
            title = { Text(stringResource(id = R.string.camera_denied)) },
            text = { Text(stringResource(id = R.string.camera_denied_body)) },
            confirmButton = {
                TextButton(onClick = {
                    cameraPermission.launchPermissionRequest()
                    showPermissionDeniedAlert = false
                }) {
                    Text(stringResource(id = R.string.grant))
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDeniedAlert = false }) {
                    Text(stringResource(id = R.string.dismiss))
                }
            },
            onDismissRequest = {
                showPermissionDeniedAlert = false
            }
        )
    }

    if (showPermissionPermanentlyDeniedSnackBar) {
        LaunchedEffect(snackbarHostState) {
            val res = snackbarHostState.showSnackbar(
                ctx.getString(R.string.camera_perm),
                ctx.getString(R.string.go_settings),
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

