package com.example.entheos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class vendorAdapter(private val vendorlist:MutableList<vendor>):RecyclerView.Adapter<vendorAdapter.VendorViewHolder>() {

    class VendorViewHolder(itemview:View):RecyclerView.ViewHolder(itemview){
        val vendor_image:ImageView = itemview.findViewById(R.id.vendor_logo)
        val vendor_title:TextView = itemview.findViewById(R.id.vendor_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VendorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.vendor_ticket,parent,false)
        return VendorViewHolder(view)
    }

    override fun getItemCount(): Int {
        return vendorlist.size
    }

    override fun onBindViewHolder(holder: VendorViewHolder, position: Int) {
        val AddVendorModel = vendorlist[position]

        holder.vendor_image.setImageResource(AddVendorModel.image)
        holder.vendor_title.setText(AddVendorModel.name)
    }
}