package com.example.entheos

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class promoAdapter(private val promolist:MutableList<promos>):RecyclerView.Adapter<promoAdapter.PromoViewHolder>() {

    class PromoViewHolder(itemview:View):RecyclerView.ViewHolder(itemview){
        val promo_image:ImageView = itemview.findViewById(R.id.promo_logo)
        val promo_title:TextView = itemview.findViewById(R.id.promo_title)
        val promo_date:TextView = itemview.findViewById(R.id.promo_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.promos_ticket,parent,false)

        return PromoViewHolder(view)
    }

    override fun getItemCount(): Int {
        return promolist.size
    }

    override fun onBindViewHolder(holder: PromoViewHolder, position: Int) {
        val AddPromoModel = promolist[position]

        holder.promo_image.setImageResource(AddPromoModel.image)
        holder.promo_title.setText(AddPromoModel.name)
        holder.promo_date.setText(AddPromoModel.date)

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context,promos_info::class.java)
            intent.putExtra("promo_image",AddPromoModel.image)
            intent.putExtra("promo_title",AddPromoModel.name)
            intent.putExtra("promo_date",AddPromoModel.date)
            holder.itemView.context.startActivity(intent)
        }
    }
}