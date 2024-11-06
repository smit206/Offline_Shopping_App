package com.example.entheos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class pricelistAdapter(
    private val productlist:List<Product>,
    private val onItemClick: (Product) -> Unit
):RecyclerView.Adapter<pricelistAdapter.PriceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PriceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product,parent,false)
        return PriceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PriceViewHolder, position: Int) {
        holder.bind(productlist[position])
    }

    override fun getItemCount(): Int {
        return productlist.size
    }

    inner class PriceViewHolder(itemview:View):RecyclerView.ViewHolder(itemview){
        val nameTextView:TextView= itemview.findViewById(R.id.tv_product_name)
        val priceTextView:TextView= itemview.findViewById(R.id.tv_product_price)
        val shopNameTextView:TextView = itemview.findViewById(R.id.tv_shop_name)
        val productImageView:ImageView = itemview.findViewById(R.id.iv_product_image)

        fun bind(product: Product){
            nameTextView.text = product.name
            priceTextView.text = product.price
            shopNameTextView.text = product.shopName

            if(product.photoUrls.isNotEmpty()){
                Glide.with(itemView.context)
                    .load(product.photoUrls[0])
                    .into(productImageView)
            }

            itemView.setOnClickListener {
                onItemClick(product)
            }
        }
    }


}