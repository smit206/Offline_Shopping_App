package com.example.entheos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PhotoAdapter(
    private val photoUrls:List<String>
):RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoAdapter.PhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_photo,parent,false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoAdapter.PhotoViewHolder, position: Int) {
        holder.bind(photoUrls[position])
    }

    override fun getItemCount(): Int {
        return photoUrls.size
    }

    inner class PhotoViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        private val photoImageView:ImageView = itemView.findViewById(R.id.iv_photo)

        fun bind(photoUrl: String){
            Glide.with(itemView.context)
                .load(photoUrl)
                .into(photoImageView)
        }
    }

}