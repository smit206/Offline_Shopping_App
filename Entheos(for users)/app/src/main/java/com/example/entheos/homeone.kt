package com.example.entheos

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class homeone : Fragment() {

    var recyclerview:RecyclerView? = null
    var vendor:LinearLayout? = null
    var price:LinearLayout? = null
    var promo:LinearLayout? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.fragment_homeone, container, false)
        recyclerview = view.findViewById<RecyclerView>(R.id.rvadds)

        vendor = view.findViewById(R.id.vendors)
        price = view.findViewById(R.id.pricelist)
        promo = view.findViewById(R.id.promos)

        recyclerview!!.apply {
            layoutManager = GridLayoutManager(requireContext(), 1, GridLayoutManager.HORIZONTAL, false) }

        val data= ArrayList<addViewModel>()

        data.add(addViewModel("Sponsor Advertise\nPoster-1",R.drawable.ad1))
        data.add(addViewModel("Sponsor Advertise\nPoster-2",R.drawable.ad1))
        data.add(addViewModel("Sponsor Advertise\nPoster-3",R.drawable.ad1))
        data.add(addViewModel("Sponsor Advertise\nPoster-4",R.drawable.ad1))
        data.add(addViewModel("Sponsor Advertise\nPoster-5",R.drawable.ad1))

        val adapter = CustomAdapter(data)
        recyclerview!!.adapter = adapter

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
}