package com.example.challengechapter6.network

import com.example.challengechapter6.model.Cart
import com.example.challengechapter6.model.ResponseDataCartItem
import retrofit2.Call
import retrofit2.http.*

interface RestfulAPICart {
    @GET("users/{idu}/cart")
    fun getAllCart(@Path("idu") idu : Int) : Call<List<ResponseDataCartItem>>

    @POST("users/{idu}/cart")
    fun addCart(@Path("idu") idu : Int, @Body request : Cart) : Call<ResponseDataCartItem>

    @DELETE("users/{idu}/cart/{idc}")
    fun deleteCart(@Path("idu") idu : Int, @Path("idc") idc : Int) : Call<Int>
}