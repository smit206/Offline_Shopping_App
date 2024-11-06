package com.example.entheos_shop

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager.Request
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Video.Media
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.storage.FirebaseStorage
import com.google.protobuf.Value
import java.io.ByteArrayOutputStream
import java.lang.Exception
import kotlin.math.log

class profile_frag : Fragment() {

    var uname:TextView? = null
    var uemail:TextView? = null
    var upic:ImageView?= null
    var logout: Button? = null
    var shop_desc:EditText? = null
    var shop_add:EditText? = null
    var shop_website:EditText? = null
    var shop_info:EditText? = null
    var btn_edit:Button? = null
    lateinit var sf:SharedPreferences

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private var myRef:DatabaseReference? = null
    private lateinit var storage: FirebaseStorage
    private var selectedImageUri:Uri? = null
    private lateinit var googleSingInClient: GoogleSignInClient

    @SuppressLint("MissingInflatedId")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val inflater = inflater.inflate(R.layout.fragment_profile_frag, container, false)
        // Inflate the layout for this fragment

        uname = inflater.findViewById(R.id.txt_uname)
        uemail = inflater.findViewById(R.id.txt_email)
        upic = inflater.findViewById(R.id.iv_profilepic)
        shop_desc = inflater.findViewById(R.id.et_shop_desc)
        shop_add = inflater.findViewById(R.id.et_shop_address)
        shop_website = inflater.findViewById(R.id.et_shop_email)
        shop_info = inflater.findViewById(R.id.et_shop_info)
        btn_edit = inflater.findViewById(R.id.btn_edit)
        logout = inflater.findViewById(R.id.btn_logout)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        myRef = database.reference
        storage = FirebaseStorage.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSingInClient = GoogleSignIn.getClient(requireActivity(),gso)

        val user = auth.currentUser
        user?.let {
            database.reference.child("shops").child(user.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            val userData = snapshot.getValue(User::class.java)

                            uname!!.text = userData?.fullname
                            uemail!!.text = userData?.email
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e(TAG, "Error getting user data",error.toException())
                    }
                })
        }

        getinfo()

        btn_edit!!.setOnClickListener {
            setinfo(shop_desc!!.text.toString(),shop_add!!.text.toString(),shop_website!!.text.toString(),shop_info!!.text.toString())
        }

        upic!!.setOnClickListener {
            checkpermission()
        }

