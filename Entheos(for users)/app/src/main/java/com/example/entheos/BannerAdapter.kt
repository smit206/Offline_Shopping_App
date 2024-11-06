package com.example.entheos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class BannerAdapter(private val bannerList:List<Banner>):RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.add_ticket,parent,false)
        return BannerViewHolder(view)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        val banner = bannerList[position]
        holder.shopNameTextView.text = banner.shopName
        Glide.with(holder.itemView.context)
            .load(banner.imageUrl)
            .placeholder(R.drawable.ad1)
            .into(holder.bannerImageView)
    }

    override fun getItemCount(): Int {
        return bannerList.size
    }

    class BannerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val shopNameTextView: TextView = itemView.findViewById(R.id.ad_name)
        val bannerImageView: ImageView = itemView.findViewById(R.id.ad_img)
    }
}