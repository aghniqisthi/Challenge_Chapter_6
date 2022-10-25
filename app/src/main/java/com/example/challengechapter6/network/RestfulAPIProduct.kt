package com.example.challengechapter6.network

import com.example.challengechapter6.model.ResponseDataProductItem
import retrofit2.Call
import retrofit2.http.GET

interface RestfulAPIProduct {
    @GET("api/v1/products.json?brand=maybelline")
    fun getAllProduct() : Call<List<ResponseDataProductItem>>
}