//        sf = requireActivity().getSharedPreferences("userdata",Context.MODE_PRIVATE)


        logout!!.setOnClickListener {
            signout()

            val i = Intent(requireActivity(), login::class.java)
            i.putExtra("action",true)
            startActivity(i)
        }

        loadProfilePicture()

        return inflater
    }

    override fun onStart() {
        super.onStart()
        loadProfilePicture()
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onResume() {
        super.onResume()
        if(ActivityCompat.checkSelfPermission(requireActivity(),android.Manifest.permission.READ_MEDIA_IMAGES)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireActivity(),android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES,android.Manifest.permission.READ_EXTERNAL_STORAGE),imagecode)
        }
        loadProfilePicture()
    }

    fun signout(){
        auth.signOut()

        //Google sign-out
        googleSingInClient.signOut().addOnCompleteListener {
            Toast.makeText(requireActivity(), "Signed out successfully", Toast.LENGTH_SHORT).show()
        }
    }

    val imagecode = 123
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun checkpermission(){
        if(Build.VERSION.SDK_INT>=32){
            if(ActivityCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.READ_MEDIA_IMAGES)!= PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES),imagecode)
                if(ActivityCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.READ_MEDIA_IMAGES)!= PackageManager.PERMISSION_GRANTED){
                    showdialog()
                }
            }
            else{
                loadImage()
                return
            }
        }
        else{
            if(ActivityCompat.checkSelfPermission(requireActivity(),android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),imagecode)
                if(ActivityCompat.checkSelfPermission(requireActivity(),android.Manifest.permission.READ_MEDIA_IMAGES)!= PackageManager.PERMISSION_GRANTED){
                    showdialog()
                }
            }
            else{
                loadImage()
                return
            }
        }
    }


    private fun setinfo(text1:String,text2:String,text3:String,text4:String){
        val current = auth.currentUser
        var allokay:Boolean = false
        if(current != null){
            myRef!!.child("shops").child(current!!.uid).child("shop_info").setValue(text1)
                .addOnSuccessListener {
                    allokay = true
                }
                .addOnFailureListener { ex ->
                    allokay = false
                }
            myRef!!.child("shops").child(current!!.uid).child("shop_address").setValue(text2)
                .addOnSuccessListener {
                    allokay = true
                }
                .addOnFailureListener { ex ->
                    allokay = false
                }
            myRef!!.child("shops").child(current!!.uid).child("shop_website").setValue(text3)
                .addOnSuccessListener {
                    allokay = true
                }
                .addOnFailureListener { ex ->
                    allokay= false
                }
            myRef!!.child("shops").child(current!!.uid).child("disc").setValue(text4)
                .addOnSuccessListener {
                    allokay = true
                }
                .addOnFailureListener {ex ->
                    allokay = false
                }
            if(!allokay){
                Toast.makeText(requireActivity(), "Shop Information Updated.", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(requireActivity(), "Failed To Update!!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireActivity(), "User Not authenticated!!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getinfo(){
        val current = auth.currentUser
        if (current != null){
            myRef!!.child("shops").child(current.uid).child("shop_info").addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val text = snapshot.getValue(String::class.java)
                    shop_desc!!.setText(text)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireActivity(), "Failed to loadtext : ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
            myRef!!.child("shops").child(current.uid).child("shop_address").addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val text = snapshot.getValue(String::class.java)
                    shop_add!!.setText(text)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireActivity(), "Failed to loadtext : ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
            myRef!!.child("shops").child(current.uid).child("shop_website").addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val text = snapshot.getValue(String::class.java)
                    shop_website!!.setText(text)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireActivity(), "Failed to loadtext : ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
            myRef!!.child("shops").child(current.uid).child("disc").addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val text = snapshot.getValue(String::class.java)
                    shop_info!!.setText(text)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireActivity(), "Failed to loadtext : ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
        else{
            Toast.makeText(requireActivity(), "User not authenticated!!", Toast.LENGTH_SHORT).show()
        }
    }

//    private fun getinfo():String{
//        current?.let {
//
//        }
//    }

    val PICK_IMAGE_CODE = 123

    fun loadImage(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent,PICK_IMAGE_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==PICK_IMAGE_CODE && resultCode == Activity.RESULT_OK){
            data?.data?.let { uri ->
                selectedImageUri = uri
                upic!!.setImageURI(uri)
                uploadProfilePicture(uri)
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun uploadProfilePicture(uri: Uri){
        val user = auth.currentUser

        if(user != null){
            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.getReferenceFromUrl("gs://entheos-aa7c2.appspot.com")
            val imagePAth = user.uid+".jpg"
            val imageRef = storageRef.child("shop_images/" + imagePAth)
            try {
                val drawable = upic!!.drawable as BitmapDrawable
                val bitmap = drawable.bitmap
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos)
                val data = baos.toByteArray()
                val uploadTask = imageRef.putBytes(data)

                uploadTask.addOnSuccessListener { taskSnapshot ->
                    val downloadurl = taskSnapshot.storage.downloadUrl
                    downloadurl.addOnSuccessListener { uri ->
                        val downloadURL = uri.toString()
                        myRef!!.child("shops").child(user.uid).child("ShopImage").setValue(downloadURL)
                        Toast.makeText(requireActivity(), "Shop Picture uploaded Successfully", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener { exception ->
                        Log.e(TAG, "Failed to get download URL for uploaded image", exception)
                        Toast.makeText(requireActivity(), "Failed to get download URL for uploaded image", Toast.LENGTH_SHORT).show()
                    }
                }
                uploadTask.addOnFailureListener{
                    Toast.makeText(requireActivity(), "Failed To Upload", Toast.LENGTH_SHORT).show()
                }
            }catch (e:Exception){
                Log.e(TAG, "Error compressing or uploading image", e)
                Toast.makeText(requireActivity(), "failed to upload", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireActivity(), "Please sign in to upload", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadProfilePicture(){
        val user = auth.currentUser
        user?.let {currentUser ->
            val storageRef = storage.getReferenceFromUrl("gs://entheos-aa7c2.appspot.com")
            val imageRef = storageRef.child("shop_images/${currentUser.uid}.jpg")
            try {
                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    Glide.with(requireContext())
                        .load(downloadUri)
                        .placeholder(R.drawable.profile)
                        .into(upic!!)
                }.addOnFailureListener { exception ->
                    Log.e(TAG, "Failed to load shop picture", exception)
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    fun showdialog(){
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Permission Required")
        builder.setMessage("Some Permission are needed to be allowed to use this feature.")
        builder.setPositiveButton("Grant"){d,_ ->
            d.cancel()
            startActivity(
                Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package com.example.entheos_shop")))
        }
        builder.setNegativeButton("Cancel"){d,_ ->
            d.dismiss()
        }
        builder.show()
    }

}