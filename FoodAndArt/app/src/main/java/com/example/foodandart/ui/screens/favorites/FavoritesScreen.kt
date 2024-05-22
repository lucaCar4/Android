package com.example.foodandart.ui.screens.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
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
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.foodandart.R
import com.example.foodandart.data.firestore.storage.getURIFromPath
import com.example.foodandart.ui.FoodAndArtRoute
import com.example.foodandart.ui.screens.home.showCards
import java.util.Objects

@Composable
fun FavoritesScreen(
    navController: NavController,
    viewModel: FavoritesViewModel
) {
    Column {
        Cards(viewModel = viewModel, navController = navController)
    }
}

@Composable
fun Cards(viewModel: FavoritesViewModel, navController: NavController) {
    LazyColumn(
        modifier = Modifier.padding(16.dp)
    ) {
        val cards = viewModel.cards.filter { viewModel.favorites.contains(it.key) }
        for ((key, value) in cards) {
            item {
                FoodAndArtCard(id = key, data = value, viewModel = viewModel, navController = navController)
            }
        }
    }
}

@Composable
fun FoodAndArtCard(id: String, data: Map<String, Any>, viewModel: FavoritesViewModel, navController: NavController) {
    val ctx = LocalContext.current
    val images = data["images"] as? List<String>
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
                val tint by remember { mutableStateOf(Color.Yellow) }
                IconButton(
                    onClick = {
                        viewModel.removeFavorites(id)
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

