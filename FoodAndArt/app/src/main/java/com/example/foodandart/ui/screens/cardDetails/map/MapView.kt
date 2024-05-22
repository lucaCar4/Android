package com.example.foodandart.ui.screens.cardDetails.map


import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import com.example.foodandart.ui.screens.cardDetails.CardDetailsViewModel
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.foodandart.data.remote.OSMDataSource
import org.koin.compose.koinInject
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.InfoWindow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Map(viewModel: CardDetailsViewModel) {
    val sheetState = rememberModalBottomSheetState()
    if (viewModel.showMap) {
        ModalBottomSheet(
            onDismissRequest = {
                viewModel.showMap = false
            },
            sheetState = sheetState
        ) {
            val geoPoints = viewModel.document?.get("coordinates") as? Map<String, com.google.firebase.firestore.GeoPoint>
            if (!geoPoints.isNullOrEmpty()) {
                MapScreen(geoPoints)
            } else {
                MapScreen(geoPoints = emptyMap())
            }
        }
    }
}

@Composable
fun MapScreen(geoPoints: Map<String, com.google.firebase.firestore.GeoPoint>) {
    val context = LocalContext.current
    val osmDataSource = koinInject<OSMDataSource>()
    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(15.0)
            if (geoPoints.isNotEmpty()) {
                val osmdPoint =
                    GeoPoint(geoPoints.values.first().latitude - 0.02, geoPoints.values.first().longitude)
                controller.setCenter(osmdPoint)
            }
        }
    }
    geoPoints.forEach { (name, geoPoint) ->
        val osmdPoint = GeoPoint(geoPoint.latitude, geoPoint.longitude)
        val marker = Marker(mapView)
        marker.position = osmdPoint
        marker.setOnMarkerClickListener { _, _ ->
            true
        }
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        mapView.overlays.add(marker)
    }

    mapView.invalidate()

    MapViewWrapper(mapView)
}

@Composable
fun MapViewWrapper(mapView: MapView) {
    AndroidView(
        factory = { mapView }
    )
}









