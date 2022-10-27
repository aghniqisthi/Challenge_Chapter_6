package com.example.challengechapter6.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.challengechapter6.model.*
import com.example.challengechapter6.network.*
import com.example.challengechapter6.view.HomeActivity
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

    fun callApiCart(iduser:Int){
        RetrofitClientCart.instance.getAllCart(iduser).enqueue(object :
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

    fun callPostApiCart(id:Int, name: String, price:String, description:String, imageLink:String, hexValue:String){
        RetrofitClientCart.instance.addCart(id, Cart(name, price, description, imageLink, hexValue)).enqueue(object :
            Callback<ResponseDataCartItem> {
            override fun onResponse(call: Call<ResponseDataCartItem>, response: Response<ResponseDataCartItem>) {
                if(response.isSuccessful) postLDCart.postValue(response.body())
            }
            override fun onFailure(call: Call<ResponseDataCartItem>, t: Throwable) {

            }

        })
    }

    fun callDeleteCart(idu:Int, idc: Int) {
        RetrofitClientCart.instance.deleteCart(idu, idc).enqueue(object : Callback<Int> {
            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                if (response.isSuccessful) {
                    deleteCart.postValue(response.body())
                }
                callApiCart(idu)
            }
            override fun onFailure(call: Call<Int>, t: Throwable) {
                callApiCart(idu)
            }
        })
    }
}
