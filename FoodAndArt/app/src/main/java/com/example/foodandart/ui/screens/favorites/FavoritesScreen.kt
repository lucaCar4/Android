package com.example.foodandart.ui.screens.favorites

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


@Composable
fun FavoritesScreen(
    navController: NavController
) {
        var expanded by remember { mutableStateOf(false) }
        var selectedItem by remember { mutableStateOf("Select Item") }

        Box {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .wrapContentSize(Alignment.TopEnd)
            ) {
                Text("Selected item: $selectedItem")

                Button(onClick = { expanded = true }) {
                    Text(selectedItem)
                }

                if (expanded) {
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.align(Alignment.End),
                        offset = DpOffset(16.dp, 0.dp),
                    ) {
                        DropdownMenuItem(
                            text = { Text("Settings") },
                            onClick = {
                            selectedItem = "Item 1"
                            expanded = false
                        })
                        DropdownMenuItem(
                            text = { Text("Settings") },
                            onClick = {
                            selectedItem = "Item 2"
                            expanded = false
                        })
                        DropdownMenuItem(
                            text = { Text("Settings") },
                            onClick = {
                            selectedItem = "Item 3"
                            expanded = false
                        })
                    }
                }
            }
        }
    }

