package com.example.entheos

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class home : AppCompatActivity(),maps.OnButtonClickeListener {
    var bottom:BottomNavigationView? = null
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val fraghome = homeone()
        val fragloc = directories()
        val messages = messages()
        val events = Events()

        var toolbar:androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)

        bottom = findViewById(R.id.bottom_home)

        setSupportActionBar(toolbar)
        supportActionBar!!.title = ""

        toolbar.setLogo(R.drawable.name1)

        setCurrentFragment(fraghome)

        val fragtoload = intent.getStringExtra("frag")
        if(fragtoload == "frag2"){
            setCurrentFragment(fragloc)
            bottom!!.selectedItemId = R.id.location
        }

        val frag = intent.getStringExtra("frag")
        if(frag == "add_frag"){
            setCurrentFragment(events)
            bottom!!.selectedItemId = R.id.events
        }

        bottom!!.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.home-> setCurrentFragment(fraghome)
                R.id.location->setCurrentFragment(fragloc)
                R.id.chats->setCurrentFragment(messages)
                R.id.events->setCurrentFragment(events)
                R.id.profile->true
            }
            true
        }
    }

    private fun setCurrentFragment(fragment:Fragment){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragview,fragment)
            commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    override fun onButtonClicked(image: Int, text: String) {
        val intent = Intent(this@home,info::class.java)
        intent.putExtra("image",image)
        intent.putExtra("text",text)
        startActivity(intent)
    }
}
