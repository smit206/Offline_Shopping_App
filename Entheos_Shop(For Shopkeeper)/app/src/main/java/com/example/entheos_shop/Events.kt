package com.example.entheos_shop

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
import com.example.entheos_shop.eventsAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Exception

class Events : Fragment() {

    private var title:String? = null
    private var desc:String? = null
    private var date:String? = null
    private var time:String? = null
    private var recyclerview:RecyclerView? = null
    private lateinit var eventList:MutableList<Eventdata>

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    val REQUEST_CODE = 1

    private lateinit var adapter: eventsAdapter

    private lateinit var add:ImageView

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_events, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference.child("Events")

        recyclerview = view.findViewById(R.id.rvevents)

        add = view.findViewById(R.id.add_event)

        add!!.setOnClickListener {
            val intent = Intent(requireActivity(),add_event::class.java)
            startActivityForResult(intent,REQUEST_CODE)
        }

        recyclerview!!.layoutManager = LinearLayoutManager(requireContext())
        eventList= mutableListOf()
        adapter = eventsAdapter(eventList)
        recyclerview!!.adapter = adapter

        loadeventsFromFirebase()

        return view
    }

    private fun loadeventsFromFirebase(){
        val currentUserId = firebaseAuth.currentUser?.uid
        
        if(currentUserId != null){
            databaseReference.addValueEventListener(object: ValueEventListener{
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    eventList.clear()
                    for(eventSnapshot in snapshot.children){
                        try{
                            val event = eventSnapshot.getValue(Eventdata::class.java)
                            if(event != null && event.senderId == currentUserId){
                                eventList.add(event)
                            }
                        }catch (e:Exception){
                            e.printStackTrace()
                        }
                    }
                    adapter.notifyDataSetChanged()
                    recyclerview!!.scrollToPosition(adapter.itemCount-1)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireActivity(), "Failed to load Event's List", Toast.LENGTH_SHORT).show()
                }

            })
        }else{
            Toast.makeText(requireActivity(), "User not logged in", Toast.LENGTH_SHORT).show()
        }
        
    }

}