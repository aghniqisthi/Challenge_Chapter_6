package com.example.marcelle.network

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

    private  val logging : HttpLoggingInterceptor get(){
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        return httpLoggingInterceptor.apply {
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        }
    }

    private val clint = OkHttpClient.Builder().addInterceptor(logging).build()

    val instance : com.example.marcelle.network.RestfulAPIProduct by lazy {
        val retrofit = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()
        retrofit.create(RestfulAPIProduct::class.java)
    }
}