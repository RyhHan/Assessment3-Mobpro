package com.andimuhammadraihansyamsu607062330113.mybookassess3.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.andimuhammadraihansyamsu607062330113.mybookassess3.R
import com.andimuhammadraihansyamsu607062330113.mybookassess3.model.Buku

@Composable
fun BukuDialog(
    buku: Buku,
    onDismissRequest: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val context = LocalContext.current
    var showConfirmDialog by remember { mutableStateOf(false) }

    if (showConfirmDialog) {
        // Menampilkan dialog konfirmasi untuk menghapus buku
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = {
                Text(text = "Konfirmasi Penghapusan")
            },
            text = {
                Text(text = "Apakah Anda yakin ingin menghapus buku ini?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Menghapus buku setelah konfirmasi
                        onDeleteClick()
                        showConfirmDialog = false
                    }
                ) {
                    Text("Ya")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showConfirmDialog = false
                    }
                ) {
                    Text("Tidak")
                }
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = "Detail Buku")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(buku.coverUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Cover buku: ${buku.judul}",
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.loading_img),
                    error = painterResource(R.drawable.baseline_broken_image_24),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(MaterialTheme.shapes.medium)
                )
                Text(text = "Judul: ${buku.judul}")
                Text(text = "Penulis: ${buku.penulis}")
                Text(text = "Tahun Terbit: ${buku.tahunTerbit}")
                Text(text = "Deskripsi: ${buku.description}")
                Text(
                    text = "Status: ${buku.status}",
                    color = when (buku.status) {
                        "belum baca" -> Color(0xFFE57373)
                        "sedang baca" -> Color(0xFFFFEB3B)
                        "sudah baca" -> Color(0xFF81C784)
                        else -> Color.Gray
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onEditClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Edit")
            }
        },
        dismissButton = {
            Button(
                onClick = { showConfirmDialog = true }, // Menampilkan konfirmasi penghapusan
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Hapus")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewBukuDialog() {
    BukuDialog(
        buku = Buku(
            id = 1,
            userId = "user123",
            judul = "Belajar Compose",
            penulis = "Andi",
            tahunTerbit = 2023,
            description = "Buku tentang belajar Jetpack Compose.",
            status = "sedang baca",
            coverUrl = "",
            createdAt = "2023-10-01",
            updatedAt = "2023-10-01",
        ),
        onDismissRequest = {},
        onEditClick = {},
        onDeleteClick = {}
    )
}