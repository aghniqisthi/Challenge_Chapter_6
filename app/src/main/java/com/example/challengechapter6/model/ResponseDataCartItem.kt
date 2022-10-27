package com.example.challengechapter6.model


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ResponseDataCartItem(
    @SerializedName("description")
    val description: String,
    @SerializedName("hexValue")
    val hexValue: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("imageLink")
    val imageLink: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("price")
    val price: String,
    @SerializedName("userId")
    val userId: String
) : Serializable