package com.example.entheos

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class promos_discount : AppCompatActivity() {

    private val promos = mutableListOf(promos(R.drawable.ic_audible,"Audbling","10-Nov-2020"),
        promos(R.drawable.ic_wave_it_solutions,"Wave it Solutions","11-Nov-2020"),
        promos(R.drawable.ic_human_tree,"Human tree","15-Nov-2020"),
        promos(R.drawable.ic_team_work,"Team work Hearing","13-Nov-2020"),
        promos(R.drawable.ic_audible,"Audbling","10-Nov-2020"),
        promos(R.drawable.ic_wave_it_solutions,"Wave it Solutions","11-Nov-2020"),
        promos(R.drawable.ic_human_tree,"Human tree","15-Nov-2020"),
        promos(R.drawable.ic_team_work,"Team work Hearing","13-Nov-2020"),
        promos(R.drawable.ic_audible,"Audbling","10-Nov-2020"),
        promos(R.drawable.ic_wave_it_solutions,"Wave it Solutions","11-Nov-2020"),
        promos(R.drawable.ic_human_tree,"Human tree","15-Nov-2020"),
        promos(R.drawable.ic_team_work,"Team work Hearing","13-Nov-2020")
        )

    private lateinit var adapter: promoAdapter

    var recycler:RecyclerView? = null

    var back:LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promos_discount)

        back = findViewById(R.id.btn_back_promo)

        back!!.setOnClickListener {
            val i = Intent(this@promos_discount,home::class.java)
            startActivity(i)
        }

        recycler = findViewById(R.id.rv_promos)
        recycler!!.layoutManager = GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false)

        adapter = promoAdapter(promos)
        recycler!!.adapter = adapter
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val i = Intent(this@promos_discount,home::class.java)
        startActivity(i)
    }
}

data class promos(val image:Int,val name:String,val date:String)