package com.example.entheos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vanniktech.emoji.EmojiTextView
import java.lang.IllegalArgumentException

class messageAdapter(private val messagelist:MutableList<Message>):RecyclerView.Adapter<messageAdapter.MessageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = when(viewType){
            MessageType.SENT.ordinal->{
                layoutInflater.inflate(R.layout.m_sent,parent,false)
            }
            MessageType.RECEIVED.ordinal->{
                layoutInflater.inflate(R.layout.m_receive,parent,false)
            }
            else ->{
                throw IllegalArgumentException("Invalid View type")
            }
        }
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messagelist[position])
    }

    override fun getItemCount(): Int {
        return messagelist.size
    }

    override fun getItemViewType(position: Int): Int {
        return messagelist[position].type.ordinal
    }

    fun clearMessages(){
        messagelist.clear()
    }

    fun addMessage(messages: List<Message>){
        if(messages.isNotEmpty()){
            val startPosition = this.messagelist.size
            this.messagelist.addAll(messages)
            notifyItemRangeInserted(startPosition,messages.size)
        }
    }


    inner class MessageViewHolder(itemview: View):RecyclerView.ViewHolder(itemview){
        private val messageTV:EmojiTextView = itemview.findViewById(R.id.tv_message)
        private val time:TextView = itemview.findViewById(R.id.tvtime)
        private val username:TextView = itemview.findViewById(R.id.tvuser)
        private val userpic:ImageView = itemview.findViewById(R.id.ivuser)

        fun bind(message: Message){
            messageTV.setText(message.text)
            time.setText(message.time)
            username.setText(message.username)

            Glide.with(itemView.context)
                .load(message.profilepic)
                .placeholder(R.drawable.profile)
                .into(userpic)

            val layoutParams = itemView.layoutParams as ViewGroup.MarginLayoutParams
            if(message.type == MessageType.SENT){
                layoutParams.marginStart = 100
                layoutParams.marginEnd = 0
//                (itemView as LinearLayout).gravity = Gravity.END
            }else{
                layoutParams.marginStart = 0
                layoutParams.marginEnd = 100
//                (itemView as LinearLayout).gravity = Gravity.START
            }
        }
    }
}