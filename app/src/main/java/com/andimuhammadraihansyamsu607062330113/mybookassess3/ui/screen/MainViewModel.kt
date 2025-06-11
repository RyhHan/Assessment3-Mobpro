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

    var data = mutableStateOf<List<Buku>>(emptyList())
        private set

    var status = MutableStateFlow(ApiStatus.LOADING)
        private set

    var errorMessage = mutableStateOf<String?>(null)
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

    fun addNewBuku(
        judul: String,
        userId: String,
        penulis: String,
        tahunTerbit: String,
        description: String,
        coverImage: Bitmap?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val coverImagePart = coverImage!!.toMultipartBody()  // Convert Bitmap to Multipart
                val judulPart = judul.toRequestBody("text/plain".toMediaTypeOrNull())
                val penulisPart = penulis.toRequestBody("text/plain".toMediaTypeOrNull())
                val tahunTerbitPart = tahunTerbit.toRequestBody("text/plain".toMediaTypeOrNull())
                val descriptionPart = description.toRequestBody("text/plain".toMediaTypeOrNull())
                val userIdPart = userId.toRequestBody("text/plain".toMediaTypeOrNull())

                val response = BukuApi.service.addBuku(
                    coverImagePart,
                    judulPart,
                    penulisPart,
                    tahunTerbitPart,
                    descriptionPart,
                    userIdPart
                )

                if (response.message == "Buku created successfully") {
                    retrieveData(userId)

                } else {
                    throw Exception("Failed to add book: ${response.message}")
                }

            } catch (e: Exception) {
                errorMessage.value = "Error1: ${e.message}"
                Log.d("MainViewModel", "Failure: ${e.message}")
            }
        }
    }

    fun deleteBook(bookId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = BukuApi.service.deleteBuku(bookId)

                if (response.message == "Buku deleted successfully") {
                    Log.d("MainViewModel", "Book deleted successfully")
                    data.value = data.value.filter { it.id != bookId }
                } else {
                    errorMessage.value = "Failed to delete book"
                }
            } catch (e: Exception) {
                errorMessage.value = "Error: ${e.message}"
                Log.d("MainViewModel", "Failure: ${e.message}")
            }
        }
    }

    private fun Bitmap.toMultipartBody(): MultipartBody.Part {
        val stream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 80, stream)
        val byteArray = stream.toByteArray()
        val requestBody = byteArray.toRequestBody("image/jpg".toMediaTypeOrNull(), 0, byteArray.size)
        return MultipartBody.Part.createFormData("coverUrl", "cover.jpg", requestBody)  // Gunakan "coverUrl" untuk key sesuai dengan nama parameter di backend
    }


    fun clearMessage() {
        errorMessage.value = null
    }
}
