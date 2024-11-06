package com.example.entheos

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView

class shopAdapter(private var mList:List<shoplists>, private val context: Context):RecyclerView.Adapter<shopAdapter.ViewHolder>() {

    private var onShopItemClickListener: OnShopItem? = null

    fun setOnShopItemClickListener(listener: OnShopItem){
        onShopItemClickListener = listener
    }

    class ViewHolder(ItemView: View):RecyclerView.ViewHolder(ItemView){
        val image:ShapeableImageView? = itemView.findViewById(R.id.shop_logo)
        val title:TextView? = itemView.findViewById(R.id.shop_title)
        val text:TextView? = itemView.findViewById(R.id.shop_text)
        val btn:Button? = itemView.findViewById(R.id.btn_getdirection)
        val send:ImageView? = itemView.findViewById(R.id.imageView2)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.shop_ticket,parent,false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val AddShopModel = mList[position]

        holder.title!!.text = AddShopModel.fullname
        holder.text!!.text = AddShopModel.shop_address

        var lat = AddShopModel.latitude
        var lon = AddShopModel.longitude
        var name = AddShopModel.fullname

        if(AddShopModel.ShopImage.isNotEmpty()){
            Glide.with(context)
                .load(AddShopModel.ShopImage)
                .into(holder.image!!)
        }else{
            holder.image!!.setImageResource(R.drawable.ic_home_vendors)
        }


        holder.btn!!.setOnClickListener{
            lat = AddShopModel.latitude
            lon = AddShopModel.longitude
            name = AddShopModel.fullname
            val bundle = Bundle()
            bundle.putDouble("lat",lat)
            bundle.putDouble("lon",lon)
            bundle.putString("mapdata",name)
            onShopItemClickListener!!.onItemClick(bundle)
        }

        holder.send!!.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context,info::class.java)
            intent.putExtra("text",AddShopModel.fullname)
            intent.putExtra("image",AddShopModel.ShopImage)
            intent.putExtra("desc",AddShopModel.shop_info)
            intent.putExtra("site",AddShopModel.shop_website)
            context.startActivity(intent)
        }




    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateShopList(newShopList: List<shoplists>){
        mList = newShopList
        notifyDataSetChanged()
    }

    interface OnShopItem{
        fun onItemClick(bundle: Bundle)
    }
}