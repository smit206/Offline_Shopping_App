package com.example.entheos

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class shopAdapter(private val mList:List<shopmodel>, private val itemClickListener:OnShopItem):RecyclerView.Adapter<shopAdapter.ViewHolder>() {

    class ViewHolder(ItemView: View):RecyclerView.ViewHolder(ItemView){
        val image:ImageView? = itemView.findViewById(R.id.shop_logo)
        val title:TextView? = itemView.findViewById(R.id.shop_title)
        val text:TextView? = itemView.findViewById(R.id.shop_text)
        val btn:Button? = itemView.findViewById(R.id.btn_lgnface)
        val send:ImageView? = itemView.findViewById(R.id.imageView2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.shop_ticket,parent,false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val AddShopModel = mList[position]

        holder.image!!.setImageResource(AddShopModel.image!!)
        holder.title!!.setText(AddShopModel.title)
        holder.text!!.setText(AddShopModel.text)


        holder.btn!!.setOnClickListener{
            itemClickListener.onItemClick(AddShopModel.let!!,AddShopModel.lon!!,AddShopModel.title!!)
        }

        holder.send!!.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context,info::class.java)
            intent.putExtra("text",AddShopModel.title)
            intent.putExtra("image",AddShopModel.image)
            context.startActivity(intent)
        }

//        val lat = AddShopModel.let
//        val lon = AddShopModel.lon
//        val bundle = Bundle()
//        bundle.putDouble("lat",lat!!)
//        bundle.putDouble("lon",lon!!)


    }

    interface OnShopItem{
        fun onItemClick(lat:Double,lon:Double,mapdata:String)
    }
}