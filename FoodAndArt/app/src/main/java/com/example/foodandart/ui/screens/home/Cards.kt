package com.example.foodandart.ui.screens.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.foodandart.data.firestore.cloud_database.getCardsWithFilters
import com.example.foodandart.data.firestore.storage.getURIFromPath
import com.google.firebase.firestore.QueryDocumentSnapshot
import kotlinx.coroutines.tasks.await

@Composable
fun Cards(vm: HomeViewModel, contentPadding : PaddingValues) {
    LazyColumn (
        modifier = Modifier.padding(16.dp)
    ) {
        items(vm.docs){ document -> FoodAndArtCard(document) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodAndArtCard(document: QueryDocumentSnapshot) {
    val ctx = LocalContext.current
    val images = document.data.get("images") as? List<String>
    val image = getURIFromPath(images?.get(0) ?: "")
    //Log.d("Cards", "${document.data["title"].toString()}, Images : ${images?.get(0)}")
       Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ),
            onClick = {  }

        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        ImageRequest.Builder(ctx)
                            .data(image)
                            .crossfade(true)
                            .build(),
                        "Captured image",
                        contentScale = ContentScale.Fit,
                        //colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondary),
                        modifier = Modifier.fillMaxWidth()
                    )
                    var tint by remember { mutableStateOf(Color.LightGray)}
                    IconButton(
                        onClick = { if (tint == Color.LightGray) tint = Color.Yellow else tint = Color.LightGray },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .background(Color.Black.copy(alpha = 0.3f), shape = RectangleShape)
                            .size(30.dp),
                    ) {
                        Icon(Icons.Outlined.Star, contentDescription = "Favorites", tint = tint)
                    }
                }
                Spacer(Modifier.size(8.dp))
                Text(
                    document.data["title"].toString(),
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
}

