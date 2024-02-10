package com.example.entheos

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vanniktech.emoji.EmojiEditText
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.EmojiPopup
import com.vanniktech.emoji.EmojiTextView
import com.vanniktech.emoji.google.GoogleEmojiProvider

class messages : Fragment() {

    private val messagelist= mutableListOf(Message("Hello, how are you?","10:50 am",MessageType.SENT),
        Message("I'm Good,Thanks!","10:51 am",MessageType.RECEIVED),
        Message("What about you?","10:52 am",MessageType.SENT),
        Message("I'm greate too!","10:53 am",MessageType.RECEIVED))


    private lateinit var recyclev:RecyclerView
    private lateinit var adapter:messageAdapter
    private lateinit var emoji:ImageView
    private lateinit var m_text:EmojiEditText
    private lateinit var m_send:ImageView

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        EmojiManager.install(GoogleEmojiProvider())
        val view = inflater.inflate(R.layout.fragment_messages, container, false)

        emoji = view.findViewById(R.id.m_emoji)
        m_text = view.findViewById(R.id.m_text)
        m_send = view.findViewById(R.id.bu_send)

        recyclev = view.findViewById(R.id.rvmessage)
        recyclev.layoutManager = LinearLayoutManager(context)

        adapter = messageAdapter(messagelist)
        recyclev.adapter = adapter

        val rootView = view.findViewById<LinearLayout>(R.id.root)

        val popup:EmojiPopup = EmojiPopup.Builder
            .fromRootView(rootView)
            .build(m_text)

        emoji.setOnClickListener {
            popup.toggle()
        }

        m_send.setOnClickListener {
            val newMessage = Message("${m_text.text}","11:00 am",MessageType.SENT)
            adapter.addMessage(newMessage)
            m_text.setText("")
        }

        return view
    }

//    @SuppressLint("ServiceCast")
//    private fun showEmojiKeyboard(){
//       if()
//    }

}

data class Message(val text:String, val time:String,val type:MessageType)

enum class MessageType{
    SENT,
    RECEIVED
}

//EmojiPopup popup = EmojiPopup.Builder.fromRootView(
//    findViewByid(R.id.root_view)
//).build(m_text);
//
//emoji.setOnClickListener(new View.OnClickListener(){
//    @override
//    public void onClick(View view){
//        popup.toggle();
//    }
//});
//
//sendBtn.setOnClickListener(new View.OnClickListener(){
//    @override
//    public void onClick(View view){
//        EmojiTextView emojiTextView = LayoutInflater
//                .from(view.getContec())
//            .inflate(R.layout.emoji_text_view,layout,false);
//    }
//});