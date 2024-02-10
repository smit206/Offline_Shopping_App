package com.example.entheos

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class pricelist_act : AppCompatActivity() {

    private val prices = mutableListOf(pricelist(R.drawable.raspberry,"Raspberry pi"),
        pricelist(R.drawable.canada,"Polar Canada"),
        pricelist(R.drawable.fish,"Bigger Fish"),
        pricelist(R.drawable.lion,"King's Crown"),
        pricelist(R.drawable.wisehome,"Wise Home")
    )

    private lateinit var adapter:pricelistAdapter

    var back: LinearLayout? = null

    var recycler: RecyclerView? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_price_list)

        back = findViewById(R.id.btn_back_price)

        back!!.setOnClickListener {
            val intent = Intent(this@pricelist_act,home::class.java)
            startActivity(intent)
        }

        recycler = findViewById(R.id.rv_pricelist)
        recycler!!.layoutManager = GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false)

        adapter = pricelistAdapter(prices)
        recycler!!.adapter = adapter

    }
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@pricelist_act,home::class.java)
        startActivity(intent)
    }
}

data class pricelist(val image:Int,val name:String)