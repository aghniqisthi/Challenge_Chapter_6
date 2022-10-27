package com.example.challengechapter6.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
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
    lateinit var viewModelUser: ViewModelUser


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModelUser = ViewModelProvider(this).get(ViewModelUser::class.java)
        viewModelCart = ViewModelProvider(this).get(ViewModelCart::class.java)

        viewModelUser.dataUser.observe(this, {
            showData(it.id)
        })

        binding.txtBackFav.setOnClickListener {
            val pindah = Intent(this, HomeActivity::class.java)
            startActivity(pindah)
        }
    }

    fun showData(idu:Int){
        viewModelCart.getLDCart().observe(this, Observer {
            if (it != null) {
                binding.rvCart.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                binding.rvCart.adapter = CartAdapter(it)
            } else {
                Toast.makeText(this, "There is no data to show", Toast.LENGTH_SHORT).show()
            }
        })
        viewModelCart.callApiCart(idu)
    }
}