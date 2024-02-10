package com.example.entheos

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Vendors : AppCompatActivity() {

    private val vendors = mutableListOf(vendor(R.drawable.raspberry,"Raspberry pi"),
        vendor(R.drawable.canada,"Polar Canada"),
        vendor(R.drawable.fish,"Bigger Fish"),
        vendor(R.drawable.lion,"King's Crown"),
        vendor(R.drawable.wisehome,"Wise Home")
    )

    private lateinit var adapter:vendorAdapter

    var back:LinearLayout? = null

    var recycler:RecyclerView? = null
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vendors)


        back = findViewById(R.id.btn_back)

        back!!.setOnClickListener {
            val intent = Intent(this@Vendors,home::class.java)
            startActivity(intent)
        }

        recycler = findViewById(R.id.rv_vendor)
        recycler!!.layoutManager = GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false)

        adapter =vendorAdapter(vendors)
        recycler!!.adapter = adapter

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@Vendors,home::class.java)
        startActivity(intent)
    }
}

data class vendor(val image:Int,val name:String)