package com.example.challengechapter6.network

import android.net.Uri
import com.example.challengechapter6.model.EditUser
import com.example.challengechapter6.model.ResponseDataUserItem
import com.example.challengechapter6.model.User
import okhttp3.MultipartBody
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