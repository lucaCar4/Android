package com.example.foodandart.ui.screens.purchases

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.provider.CalendarContract
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Tab
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.foodandart.R
import com.example.foodandart.data.firestore.storage.getURIFromPath
import com.example.foodandart.data.remote.OSMDataSource
import com.example.foodandart.ui.screens.login.sign_up.utils.PermissionStatus
import com.example.foodandart.ui.screens.login.sign_up.utils.rememberPermission
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

val snackbarHostState = SnackbarHostState()
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchasesScreen(navController: NavController, viewModel: PurchasesViewModel) {
    var state by remember { mutableStateOf(0) }
    val elms = listOf(stringResource(id = R.string.events), stringResource(id = R.string.history))
    val icons = listOf(Icons.Outlined.Event, Icons.Outlined.History)
    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) {
        Column {
            PrimaryTabRow(selectedTabIndex = state) {
                elms.forEachIndexed { index, title ->
                    Tab(
                        selected = state == index,
                        onClick = { state = index },
                        text = { Text(text = title) },
                        icon = { Icon(imageVector = icons[index], contentDescription = "Tab Icon") }
                    )
                }
            }
            var purchases = viewModel.purchases
            if (state == 0) {
                purchases = purchases.filter {
                    LocalDate.parse(
                        it.data?.get("date").toString(),
                        DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    ) >= LocalDate.now()
                }
                purchases = purchases.sortedBy {
                    LocalDate.parse(
                        it.data?.get("date").toString(),
                        DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    )
                }
            }
            Events(purchases = purchases, viewModel = viewModel, state)
        }
    }
}

@Composable
fun Events(purchases: List<DocumentSnapshot>, viewModel: PurchasesViewModel, state: Int) {
    val ctx = LocalContext.current

    LazyColumn {
        items(purchases) { purchase ->
            val card = viewModel.selectedCards[purchase.data?.get("card")]
            val images = card?.get("images") as? List<String>
            val image = getURIFromPath(images?.get(0) ?: "")
            ListItem(
                headlineContent = { Text(card?.get("title").toString()) },
                supportingContent = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = purchase.data?.get("date").toString())
                        if (state == 0) {
                            Spacer(modifier = Modifier.padding(8.dp))
                            AddEvent(viewModel = viewModel, purchase, card)
                        }
                    }
                },
                leadingContent = {
                    AsyncImage(
                        ImageRequest.Builder(ctx)
                            .data(image)
                            .crossfade(true)
                            .build(),
                        "Captured image",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .size(80.dp)
                    )
                }
            )
        }
    }
}

@Composable
fun AddEvent(viewModel: PurchasesViewModel, purchases: DocumentSnapshot, card: Map<String, Any>?) {
    val ctx = LocalContext.current
    val osmDataSource = koinInject<OSMDataSource>()
    val coroutineScope = rememberCoroutineScope()

    val calendarPermission = rememberPermission(
        Manifest.permission.WRITE_CALENDAR
    ) { status ->

        when (status) {
            PermissionStatus.Unknown -> {}
            PermissionStatus.Granted -> {
                calendar(ctx, purchases, card, osmDataSource, coroutineScope)
            }

            PermissionStatus.Denied -> viewModel.showPermissionDeniedAlert = true
            PermissionStatus.PermanentlyDenied -> viewModel.showPermissionPermanentlyDeniedSnackBar =
                true
        }
    }

    fun addEvent() {
        if (calendarPermission.status.isGranted) {
            calendar(ctx, purchases, card, osmDataSource, coroutineScope)
        } else {
            calendarPermission.launchPermissionRequest()
        }
    }

    Button(onClick = { addEvent() }, modifier = Modifier.fillMaxWidth()) {
        Icon(
            imageVector = Icons.Outlined.Event,
            contentDescription = "Add Event",
            modifier = Modifier.size(15.dp)
        )
        Text(text = stringResource(id = R.string.add_event), fontSize = 10.sp)
    }
    showPermission(
        viewModel = viewModel,
        purchases = purchases,
        card = card,
        osmDataSource = osmDataSource,
        scope = coroutineScope
    )
}

@Composable
fun showPermission(
    viewModel: PurchasesViewModel,
    purchases: DocumentSnapshot,
    card: Map<String, Any>?,
    osmDataSource: OSMDataSource,
    scope: CoroutineScope
) {
    val ctx = LocalContext.current
    if (viewModel.showPermissionDeniedAlert) {
        AlertDialog(
            title = { Text(stringResource(id = R.string.calendar_denied)) },
            text = { Text(stringResource(id = R.string.calendar_denied_body)) },
            confirmButton = {
                TextButton(onClick = {
                    calendar(ctx, purchases, card, osmDataSource, scope)
                    viewModel.showPermissionDeniedAlert = false
                }) {
                    Text(stringResource(id = R.string.grant))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showPermissionDeniedAlert = false }) {
                    Text(stringResource(id = R.string.dismiss))
                }
            },
            onDismissRequest = {
                viewModel.showPermissionDeniedAlert = false
            }
        )
    }

    if (viewModel.showPermissionPermanentlyDeniedSnackBar) {
        LaunchedEffect(snackbarHostState) {
            val res = snackbarHostState.showSnackbar(
                ctx.getString(R.string.calendar_perm),
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
                viewModel.showPermissionPermanentlyDeniedSnackBar = false
            }
        }
    }

}


fun calendar(
    ctx: Context,
    purchase: DocumentSnapshot,
    card: Map<String, Any>?,
    osmDataSource: OSMDataSource,
    scope: CoroutineScope
) {
    val coordinates = card?.get("coordinates") as? List<GeoPoint>
    if (!coordinates.isNullOrEmpty()) {
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

        fun getName() = scope.launch {
            if (isOnline()) {
                val res = osmDataSource.getPalaceName(coordinates.first())
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val date: Date = dateFormat.parse(purchase.data?.get("date").toString()) ?: Date()
                Log.d("Dataaaa", date.toString())
                val startTimeInMillis = date.time
                val intent = Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTimeInMillis)
                    .putExtra(CalendarContract.Events.TITLE, card["title"].toString())
                    .putExtra(
                        CalendarContract.Events.DESCRIPTION,
                        ctx.getString(R.string.calendar_description_start) + " " + purchase.data?.get(
                            "quantity"
                        ).toString() + " " + ctx.getString(R.string.calendar_description_end)
                    )
                    .putExtra(CalendarContract.Events.EVENT_LOCATION, res.displayName)

                ctx.startActivity(intent)
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
        getName()
    }
}