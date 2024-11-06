package com.example.entheos_shop

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {
    var lgnlbl:TextView? = null
    var rgsbtn:Button? = null
    var textlog:TextView? = null
    var fname:EditText? = null
    var email:EditText? = null
    var pass:EditText? = null
    var googleBtn:Button? = null
    lateinit var sf:SharedPreferences
    lateinit var editor:SharedPreferences.Editor

    private lateinit var auth:FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient


    @SuppressLint("MissingInflatedId", "CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        lgnlbl = findViewById(R.id.lgn_tv)
        rgsbtn = findViewById(R.id.btn_rgs)
        textlog = findViewById(R.id.lgn_tv)

        fname = findViewById(R.id.f_name)
        email = findViewById(R.id.email)
        pass = findViewById(R.id.pass)
        googleBtn = findViewById(R.id.btn_sngggl)
        auth = FirebaseAuth.getInstance()
        
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        
        googleSignInClient = GoogleSignIn.getClient(this,gso)


        sf = getSharedPreferences("uinfo",Context.MODE_PRIVATE)
        editor = sf.edit()

        val content = SpannableString(textlog!!.text)
        content.setSpan(UnderlineSpan(),0,content.length,0)

        textlog!!.text = content

        rgsbtn!!.setOnClickListener {
            var i1 = email!!.text.toString()
            var i2 = pass!!.text.toString()
            var fullname = fname!!.text.toString()

            auth.createUserWithEmailAndPassword(i1,i2)
                .addOnCompleteListener(this){task ->
                    if(task.isSuccessful){
                        val user = auth.currentUser

                        val userData = hashMapOf(
                            "fullname" to fullname,
                            "email" to i1
                        )

                        user?.let {
                            FirebaseDatabase.getInstance().reference.child("shops").child(user.uid)
                                .setValue(userData)
                                .addOnSuccessListener {
                                    Toast.makeText(this@MainActivity, "Successfully Login", Toast.LENGTH_SHORT).show()
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
        
        googleBtn!!.setOnClickListener { 
            signInWithGoogle()
        }

        lgnlbl!!.setOnClickListener{
            val intent = Intent(this@MainActivity,login::class.java)
            startActivity(intent)

            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
        }
    }
    
    private fun signInWithGoogle(){
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result->
        if(result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResult(task)
        }else{
            Toast.makeText(this@MainActivity, "SignIn Canceled", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun handleResult(task: Task<GoogleSignInAccount>){
        if(task.isSuccessful){
            val account: GoogleSignInAccount? = task.result
            if(account != null){
                updateUI(account)
            }
        }else{
            Toast.makeText(this@MainActivity, "SignIn Failed, try again later!", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun updateUI(account:GoogleSignInAccount){
        val credentials = GoogleAuthProvider.getCredential(account.idToken,null)
        auth.signInWithCredential(credentials).addOnCompleteListener { task ->
            if(task.isSuccessful){
                val user = auth.currentUser
                
                if(user != null){
                    val fullname = account.displayName
                    val email = account.email
                    
                    val userRef = FirebaseDatabase.getInstance().reference.child("shops").child(user.uid)
                    
                    val userData = hashMapOf(
                        "fullname" to fullname,
                        "email" to email
                    )
                    
                    userRef.setValue(userData)
                        .addOnSuccessListener { 
                            editor.putString("username",fullname)
                            editor.putString("email",email)
                            editor.apply()

                            Toast.makeText(this@MainActivity, "Logged in successfully", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@MainActivity,home::class.java)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this@MainActivity, "Failed to save user data", Toast.LENGTH_SHORT).show()
                        }
                }
            }else{
                Toast.makeText(this@MainActivity, "Autentication Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}