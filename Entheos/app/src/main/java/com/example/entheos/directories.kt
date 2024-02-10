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
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class directories : Fragment(), shopAdapter.OnShopItem {

    var recyclerview:RecyclerView? = null
    private var directories: Button? = null
    private var maps: Button? = null

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



        val data = ArrayList<shopmodel>()

//        21.761333714455336, 70.37904551818707
//        21.79137817737359, 70.221832323284
//        22.286246989225283, 73.36433318096621
//        21.766791879997676, 70.20056870979106
//        22.298675526576833, 73.23583960980233

        data.add(shopmodel(R.drawable.raspberry,"A & E Audiology & Hearing Center","2160 Nall dr, Site 100 Lancaster, PA 17603",21.761333714455336,70.37904551818707))
        data.add(shopmodel(R.drawable.canada,"Advanced Audiology Consultants","3239 Rainbow Drive Valley City, OH 44280",21.79137817737359,70.221832323284))
        data.add(shopmodel(R.drawable.fish,"Advanced Hearing Solutions","3752 Simons Hollow Road Jersey Shore, PA 17740",22.286246989225283,73.36433318096621))
        data.add(shopmodel(R.drawable.lion,"Albama Hearing Associates","3749 Briarwood Drive Laurel Springs, NJ 08021",21.766791879997676,70.20056870979106))
        data.add(shopmodel(R.drawable.wisehome,"Alaska Hearing & Tinnitus Center","1437 Drainer Avenue Pensacola, FL 32501",22.298675526576833,73.23583960980233))

        val adapter = shopAdapter(data,this)
        recyclerview!!.adapter = adapter

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
        return view
    }

    override fun onItemClick(lat: Double, lon: Double,mapdata:String) {
        val fragmap = maps()
        val bundle = Bundle()
        bundle.putDouble("lat",lat)
        bundle.putDouble("lon",lon)
        bundle.putString("mapdata",mapdata)
        fragmap.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragview,fragmap)
            .addToBackStack(null)
            .commit()
    }
}