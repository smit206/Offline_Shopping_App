package com.example.entheos_shop

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlin.math.PI

class ShopKeeperBannerFragment : Fragment() {

    private val PICK_IMAGE_REQUEST = 1
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var database: FirebaseDatabase
    private var myRef:DatabaseReference? = null
    private var selectedImageUri: Uri? = null
    private lateinit var uploadButton: Button
    private lateinit var selectImageButton: Button
    private lateinit var bannerPreview: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_shop_keeper_banner, container, false)

        storage = FirebaseStorage.getInstance()
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        myRef = database.reference

        selectImageButton = view.findViewById(R.id.selectImageButton)
        uploadButton = view.findViewById(R.id.uploadButton)
        bannerPreview = view.findViewById(R.id.bannerPreview)
        
        selectImageButton.setOnClickListener { 
            openFileChooser()
        }

        uploadButton.setOnClickListener {
            uploadBannerToFirebase(selectedImageUri!!)
        }


        return view
    }
    
    private fun openFileChooser(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null){
            selectedImageUri = data.data
            bannerPreview.setImageURI(selectedImageUri)
        }
    }
    
    private fun uploadBannerToFirebase(uri: Uri){
        val user = auth.currentUser
        user?.let {
            myRef!!.child("shops").child(user.uid).addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val shopName = snapshot.child("fullname").getValue(String::class.java)
                    if(shopName != null){
                        val storageRef = storage.reference.child("banners/${System.currentTimeMillis()}.jpg")
                        storageRef.putFile(uri).addOnSuccessListener {
                            storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                                val banner = hashMapOf(
                                    "shopName" to shopName,
                                    "imageUrl" to downloadUri.toString()
                                )
                                myRef!!.child("banners").push().setValue(banner).addOnCompleteListener { task ->
                                    if(task.isSuccessful){
                                        Toast.makeText(context, "Banner Upload successfully.", Toast.LENGTH_SHORT).show()
                                    }else{
                                        Toast.makeText(context, "Failed to upload banner!!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }else{
                        Toast.makeText(context, "Shop name not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Error fetching shop name", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}