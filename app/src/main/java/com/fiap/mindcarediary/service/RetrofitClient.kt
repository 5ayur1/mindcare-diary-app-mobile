package com.fiap.mindcarediary.service

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://10.0.2.2:8080/"

    private lateinit var tokenManager: TokenManager

    private lateinit var retrofit: Retrofit

    lateinit var api: ApiService

    fun initialize(context: Context) {

        tokenManager = TokenManager(context.applicationContext)

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(
                AuthInterceptor(tokenManager)
            )
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()

        api = retrofit.create(ApiService::class.java)
    }
}