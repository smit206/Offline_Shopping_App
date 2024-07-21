package com.example.entheos

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ProductDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)

        val product = intent.getSerializableExtra("product") as Product

        val nameTextView:TextView = findViewById(R.id.dtv_product_name)
        val priceTextView:TextView = findViewById(R.id.dtv_product_price)
        val descriptionTextView:TextView = findViewById(R.id.dtv_product_desc)
        val addressTextView:TextView = findViewById(R.id.dtv_shop_address)
        val photoRecyclerView:RecyclerView = findViewById(R.id.rv_product_photos)
        val getLocationbtn:Button = findViewById(R.id.btn_get_location)

        nameTextView.text = product.name
        priceTextView.text = product.price
        descriptionTextView.text = product.description
        addressTextView.text = "Shop address here"

        val photoAdapter = PhotoAdapter(product.photoUrls)
        photoRecyclerView.adapter = photoAdapter
        photoRecyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)

        getLocationbtn.setOnClickListener {
            val intent = Intent(this@ProductDetailsActivity,home::class.java)
            startActivity(intent)
        }

    }
}