package com.example.entheos

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class infopopup : AppCompatActivity() {

    var btn:Button? = null
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_infopopup)


        btn = findViewById(R.id.buinfo)


        btn!!.setOnClickListener {
            val intent = Intent(this,info::class.java)
            startActivity(intent)
        }
    }
}