package com.example.challengechapter6.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.challengechapter6.databinding.ActivityRegisterBinding
import com.example.challengechapter6.model.ViewModelUser

class RegisterActivity : AppCompatActivity() {

    lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //seleksi pass = confpass
        binding.btnRegister.setOnClickListener {

            //ambil inputan data dari layout
            var editUsername = binding.editUsername.text.toString()
            var editName = binding.editNama.text.toString()
            var editPass = binding.editPassword.text.toString()
            var editConfPass = binding.editConfirmPassword.text.toString()
            var editAge = binding.editAge.text.toString()
            var editAddr = binding.editAddress.text.toString()

            //seleksi password
            if (editPass.equals(editConfPass)) {
                //masukin data ke API
                addUser(editName, editUsername, editPass, editAddr, editAge.toInt())
                //back to login
                var pindah = Intent(this, LoginActivity::class.java)
                startActivity(pindah)
            } else toast("Password Doesn't Match!")
        }
    }

    fun toast(mess:String){
        Toast.makeText(this, mess, Toast.LENGTH_SHORT).show()
    }

    fun addUser(name:String, username:String, password:String, address:String, age:Int){
        var viewModel = ViewModelProvider(this).get(ViewModelUser::class.java)
        viewModel.callPostAPIUser(name, username, password, address, age)
        viewModel.addLiveDataUser().observe(this, {
            if(it!=null){
                Toast.makeText(this, "Registration Success!", Toast.LENGTH_SHORT).show()
            }
        })
    }
}