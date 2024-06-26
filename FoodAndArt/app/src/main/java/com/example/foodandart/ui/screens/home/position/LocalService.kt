package com.example.foodandart.ui.screens.home.position

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.example.foodandart.ui.screens.home.HomeViewModel
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.firebase.firestore.GeoPoint

enum class MonitoringStatus {
    Monitoring,
    Paused,
    NotMonitoring
}

data class Coordinates(val latitude: Double, val longitude: Double)

class LocationService(private val ctx: Context, viewModel: HomeViewModel) {

    var coordinates: Coordinates? by mutableStateOf(null)
        private set

    private var monitoringStatus: MonitoringStatus by mutableStateOf(MonitoringStatus.NotMonitoring)

    private var isLocationEnabled: Boolean? by mutableStateOf(null)

    private val locationProviderClient = LocationServices.getFusedLocationProviderClient(ctx)

    private val locationRequest =
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .apply {
                setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            }
            .build()

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
            coordinates = Coordinates(p0.locations.last().latitude, p0.locations.last().longitude)
            viewModel.geoPoint = GeoPoint(coordinates!!.latitude, coordinates!!.longitude)
            Log.d("Position", "Settata geopoint${viewModel.geoPoint}")
            endLocationRequest()
        }
    }

    fun openLocationSettings() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        if (intent.resolveActivity(ctx.packageManager) != null) {
            ctx.startActivity(intent)
        }
    }

    fun requestCurrentLocation() {
        Log.d("Distance", "Entro in current")
        val locationManager = ctx.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (isLocationEnabled == false) return
        val permissionGranted = ContextCompat.checkSelfPermission(
            ctx,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (!permissionGranted) return
        Log.d("Distance", "Chiamo ")
        locationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
        monitoringStatus = MonitoringStatus.Monitoring
    }

    fun endLocationRequest() {
        if (monitoringStatus != MonitoringStatus.NotMonitoring) return
        locationProviderClient.removeLocationUpdates(locationCallback)
        monitoringStatus = MonitoringStatus.Paused
    }

}