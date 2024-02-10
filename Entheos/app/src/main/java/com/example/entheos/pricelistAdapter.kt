package com.example.entheos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class pricelistAdapter(private val pricelist:MutableList<pricelist>):RecyclerView.Adapter<pricelistAdapter.PriceViewHolder>() {

    class PriceViewHolder(itemview:View):RecyclerView.ViewHolder(itemview){
        val price_image:ImageView= itemview.findViewById(R.id.vendor_logo)
        val price_title:TextView= itemview.findViewById(R.id.vendor_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PriceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.vendor_ticket,parent,false)
        return PriceViewHolder(view)
    }

    override fun getItemCount(): Int {
        return pricelist.size
    }

    override fun onBindViewHolder(holder: PriceViewHolder, position: Int) {
        val AddPriceModel = pricelist[position]

        holder.price_image.setImageResource(AddPriceModel.image)
        holder.price_title.setText(AddPriceModel.name)
    }


}