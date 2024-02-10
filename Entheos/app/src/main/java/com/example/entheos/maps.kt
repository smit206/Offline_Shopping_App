package com.example.entheos

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dalvik.system.InMemoryDexClassLoader
import java.lang.RuntimeException

class maps : Fragment(),OnMapReadyCallback {

    data class MarkerData(val image:Int,val text:String)
    interface OnButtonClickeListener{
        fun onButtonClicked(image:Int,text: String)
    }

    private var mqmap:GoogleMap? = null
    private var directories: Button? = null
    private var maps: Button? = null
    private var btn:Button? = null
    private var onButtonClickeListener:OnButtonClickeListener? = null

    var lat = 0.0
    var lon = 0.0
    var mapdata = ""


    private val callback = OnMapReadyCallback { googleMap ->

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

        //        21.761333714455336, 70.37904551818707
//        21.79137817737359, 70.221832323284
//        22.286246989225283, 73.36433318096621
//        21.766791879997676, 70.20056870979106
//        22.298675526576833, 73.23583960980233

        val rammandir = if(lat == 0.0 && lon == 0.0){
            LatLng(21.761333714455336,70.37904551818707)
        }else{
            LatLng(lat,lon)
        }
        val locations = listOf(LatLng(21.761333714455336, 70.37904551818707) to MarkerData(R.drawable.raspberry,"A & E Audiology & Hearing Center"),
            LatLng(21.79137817737359, 70.221832323284) to MarkerData(R.drawable.canada,"Advanced Audiology Consultants"),
            LatLng(22.286246989225283, 73.36433318096621) to MarkerData(R.drawable.fish,"Advanced Hearing Solutions"),
            LatLng(21.766791879997676, 70.20056870979106) to MarkerData(R.drawable.lion,"Albama Hearing Associates"),
            LatLng(22.298675526576833, 73.23583960980233) to MarkerData(R.drawable.wisehome,"Alaska Hearing & Tinnitus Center"))

        val icon = BitmapDescriptorFactory.fromResource(R.drawable.custom_pin)

        googleMap.addMarker(MarkerOptions().position(rammandir).title(mapdata).icon(icon))
        for ((i,j) in locations){
            val marker = googleMap.addMarker(MarkerOptions().position(i).title(j.text).icon(icon))
            marker!!.tag = MarkerData(j.image,i.toString())
        }

        googleMap.uiSettings.isMapToolbarEnabled = true

        val zoomLevel = 15.0f
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(rammandir,zoomLevel))

        googleMap.setOnMarkerClickListener { marker ->
            marker.showInfoWindow()
            true
        }

//    val markerData = marker.tag as? MarkerData
//    if(markerData != null){
//        val intent = Intent(requireContext(),info::class.java)
//        intent.putExtra("image",markerData.image)
//        intent.putExtra("text",markerData.text)
//        startActivity(intent)


        //        val initzoom = 1.0f
//        val targetzoom = 15.0f
//        val valueAnimator = ValueAnimator.ofFloat(initzoom,targetzoom)
//        valueAnimator.duration = 3000
//
//        valueAnimator.addUpdateListener { animator ->
//            val animatedValue = animator.animatedValue as Float
//            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(rammandir,animatedValue))
//        }
//        valueAnimator.start()


        googleMap.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter{
            override fun getInfoWindow(marker: Marker): View? {
                return null
            }

            @SuppressLint("ResourceAsColor")
            override fun getInfoContents(marker: Marker): View? {
                val customInfoWindow = LayoutInflater.from(requireContext()).inflate(R.layout.activity_infopopup,null)

                val titletext:TextView? = customInfoWindow.findViewById(R.id.tvinfo)
                titletext!!.text = marker.title

                val pink = ContextCompat.getColor(requireContext(),R.color.btn_pink)

                btn = customInfoWindow.findViewById(R.id.buinfo)

//                val title = marker.title
//                val markerData = marker.tag as MarkerData

//                markerData?.let { onButtonClickeListener?.onButtonClicked(it.image,it.text) }

//                customInfoWindow.setOnClickListener {
//                    Toast.makeText(requireActivity(), "Info Window Clicked...", Toast.LENGTH_SHORT).show()
//                }
//
//                if(markerData != null){
//                    val intent = Intent(requireActivity(),info::class.java)
//                    intent.putExtra("image",markerData.image)
//                    intent.putExtra("text",title)
//                    startActivity(intent)
//                }
//                else {
//                    Toast.makeText(requireActivity(), "marker is null", Toast.LENGTH_SHORT).show()
//                }

                return customInfoWindow
            }
        })

    }

    @SuppressLint("MissingInflatedId", "ResourceAsColor")
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.fragment_maps, container, false)

        lat = arguments?.getDouble("lat",0.0)?: 0.0
        lon = arguments?.getDouble("lon",0.0)?: 0.0
        mapdata = arguments?.getString("mapdata","").toString()

        directories = view.findViewById(R.id.btn_directions)
        maps = view.findViewById(R.id.btn_map)

        directories!!.setOnClickListener {
            val direct = directories()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragview,direct)
                .addToBackStack(null)
                .commit()
        }

        maps!!.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragview,this)
                .addToBackStack(null)
                .commit()
        }
        maps!!.isPressed = true
        if (maps!!.isPressed){
            val darkpurple = ContextCompat.getColor(requireContext(),R.color.darkpurple)
            val white = ContextCompat.getColor(requireContext(),R.color.white)
            val content = SpannableString(maps!!.text)
            content.setSpan(UnderlineSpan(),0,content.length,0)
            maps!!.text = content
            maps!!.setBackgroundColor(darkpurple)
            maps!!.setTextColor(white)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onMapReady(googlemap: GoogleMap) {
        mqmap = googlemap

        googlemap.uiSettings.isMapToolbarEnabled = true

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is OnButtonClickeListener){
            onButtonClickeListener = context
        }else{
            throw RuntimeException("$context must implement OnButtonClickListener")
        }
    }
}