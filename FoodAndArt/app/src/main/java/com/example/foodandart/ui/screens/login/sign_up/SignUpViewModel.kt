package com.example.foodandart.ui.screens.login.sign_up

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.foodandart.service.AccountService
import com.example.foodandart.ui.FoodAndArtRoute
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.foodandart.data.firestore.cloud_database.addUserDocument
import com.example.foodandart.data.firestore.storage.updateUserImage
import com.google.firebase.firestore.GeoPoint

class SignUpViewModel( private val accountService: AccountService) : ViewModel() {

    private val length = 6

    var city by mutableStateOf("")
    private var cityGeoPoint by mutableStateOf(GeoPoint(0.0,0.0))
    var existDestination by mutableStateOf(false)

    var image: Uri by mutableStateOf(Uri.EMPTY)

    var name by mutableStateOf("")

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var isWrong by  mutableStateOf(false)
    var emailAlreadyUsed by  mutableStateOf(false)
    var passwordLength by  mutableStateOf(false)

    fun updateEmail(newEmail: String) {
        email = newEmail
    }

    fun updateName(newName: String) {
        name = newName
    }

    fun updateCity(newCity: String) {
        city = newCity
    }

    fun updatePassword(newPassword: String) {
        password = newPassword
    }

    fun updateConfirmPassword(newConfirmPassword: String) {
        confirmPassword = newConfirmPassword
    }

    private fun validate(exception : String) {
        Log.d("Login", exception)
        if (exception.contains("email")) {
            emailAlreadyUsed = true
            passwordLength = password.length < length
        } else if (exception.contains("password")) {
            passwordLength = true
            emailAlreadyUsed = false
        }
    }
    fun onSignUpClick(navController: NavController) {
        viewModelScope.launch {
            isWrong = password != confirmPassword
            var result = ""
            if (!isWrong && existDestination ) {
                result = accountService.signUp(email, password)
                if (result == "") {
                    updateUserImage(accountService.currentUserId, image)
                    addUserDocument(name, cityGeoPoint, city)
                    navController.navigate(FoodAndArtRoute.Home.route) {
                        navController.popBackStack()
                    }
                }
            } else {
                validate(result)
            }
        }
    }

}