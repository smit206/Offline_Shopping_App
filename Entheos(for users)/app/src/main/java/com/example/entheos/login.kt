package com.example.entheos

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class login : AppCompatActivity() {
    var l_email:EditText? = null
    var l_pass:EditText? = null
    var forget:TextView? = null
    var rgslbl:TextView? = null
    var lgnbtn:Button? = null
    var textor:TextView? = null
    var textcreate:TextView? = null
    var email = ""
    lateinit var sharedPref:SharedPreferences

    private lateinit var auth: FirebaseAuth

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        l_email = findViewById(R.id.l_email)
        l_pass= findViewById(R.id.l_pass)
        forget = findViewById(R.id.txt2)

        rgslbl = findViewById(R.id.lgn_rgs)
        lgnbtn= findViewById(R.id.btn_lgn)
        textor = findViewById(R.id.txt3)
        textcreate = findViewById(R.id.lgn_rgs)

        auth = FirebaseAuth.getInstance()

        sharedPref = getSharedPreferences("userdata", Context.MODE_PRIVATE)


        val content = SpannableString(textor!!.text)
        content.setSpan(UnderlineSpan(), 0 ,content.length,0)

        val new = SpannableString(textcreate!!.text)
        new.setSpan(UnderlineSpan(), 0, new.length,0)
        textcreate!!.text = new

        textor!!.text = content

        val d = intent.getBooleanExtra("action",false)
        if(d){
            val editor = sharedPref.edit()
            editor.clear()
            editor.apply()
        }else{
            email = sharedPref.getString("email","").toString()
        }

        lgnbtn!!.setOnClickListener {
            if(l_email!!.text.isEmpty() && l_pass!!.text.isEmpty()){
                Toast.makeText(this, "Please Enter Email and Password!!", Toast.LENGTH_SHORT).show()
            }else{
                val i1 = l_email!!.text.toString()
                val i2 = l_pass!!.text.toString()

                auth.signInWithEmailAndPassword(i1,i2)
                    .addOnCompleteListener(this){task ->
                        if(task.isSuccessful){
                            val user = auth.currentUser
                            Toast.makeText(this@login, "Logged in Successfully", Toast.LENGTH_SHORT).show()
                            val editor:SharedPreferences.Editor = sharedPref.edit()
                            editor.putString("email",l_email!!.text.toString())
                            editor.apply()
                            val intent = Intent(this,home::class.java)
                            startActivity(intent)
                            finish()
                        } else{
                            Toast.makeText(this@login, "Authentication Failed", Toast.LENGTH_SHORT).show()
                        }
                    }

            }
        }

        forget!!.setOnClickListener {
            showDialog()
        }


        rgslbl!!.setOnClickListener {
            val intent = Intent(this@login,MainActivity::class.java)
            startActivity(intent)

            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
        }
    }

    @SuppressLint("MissingInflatedId")
    private fun showDialog(){
        val dialog = LayoutInflater.from(this@login).inflate(R.layout.forgot_pass,null)
        var frgemail:String
        var send = dialog.findViewById<Button>(R.id.buSend)
        var cancel= dialog.findViewById<Button>(R.id.buCancel)
        var emailfrg = dialog.findViewById<EditText>(R.id.etfrgemail)

        val message = AlertDialog.Builder(this@login)
            .setView(dialog)
            .create()

        message.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        message.show()

        send!!.setOnClickListener {
            message.dismiss()
        }
        cancel!!.setOnClickListener {
            message.dismiss()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    override fun onStart() {
        super.onStart()

        if(!email.isEmpty()){
            val i = Intent(this,home::class.java)
            startActivity(i)
            finish()
        }
    }
}