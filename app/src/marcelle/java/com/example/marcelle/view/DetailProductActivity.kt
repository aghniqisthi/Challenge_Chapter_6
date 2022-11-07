package com.example.marcelle.view

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.challengechapter6.databinding.ActivityDetailProductBinding
import com.example.marcelle.model.ResponseDataProductItem
import com.example.marcelle.viewmodel.ViewModelCart
import com.example.marcelle.viewmodel.ViewModelUser

class DetailProductActivity : AppCompatActivity() {

    lateinit var binding: ActivityDetailProductBinding
    lateinit var dataProduct : ResponseDataProductItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataProduct = intent.getSerializableExtra("detail") as ResponseDataProductItem

        if(dataProduct.productColors.isEmpty()) {
            binding.layoutDetailProduct.setBackgroundColor(Color.parseColor("#F8DBDE"))
        } else binding.layoutDetailProduct.setBackgroundColor(Color.parseColor(dataProduct.productColors[0].hexValue))

        binding.txtTitle.text = dataProduct.name
        binding.txtPrice.text = dataProduct.price
        binding.txtDesc.text = "${dataProduct.description}\n"
        Glide.with(this).load(dataProduct.imageLink).into(binding.imgProduct)

        binding.btnBackDet.setOnClickListener {
            val pindah = Intent(this, HomeActivity::class.java)
            startActivity(pindah)
        }

        binding.btnCart.setOnClickListener {
            if(dataProduct.productColors.isEmpty()) {
                addCart(dataProduct.name, dataProduct.price, dataProduct.description, dataProduct.imageLink, "")
            }
            else {
                addCart(dataProduct.name, dataProduct.price, dataProduct.description, dataProduct.imageLink, dataProduct.productColors[0].hexValue)
            }
            val pindah = Intent(this, HomeActivity::class.java)
            startActivity(pindah)
        }

    }

    fun addCart(name: String, price: String, description: String, imageLink: String, hexValue:String){
        var viewModel = ViewModelProvider(this).get(ViewModelCart::class.java)
        var viewModelUser = ViewModelProvider(this).get(ViewModelUser::class.java)

        viewModelUser.dataUser.observe(this, {
            viewModel.callPostApiCart(it.id, name, price, description, imageLink, hexValue)
        })
        viewModel.addLiveDataCart().observe(this, {
            if(it!=null){
                Toast.makeText(this, "Cart Added!", Toast.LENGTH_SHORT).show()
            }
        })
    }
}