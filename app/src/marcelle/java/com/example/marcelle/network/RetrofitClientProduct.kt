package com.example.marcelle.network

import com.example.challengechapter6.network.RestfulAPICart
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

object RetrofitClientProduct {
    const val BASE_URL = "https://makeup-api.herokuapp.com/"

    val instance : RestfulAPIProduct by lazy {
        val retrofit = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()
        retrofit.create(RestfulAPIProduct::class.java)
    }

}