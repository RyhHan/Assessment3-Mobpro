package com.andimuhammadraihansyamsu607062330113.mybookassess3.ui.screen

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andimuhammadraihansyamsu607062330113.mybookassess3.model.Buku
import com.andimuhammadraihansyamsu607062330113.mybookassess3.network.ApiStatus
import com.andimuhammadraihansyamsu607062330113.mybookassess3.network.BukuApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

class MainViewModel : ViewModel() {

    var data = mutableStateOf<List<Buku>>(emptyList()) // Store books
        private set

    var status = MutableStateFlow(ApiStatus.LOADING) // Store API request status
        private set

    var errorMessage = mutableStateOf<String?>(null) // Store error message
        private set

    // Function to fetch data from API
    fun retrieveData(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            status.value = ApiStatus.LOADING
            try {
                val response = BukuApi.service.getBukuPublic(userId)
                if (response.bukus.isNotEmpty()) {
                    data.value = response.bukus
                    Log.d("MainViewModel", "Success: ${data.value}")
                    status.value = ApiStatus.SUCCESS
                } else {
                    throw Exception("No books found")
                }
            } catch (e: Exception) {
                Log.d("MainViewModel", "Failure: ${e.message}")
                status.value = ApiStatus.FAILED
                errorMessage.value = "Error fetching books: ${e.message}"
            }
        }
    }

    // Function to save new data (example: animal data)
    fun saveData(userId: String, nama: String, namaLatin: String, bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = BukuApi.service.postHewan(
                    userId,
                    nama.toRequestBody("text/plain".toMediaTypeOrNull()),
                    namaLatin.toRequestBody("text/plain".toMediaTypeOrNull()),
                    bitmap.toMultipartBody()
                )

                if (result.status == "success")
                    retrieveData(userId) // Refresh the list
                else
                    throw Exception(result.message)
            } catch (e: Exception) {
                Log.d("MainViewModel", "Failure: ${e.message}")
                errorMessage.value = "Error: ${e.message}"
            }
        }
    }

    // Extension function to convert bitmap to multipart
    private fun Bitmap.toMultipartBody(): MultipartBody.Part {
        val stream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 80, stream)
        val byteArray = stream.toByteArray()
        val requestBody = byteArray.toRequestBody(
            "image/jpg".toMediaTypeOrNull(), 0, byteArray.size
        )
        return MultipartBody.Part.createFormData("image", "image.jpg", requestBody)
    }

    // Clear error message
    fun clearMessage() {
        errorMessage.value = null
    }
}
