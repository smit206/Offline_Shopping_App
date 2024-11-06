package com.example.entheos_shop

import android.annotation.SuppressLint
import android.app.Activity
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
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
    var googleBtn:Button? = null
    lateinit var sharedPref:SharedPreferences
    lateinit var editor:SharedPreferences.Editor

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    @SuppressLint("MissingInflatedId", "CutPasteId")
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
        googleBtn = findViewById(R.id.btn_lgnggl)

        auth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this,gso)

        sharedPref = getSharedPreferences("userdata", Context.MODE_PRIVATE)
        editor = sharedPref.edit()


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

        googleBtn!!.setOnClickListener {
            singInWithGoogle()
        }


        rgslbl!!.setOnClickListener {
            val intent = Intent(this@login,MainActivity::class.java)
            startActivity(intent)

            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
        }
    }

    @SuppressLint("MissingInflatedId")
    private fun showDialog(){
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.forgot_pass,null)
        dialogBuilder.setView(dialogView)

        val emailEditText = dialogView.findViewById<EditText>(R.id.etfrgemail)

        dialogBuilder.setTitle("Forgot Password")
        dialogBuilder.setPositiveButton("Send OTP") {_, _ ->
            val email = emailEditText.text.toString()
            if(email.isNotEmpty()){
                sendResetEmail(email)
            }else{
                Toast.makeText(this@login, "Please enter your email.", Toast.LENGTH_SHORT).show()
            }
        }
        dialogBuilder.setNegativeButton("Cancel") {dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    private fun sendResetEmail(email: String) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    Toast.makeText(this@login, "Password reset email sent to $email", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this@login, "Failed to send reset email", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun singInWithGoogle(){
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result ->
        if(result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResult(task)
        }else{
            Toast.makeText(this@login, "SignIn Canceled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleResult(task: Task<GoogleSignInAccount>) {
        if(task.isSuccessful){
            val account:GoogleSignInAccount? = task.result
            if(account!= null){
                updateUI(account)
            }
        }else{
            Toast.makeText(this@login, "SignIn Failed, try again later!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credentials = GoogleAuthProvider.getCredential(account.idToken,null)
        auth.signInWithCredential(credentials).addOnCompleteListener {
            if(it.isSuccessful){
                val user = auth.currentUser
                if(user != null){
                    val userRef = FirebaseDatabase.getInstance().reference.child("shops").child(user.uid)

                    // Check if the user already exists in the database
                    userRef.get().addOnCompleteListener { datasnapshot ->
                        val fullname = account.displayName?: "Unknown User"
                        val email = account.email?: "Unknown Email"

                        val userData = hashMapOf(
                            "fullname" to fullname,
                            "email" to email
                        )
                        
                        userRef.setValue(userData)
                            .addOnSuccessListener { 
                                editor.putString("username",fullname)
                                editor.putString("email",email)
                                editor.apply()

                                Toast.makeText(this@login, "Logged in successfully", Toast.LENGTH_SHORT).show()
                                
                                val intent = Intent(this@login,home::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this@login, "Welcome Back!", Toast.LENGTH_SHORT).show()

                                val intent = Intent(this@login,home::class.java)
                                startActivity(intent)
                                finish()
                            }
                    }
                }
            }else{
                Toast.makeText(this@login, "Authentication Failed", Toast.LENGTH_SHORT).show()
            }
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