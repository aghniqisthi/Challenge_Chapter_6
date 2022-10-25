package com.example.challengechapter6.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.challengechapter6.adapter.CartAdapter
import com.example.challengechapter6.adapter.ProductAdapter
import com.example.challengechapter6.databinding.ActivityCartBinding
import com.example.challengechapter6.model.ViewModelUser
import com.example.challengechapter6.viewmodel.ViewModelCart
import com.example.challengechapter6.viewmodel.ViewModelProduct

class CartActivity : AppCompatActivity() {

    lateinit var binding: ActivityCartBinding
    lateinit var viewModelCart: ViewModelCart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModelCart = ViewModelProvider(this).get(ViewModelCart::class.java)

        showData()

        binding.txtBackFav.setOnClickListener {
            val pindah = Intent(this, HomeActivity::class.java)
            startActivity(pindah)
        }
    }

    fun showData(){
        viewModelCart.getLDCart().observe(this, Observer {
            if (it != null) {
                binding.rvCart.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                binding.rvCart.adapter = CartAdapter(it)
            } else {
                Toast.makeText(this, "There is no data to show", Toast.LENGTH_SHORT).show()
            }
        })
        viewModelCart.callApiCart()
    }
}