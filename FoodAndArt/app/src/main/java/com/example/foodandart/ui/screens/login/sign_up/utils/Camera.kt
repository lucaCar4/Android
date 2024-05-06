package com.example.foodandart.ui.screens.login.sign_up.utils

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File

interface CameraLauncher {
    var captureImageUri: Uri
    fun captureImage()
}

var imageUri: Uri = Uri.EMPTY

@Composable
fun rememberCameraLauncher(
    onResult: (Uri) -> Unit = {}
): CameraLauncher {
    val ctx = LocalContext.current

    var capturedImageUri by remember { mutableStateOf(Uri.EMPTY) }
    val cameraActivityLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { pictureTaken ->
            if (pictureTaken) {
                capturedImageUri = imageUri
                onResult(capturedImageUri)
            }
        }

    val cameraLauncher by remember {
        derivedStateOf {
            object : CameraLauncher {
                override var captureImageUri = capturedImageUri

                override fun captureImage() {
                    val imageFile = File.createTempFile("tmp_image", ".jpg", ctx.externalCacheDir)
                    val newImageUri =
                        FileProvider.getUriForFile(ctx, ctx.packageName + ".provider", imageFile)
                    Log.d("Dest", newImageUri.path.toString())
                    imageUri = newImageUri
                    cameraActivityLauncher.launch(newImageUri)
                }
            }
        }
    }
    return cameraLauncher
}

fun takePicture(cameraPermission: PermissionHandler, cameraLauncher: CameraLauncher) {
    if (cameraPermission.status.isGranted) {
        cameraLauncher.captureImage()
    } else {
        cameraPermission.launchPermissionRequest()
    }
}

