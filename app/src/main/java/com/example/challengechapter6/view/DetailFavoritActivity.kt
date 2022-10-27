package com.example.challengechapter6.view

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.challengechapter6.databinding.ActivityDetailFavoritBinding
import com.example.challengechapter6.model.ResponseDataCartItem
import com.example.challengechapter6.model.ViewModelUser
import com.example.challengechapter6.viewmodel.ViewModelCart

class DetailFavoritActivity : AppCompatActivity() {

    lateinit var binding: ActivityDetailFavoritBinding
    lateinit var dataCart : ResponseDataCartItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailFavoritBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataCart = intent.getSerializableExtra("details") as ResponseDataCartItem

        if(dataCart.hexValue == null || dataCart.hexValue == "") {
            binding.layoutDetailFavorit.setBackgroundColor(Color.parseColor("#E6DFF3"))
        } else binding.layoutDetailFavorit.setBackgroundColor(Color.parseColor(dataCart.hexValue))

        binding.txtTitle.text = dataCart.name
        binding.txtPrice.text = dataCart.price
        binding.txtDesc.text = "${dataCart.description}\n"
        Glide.with(this).load(dataCart.imageLink).into(binding.imgProduct)

        binding.btnBackDet.setOnClickListener {
            val pindah = Intent(this, CartActivity::class.java)
            startActivity(pindah)
        }

        binding.btnRemoveCart.setOnClickListener {
            var viewModel = ViewModelProvider(this).get(ViewModelCart::class.java)
            var viewModelUser = ViewModelProvider(this).get(ViewModelUser::class.java)

            viewModelUser.dataUser.observe(this, {
                viewModel.callDeleteCart(it.id, dataCart.id.toInt())
            })

            Toast.makeText(this, "${dataCart.name} removed from Cart!", Toast.LENGTH_SHORT).show()
            val pindah = Intent(this, CartActivity::class.java)
            startActivity(pindah)
        }
    }
}