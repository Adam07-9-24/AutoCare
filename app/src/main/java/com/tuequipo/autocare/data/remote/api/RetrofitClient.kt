// data/remote/api/RetrofitClient.kt
package com.tuequipo.autocare.data.remote.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://api.api-ninjas.com/v1/"
    private const val API_KEY = "TU_API_KEY" // Reemplazar con una API Key válida

    private val authInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("X-Api-Key", API_KEY)
            .build()
        chain.proceed(request)
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    val carApiService: CarApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CarApiService::class.java)
    }
}
