package com.example.foodandart.ui.screens.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.foodandart.data.firestore.cloud_database.getCardsWithFilters

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel
) {
    var isActive by remember{ mutableStateOf(false) }
    Scaffold (
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
                query =  "",//text showed on SearchBar
                onQueryChange = {}, //update the value of searchText
                onSearch = { }, //the callback to be invoked when the input service triggers the ImeAction.Search action
                active = isActive, //whether the user is searching or not
                onActiveChange = { isActive= !isActive }, //the callback to be invoked when this search bar's active state is changed
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 3.dp),
                placeholder = { Text(text = "Search")}
            ) {
                LazyColumn {

                }
            }
        }
    ) { contentPadding ->
        Column {
            FilterChips(contentPadding = contentPadding, viewModel = viewModel)
            Cards(viewModel, contentPadding)
        }
    }

}

@Composable
fun FilterChips(contentPadding : PaddingValues, viewModel: HomeViewModel) {

    val chips = listOf("Restaurants", "Museums", "Packages", "Position")

    LazyRow(modifier = Modifier
        .padding(contentPadding)
        .padding(16.dp, 0.dp),)
    {
        items(chips) { name ->
            Chip(name = name, viewModel)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Chip(name : String, vm: HomeViewModel) {
    var state = vm.getVar(name).toBoolean()
    FilterChip(
        selected = state,
        onClick = { vm.setChip ((!state).toString(), name) },
        label = { Text(text = name)},
        leadingIcon = if (state) {
            {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Done icon",
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        } else {
            null
        },
        modifier = Modifier.padding(8.dp, 0.dp)
    )
}