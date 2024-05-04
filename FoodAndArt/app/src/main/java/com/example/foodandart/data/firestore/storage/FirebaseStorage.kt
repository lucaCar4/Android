package com.example.foodandart.data.firestore.storage

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.foodandart.accountService
import com.example.foodandart.data.firestore.cloud_database.getCardById
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await
import java.io.File

@Composable
fun getURIFromPath(path: String) : Uri {
    val storage = Firebase.storage
    val storageRef = storage.reference
    val spaceRef = storageRef.child(path)
    var uriRef by remember { mutableStateOf(Uri.EMPTY) }
    spaceRef.downloadUrl.addOnSuccessListener { uri ->
        uriRef = uri
    }.addOnFailureListener { exception ->
    }
    return uriRef
}

suspend fun updateUserImage(userId : String, image : Uri) {
    Log.d("Storage", userId)
    val storage = Firebase.storage
    val storageRef = storage.reference
    val riversRef = storageRef.child("$userId/profile_image.jpg")
    riversRef.putFile(image).await()
}

suspend fun getUserImage(): Uri? {
    val storage = Firebase.storage
    val storageRef = storage.reference
    var uriRef = Uri.EMPTY
    val spaceRef = storageRef.child("${accountService.currentUserId}/profile_image.jpg")
    spaceRef.downloadUrl.addOnSuccessListener { uri ->
        uriRef = uri
    }.await()
    return uriRef
}
