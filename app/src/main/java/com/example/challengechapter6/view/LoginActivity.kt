package com.example.challengechapter6.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.challengechapter6.databinding.ActivityLoginBinding
import com.example.challengechapter6.model.ResponseDataUserItem
import com.example.challengechapter6.model.ViewModelUser
import com.example.challengechapter6.network.RetrofitClientUser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    lateinit var viewModelUser : ViewModelUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            //data inputan user
            var inputUsername = binding.editUsernameLog.text.toString()
            var inputPass = binding.editPasswordLog.text.toString()

            if(inputUsername != null && inputPass !=null){
                viewModelUser = ViewModelProvider(this).get(ViewModelUser::class.java)
                requestLogin(inputUsername, inputPass)
            }
            else if(inputUsername == null && inputPass == null) toast("Empty Username or Password!")
        }

        binding.txtRegister.setOnClickListener{
            val pindah = Intent(this, RegisterActivity::class.java)
            startActivity(pindah)
        }

        /* binding.txtLangEn.setOnClickListener{
            setLocale("en")
        }
        binding.txtLangIdn.setOnClickListener{
            setLocale("id")
        }
        binding.txtLangKor.setOnClickListener{
            setLocale("ko")
        } */
    }

    fun toast(mess:String){
        Toast.makeText(this, mess, Toast.LENGTH_LONG).show()
    }

    fun requestLogin(username:String, password:String){
        RetrofitClientUser.instance.getAllUser().enqueue(object : Callback<List<ResponseDataUserItem>> {
            override fun onResponse(call: Call<List<ResponseDataUserItem>>, response: Response<List<ResponseDataUserItem>>) {
                var data = false
                if(response.isSuccessful){
                    if(response.body() != null){
                        val respon = response.body()
                        for (i in 0 until respon!!.size){
                            if(respon[i].username.equals(username) && respon[i].password.equals(password)){
                                data = true

                                //add ke datastore
                                viewModelUser.editData(respon[i].id.toInt(), respon[i].name, username, password, respon[i].address, respon[i].age)

                                toast("Login Success!")
                                var pinda = Intent(this@LoginActivity, HomeActivity::class.java)
                                startActivity(pinda)
                            }
                        }
                        if(data == false) toast("Wrong Username or Password!")
                    }
                    else toast("Empty Response!")
                }
                else toast("Failed Load Data!")
            }

            override fun onFailure(call: Call<List<ResponseDataUserItem>>, t: Throwable) {

            }
        })
    }
}