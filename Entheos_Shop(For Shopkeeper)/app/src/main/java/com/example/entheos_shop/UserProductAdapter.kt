package com.example.entheos_shop

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class UserProductAdapter(
    private val productList:MutableList<Product>,
    private val onRemoveClick: (Product) -> Unit
    ):RecyclerView.Adapter<UserProductAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productName: TextView = itemView.findViewById(R.id.tv_product_name)
        private val productPrice: TextView = itemView.findViewById(R.id.tv_product_price)
        private val productDescription: TextView = itemView.findViewById(R.id.tv_product_description)
        private val rvProductPhotos: RecyclerView = itemView.findViewById(R.id.rv_product_photos)
        private val removeButton: ImageView = itemView.findViewById(R.id.iv_remove_product)

        @SuppressLint("SetTextI18n")
        fun bind(product: Product) {
            productName.text = "Product: ${product.name}"
            productPrice.text = "Price: ${product.price}"
            productDescription.text = product.description

            rvProductPhotos.adapter = ProductPhotoAdapter(product.photoUrls)
            rvProductPhotos.layoutManager = LinearLayoutManager(itemView.context,LinearLayoutManager.HORIZONTAL,false)

            removeButton.setOnClickListener {
                onRemoveClick(product)
            }
        }
    }
}