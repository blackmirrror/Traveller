package ru.blackmirrror.traveller.features.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await

object ImageLoader {

    suspend fun loadImage(imagePath: String): Bitmap? {
        return try {
            val storageRef = Firebase.storage.reference
            val imageRef = storageRef.child(imagePath)
            val byteArray = imageRef.getBytes(MAX_DOWNLOAD_SIZE.toLong()).await()

            if (byteArray != null) {
                val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                bitmap
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private const val MAX_DOWNLOAD_SIZE = 48 * 1024 * 1024
}