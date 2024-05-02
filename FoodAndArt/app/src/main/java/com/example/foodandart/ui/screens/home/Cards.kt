package com.example.foodandart.ui.screens.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.foodandart.data.firestore.storage.getURIFromPath
import com.example.foodandart.ui.FoodAndArtRoute

@Composable
fun Cards(viewModel: HomeViewModel, contentPadding: PaddingValues, navController: NavController) {
    LazyColumn(
        modifier = Modifier.padding(16.dp)
    ) {
        for ((key, value) in viewModel.docs) {
            item { FoodAndArtCard(key, value, viewModel, navController) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodAndArtCard(
    id: String,
    values: Any,
    viewModel: HomeViewModel,
    navController: NavController
) {
    val ctx = LocalContext.current
    Log.d("Cards", "Value $values, id = $id")
    val data = values as? java.util.Map<String, Object>
    Log.d("Cards", "Data ${data.toString()}")
    val images = data?.get("images") as? List<String>
    val image = getURIFromPath(images?.get(0) ?: "")
    //Log.d("Cards", "${document.data["title"].toString()}, Images : ${images?.get(0)}")
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        onClick = { navController.navigate(FoodAndArtRoute.CardDetails.buildRoute(id)) }

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
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .fillMaxSize()
                        .size(200.dp)
                )
                var tint by remember { mutableStateOf(Color.LightGray) }
                tint = if (viewModel.favorites.contains(id)) {
                    Color.Yellow
                } else {
                    Color.LightGray
                }

                IconButton(
                    onClick = {
                        viewModel.addFavorites(id)
                    },
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
            data?.get("title")?.let {
                Text(
                    it.toString(),
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


