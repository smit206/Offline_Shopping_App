package com.example.entheos

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.core.Context

class vendorAdapter(private var vendorlist:List<vendor>, private val context: android.content.Context):RecyclerView.Adapter<vendorAdapter.VendorViewHolder>() {

    private var onVendorItemClickListener: OnVendorItem? = null

    fun setOnVendorItemClickListener(listener: OnVendorItem){
        onVendorItemClickListener = listener
    }

    class VendorViewHolder(itemview:View):RecyclerView.ViewHolder(itemview){
        val vendor_image:ImageView = itemview.findViewById(R.id.vendor_logo)
        val vendor_title:TextView = itemview.findViewById(R.id.vendor_title)
        val vendor_disc:TextView = itemview.findViewById(R.id.vendor_text)
        val vendor_info: ImageView = itemview.findViewById(R.id.vendor_info)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VendorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.vendor_ticket,parent,false)
        return VendorViewHolder(view)
    }

    override fun getItemCount(): Int {
        return vendorlist.size
    }

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: VendorViewHolder, position: Int) {
        val AddVendorModel = vendorlist[position]

        holder.vendor_title.setText(AddVendorModel.fullname)
        holder.vendor_disc.setText(AddVendorModel.disc)

        if(AddVendorModel.ShopImage.isNotEmpty()){
            Glide.with(context)
                .load(AddVendorModel.ShopImage)
                .into(holder.vendor_image)
        }

        holder.vendor_info!!.setOnClickListener{
            val context = holder.itemView.context
            val intent = Intent(context,info::class.java)
            intent.putExtra("text",AddVendorModel.fullname)
            intent.putExtra("image",AddVendorModel.ShopImage)
            intent.putExtra("desc",AddVendorModel.shop_info)
            intent.putExtra("site",AddVendorModel.shop_website)
            context.startActivity(intent)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateVendorList(newVendorList: List<vendor>){
        vendorlist = newVendorList
        notifyDataSetChanged()
    }

    interface OnVendorItem{
        fun onItemClick(bundle: Bundle)
    }
}