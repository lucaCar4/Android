package com.example.foodandart.ui.screens.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.PersonRemove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.foodandart.R
import com.example.foodandart.data.models.Theme

var expandMenu by mutableStateOf(false)
var offsetX by mutableStateOf(0.dp)
var parentWidth by mutableIntStateOf(0)

var showExitAppDialog by mutableStateOf(false)
var showRemoveAccDialog by mutableStateOf(false)

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel
) {
    LaunchedEffect(Unit) { viewModel.initialize(navController) }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .onPlaced { it ->
                parentWidth = it.size.width
            }
    )
    {
        TopAppBar(
            title = { Text(stringResource(R.string.app_name)) },
            actions = {
                IconButton(onClick = { expandMenu = true }) {
                    Icon(Icons.Default.MoreVert, "Menu")
                }
            }
        )
        Menu(viewModel)
        SignOutAlert(viewModel)
        DeleteAccountAlert(viewModel)
    }
}

@Composable
fun Menu(viewModel: ProfileViewModel) {
    val density = LocalDensity.current
    DropdownMenu(
        expanded = expandMenu,
        onDismissRequest = { expandMenu = false },
        modifier = Modifier.onPlaced {
            val popUpWidthPx =
                parentWidth - it.size.width

            offsetX = with(density) {
                popUpWidthPx.toDp()
            }
        },
        offset = DpOffset(offsetX, 0.dp),
    ) {
        Theme.entries.forEach { theme ->
            DropdownMenuItem(
                text = {
                    Text(
                        stringResource(
                            when (theme) {
                                Theme.Light -> R.string.theme_light
                                Theme.Dark -> R.string.theme_dark
                                Theme.System -> R.string.theme_system
                            }
                        )
                    )
                },
                trailingIcon = {
                    if (theme == viewModel.state) {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = "Selected Theme"
                        )
                    }
                },
                onClick = {
                    viewModel.changeTheme(theme)
                })
        }
        HorizontalDivider()
        DropdownMenuItem(
            text = { Text(text = stringResource(id = R.string.sign_out), color = Color.Red )},
            leadingIcon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ExitToApp,
                    contentDescription = "Exit",
                    tint = Color.Red
                )
            },
            onClick = {
                expandMenu = false
                showExitAppDialog = true
            })
        HorizontalDivider()
        DropdownMenuItem(
            text = { Text(stringResource(id = R.string.delete_account)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.PersonRemove,
                    contentDescription = "Delete Account"
                )
            },
            onClick = {
                expandMenu = false
                showRemoveAccDialog = true
            })
    }
}

@Composable
fun DeleteAccountAlert(viewModel: ProfileViewModel) {
    if (showRemoveAccDialog) {
        AlertDialog(
            title = { Text(stringResource(R.string.delete_account_title)) },
            text = { Text(stringResource(R.string.delete_account_description)) },
            dismissButton = {
                Button(onClick = { showRemoveAccDialog = false }) {
                    Text(text = stringResource(R.string.cancel))
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.onDeleteAccountClick()
                    showRemoveAccDialog = false
                }) {
                    Text(text = stringResource(R.string.delete_account))
                }
            },
            onDismissRequest = { showRemoveAccDialog = false }
        )
    }
}

@Composable
fun SignOutAlert(viewModel: ProfileViewModel) {
    if (showExitAppDialog) {

        AlertDialog(
            title = { Text(stringResource(R.string.sign_out_title)) },
            text = { Text(stringResource(R.string.sign_out_description)) },
            dismissButton = {
                Button(onClick = { showExitAppDialog = false }) {
                    Text(text = stringResource(R.string.cancel))
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.onSignOutClick()
                    showExitAppDialog = false
                }) {
                    Text(text = stringResource(R.string.sign_out))
                }
            },
            onDismissRequest = { showExitAppDialog = false }
        )
    }
}



