package com.example.entheos

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.Serializable


class pricelist_act : AppCompatActivity() {

//    private val prices = mutableListOf(pricelist(R.drawable.raspberry,"Raspberry pi"),
//        pricelist(R.drawable.canada,"Polar Canada"),
//        pricelist(R.drawable.fish,"Bigger Fish"),
//        pricelist(R.drawable.lion,"King's Crown"),
//        pricelist(R.drawable.wisehome,"Wise Home")
//    )

    private var productList: MutableList<Product> = mutableListOf()

    private lateinit var productadapter:pricelistAdapter

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

        productadapter = pricelistAdapter(productList) { product ->
            val intent = Intent(this, ProductDetailsActivity::class.java)
            intent.putExtra("product", product)
            startActivity(intent)
        }

        recycler!!.adapter = productadapter
        recycler!!.layoutManager = LinearLayoutManager(this)

        fetchAllProduct()

    }
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@pricelist_act,home::class.java)
        startActivity(intent)
    }

    private fun fetchAllProduct(){
        val productRef = FirebaseDatabase.getInstance().reference.child("products")

        productRef.addValueEventListener(object : ValueEventListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                productList.clear()
                for(productSnapshot in snapshot.children){
                    val product = productSnapshot.getValue(Product::class.java)
                    product?.let {
                        productList.add(it)
                    }
                    productadapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@pricelist_act, "Failed to load products", Toast.LENGTH_SHORT).show()
            }

        })
    }
}

data class Product(
    val name: String = "",
    val price: String = "",
    val description: String = "",
    val photoUrls: List<String> = emptyList(),
    val shopName: String = "",
) : Serializable