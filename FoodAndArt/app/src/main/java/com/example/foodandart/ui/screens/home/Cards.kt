package com.example.foodandart.ui.screens.home

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.foodandart.data.firestore.storage.getURIFromPath
import com.example.foodandart.ui.FoodAndArtRoute
import com.example.foodandart.R

var showCards = emptyMap<String, Map<String, Any>>()

@Composable
fun Cards(viewModel: HomeViewModel, navController: NavController) {
    filterCards(viewModel)
    position(viewModel)
    Log.d("Cards", "Entro For")
    LazyColumn(
        modifier = Modifier.padding(16.dp)
    ) {
        for ((key, value) in showCards) {
            item { FoodAndArtCard(key, value, viewModel, navController) }
        }
    }
}

@Composable
fun FoodAndArtCard(
    id: String,
    values: Any,
    viewModel: HomeViewModel,
    navController: NavController
) {
    val ctx = LocalContext.current
    val data = values as? Map<String, Any>
    val images = data?.get("images") as? List<String>
    val image = getURIFromPath(images?.get(0) ?: "")
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        onClick = { navController.navigate(FoodAndArtRoute.CardDetails.buildRoute(id)) }

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (image != Uri.EMPTY) {
                    Log.d("Cards", "Per ${data?.get("title")}, ho $image")
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
                } else {
                    Image(
                        Icons.Outlined.Image,
                        "Captured image",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .fillMaxSize()
                            .size(200.dp)
                    )
                }
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
            Spacer(modifier = Modifier.padding(8.dp))
            Text(
                text = data?.get("title").toString(),
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Left,
                modifier = Modifier.padding(8.dp, 0.dp),
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp
            )
            Text(
                text = "${data?.get("price")} ${stringResource(id = R.string.price)}",
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Left,
                modifier = Modifier.padding(8.dp),
                fontSize = 12.sp
            )

        }
    }
}

fun position(viewModel: HomeViewModel) {
    if (viewModel.geoPoint != null && viewModel.position.toBoolean()) {
        showCards = showCards.filter { viewModel.showCardByPosition().contains(it.key) }
    }
}

fun filterCards(viewModel: HomeViewModel) {
    Log.d("MainViewModel", viewModel.cards.size.toString())
    var newCards = mutableMapOf<String, Map<String, Any>>()
    var count = 0
    if (viewModel.restaurants.toBoolean()) {
        newCards.putAll(viewModel.cards.filter { it.value["type"].toString() == "Restaurant" })
        count += 1
    }
    if (viewModel.museums.toBoolean()) {
        newCards.putAll(viewModel.cards.filter { it.value["type"].toString() == "Museum" })
        count += 1
    }
    if (viewModel.packages.toBoolean()) {
        newCards.putAll(viewModel.cards.filter { it.value["type"].toString() == "Package" })
        count += 1
    }
    if (newCards.isEmpty() && count == 0) {
        newCards = viewModel.cards
    }

    showCards = newCards
    Log.d("Cards", "FIne")
}


