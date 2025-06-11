package com.andimuhammadraihansyamsu607062330113.mybookassess3.network

import com.andimuhammadraihansyamsu607062330113.mybookassess3.model.BukuResponse
import com.andimuhammadraihansyamsu607062330113.mybookassess3.model.OpStatus
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

private const val BASE_URL = "http://103.175.219.150:3006/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface BukuApiService {
    @GET("buku")
    suspend fun getBukuPublic(
        @Query("userId") userId: String
    ): BukuResponse

    @Multipart
    @POST("buku")
    suspend fun addBuku(
        @Part coverImage: MultipartBody.Part, // Menambahkan gambar
        @Part("judul") judul: RequestBody,
        @Part("penulis") penulis: RequestBody,
        @Part("tahunTerbit") tahunTerbit: RequestBody,
        @Part("description") description: RequestBody,
        @Part("userId") userId: RequestBody
    ): OpStatus

    @DELETE("buku/{id}")
    suspend fun deleteBuku(@Path("id") id: Int): OpStatus

}

object BukuApi {
    val service: BukuApiService by lazy {
        retrofit.create(BukuApiService::class.java)
    }
}

enum class ApiStatus { LOADING, SUCCESS, FAILED }