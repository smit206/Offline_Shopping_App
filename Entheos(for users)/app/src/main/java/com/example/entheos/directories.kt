package com.example.entheos

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class directories : Fragment(), shopAdapter.OnShopItem {

    var recyclerview:RecyclerView? = null
    private var directories: Button? = null
    private var maps: Button? = null
    private val shoplist = ArrayList<shoplists>()
    private lateinit var shopAdapter: shopAdapter
    private lateinit var myRef: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @SuppressLint("MissingInflatedId", "ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(R.layout.fragment_directories, container, false)

        recyclerview = view.findViewById(R.id.rvshops)
        directories = view.findViewById(R.id.btn_directions1)
        maps = view.findViewById(R.id.btn_map1)

        recyclerview!!.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL ,false)
        }
        
        shopAdapter = shopAdapter(shoplist,requireContext())
        shopAdapter.setOnShopItemClickListener(this)
        recyclerview!!.adapter = shopAdapter

        auth = FirebaseAuth.getInstance()
        myRef = FirebaseDatabase.getInstance().reference

        directories!!.setOnClickListener {
//            directories!!.setBackgroundColor(R.color.darkpurple)
//            directories!!.setTextColor(R.color.white)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragview,this)
                .addToBackStack(null)
                .commit()
        }

        maps!!.setOnClickListener {
//            maps!!.setBackgroundColor(R.color.darkpurple)
//            maps!!.setTextColor(R.color.white)
            val mapfrag = maps()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragview,mapfrag)
                .addToBackStack(null)
                .commit()
        }




        directories!!.isPressed = true

        if (directories!!.isPressed){
            val darkpurple = ContextCompat.getColor(requireContext(),R.color.darkpurple)
            val white = ContextCompat.getColor(requireContext(),R.color.white)
            val content = SpannableString(directories!!.text)
            content.setSpan(UnderlineSpan(),0,content.length,0)
            directories!!.text = content
            directories!!.setBackgroundColor(darkpurple)
            directories!!.setTextColor(white)
        }
        fetchShopData()
        return view
    }

    override fun onItemClick(bundle: Bundle) {
        val fragmap = maps().apply {
            arguments = bundle
        }
         parentFragmentManager.beginTransaction()
            .replace(R.id.fragview,fragmap)
            .addToBackStack(null)
            .commit()
    }

    private fun fetchShopData(){

        shoplist.clear()

//        shoplist.add(shoplists("URL_TO_ICON","A & E Audiology & Hearing Center", "2160 Nall dr, Site 100 Lancaster, PA 17603", 21.761333714455336, 70.37904551818707))
//        shoplist.add(shoplists("URL_TO_ICON","Advanced Audiology Consultants", "3239 Rainbow Drive Valley City, OH 44280", 21.79137817737359, 70.221832323284))
//        shoplist.add(shoplists( "URL_TO_ICON","Advanced Hearing Solutions", "3752 Simons Hollow Road Jersey Shore, PA 17740", 22.286246989225283, 73.36433318096621))
//        shoplist.add(shoplists("URL_TO_ICON","Albama Hearing Associates", "3749 Briarwood Drive Laurel Springs, NJ 08021", 21.766791879997676, 70.20056870979106))
//        shoplist.add(shoplists("URL_TO_ICON","Alaska Hearing & Tinnitus Center", "1437 Drainer Avenue Pensacola, FL 32501", 22.298675526576833, 73.23583960980233))

        myRef.child("shops").addListenerForSingleValueEvent(object: ValueEventListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                shoplist.clear()
                    for(shopi in snapshot.children){
                        val shop = shopi.getValue(shoplists::class.java)
                        if(shop != null){
                            shoplist.add(shop)
                        }
                    }
                shopAdapter.updateShopList(shoplist )
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireActivity(), "Failed to load shop data!!", Toast.LENGTH_SHORT).show()
            }

        })

    }
}

data class shoplists(
    val ShopImage: String = "",
    val fullname: String = "",
    val shop_address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val shop_info: String = "",
    val shop_website: String = ""
)