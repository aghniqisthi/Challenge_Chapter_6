package com.example.challengechapter6.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.challengechapter6.databinding.ItemProductfavBinding
import com.example.challengechapter6.model.ResponseDataCartItem
import com.example.challengechapter6.view.DetailFavoritActivity

class CartAdapter (var listCart : List<ResponseDataCartItem>) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    class ViewHolder(var binding: ItemProductfavBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartAdapter.ViewHolder {
        var view = ItemProductfavBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartAdapter.ViewHolder, position: Int) {
        holder.binding.txtJudul.text = listCart[position].name
        holder.binding.txtHarga.text = listCart[position].price
        Glide.with(holder.itemView.context).load(listCart[position].imageLink).into(holder.binding.ivItem)

        holder.binding.cardItemFav.setOnClickListener {
            val pindah = Intent(it.context, DetailFavoritActivity::class.java)
            pindah.putExtra("details", listCart[position])
            it.context.startActivity(pindah)
        }
    }

    override fun getItemCount(): Int = listCart.size

}