package com.example.challengechapter6.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.challengechapter6.adapter.ProductAdapter
import com.example.challengechapter6.databinding.ActivityHomeBinding
import com.example.challengechapter6.model.ViewModelUser
import com.example.challengechapter6.viewmodel.ViewModelProduct
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    lateinit var binding: ActivityHomeBinding
    lateinit var viewModelUser : ViewModelUser
    lateinit var viewModelProduct: ViewModelProduct

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModelUser = ViewModelProvider(this).get(ViewModelUser::class.java)
        viewModelProduct = ViewModelProvider(this).get(ViewModelProduct::class.java)

        showData()

        binding.btnProfile.setOnClickListener {
            val pindah = Intent(this, ProfileActivity::class.java)
            startActivity(pindah)
        }

        binding.btnFavorit.setOnClickListener {
            val pindah = Intent(this, CartActivity::class.java)
            startActivity(pindah)
        }
    }

    fun showData(){
        viewModelUser.dataUser.observe(this, Observer {
            binding.txtWelcomeUser.text = it.username
        })
        viewModelProduct.getliveDataProduct().observe(this, Observer {
            if (it != null) {
                binding.rvProduct.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                binding.rvProduct.adapter = ProductAdapter(it)
            } else {
                Toast.makeText(this, "There is no data to show", Toast.LENGTH_SHORT).show()
            }
        })
        viewModelProduct.callApiProduct()
    }
}