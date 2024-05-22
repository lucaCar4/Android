package com.example.foodandart.ui.screens.cardDetails

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.foodandart.R
import com.example.foodandart.data.database.BasketElem
import com.example.foodandart.data.firestore.storage.getURIFromPath
import com.example.foodandart.data.models.BasketState
import com.example.foodandart.ui.FoodAndArtRoute
import com.example.foodandart.ui.screens.cardDetails.map.Map
import com.example.foodandart.ui.screens.login.sign_up.utils.imageUri


val snackbarHostState = SnackbarHostState()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardDetailsScreen(navController: NavController, id: String, viewModel: CardDetailsViewModel) {
    val ctx = LocalContext.current
    ShowNotification(viewModel = viewModel, navController = navController)
    viewModel.setDocument(id)
    val imagesPath = viewModel.document?.get("images") as? List<String>
    val firstUri = imagesPath?.let { getURIFromPath(path = it.first()) }
    if (viewModel.document != null) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            floatingActionButton = {
                FloatingActionButton(
                    containerColor = MaterialTheme.colorScheme.primary,
                    onClick = {
                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            putExtra(Intent.EXTRA_TEXT, "https://foodandart-d0115.web.app/card/$id")
                            type = "text/plain"
                        }
                        ctx.startActivity(Intent.createChooser(sendIntent, "Condividi il link tramite:"))
                    }) {
                    Icon(Icons.Outlined.Share, "Share Trip")
                }
            },
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = viewModel.document?.get("title").toString(),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    },
                    navigationIcon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.clickable { navController.navigateUp() })
                    }
                )
            }

        ) { contentPadding ->
            Column(
                modifier = Modifier
                    .padding(contentPadding)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (imagesPath != null) {
                    LazyRow {
                        items(imagesPath) {
                            val image = getURIFromPath(path = it)
                            AsyncImage(
                                ImageRequest.Builder(ctx)
                                    .data(image)
                                    .crossfade(true)
                                    .build(),
                                "Captured image",
                                contentScale = ContentScale.FillBounds,
                                modifier = Modifier
                                    .padding(6.dp)
                                    .size(200.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.padding(4.dp))
                Text(text = stringResource(id = R.string.description), fontSize = 25.sp)
                Text(
                    text = viewModel.document?.get("description").toString(),
                    fontSize = 18.sp,
                    textAlign = TextAlign.Left,
                    modifier = Modifier.padding(16.dp)
                )
                HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
                Spacer(modifier = Modifier.padding(4.dp))
                DateChooser(viewModel)
                Spacer(modifier = Modifier.padding(4.dp))
                HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
                Spacer(modifier = Modifier.padding(4.dp))
                BuyCard(viewModel = viewModel)
                Spacer(modifier = Modifier.padding(4.dp))
                HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
                Spacer(modifier = Modifier.padding(4.dp))
                Button(onClick = { viewModel.showMap = viewModel.showMap.not() }) {
                    Icon(imageVector = Icons.Outlined.Map, contentDescription = "Show Map")
                    Text(text = stringResource(id = R.string.show_map))
                }
                Map(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateChooser(viewModel: CardDetailsViewModel) {
    var expanded by remember { mutableStateOf(false) }
    if (!viewModel.dates.isEmpty()) {
        Log.d("Datee", viewModel.selectedDate)
        viewModel.getLimit()
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            OutlinedTextField(
                readOnly = true,
                value = viewModel.selectedDate,
                onValueChange = { },
                label = { Text(stringResource(id = R.string.date)) },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expanded
                    )
                },
                modifier = Modifier.menuAnchor()

            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                }
            ) {
                viewModel.availableDates.forEach { (key, value) ->
                    DropdownMenuItem(
                        text = { Text(text = value["date"].toString()) },
                        onClick = {
                            viewModel.selectedDate = value["date"].toString()
                            viewModel.getLimit()
                            Log.d("Datee", viewModel.selectedDate)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun BuyCard(viewModel: CardDetailsViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    Row(
        verticalAlignment = Alignment.CenterVertically
    )
    {
        IconButton(
            onClick = {
                if (viewModel.quantity - 1 >= 0) {
                    viewModel.quantity--
                }
            },
            colors = IconButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = IconButtonDefaults.iconButtonColors().disabledContainerColor,
                disabledContentColor = IconButtonDefaults.iconButtonColors().disabledContentColor,
                contentColor = IconButtonDefaults.iconButtonColors().containerColor
            )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "Arrow Back",
                tint = Color.Black
            )
        }
        Spacer(modifier = Modifier.padding(6.dp))
        Text(text = viewModel.quantity.toString())
        Spacer(modifier = Modifier.padding(6.dp))
        IconButton(
            onClick = {
                if (viewModel.quantity + 1 <= viewModel.limit) {
                    viewModel.quantity++
                }
            },
            modifier = Modifier.clip(CircleShape),
            colors = IconButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = IconButtonDefaults.iconButtonColors().disabledContainerColor,
                disabledContentColor = IconButtonDefaults.iconButtonColors().disabledContentColor,
                contentColor = IconButtonDefaults.iconButtonColors().containerColor
            )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                contentDescription = "Arrow Forward",
                tint = Color.Black
            )
        }
        Spacer(modifier = Modifier.padding(6.dp))
        Button(onClick = {
            if (viewModel.document != null) {
                checkExistingCard(viewModel = viewModel, state = state)
                viewModel.actions.addElem(
                    BasketElem(
                        card = viewModel.id,
                        date = viewModel.selectedDate,
                        quantity = viewModel.quantity
                    )
                )
            }
        }, enabled = viewModel.quantity > 0) {
            Text(text = stringResource(id = R.string.add_to_basket))
        }
        Spacer(modifier = Modifier.padding(6.dp))
        Text(
            text = (viewModel.document?.get("price").toString()
                .toInt() * viewModel.quantity).toString()
        )
    }
}

private fun checkExistingCard(state: BasketState, viewModel: CardDetailsViewModel) {
    val elems = mutableListOf<BasketElem>()
    state.basket.forEach { elem ->
        if (viewModel.document != null) {
            if (elem.card == viewModel.id && viewModel.selectedDate == elem.date) {
                elems.add(elem)
            }
        }
    }
    elems.forEach {
        viewModel.actions.removeElem(it)
    }
}

@Composable
fun ShowNotification(viewModel: CardDetailsViewModel, navController: NavController) {
    if (viewModel.addedElem) {
        val ctx = LocalContext.current
        LaunchedEffect(snackbarHostState) {
            val res = snackbarHostState.showSnackbar(
                ctx.getString(R.string.add_basket),
                ctx.getString(R.string.go_to_basket),
                duration = SnackbarDuration.Long
            )
            if (res == SnackbarResult.ActionPerformed) {
                navController.navigate(FoodAndArtRoute.Basket.route)
                viewModel.addedElem = false
            }
        }
    }
}

