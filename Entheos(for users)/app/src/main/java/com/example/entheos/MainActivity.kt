package com.example.entheos

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    var lgnlbl:TextView? = null
    var rgsbtn:Button? = null
    var textlog:TextView? = null
    var fname:EditText? = null
    var lname:EditText? = null
    var email:EditText? = null
    var pass:EditText? = null
    lateinit var sf:SharedPreferences

    private lateinit var auth:FirebaseAuth
    private lateinit var firestore: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        lgnlbl = findViewById(R.id.lgn_tv)
        rgsbtn = findViewById(R.id.btn_rgs)
        textlog = findViewById(R.id.lgn_tv)

        fname = findViewById(R.id.f_name)
        lname = findViewById(R.id.l_name)
        email = findViewById(R.id.email)
        pass = findViewById(R.id.pass)




        sf = getSharedPreferences("uinfo",Context.MODE_PRIVATE)


        val content = SpannableString(textlog!!.text)
        content.setSpan(UnderlineSpan(),0,content.length,0)

        textlog!!.text = content

        rgsbtn!!.setOnClickListener {
            var i1 = email!!.text.toString()
            var i2 = pass!!.text.toString()
            var fullname = fname!!.text.toString()+ " " +lname!!.text.toString()

            auth.createUserWithEmailAndPassword(i1,i2)
                .addOnCompleteListener(this){task ->
                    if(task.isSuccessful){
                        val user = auth.currentUser

                        val userData = hashMapOf(
                            "fullname" to fullname,
                            "email" to i1
                        )

                        user?.let {
                            FirebaseDatabase.getInstance().reference.child("users").child(user.uid)
                                .setValue(userData)
                                .addOnSuccessListener {
                                    Toast.makeText(this@MainActivity, "Successfully Login", Toast.LENGTH_SHORT).show()
                                    val editor:SharedPreferences.Editor = sf.edit()
                                    editor.putString("username",fullname)
                                    editor.putString("email",email!!.text.toString())
                                    editor.apply()
                                    val i = Intent(this@MainActivity,home::class.java)
                                    startActivity(i)
                                }
                                .addOnFailureListener{ e ->
                                    Toast.makeText(this@MainActivity, "", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                    else{
                        Toast.makeText(this@MainActivity, "Email is already registered please login :)", Toast.LENGTH_SHORT).show()
                    }
                }

        }

        lgnlbl!!.setOnClickListener{
            val intent = Intent(this@MainActivity,login::class.java)
            startActivity(intent)

            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}