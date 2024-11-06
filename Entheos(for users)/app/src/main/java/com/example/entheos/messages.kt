package com.example.entheos

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.vanniktech.emoji.EmojiEditText
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.EmojiPopup
import com.vanniktech.emoji.EmojiTextView
import com.vanniktech.emoji.google.GoogleEmojiProvider
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import java.util.Locale

class messages : Fragment() {

    private lateinit var recyclev:RecyclerView
    private lateinit var adapter:messageAdapter
    private lateinit var messageList: MutableList<Message>
    private lateinit var emoji:ImageView
    private lateinit var m_text:EmojiEditText
    private lateinit var m_send:ImageView

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        EmojiManager.install(GoogleEmojiProvider())
        val view = inflater.inflate(R.layout.fragment_messages, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference.child("messages")

        emoji = view.findViewById(R.id.m_emoji)
        m_text = view.findViewById(R.id.m_text)
        m_send = view.findViewById(R.id.bu_send)

        recyclev = view.findViewById(R.id.rvmessage)
        recyclev.layoutManager = LinearLayoutManager(requireContext())
        messageList = mutableListOf()
        adapter = messageAdapter(messageList)
        recyclev.adapter = adapter

        loadMessagesFromFirebase()

        val rootView = view.findViewById<LinearLayout>(R.id.root)

        val popup:EmojiPopup = EmojiPopup.Builder
            .fromRootView(rootView)
            .build(m_text)

        emoji.setOnClickListener {
            popup.toggle()
        }

        m_send.setOnClickListener {
            val currentUserId = firebaseAuth.currentUser?.uid

            val reference = firebaseDatabase.reference.child("users").child(currentUserId!!)

            reference.addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        val username = snapshot.child("fullname").getValue(String::class.java)
                        val profilepic = snapshot.child("ProfileImage").getValue(String::class.java)

                        val newMessage = Message(
                            m_text.text.toString(),
                            getCurrentTime(),
                            MessageType.SENT,
                            currentUserId,
                            username!!,
                            profilepic!!
                        )
                        addMessageToFirebase(newMessage)
                        m_text.setText("")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireActivity(), "Failed to send message!!", Toast.LENGTH_SHORT).show()
                }

            })
        }

        return view
    }

    private fun loadMessagesFromFirebase(){
        databaseReference.addValueEventListener(object: ValueEventListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                messageList.clear()
                val currentUserId = firebaseAuth.currentUser?.uid
                for(messageSnapshot in snapshot.children){
                    try {
                        val message = messageSnapshot.getValue(Message::class.java)
                        message?.let {
                            if(it.userId == currentUserId){
                                it.type = MessageType.SENT
                            }else{
                                it.type = MessageType.RECEIVED
                            }
                            messageList.add(it)
                        }
                    }catch (e:Exception){
                        Log.e("MessagesFragment", "Error parsing message", e)
                    }
                }
                adapter.notifyDataSetChanged()
                recyclev.scrollToPosition(adapter.itemCount - 1)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireActivity(), "Failed to load chat messages", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addMessageToFirebase(message: Message){
        val databaseReference = firebaseDatabase.reference.child("messages").push()
        databaseReference.setValue(message)
    }

    private fun getCurrentTime():String{
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(Date())
    }
}

data class Message(
    val text:String = "",
    val time:String= "",
    var type:MessageType = MessageType.SENT,
    val userId: String = "",
    val username:String ="",
    val profilepic:String =""
)

enum class MessageType{
    SENT,
    RECEIVED
}