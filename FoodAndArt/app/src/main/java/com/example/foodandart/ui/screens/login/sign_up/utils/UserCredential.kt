package com.example.foodandart.ui.screens.login.sign_up.utils

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.foodandart.R
import com.example.foodandart.ui.screens.login.sign_up.SignUpViewModel
import com.example.foodandart.ui.screens.login.sign_up.GetIcon
import com.example.foodandart.ui.screens.login.sign_up.confirmHide
import com.example.foodandart.ui.screens.login.sign_up.confirmVisibility
import com.example.foodandart.ui.screens.login.sign_up.passwordHide
import com.example.foodandart.ui.screens.login.sign_up.passwordVisibility

@Composable
fun GetCredentialField(viewModel: SignUpViewModel) {
    val email = viewModel.email
    val password = viewModel.password
    val confirmPassword = viewModel.confirmPassword
    val isWrongConfirmPassword = viewModel.isWrong

    OutlinedTextField(
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 4.dp),
        value = email,
        onValueChange = { viewModel.updateEmail(it) },
        placeholder = { Text(stringResource(R.string.email)) },
        leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = "Email") },
        isError =  viewModel.emailAlreadyUsed || email.isEmpty(),
        supportingText = {
            if(email.isEmpty()) {
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 4.dp),
        value = password,
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
        isError = viewModel.passwordLength || password.isEmpty(),
        supportingText = {
            if(password.isEmpty()) {
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 4.dp),
        value = confirmPassword,
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
}


@Composable
fun UserExtraData(viewModel: SignUpViewModel) {
    OutlinedTextField(
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 4.dp),
        value = viewModel.name,
        onValueChange = { viewModel.updateName(it)},
        placeholder = { Text(stringResource(R.string.name)) },
        leadingIcon = { Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "Name") },
        isError =  false,
        supportingText = {},
    )
    OutlinedTextField(
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 4.dp),
        value = viewModel.city,
        onValueChange = { viewModel.updateCity(it) },
        placeholder = { Text(stringResource(R.string.city)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.AddLocation,
                contentDescription = "Email"
            )
        },
    )
}