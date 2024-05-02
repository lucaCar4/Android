package com.example.foodandart.ui.screens.home

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.foodandart.R
import com.example.foodandart.ui.screens.home.position.LocationService
import com.example.foodandart.ui.screens.login.sign_up.utils.PermissionStatus
import com.example.foodandart.ui.screens.login.sign_up.utils.rememberPermission

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
            Cards(viewModel, contentPadding, navController)
        }
    }

}