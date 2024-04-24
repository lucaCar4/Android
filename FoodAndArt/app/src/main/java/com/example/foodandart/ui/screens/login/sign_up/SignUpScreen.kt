package com.notes.app.screens.sign_up

import android.graphics.drawable.Icon
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.foodandart.R
import com.example.foodandart.ui.screens.login.sign_up.SignUpViewModel

var confirmHide by mutableStateOf(true)
var confirmVisibility : VisualTransformation by mutableStateOf(PasswordVisualTransformation())
var passwordVisibility : VisualTransformation by mutableStateOf(PasswordVisualTransformation())
var passwordHide by mutableStateOf(true)

@Composable
fun SignUpScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel
) {
    val email = viewModel.email.collectAsState()
    val password = viewModel.password.collectAsState()
    val confirmPassword = viewModel.confirmPassword.collectAsState()
    val focusManager = LocalFocusManager.current
    val isWrongConfirmPassword = viewModel.isWrong

    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            Icons.Outlined.Image,
            contentDescription = "Auth image",
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp, 4.dp)
        )

        Spacer(modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp))

        OutlinedTextField(
            singleLine = true,
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp, 4.dp),
            value = email.value,
            onValueChange = { viewModel.updateEmail(it) },
            placeholder = { Text(stringResource(R.string.email)) },
            leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = "Email") },
            isError =  viewModel.emailAlreadyUsed || email.value.isEmpty(),
            supportingText = {
                if(email.value.isEmpty()) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(id = R.string.empty_email),
                        textAlign = TextAlign.End,
                    )
                } else if(viewModel.emailAlreadyUsed) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(id = R.string.emailAlreadyUsed),
                        textAlign = TextAlign.End,
                    )
                }
            },
        )

        OutlinedTextField(
            singleLine = true,
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp, 4.dp),
            value = password.value,
            onValueChange = { viewModel.updatePassword(it) },
            placeholder = { Text(stringResource(R.string.password)) },
            leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "Email") },
            trailingIcon = {
                IconButton( onClick = {
                    passwordHide = !passwordHide
                    passwordVisibility = if (passwordHide) {
                        PasswordVisualTransformation()
                    } else {
                        VisualTransformation.None
                    }
                }) {
                   GetIcon(passwordHide)
                }
            },
            visualTransformation = passwordVisibility,
            isError = viewModel.passwordLength || password.value.isEmpty(),
            supportingText = {
                if(password.value.isEmpty()) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(id = R.string.empty_password),
                        textAlign = TextAlign.End,
                    )
                } else if(viewModel.passwordLength) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(id = R.string.passwordTooShort),
                        textAlign = TextAlign.End,
                    )
                }
            },
        )

        OutlinedTextField(
            singleLine = true,
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp, 4.dp),
            value = confirmPassword.value,
            onValueChange = { viewModel.updateConfirmPassword(it) },
            placeholder = { Text(stringResource(R.string.confirm_password)) },
            leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "Email") },
            trailingIcon = {
                IconButton( onClick = {
                    confirmHide = !confirmHide
                    confirmVisibility = if (confirmHide) {
                        PasswordVisualTransformation()
                    } else {
                        VisualTransformation.None
                    }
                }) {
                    GetIcon(confirmHide)
                }
            },
            visualTransformation = confirmVisibility,
            isError = isWrongConfirmPassword || viewModel.passwordLength,
            supportingText = {
                if(isWrongConfirmPassword) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(id = R.string.confirmPasswordWrong),
                        textAlign = TextAlign.End,
                    )
                } else if(viewModel.passwordLength) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(id = R.string.passwordTooShort),
                        textAlign = TextAlign.End,
                    )
                }
            },
        )

        Spacer(modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp))

        Button(
            onClick = {
                focusManager.clearFocus()
                viewModel.onSignUpClick(navController)
                      },
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp, 0.dp)
        ) {
            Text(
                text = stringResource(R.string.sign_up),
                fontSize = 16.sp,
                modifier = modifier.padding(0.dp, 6.dp)
            )
        }
    }
}

@Composable
fun GetIcon(hide : Boolean) {
    return if (hide) {
        Icon(imageVector = Icons.Outlined.VisibilityOff , contentDescription = "Open Eye")
    } else {
        Icon(imageVector = Icons.Outlined.Visibility , contentDescription = "Close Eye")
    }
}
