package com.example.foodandart.ui.screens.cardDetails

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.foodandart.data.firestore.storage.getURIFromPath

@Composable
fun CardDetailsScreen(navController: NavController, id : String, viewModel: CardDetailsViewModel) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val ctx = LocalContext.current
    viewModel.getDocubentById(id)
    Scaffold (
        floatingActionButton = { FloatingActionButton(
            containerColor = MaterialTheme.colorScheme.primary,
            onClick = {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "Thia is my travel: $id" )
                    type = "text/plain"
                }
                ctx.startActivity(sendIntent)
            }){
            Icon(Icons.Outlined.Share, "Share Trip")
        }
        }
    ) {
            contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (viewModel.document != null) {
                val imagesPath = viewModel.document!!.data?.get("images") as? List<String>
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
                Text(
                    text = viewModel.document!!.data?.get("title").toString(),
                    fontSize = 30.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.padding(4.dp))
                Text("date")
                Spacer(modifier = Modifier.padding(8.dp))
                Text("Description")
            }
        }
    }
}

