package com.example.challengechapter6.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClientCart {
    const val BASE_URL = "https://63402098d1fcddf69cb1e5f2.mockapi.io/api/user/"

    val instance : RestfulAPICart by lazy {
        val retrofit = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()
        retrofit.create(RestfulAPICart::class.java)
    }
}