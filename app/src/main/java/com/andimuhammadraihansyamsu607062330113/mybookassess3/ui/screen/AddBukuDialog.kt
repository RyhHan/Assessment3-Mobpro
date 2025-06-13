package com.andimuhammadraihansyamsu607062330113.mybookassess3.ui.screen

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.andimuhammadraihansyamsu607062330113.mybookassess3.R
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions

@Composable
fun AddBukuDialog(
    onDismissRequest: () -> Unit,
    onSave: (String, String, String, String, String, Bitmap?) -> Unit
) {
    var judul by remember { mutableStateOf("") }
    var penulis by remember { mutableStateOf("") }
    var tahunTerbit by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }
    var selectedImage by remember { mutableStateOf<Bitmap?>(null) }
    var errorMessage by remember { mutableStateOf("") }

    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            selectedImage = getBitmapFromUri(context.contentResolver, result.uriContent!!)
        } else {
            errorMessage = "Gagal memproses gambar"
        }
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = stringResource(id = R.string.tambah_buku_baru)) },
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
                    label = { Text(stringResource(id = R.string.judul_buku)) },
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = penulis,
                    onValueChange = { penulis = it },
                    label = { Text(stringResource(id = R.string.penulis)) },
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = tahunTerbit,
                    onValueChange = { tahunTerbit = it },
                    label = { Text(stringResource(id = R.string.tahun_terbit)) },
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(id = R.string.deskripsi_buku)) },
                    modifier = Modifier.fillMaxWidth()
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(stringResource(id = R.string.status_buku))

                    listOf(
                        stringResource(id = R.string.belum_baca),
                        stringResource(id = R.string.sedang_baca),
                        stringResource(id = R.string.sudah_baca)
                    ).forEach { stat ->
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

                Text(stringResource(id = R.string.pilih_gambar), modifier = Modifier.padding(top = 8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            val options = CropImageContractOptions(
                                null, CropImageOptions(
                                    imageSourceIncludeGallery = true,
                                    imageSourceIncludeCamera = false,
                                    fixAspectRatio = true
                                )
                            )
                            launcher.launch(options)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = MaterialTheme.shapes.medium,
                        elevation = ButtonDefaults.elevatedButtonElevation(8.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(painter = painterResource(id = R.drawable.baseline_image_24), contentDescription = "Gallery")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(stringResource(id = R.string.pilih_gambar_galeri))
                        }
                    }

                    Button(
                        onClick = {
                            val options = CropImageContractOptions(
                                null, CropImageOptions(
                                    imageSourceIncludeGallery = false,
                                    imageSourceIncludeCamera = true,
                                    fixAspectRatio = true
                                )
                            )
                            launcher.launch(options)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        shape = MaterialTheme.shapes.medium,
                        elevation = ButtonDefaults.elevatedButtonElevation(8.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(painter = painterResource(id = R.drawable.baseline_camera_alt_24), contentDescription = "Camera")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(stringResource(id = R.string.ambil_foto_kamera))
                        }
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

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (judul.isNotEmpty() && penulis.isNotEmpty() && tahunTerbit.isNotEmpty() && description.isNotEmpty() && status.isNotEmpty() && selectedImage != null) {
                        onSave(judul, penulis, tahunTerbit, description, status, selectedImage)
                        onDismissRequest()
                    } else {
                        errorMessage = context.getString(R.string.pesan_error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(id = R.string.simpan))
            }
        },
        dismissButton = {
            Button(
                onClick = onDismissRequest,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(id = R.string.batal))
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
