package com.example.foodandart.ui.screens.shopping_cart

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.foodandart.R
import com.example.foodandart.data.database.Todo
import com.example.foodandart.data.firestore.cloud_database.getCardById
import com.example.foodandart.data.firestore.storage.getURIFromPath
import com.example.foodandart.data.firestore.storage.getUserImage
import com.example.foodandart.ui.screens.cardDetails.CardDetailsViewModel


@Composable
fun ShoppingCartScreen(
    navController: NavController,
    viewModel: ShoppingCartViewModel
) {
    val ctx = LocalContext.current
    val state = viewModel.state
    LazyColumn {
        Log.d("Baskett", state.value.todos.size.toString())
        items(state.value.todos) { elem ->
            val card = viewModel.selectedCards[elem.card]
            val images = card?.get("images") as? List<String>
            val image = getURIFromPath(images?.get(0) ?: "")
            ListItem(
                headlineContent = { Text(card?.get("title").toString()) },
                supportingContent = { BuyCard(viewModel = viewModel, element = elem) },
                trailingContent = {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.clickable(onClick = {

                        }
                        )
                    )
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
fun BuyCard(viewModel: ShoppingCartViewModel, element: Todo) {
    var quantity by remember { mutableIntStateOf(element.quantity) }
    Row(
        verticalAlignment = Alignment.CenterVertically
    )
    {
        Text(text = element.date)
        Spacer(modifier = Modifier.padding(4.dp))
        IconButton(
            onClick = {
                if (element.quantity - 1 >= 0) {
                    //element.quantity--
                    quantity = element.quantity
                    viewModel.addToBasket(element)
                }
            },
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "Arrow Back",
                modifier = Modifier.border(1.dp, Color.Black, CircleShape)
            )
        }
        Spacer(modifier = Modifier.padding(6.dp))
        Text(text = quantity.toString())
        Spacer(modifier = Modifier.padding(6.dp))
        IconButton(
            onClick = {
                if (element.quantity + 1 <= viewModel.limit) {
                    //element.quantity++
                    quantity = element.quantity
                    viewModel.addToBasket(element)
                }
            },
            modifier = Modifier.clip(CircleShape)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                contentDescription = "Arrow Forward",
                modifier = Modifier.border(1.dp, Color.Black, CircleShape)
            )
        }
    }
}
