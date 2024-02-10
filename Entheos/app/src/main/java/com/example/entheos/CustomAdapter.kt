package com.example.entheos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CustomAdapter(private val mList:List<addViewModel>):RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    class ViewHolder(ItemView: View):RecyclerView.ViewHolder(ItemView){
        val name:TextView = itemView.findViewById(R.id.ad_name)
        val image:ImageView = itemView.findViewById(R.id.ad_img)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.add_ticket,parent,false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val AddViewModel = mList[position]
        holder.name.setText(AddViewModel.name)
        holder.image.setImageResource(R.drawable.ad1)
    }
}