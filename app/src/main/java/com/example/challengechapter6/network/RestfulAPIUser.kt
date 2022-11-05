package com.example.challengechapter6.network

import com.example.challengechapter6.model.EditUser
import com.example.challengechapter6.model.ResponseDataUserItem
import com.example.challengechapter6.model.User
import retrofit2.Call
import retrofit2.http.*

interface RestfulAPIUser {
    @GET("users")
    fun getAllUser() : Call<List<ResponseDataUserItem>>

    @POST("users")
    fun addUser(@Body request : User) : Call<ResponseDataUserItem>

    @PUT("users/{id}")
    fun putUser(@Path("id") id:Int,
                @Body request: EditUser) : Call<List<ResponseDataUserItem>>
}