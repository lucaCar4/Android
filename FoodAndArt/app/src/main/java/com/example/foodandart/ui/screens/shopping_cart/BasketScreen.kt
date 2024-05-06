package com.example.foodandart.ui.screens.shopping_cart

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.foodandart.R
import com.example.foodandart.data.database.BasketElem
import com.example.foodandart.data.firestore.storage.getURIFromPath
import com.example.foodandart.data.models.BasketState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasketScreen(
    navController: NavController,
    viewModel: BasketViewModel
) {
    val state by viewModel.state.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.title),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier.clickable { navController.navigateUp() }
                    )
                }
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxWidth()
        ) {
            BasketList(viewModel = viewModel, state = state)
            Spacer(modifier = Modifier.padding(8.dp))
            Button(
                enabled = state.basket.isNotEmpty(),
                onClick = {
                    viewModel.buy(state)
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = stringResource(id = R.string.buy))
            }
        }
    }
}


@Composable
fun BasketList(viewModel: BasketViewModel, state: BasketState) {
    val ctx = LocalContext.current
    LazyColumn {
        items(state.basket) { elem ->
            val card = viewModel.selectedCards[elem.card]
            val images = card?.get("images") as? List<String>
            val image = getURIFromPath(images?.get(0) ?: "")
            ListItem(
                headlineContent = { Text(card?.get("title").toString()) },
                supportingContent = { BasketElem(viewModel = viewModel, element = elem) },
                trailingContent = {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.clickable(onClick = {
                            viewModel.actions.removeElem(elem)
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
fun BasketElem(viewModel: BasketViewModel, element: BasketElem) {
    val limit = viewModel.limits[element.card]?.get(element.date)
    if (limit != null) {
        if (element.quantity > limit) {
            val updatedElement = element.copy(quantity = limit)
            viewModel.actions.updateElem(updatedElement)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        )
        {
            Text(text = element.date)
            Spacer(modifier = Modifier.padding(4.dp))
            IconButton(
                onClick = {
                    if (element.quantity - 1 > 0) {
                        val updatedElement = element.copy(quantity = element.quantity - 1)
                        viewModel.actions.updateElem(updatedElement)
                    } else {
                        viewModel.actions.removeElem(element)
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
            Text(text = element.quantity.toString())
            Spacer(modifier = Modifier.padding(6.dp))
            IconButton(
                onClick = {
                    if (element.quantity + 1 <= limit) {
                        val updatedElement = element.copy(quantity = element.quantity + 1)
                        viewModel.actions.updateElem(updatedElement)
                    } else {
                        val updatedElement = element.copy(quantity = limit)
                        viewModel.actions.updateElem(updatedElement)
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
}

