package com.example.marcelle.network

import com.example.marcelle.model.EditUser
import com.example.marcelle.model.ResponseDataUserItem
import com.example.marcelle.model.User
import retrofit2.Call
import retrofit2.http.*

interface RestfulAPIUser {
    @GET("users")
    fun getAllUser() : Call<List<ResponseDataUserItem>>

    @POST("users")
    fun addUser(@Body request : User) : Call<ResponseDataUserItem>

    @PUT("users/{id}")
    fun putUser(@Path("id") id:Int,
                @Body request: EditUser
    ) : Call<List<ResponseDataUserItem>>
}