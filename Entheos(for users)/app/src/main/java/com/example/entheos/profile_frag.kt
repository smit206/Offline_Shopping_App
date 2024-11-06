package com.example.entheos

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
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
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

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private var myRef:DatabaseReference? = null
    private lateinit var storage: FirebaseStorage
    private var selectedImageUri:Uri? = null
    private lateinit var googleSignInClient: GoogleSignInClient

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
        logout = inflater.findViewById(R.id.btn_logout)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        myRef = database.reference
        storage = FirebaseStorage.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(),gso)

        val user = auth.currentUser
        user?.let {
            database.reference.child("users").child(user.uid)
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
        googleSignInClient.signOut().addOnCompleteListener {
            Toast.makeText(requireActivity(), "Signed out succcessfully!", Toast.LENGTH_SHORT).show()
        }

        //Revoke access to force account selection on next sign-in
        googleSignInClient.revokeAccess().addOnCompleteListener {
            // Toast.makeText(requireActivity(), "Access revoked, accout selection will be required next time", Toast.LENGTH_SHORT).show()
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
            val imageRef = storageRef.child("images/" + imagePAth)
            try {
                val drawable = upic!!.drawable as BitmapDrawable
                val bitmap = drawable.bitmap

                val resizedBitmap = Bitmap.createScaledBitmap(bitmap,50,50,true)

                val baos = ByteArrayOutputStream()
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG,100,baos)
                val data = baos.toByteArray()
                val uploadTask = imageRef.putBytes(data)

                uploadTask.addOnSuccessListener { taskSnapshot ->
                    val downloadurl = taskSnapshot.storage.downloadUrl
                    downloadurl.addOnSuccessListener { uri ->
                        val downloadURL = uri.toString()
                        myRef!!.child("users").child(user.uid).child("ProfileImage").setValue(downloadURL)
                        Toast.makeText(requireActivity(), "Profile Picture uploaded Successfully", Toast.LENGTH_SHORT).show()
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
            val imageRef = storageRef.child("images/${currentUser.uid}.jpg")
            imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                Glide.with(requireContext())
                    .load(downloadUri)
                    .placeholder(R.drawable.profile)
                    .into(upic!!)
            }.addOnFailureListener { exception ->
                Log.e(TAG, "Failed to load profile picture", exception)
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
                Uri.parse("package:com.example.entheos")))
        }
        builder.setNegativeButton("Cancel"){d,_ ->
            d.dismiss()
        }
        builder.show()
    }

}