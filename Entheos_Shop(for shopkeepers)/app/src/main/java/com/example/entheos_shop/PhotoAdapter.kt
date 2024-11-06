package com.example.entheos_shop

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PhotoAdapter(
    private val photoList:List<Uri>,
    private val onRemoveClick: (Int) -> Unit
    ): RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    inner class PhotoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val imageView:ImageView = itemView.findViewById(R.id.iv_photo1)
        private val removeButton:ImageView = itemView.findViewById(R.id.iv_remove1)
        fun bind(photoUri: Uri){
            Glide.with(itemView.context)
                .load(photoUri)
                .into(imageView)

            removeButton.setOnClickListener {
                onRemoveClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_select_photo,parent,false)
        return PhotoViewHolder(view)
    }

    override fun getItemCount(): Int {
        return photoList.size
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(photoList[position])
    }
}