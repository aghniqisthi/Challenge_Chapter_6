package com.example.challengechapter6.network

import com.example.challengechapter6.model.Cart
import com.example.challengechapter6.model.ResponseDataCartItem
import retrofit2.Call
import retrofit2.http.*

interface RestfulAPICart {
    @GET("cart")
    fun getAllCart() : Call<List<ResponseDataCartItem>>

    @POST("cart")
    fun addCart(@Body request : Cart) : Call<ResponseDataCartItem>

    @DELETE("cart/{id}")
    fun deleteCart(@Path("id") id : Int) : Call<Int>
}