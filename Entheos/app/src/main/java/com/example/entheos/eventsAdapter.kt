package com.example.entheos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class eventsAdapter(private val eventList:MutableList<event>):RecyclerView.Adapter<eventsAdapter.EventViewHolder>() {


    class EventViewHolder(itemview: View):RecyclerView.ViewHolder(itemview){
        val event_title:TextView = itemview.findViewById(R.id.event_title)
        val event_date:TextView = itemview.findViewById(R.id.event_date)
        val event_time:TextView = itemview.findViewById(R.id.event_time)
        val event_text:TextView = itemview.findViewById(R.id.event_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.event_ticket,parent,false)

        return EventViewHolder(view)

    }

    override fun getItemCount(): Int {
        return eventList.size
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val AddEventModel = eventList[position]

        holder.event_title.setText(AddEventModel.title)
        holder.event_date.setText(AddEventModel.date)
        holder.event_time.setText(AddEventModel.time)
        holder.event_text.setText(AddEventModel.text)
    }
}