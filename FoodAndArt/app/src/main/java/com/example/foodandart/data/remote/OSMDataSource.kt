package com.example.foodandart.data.remote

import android.util.Log
import com.google.firebase.firestore.GeoPoint
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OSMPlace(
    @SerialName("place_id")
    val id: Int,
    @SerialName("lat")
    val latitude: Double,
    @SerialName("lon")
    val longitude: Double,
    @SerialName("display_name")
    val displayName: String
)

class OSMDataSource(
    private val httpClient: HttpClient
) {
    private val baseUrl = "https://nominatim.openstreetmap.org"
    suspend fun getPalaceName(coordinates: GeoPoint) : OSMPlace {
        val url = "$baseUrl/reverse?lat=${coordinates.latitude}&lon=${coordinates.longitude}&format=json"
        Log.d("Dest", url)
        return httpClient.get(url).body()
    }

}