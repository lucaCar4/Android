package com.example.foodandart.ui.screens.login.sign_up

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.foodandart.R
import com.example.foodandart.ui.screens.login.sign_up.utils.Camera
import com.example.foodandart.ui.screens.login.sign_up.utils.GetCredentialField
import com.example.foodandart.ui.screens.login.sign_up.utils.UserExtraData

var confirmHide by mutableStateOf(true)
var confirmVisibility: VisualTransformation by mutableStateOf(PasswordVisualTransformation())
var passwordVisibility: VisualTransformation by mutableStateOf(PasswordVisualTransformation())
var passwordHide by mutableStateOf(true)

@Composable
fun SignUpScreen(
    navController: NavController,
    viewModel: SignUpViewModel
) {
    if (viewModel.isSignUp) {
        SignUp(viewModel = viewModel, navController = navController)
    } else {
        WaitForSignUp(viewModel = viewModel)
    }
    
}
@Composable
fun WaitForSignUp(viewModel: SignUpViewModel) {
    Column(
        modifier =
        Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(color = MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground)
    }
}
@Composable
fun SignUp(viewModel: SignUpViewModel, navController: NavController) {
    val snackbarHostState = remember { SnackbarHostState() }
    val focus = LocalFocusManager.current
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Camera(snackbarHostState = snackbarHostState, viewModel)
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            )
            UserExtraData(viewModel = viewModel)
            GetCredentialField(viewModel = viewModel)
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            )
            Button(
                onClick = {
                    focus.clearFocus(true)
                    viewModel.onSignUpClick(navController)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 0.dp)
            ) {
                Text(
                    text = stringResource(R.string.sign_up),
                    fontSize = 16.sp,
                    modifier = Modifier.padding(0.dp, 6.dp)
                )
            }
        }
    }
}

@Composable
fun GetIcon(hide: Boolean) {
    return if (hide) {
        Icon(imageVector = Icons.Outlined.VisibilityOff, contentDescription = "Open Eye")
    } else {
        Icon(imageVector = Icons.Outlined.Visibility, contentDescription = "Close Eye")
    }
}


