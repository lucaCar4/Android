package com.example.foodandart.data.firestore.storage

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.foodandart.accountService
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await

@Composable
fun getURIFromPath(path: String): Uri {
    val storage = Firebase.storage
    val storageRef = storage.reference
    var uriRef by remember { mutableStateOf(Uri.EMPTY) }
    if (path != "") {
        val spaceRef = storageRef.child(path)
        spaceRef.downloadUrl.addOnSuccessListener { uri ->
            uriRef = uri
        }.addOnFailureListener {_ ->
        }
    } else {
        return Uri.EMPTY
    }
    return uriRef
}

suspend fun updateUserImage(userId: String, image: Uri) {
    Log.d("Storage", userId)
    val storage = Firebase.storage
    val storageRef = storage.reference
    val riversRef = storageRef.child("$userId/profile_image.jpg")
    riversRef.putFile(image).await()
    Log.d("Imageee", "Caricata")
}

suspend fun getUserImage(): Uri? {
    val storage = Firebase.storage
    val storageRef = storage.reference
    var uriRef = Uri.EMPTY
    if (accountService.currentUserId != "") {
        val spaceRef = storageRef.child("${accountService.currentUserId}/profile_image.jpg")
        spaceRef.downloadUrl
            .addOnSuccessListener { uri ->
                uriRef = uri
            }
            .addOnFailureListener { exception ->
                uriRef = Uri.EMPTY
            }.await()
    }
    return uriRef
}

suspend fun removeUser() {
    val storage = Firebase.storage
    val storageRef = storage.reference
    storageRef.child("${accountService.currentUserId}/profile_image.jpg").delete().await()
}

