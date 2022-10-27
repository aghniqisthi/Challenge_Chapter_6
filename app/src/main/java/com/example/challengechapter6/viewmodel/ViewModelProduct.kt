package com.example.challengechapter6.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.challengechapter6.model.ResponseDataProductItem
import com.example.challengechapter6.network.RestfulAPIProduct
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ViewModelProduct @Inject constructor(var api: RestfulAPIProduct) : ViewModel() {
    var liveDataProduct : MutableLiveData<List<ResponseDataProductItem>>

    init {
        liveDataProduct = MutableLiveData()
    }
    fun getliveDataProduct() : MutableLiveData<List<ResponseDataProductItem>> {
        return liveDataProduct
    }

    fun callApiProduct(){
        api.getAllProduct().enqueue(object :
            Callback<List<ResponseDataProductItem>> {
            override fun onResponse(call: Call<List<ResponseDataProductItem>>, response: Response<List<ResponseDataProductItem>>) {
                if (response.isSuccessful){
                    liveDataProduct.postValue(response.body())
                }
                else{

                }
            }
            override fun onFailure(call: Call<List<ResponseDataProductItem>>, t: Throwable) {

            }
        })
    }
}