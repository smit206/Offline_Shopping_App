package com.example.entheos

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Runnable

class homeone : Fragment() {

    var vendor:LinearLayout? = null
    var price:LinearLayout? = null
    var promo:LinearLayout? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var database: FirebaseDatabase
    private var myRef: DatabaseReference? = null
    private lateinit var bannerAdapter: BannerAdapter
    private lateinit var bannerList: MutableList<Banner>

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.fragment_homeone, container, false)

        vendor = view.findViewById(R.id.vendors)
        price = view.findViewById(R.id.pricelist)
        promo = view.findViewById(R.id.promos)

        recyclerView = view.findViewById(R.id.rvadds)
        database = FirebaseDatabase.getInstance()
        myRef = database.reference
        recyclerView.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)

        bannerList = mutableListOf()
        bannerAdapter = BannerAdapter(bannerList)
        recyclerView.adapter = bannerAdapter

        loadBannersFromFirebase()


        val data= ArrayList<addViewModel>()

        data.add(addViewModel("Sponsor Advertise\nPoster-1",R.drawable.ad1))
        data.add(addViewModel("Sponsor Advertise\nPoster-2",R.drawable.ad1))
        data.add(addViewModel("Sponsor Advertise\nPoster-3",R.drawable.ad1))
        data.add(addViewModel("Sponsor Advertise\nPoster-4",R.drawable.ad1))
        data.add(addViewModel("Sponsor Advertise\nPoster-5",R.drawable.ad1))



        vendor!!.setOnClickListener {
            val intent = Intent(requireActivity(),Vendors::class.java)
            startActivity(intent)
        }

        price!!.setOnClickListener {
            val i = Intent(requireActivity(),pricelist_act::class.java)
            startActivity(i)
        }

        promo!!.setOnClickListener {
            val i = Intent(requireActivity(),promos_discount::class.java)
            startActivity(i)
        }


        return view
    }

    private fun loadBannersFromFirebase(){
        myRef!!.child("banners").addValueEventListener(object: ValueEventListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                bannerList.clear()
                for(dataSnapshot in snapshot.children){
                    val banner = dataSnapshot.getValue(Banner::class.java)
                    banner?.let { bannerList.add(it) }
                }
                bannerAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Nothing Matters!!
            }

        })
    }

    private fun autoScrollRecyclerView(){
        val handler = Handler()
        val scrollRunnable = object : Runnable{
            override fun run() {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager

                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                val totalItems = bannerList.size

                if(firstVisibleItemPosition == 0 && totalItems > 1){
                    handler.postDelayed(this,3000)
                    recyclerView.smoothScrollToPosition(1)
                }
            }
        }
        handler.postDelayed(scrollRunnable,3000)
    }

    override fun onResume() {
        super.onResume()
        autoScrollRecyclerView()
    }
}

data class Banner(
    val shopName: String = "",
    val imageUrl: String = ""
)