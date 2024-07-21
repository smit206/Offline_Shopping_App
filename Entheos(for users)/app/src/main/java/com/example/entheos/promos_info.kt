package com.example.entheos

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

class promos_info : AppCompatActivity() {

    var back:LinearLayout? = null
    var infoimage:ImageView? = null
    var title:TextView? = null
    var date:TextView? = null
    var btn:Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promos_info)

        back = findViewById(R.id.btn_back_info)
        infoimage = findViewById(R.id.info_logo)
        title = findViewById(R.id.info_title)
        date = findViewById(R.id.info_date)
        btn= findViewById(R.id.btn_info)

        val image = intent.getIntExtra("promo_image",0)
        val til = intent.getStringExtra("promo_title")
        val dat = intent.getStringExtra("promo_date")

        infoimage!!.setImageResource(image)
        title!!.setText(til)
        date!!.setText(dat)
        btn!!.setText(til)

        back!!.setOnClickListener {
            val intent = Intent(this@promos_info,promos_discount::class.java)
            startActivity(intent)
        }
    }
}