package com.example.challengechapter6.network

import com.example.challengechapter6.model.ResponseDataUserItem
import com.example.challengechapter6.model.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface RestfulAPIUser {
    @GET("users")
    fun getAllUser() : Call<List<ResponseDataUserItem>>

    @POST("users")
    fun addUser(@Body request : User) : Call<ResponseDataUserItem>

    @PUT("users/{id}")
    @Multipart
    fun putUser(@Path("id") id:Int,
                @Part("name") name: RequestBody,
                @Part("username") username : RequestBody,
                @Part("password") password : RequestBody,
                @Part("address") address : RequestBody,
                @Part("age") age : RequestBody,
                @Part image : MultipartBody.Part) : Call<List<ResponseDataUserItem>>
}