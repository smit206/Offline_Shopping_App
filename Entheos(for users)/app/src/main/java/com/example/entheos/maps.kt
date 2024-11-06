package com.example.entheos

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager.Request
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.Drawable
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
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dalvik.system.InMemoryDexClassLoader
import java.lang.RuntimeException

class maps : Fragment(),OnMapReadyCallback {

    interface OnButtonClickeListener{
        fun onButtonClicked(image:Int,text: String)
    }

    private lateinit var googleMap:GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var auth: FirebaseAuth
    private lateinit var myRef: DatabaseReference
    private var directories: Button? = null
    private var maps: Button? = null
    private var btn:Button? = null
    private var onButtonClickeListener:OnButtonClickeListener? = null

    var lat = 0.0
    var lon = 0.0
    var mapdata = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            lat = it.getDouble("lat",0.0)
            lon = it.getDouble("lon",0.0)
            mapdata = it.getString("mapdata","")
        }
    }

    @SuppressLint("MissingInflatedId", "ResourceAsColor")
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.fragment_maps, container, false)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        auth = FirebaseAuth.getInstance()
        myRef = FirebaseDatabase.getInstance().reference

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

    private fun fetchShopLocations(){
        myRef.child("shops").addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(shopSnapshot in snapshot.children){
                    val shop = shopSnapshot.getValue(Shop::class.java)
                    if(shop != null && shop.latitude != 0.0 && shop.longitude != 0.0){
                        addShopMarker(shop)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireActivity(), "Failed to load shop locations", Toast.LENGTH_SHORT).show()
            }

        })
    }

    data class Shop(
        val fullname: String = "",
        val latitude: Double = 0.0,
        val longitude: Double = 0.0,
        val ShopImage: String = ""
    )

    private fun addShopMarker(shop:Shop){
        val position = LatLng(shop.latitude,shop.longitude)
         Glide.with(requireContext())
             .asBitmap()
             .load(shop.ShopImage)
             .apply(RequestOptions().override(100,100))
             .into(object : CustomTarget<Bitmap>(){
                 override fun onResourceReady(
                     resource: Bitmap,
                     transition: Transition<in Bitmap>?
                 ) {
                     val pinBackground = ContextCompat.getDrawable(requireContext(),R.drawable.custom_pin)
                     val combinedBitmap = overlayBitmap(pinBackground,resource)
                     val icon = BitmapDescriptorFactory.fromBitmap(combinedBitmap)
                     googleMap.addMarker(
                         MarkerOptions().position(position).title(shop.fullname).icon(icon)
                     )

                 }

                 override fun onLoadCleared(placeholder: Drawable?) {

                 }

             })
    }

    private fun overlayBitmap(background: Drawable?, foreground:Bitmap):Bitmap{

        if(background == null) return foreground

        val bgWidth = background.intrinsicWidth
        val bgHeight = background.intrinsicHeight
        background.setBounds(0,0,bgWidth,bgHeight)

        val combinedBitmap = Bitmap.createBitmap(bgWidth,bgHeight,Bitmap.Config.ARGB_8888)
        val canvas = Canvas(combinedBitmap)
        background.draw(canvas)

        val fgWidth = foreground.width
        val fgHeight = foreground.height
        val left = (bgWidth - fgWidth)/2
        val top = (bgHeight - fgHeight)/2

        canvas.drawBitmap(foreground,left.toFloat(),top.toFloat(),null)
        return combinedBitmap
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googlemap: GoogleMap) {
        googleMap = googlemap
        fetchShopLocations()
        googlemap.uiSettings.isMapToolbarEnabled = true
        val initialPosition:LatLng
        if(lat == 0.0 && lon == 0.0){
            initialPosition = LatLng(23.0189201, 72.5221128)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialPosition, 10.0f))
        }else{
            initialPosition = LatLng(lat,lon)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialPosition, 15.0f))
        }

        googleMap.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter{
            override fun getInfoContents(p0: Marker): View? {
                val customInfoWindow = LayoutInflater.from(requireContext()).inflate(R.layout.activity_infopopup,null)
                val titletext:TextView?  = customInfoWindow.findViewById(R.id.tvinfo)
                titletext?.text = p0.title
                btn = customInfoWindow.findViewById(R.id.buinfo)
                return customInfoWindow
            }

            override fun getInfoWindow(p0: Marker): View? = null

        })
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