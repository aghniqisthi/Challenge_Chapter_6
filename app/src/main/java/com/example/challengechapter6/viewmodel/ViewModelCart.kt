package com.example.challengechapter6.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.challengechapter6.model.*
import com.example.challengechapter6.network.RestfulAPICart
import com.example.challengechapter6.network.RestfulAPIProduct
import com.example.challengechapter6.network.RetrofitClientCart
import com.example.challengechapter6.network.RetrofitClientUser
import com.example.challengechapter6.view.LoginActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class ViewModelCart (application: Application) : AndroidViewModel(application){

    var liveDataCart : MutableLiveData<List<ResponseDataCartItem>>
    lateinit var postLDCart : MutableLiveData<ResponseDataCartItem>
    lateinit var deleteCart : MutableLiveData<Int>

    init {
        liveDataCart = MutableLiveData()
        postLDCart = MutableLiveData()
        deleteCart = MutableLiveData()
        callApiCart()
    }

    fun getLDCart() : MutableLiveData<List<ResponseDataCartItem>> {
        return liveDataCart
    }
    fun addLiveDataCart() : MutableLiveData<ResponseDataCartItem> {
        return postLDCart
    }
    fun getdeleteCart(): MutableLiveData<Int> {
        return deleteCart
    }

    fun callApiCart(){
        RetrofitClientCart.instance.getAllCart().enqueue(object :
            Callback<List<ResponseDataCartItem>> {
            override fun onResponse(call: Call<List<ResponseDataCartItem>>, response: Response<List<ResponseDataCartItem>>) {
                if (response.isSuccessful){
                    liveDataCart.postValue(response.body())
                }
                else{

                }
            }
            override fun onFailure(call: Call<List<ResponseDataCartItem>>, t: Throwable) {

            }
        })
    }

    fun callPostApiCart(name: String, price:String, description:String, imageLink:String, hexValue:String){
        RetrofitClientCart.instance.addCart(Cart(name, price, description, imageLink, hexValue)).enqueue(object :
            Callback<ResponseDataCartItem> {
            override fun onResponse(call: Call<ResponseDataCartItem>, response: Response<ResponseDataCartItem>) {
                if(response.isSuccessful) postLDCart.postValue(response.body())
            }
            override fun onFailure(call: Call<ResponseDataCartItem>, t: Throwable) {

            }

        })
    }

    fun callDeleteCart(id: Int) {
        RetrofitClientCart.instance.deleteCart(id).enqueue(object : Callback<Int> {
            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                if (response.isSuccessful) {
                    deleteCart.postValue(response.body())
                }
                callApiCart()
            }
            override fun onFailure(call: Call<Int>, t: Throwable) {
                callApiCart()
            }
        })
    }
}
