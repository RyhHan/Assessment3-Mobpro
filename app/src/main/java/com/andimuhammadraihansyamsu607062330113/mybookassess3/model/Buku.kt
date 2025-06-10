package com.andimuhammadraihansyamsu607062330113.mybookassess3.model

import com.squareup.moshi.Json

// Model untuk Buku
data class Buku(
    val id: Int,
    val userId: String,
    val judul: String,
    val penulis: String,
    val tahunTerbit: Int,
    val description: String,
    val status: String,
    val coverUrl: String,
    val createdAt: String,
    val updatedAt: String
)

data class BukuResponse(
    @Json(name = "bukus") val bukus: List<Buku>
)
