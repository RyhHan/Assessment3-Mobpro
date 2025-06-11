package com.andimuhammadraihansyamsu607062330113.mybookassess3.ui.screen

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import java.io.File

@Composable
fun AddBukuDialog(
    onDismissRequest: () -> Unit,
    onSave: (String, String, String, String, Bitmap?) -> Unit
) {
    var judul by remember { mutableStateOf("") }
    var penulis by remember { mutableStateOf("") }
    var tahunTerbit by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedImage by remember { mutableStateOf<Bitmap?>(null) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current
    val tempFile = remember { File(context.cacheDir, "temp_image.jpg") }
    imageUri = Uri.fromFile(tempFile)

    val pickImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedImage = getBitmapFromUri(context.contentResolver, it)
        }
    }

    val takePictureLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            val tempUri = imageUri
            if (tempUri != null) {
                selectedImage = getBitmapFromUri(context.contentResolver, tempUri)
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = "Tambah Buku Baru") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = judul,
                    onValueChange = { judul = it },
                    label = { Text("Judul Buku") },
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = penulis,
                    onValueChange = { penulis = it },
                    label = { Text("Penulis") },
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = tahunTerbit,
                    onValueChange = { tahunTerbit = it },
                    label = { Text("Tahun Terbit") },
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Deskripsi Buku") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Button(onClick = {
                        pickImageLauncher.launch("image/*")
                    }) {
                        Text("Pilih Gambar dari Galeri")
                    }

                    Button(onClick = {
                        takePictureLauncher.launch(imageUri!!)
                    }) {
                        Text("Ambil Foto dari Kamera")
                    }
                }

                selectedImage?.let {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(it)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Cover Buku",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .padding(top = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(judul, penulis, tahunTerbit, description, selectedImage)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismissRequest,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Batal")

            }
        }
    )
}

fun getBitmapFromUri(contentResolver: ContentResolver, uri: Uri): Bitmap? {
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
        @Suppress("DEPRECATION")
        MediaStore.Images.Media.getBitmap(contentResolver, uri)
    } else {
        val source = ImageDecoder.createSource(contentResolver, uri)
        ImageDecoder.decodeBitmap(source)
    }
}