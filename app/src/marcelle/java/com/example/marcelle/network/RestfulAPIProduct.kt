package com.example.marcelle.network

import com.example.marcelle.model.ResponseDataProductItem
import dagger.Provides
import com.example.challengechapter6.ProductApps
import retrofit2.Call
import retrofit2.http.GET
import javax.inject.Singleton

interface RestfulAPIProduct {

    @GET("api/v1/products.json?brand=marcelle")
    fun getAllProduct() : Call<List<ResponseDataProductItem>>
}