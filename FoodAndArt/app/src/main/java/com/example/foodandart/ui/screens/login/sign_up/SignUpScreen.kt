package com.example.foodandart.ui.screens.login.sign_up

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.example.foodandart.R
import com.example.foodandart.data.remote.OSMDataSource
import com.example.foodandart.data.remote.OSMPlace
import com.example.foodandart.ui.screens.login.sign_up.utils.Camera
import com.example.foodandart.ui.screens.login.sign_up.utils.FindDestination
import com.example.foodandart.ui.screens.login.sign_up.utils.GetCredentialField
import com.example.foodandart.ui.screens.login.sign_up.utils.PermissionStatus
import com.example.foodandart.ui.screens.login.sign_up.utils.UserExtraData
import com.example.foodandart.ui.screens.login.sign_up.utils.rememberCameraLauncher
import com.example.foodandart.ui.screens.login.sign_up.utils.rememberPermission
import com.example.foodandart.ui.screens.login.sign_up.utils.saveImageToStorage
import com.example.foodandart.ui.screens.login.sign_up.utils.takePicture
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

var confirmHide by mutableStateOf(true)
var confirmVisibility: VisualTransformation by mutableStateOf(PasswordVisualTransformation())
var passwordVisibility: VisualTransformation by mutableStateOf(PasswordVisualTransformation())
var passwordHide by mutableStateOf(true)
var captureImageUri by mutableStateOf<Uri?>(null)

@Composable
fun SignUpScreen(
    navController: NavController,
    viewModel: SignUpViewModel
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val focus = LocalFocusManager.current
    val ctx = LocalContext.current
    val osmDataSource = koinInject<OSMDataSource>()

    var place by remember { mutableStateOf<OSMPlace?>(null) }
    var placeNotFound by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    fun openWirelessSettings() {
        val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        if (intent.resolveActivity(ctx.applicationContext.packageManager) != null) {
            ctx.applicationContext.startActivity(intent)
        }
    }

    fun isOnline(): Boolean {
        val connectivityManager = ctx
            .applicationContext
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true ||
                capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
    }

    fun searchPlaces() = coroutineScope.launch {
        Log.d("Dest", "Entro")
        if (isOnline()) {
            Log.d("Dest", "Cerco")
            val res = osmDataSource.searchPlaces(viewModel.city)
            Log.d("Dest", "Result $res")
            place = res.getOrNull(0)
            if (place != null) {
                viewModel.city = place!!.displayName.split(',')[1]
                viewModel.cityGeoPoint = GeoPoint(place!!.latitude, place!!.longitude)
                viewModel.existDestination = true
            } else {
                viewModel.existDestination = false
            }
            placeNotFound = res.isEmpty()
        } else {
            val res = snackbarHostState.showSnackbar(
                message = "No Internet connectivity",
                actionLabel = "Go to Settings",
                duration = SnackbarDuration.Long
            )
            if (res == SnackbarResult.ActionPerformed) {
                openWirelessSettings()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Camera(snackbarHostState = snackbarHostState, viewModel)
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            )
            UserExtraData(viewModel = viewModel)
            GetCredentialField(viewModel = viewModel)
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            )
            Button(
                onClick = {
                    focus.clearFocus(true)
                    searchPlaces()
                    viewModel.onSignUpClick(navController)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 0.dp)
            ) {
                Text(
                    text = stringResource(R.string.sign_up),
                    fontSize = 16.sp,
                    modifier = Modifier.padding(0.dp, 6.dp)
                )
            }
        }
    }

}

@Composable
fun GetIcon(hide: Boolean) {
    return if (hide) {
        Icon(imageVector = Icons.Outlined.VisibilityOff, contentDescription = "Open Eye")
    } else {
        Icon(imageVector = Icons.Outlined.Visibility, contentDescription = "Close Eye")
    }
}


