package com.example.challengechapter6.model

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.example.challengechapter6.datastore.UserPreferencesRepository
import com.example.challengechapter6.network.RetrofitClientUser
import com.example.challengechapter6.view.LoginActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewModelUser(application: Application) : AndroidViewModel(application) {
        private val repo = UserPreferencesRepository(application)
        val dataUser = repo.readProto.asLiveData()

        fun editData(id:Int, nama : String, username:String, password:String, address:String, age:Int) = viewModelScope.launch{
            repo.saveData(id, nama, username, password, address, age)
        }

        fun clearData() = viewModelScope.launch{
            repo.deleteData()
        }

    lateinit var liveDataUser : MutableLiveData<List<ResponseDataUserItem>>
    lateinit var postLDUser : MutableLiveData<ResponseDataUserItem>
    lateinit var putLDUser : MutableLiveData<List<ResponseDataUserItem>>

    init {
        liveDataUser = MutableLiveData()
        postLDUser = MutableLiveData()
        putLDUser = MutableLiveData()
        callAPIUser()
    }

    fun ambilLiveDataUser() : MutableLiveData<List<ResponseDataUserItem>> {
        return liveDataUser
    }

    fun addLiveDataUser() : MutableLiveData<ResponseDataUserItem> {
        return postLDUser
    }

    fun editLiveDataUser() : MutableLiveData<List<ResponseDataUserItem>> {
        return putLDUser
    }

    fun callEditUser(id:Int, name : String, username :String, age :Int, addr : String, image: Uri?){
        RetrofitClientUser.instance.putUser(id, EditUser(name, username, addr, age, image)).enqueue(object : Callback<List<ResponseDataUserItem>> {
            override fun onResponse(call: Call<List<ResponseDataUserItem>>, response: Response<List<ResponseDataUserItem>>) {
                if(response.isSuccessful){
                    putLDUser.postValue(response.body())
                }
            }
            override fun onFailure(call: Call<List<ResponseDataUserItem>>, t: Throwable) {

            }
        })
    }

    fun callPostAPIUser(name:String, username:String, password:String, address:String, age:Int){
        RetrofitClientUser.instance.addUser(User(name, username, password, address, age)).enqueue(object :
            Callback<ResponseDataUserItem> {
            override fun onResponse(call: Call<ResponseDataUserItem>, response: Response<ResponseDataUserItem>) {
                if(response.isSuccessful) postLDUser.postValue(response.body())
            }
            override fun onFailure(call: Call<ResponseDataUserItem>, t: Throwable) {

            }

        })
    }

    fun callAPIUser(){
        GlobalScope.async {
            RetrofitClientUser.instance.getAllUser().enqueue(object : Callback<List<ResponseDataUserItem>>{
                override fun onResponse(call: Call<List<ResponseDataUserItem>>, response: Response<List<ResponseDataUserItem>>) {
                    if (response.isSuccessful){
                        liveDataUser.postValue(response.body())
                    }
                }
                override fun onFailure(call: Call<List<ResponseDataUserItem>>, t: Throwable) {

                }
            })
        }
    }
}