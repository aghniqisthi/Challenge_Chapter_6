package com.example.challengechapter6.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClientUser {
    const val BASE_URL = "https://63402098d1fcddf69cb1e5f2.mockapi.io/api/user/"

    private  val logging : HttpLoggingInterceptor get(){
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        return httpLoggingInterceptor.apply {
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        }
    }

    private val clint = OkHttpClient.Builder().addInterceptor(logging).build()

    val instance : RestfulAPIUser by lazy {
        val retrofit = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()
        retrofit.create(RestfulAPIUser::class.java)
    }
}

