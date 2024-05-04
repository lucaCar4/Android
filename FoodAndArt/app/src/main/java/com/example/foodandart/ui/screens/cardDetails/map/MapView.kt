package com.example.foodandart.ui.screens.cardDetails.map

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.example.foodandart.ui.screens.cardDetails.CardDetailsViewModel
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.views.overlay.Marker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Map(viewModel: CardDetailsViewModel) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    if (viewModel.showMap) {
        ModalBottomSheet(
            onDismissRequest = {
                viewModel.showMap = false
            },
            sheetState = sheetState
        ) {
            val geoPoints =
                viewModel.document?.data?.get("coordinates") as? List<com.google.firebase.firestore.GeoPoint>
            if (!geoPoints.isNullOrEmpty()) {
                MapScreen(geoPoints)
            } else {
                MapScreen(geoPoints = emptyList())
            }
        }
    }
}

@Composable
fun MapScreen(geoPoints: List<com.google.firebase.firestore.GeoPoint>) {
    val context = LocalContext.current

    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(15.0)
            if (geoPoints.isNotEmpty()) {
                val osmdPoint =
                    GeoPoint(geoPoints.first().latitude - 0.02, geoPoints.first().longitude)
                controller.setCenter(osmdPoint)
            }
        }
    }
    for (geoPoint in geoPoints) {
        val osmdPoint = GeoPoint(geoPoint.latitude, geoPoint.longitude)
        val marker = Marker(mapView)
        marker.position = osmdPoint
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
