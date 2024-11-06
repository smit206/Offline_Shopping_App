package com.example.entheos

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Vendors : AppCompatActivity() {

    private lateinit var adapter:vendorAdapter
    var back:LinearLayout? = null
    var recycler:RecyclerView? = null
    private val vendorlist = ArrayList<vendor>()
    private lateinit var myRef: DatabaseReference
    private lateinit var auth: FirebaseAuth

    @SuppressLint("MissingInflatedId", "WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vendors)


        back = findViewById(R.id.btn_back)

        back!!.setOnClickListener {
            val intent = Intent(this@Vendors,home::class.java)
            startActivity(intent)
        }

        recycler = findViewById(R.id.rv_vendor)
        recycler!!.layoutManager = LinearLayoutManager(this,LinearLayout.VERTICAL,false)

        adapter =vendorAdapter(vendorlist,this)
        recycler!!.adapter = adapter

        auth = FirebaseAuth.getInstance()
        myRef = FirebaseDatabase.getInstance().reference

        fetchVendorData()

    }

    private fun fetchVendorData(){
        vendorlist.clear()

        myRef.child("shops").addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                vendorlist.clear()
                for (v in snapshot.children){
                    val vendor = v.getValue(vendor::class.java)
                    if(vendor != null){
                        vendorlist.add(vendor)
                    }
                }
                adapter.updateVendorList(vendorlist)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Vendors, "Failed to load shop data!!", Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@Vendors,home::class.java)
        startActivity(intent)
    }
}

data class vendor(
    val ShopImage:String = "",
    val fullname:String = "",
    val disc:String = "",
    val shop_info:String = "",
    val shop_website:String = ""
)