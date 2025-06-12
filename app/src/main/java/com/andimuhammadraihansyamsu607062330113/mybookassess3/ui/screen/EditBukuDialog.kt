package com.andimuhammadraihansyamsu607062330113.mybookassess3.ui.screen

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.andimuhammadraihansyamsu607062330113.mybookassess3.model.Buku

@Composable
fun EditBukuDialog(
    buku: Buku,
    onDismissRequest: () -> Unit,
    onSave: (Int, String, String, String, String, String, Bitmap?) -> Unit
) {
    var judul by remember { mutableStateOf(buku.judul) }
    var penulis by remember { mutableStateOf(buku.penulis) }
    var tahunTerbit by remember { mutableStateOf(buku.tahunTerbit.toString()) }
    var description by remember { mutableStateOf(buku.description) }
    var status by remember { mutableStateOf(buku.status) }
    var selectedImage by remember { mutableStateOf<Bitmap?>(null) }

    val context = LocalContext.current

    val pickImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedImage = getBitmapFromUri(context.contentResolver, it)
        }
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = "Edit Buku") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
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

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Status Buku:")

                    listOf("belum baca", "sedang baca", "sudah baca").forEach { stat ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = status == stat,
                                onClick = { status = stat }
                            )
                            Text(stat, modifier = Modifier.padding(start = 4.dp))
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Button(onClick = { pickImageLauncher.launch("image/*") }) {
                        Text("Pilih Gambar dari Galeri")
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
                    onSave(buku.id, judul, penulis, tahunTerbit, description, status, selectedImage)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            Button(onClick = onDismissRequest) {
                Text("Batal")
            }
        }
    )
}
