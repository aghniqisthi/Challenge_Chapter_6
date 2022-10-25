package com.example.challengechapter6.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.challengechapter6.databinding.ItemProductBinding
import com.example.challengechapter6.model.ResponseDataProductItem
import com.example.challengechapter6.view.DetailProductActivity

class ProductAdapter (var listProduct : List<ResponseDataProductItem>) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    class ViewHolder(var binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductAdapter.ViewHolder {
        var view = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductAdapter.ViewHolder, position: Int) {
        holder.binding.txtJudul.text = listProduct[position].name
        holder.binding.txtHarga.text = listProduct[position].price
        Glide.with(holder.itemView.context).load(listProduct[position].imageLink).into(holder.binding.ivItem)

        holder.binding.cardItem.setOnClickListener {
            val pinda = Intent(it.context, DetailProductActivity::class.java)
            pinda.putExtra("detail", listProduct[position])
            it.context.startActivity(pinda)
        }
    }

    override fun getItemCount(): Int = listProduct.size

}