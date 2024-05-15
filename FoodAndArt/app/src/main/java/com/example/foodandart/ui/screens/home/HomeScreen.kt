package com.example.foodandart.ui.screens.home


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.foodandart.notificationService
import com.example.foodandart.ui.FoodAndArtRoute
import com.example.foodandart.ui.screens.home.position.LocationService

val snackbarHostState = SnackbarHostState()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel,
) {
    var isActive by remember { mutableStateOf(false) }
    val locationService = LocationService(LocalContext.current, viewModel)
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            SearchBar(
                leadingIcon = { Icon(Icons.Outlined.Search, "Search") },
                trailingIcon = {
                    if (isActive) {
                        Icon(
                            imageVector = Icons.Outlined.Clear, "Close",
                            modifier = Modifier.clickable {
                                isActive = false
                            }
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.ShoppingCart, "Basket",
                            modifier = Modifier.clickable {
                                navController.navigate(FoodAndArtRoute.Basket.route)
                            }
                        )
                    }
                },
                query = "",//text showed on SearchBar
                onQueryChange = {}, //update the value of searchText
                onSearch = { }, //the callback to be invoked when the input service triggers the ImeAction.Search action
                active = isActive, //whether the user is searching or not
                onActiveChange = {
                    isActive = !isActive
                }, //the callback to be invoked when this search bar's active state is changed
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 3.dp),
                placeholder = { Text(text = "Search") }
            ) {
                LazyColumn {

                }
            }
        }
    ) { contentPadding ->
        Column {
            FilterChips(contentPadding = contentPadding, viewModel = viewModel, locationService)
            Cards(viewModel, navController)
        }
    }

}