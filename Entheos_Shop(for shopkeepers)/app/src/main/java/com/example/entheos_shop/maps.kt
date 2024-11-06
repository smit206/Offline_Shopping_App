package com.example.entheos_shop

import android.Manifest
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService

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
import com.google.firebase.database.getValue
import dalvik.system.InMemoryDexClassLoader
import java.lang.RuntimeException

@Suppress("UNUSED_EXPRESSION")
class maps : Fragment(),OnMapReadyCallback {

    private lateinit var googleMap:GoogleMap
    
    private lateinit var auth:FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var myRef:DatabaseReference

    private var mqmap:GoogleMap? = null
    private var btn:Button? = null
//    private var onButtonClickeListener:OnButtonClickeListener? = null
    private lateinit var locationManager:LocationManager
    private lateinit var currentMarker: Marker

    private var lat: Double = 0.0
    private var lon: Double = 0.0
    var mapdata = ""

    var rammandir = LatLng(lat,lon)


    private val callback = OnMapReadyCallback { map ->

        googleMap = map

        rammandir = if(lat == 0.0 && lon == 0.0){
            LatLng(21.761333714455336,70.37904551818707)
        }else{
            LatLng(lat,lon)
        }


        val icon = BitmapDescriptorFactory.fromResource(R.drawable.custom_pin)

        currentMarker = googleMap.addMarker(MarkerOptions().position(rammandir).title(mapdata).icon(icon))!!


        googleMap.uiSettings.isMapToolbarEnabled = true

        val zoomLevel = 15.0f
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(rammandir,zoomLevel))

    }

    @SuppressLint("MissingInflatedId", "ResourceAsColor")
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.fragment_maps, container, false)
        
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        myRef = database.reference
        btn = view.findViewById(R.id.btn_currentLoc)

        var LOCATION_PERMISSION_CODE = 123
        val myLocationL = MyLocationListener()
        locationManager = requireActivity().getSystemService(LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_CODE
            )
        }else{
            requestLocationUpdates()
        }

        getinfo()


        btn!!.setOnClickListener {
            updateLocation()
            val user = auth.currentUser
            if(user != null){
                myRef.child("shops").child(user.uid).child("latitude").setValue(lat)
                myRef.child("shops").child(user.uid).child("longitude").setValue(lon)
            }else{
                Toast.makeText(requireActivity(), "User Not authenticated!!", Toast.LENGTH_SHORT).show()
            }
        }
        
        

        lat = arguments?.getDouble("lat",0.0)?: 0.0
        lon = arguments?.getDouble("lon",0.0)?: 0.0
        mapdata = arguments?.getString("mapdata","").toString()

        return view
    }
    
    private fun getinfo(){
        val user = auth.currentUser
        if(user != null){
            myRef!!.child("shops").child(user.uid).child("latitude").addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val text = snapshot.getValue(Double::class.java)
                    if (text != null) {
                        lat = text
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireActivity(), "Failed to load location", Toast.LENGTH_SHORT).show()
                }

            })
            myRef!!.child("shops").child(user.uid).child("longitude").addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val text = snapshot.getValue(Double::class.java)
                    if (text != null) {
                        lon = text
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireActivity(), "Failed to load location", Toast.LENGTH_SHORT).show()
                }

            })
        }else{
            Toast.makeText(requireActivity(), "User not authenticated!!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        updateLocation()
        getinfo()
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates(){
        val myLocationL = MyLocationListener()
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0f,myLocationL)
    }

    private val locationListener:LocationListener = object :LocationListener{
        override fun onLocationChanged(location: Location) {
            lat = location.latitude.toDouble()
            lon = location.longitude.toDouble()
        }

    }

    @SuppressLint("MissingPermission")
    private fun updateLocation(){
        try{
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0f,locationListener)
            if(lat != 0.0 && lon != 0.0){
                val newPosition = LatLng(lat,lon)
                currentMarker.position = newPosition
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newPosition, 15.0f))
            }
        }catch (ex:Exception){
            Toast.makeText(requireActivity(), "Something Went Wrong", Toast.LENGTH_SHORT).show()
        }
    }


    var myLocation:Location? = null
    inner class MyLocationListener : LocationListener{
        constructor(): super(){
            myLocation = Location("me")
        }

        override fun onLocationChanged(location: Location) {
            myLocation = location
        }
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
}