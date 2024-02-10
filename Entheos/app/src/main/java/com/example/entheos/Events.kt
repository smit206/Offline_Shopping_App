package com.example.entheos

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.lang.Exception

class Events : Fragment() {

    private var title:String? = null
    private var desc:String? = null
    private var date:String? = null
    private var time:String? = null

    val REQUEST_CODE = 1

    private val eventdata = mutableListOf(event("Flamingo's Parasol Events","07-Nov-2020","07:30 pm","varius sit amet mattis vulputate enim nulla aliquet porttitor lacus"),
        event("Chic Cookie Event Design","05-Nov-2020","12:30 pm","vestibulum lorem sed risus ultricies tristique nulla aliquet enim tortor"),
        event("Lighning Bug Event","09-Nov-2020","09:00 am","quis hendrerit dolor magna eget est lorem ipsum dolor sit"),
        event("Lvy and Fairy Lights","15-Nov-2020","10:30 am","libero enim sed faucibus turpis in eu mi bibendum neque"),
        event("Extra Event","29-Jan-2024","05:30 pm","dictum sit amet justo donec enim diam vulputate ut pharetra"),
        event("Crecket Match","30-Jan-2024","09:00 am","sed elementum tempus egestas sed sed risus pretium quam vulputate dignissim suspendisse in est ante in nibh mauris cursus mattis molestie a iaculis at erat pellentesque adipiscing commodo elit at imperdiet dui accumsan sit amet nulla facilisi morbi tempus iaculis urna id volutpat lacus laoreet non curabitur gravida arcu ac")
    )

    private lateinit var adapter: eventsAdapter

    private lateinit var add:ImageView

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_events, container, false)

//        val data = arguments?.getString("frag")
//        if(data == "add_frag"){
//            title = arguments?.getString("title")
//            desc = arguments?.getString("desc")
//            date = arguments?.getString("date")
//            time = arguments?.getString("time")
//        }

        var recyclerview:RecyclerView? = null

        recyclerview = view.findViewById(R.id.rvevents)

        add = view.findViewById(R.id.add_event)

        add!!.setOnClickListener {
            val intent = Intent(requireActivity(),add_event::class.java)
            startActivityForResult(intent,REQUEST_CODE)
        }



        recyclerview.layoutManager = LinearLayoutManager(context)

        adapter = eventsAdapter(eventdata)
        recyclerview.adapter = adapter


        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE){
            data?.let {
                title=it.getStringExtra("title")
                desc= it.getStringExtra("desc")
                date = it.getStringExtra("date")
                time = it.getStringExtra("time")

                eventdata.add(event(title.toString(),date.toString(),time.toString(),desc.toString()))

                adapter.notifyItemInserted(eventdata.size-1)
            }
        }

    }

}
data class event(val title:String,val date:String,val time:String,val text:String)