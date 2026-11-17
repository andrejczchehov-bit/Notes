package com.example.mynotes.data

import com.example.mynotes.data.security.RequestSigningInterceptor
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Клиент Retrofit для Spaceflight News API v4 с настроенными таймаутами, логированием, CertificatePinner и HMAC-подписью (Лабораторная работа №10 & №15).
 */
object RetrofitClient {
    private const val BASE_URL = "https://api.spaceflightnewsapi.net/v4/"

    val api: PostsApi by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val certificatePinner = CertificatePinner.Builder()
            .add("api.spaceflightnewsapi.net", "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=", "sha256/BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB=")
            .build()

        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .addInterceptor(RequestSigningInterceptor())
            .certificatePinner(certificatePinner)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PostsApi::class.java)
    }
}
