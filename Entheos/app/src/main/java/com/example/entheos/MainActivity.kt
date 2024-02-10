package com.example.entheos

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    var lgnlbl:TextView? = null
    var rgsbtn:Button? = null
    var textlog:TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lgnlbl = findViewById(R.id.lgn_tv)
        rgsbtn = findViewById(R.id.btn_rgs)
        textlog = findViewById(R.id.lgn_tv)

        val content = SpannableString(textlog!!.text)
        content.setSpan(UnderlineSpan(),0,content.length,0)

        textlog!!.text = content

        rgsbtn!!.setOnClickListener {
            val i = Intent(this@MainActivity,home::class.java)
            startActivity(i)
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